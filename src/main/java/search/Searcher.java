package search;

import exception.RecordNotFoundException;
import loader.Loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

public class Searcher {
    private Loader loader;
    private Scanner scanner;
    private TreeSet<String> recordFieldSet;

    public Searcher(Integer columnNumber) {
        loader = new Loader();
        loader.loadRecordBytePosition(columnNumber);
        recordFieldSet = new TreeSet<>(loader.getRecordBytePosition().keySet());
    }

    public void run() {
        scanner = new Scanner(System.in);
        boolean stop = false;
        do{
            System.out.println("Введите строку:");
            String searchQuery = scanner.nextLine().toLowerCase();
            if(searchQuery.equals("!quit")){
                stop = true;
                continue;
            }
            long start = System.currentTimeMillis();
            List<String> foundRecords = Collections.emptyList();
            try {
                foundRecords = searchRecords(searchQuery, recordFieldSet, loader.getRecordBytePosition());
                if(foundRecords.isEmpty()){
                    throw new RecordNotFoundException("No such record in csv file");
                }
            } catch (RecordNotFoundException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            printRecords(foundRecords);
            printTimeAndQuantity(end-start, foundRecords.size());
        } while(!stop);
    }

    public List<String> searchRecords(String searchQuery,
                                      TreeSet<String> recordFieldSet,
                                      LinkedHashMap<String, List<List<Integer>>> recordBytePosition) {
        ArrayList<String> foundRecords = new ArrayList<>();
        try{
            FileInputStream fis = new FileInputStream(loader.getCsvFile());
            for(String field: recordFieldSet){
                if (field.toLowerCase().startsWith(searchQuery)){
                    List<List<Integer>> recordsBytePositionList = new ArrayList<>(new ArrayList<>(recordBytePosition.get(field)));
                    while (recordsBytePositionList.size() > 0){
                        List<Integer> recordBytePositionList = recordsBytePositionList.get(0);
                        int startPosition = recordBytePositionList.get(0);
                        try {
                            fis.getChannel().position(startPosition);
                        } catch (IOException e) {
                            System.err.println("File not found");
                            e.printStackTrace();
                        }
                        int bytesSize = recordBytePositionList.get(1);
                        byte[] bytes = new byte[bytesSize];
                        try {
                            fis.read(bytes);
                        } catch (IOException e) {
                            System.err.println("Cannot read bytes from file");
                            e.printStackTrace();
                        }
                        foundRecords.add(field.concat("[") + new String(bytes).concat("]"));
                        recordsBytePositionList.remove(0);
                    }
                }
            }
            fis.close();
        }
        catch (IOException e){
            System.err.println("Cannot open input stream");
            e.printStackTrace();
        }
        return foundRecords;
    }

    private void printRecords(List<String> records){
        records.forEach(System.out::println);
    }

    private void printTimeAndQuantity(long time, int quantity){
        System.out.println("Колиство найденных записей: " + quantity);
        System.out.println("Время, затраченное на поиск: " + time + " мс");
    }
}
