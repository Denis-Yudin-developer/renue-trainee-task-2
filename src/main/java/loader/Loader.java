package loader;

import parser.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Loader {
    private LinkedHashMap<String, List<List<Integer>>> recordBytePosition;
    private File csvFile;
    private final String fileName = "/airports.csv";

    public Loader() {
        csvFile = new File(getResourcePath(getExecutionPath()));
        recordBytePosition = new LinkedHashMap<>();
    }

    public String getFileName() {
        return fileName;
    }

    public File getCsvFile(){
        return csvFile;
    }

    public void loadRecordBytePosition(Integer columnNumber) {
        String key;
        int recordStartBytePosition = 0;
        try (FileReader fr = new FileReader(csvFile);
             BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<Integer> bytesList = new ArrayList<>();
                CSVRecord currentRecord = new CSVRecord(line);
                key = currentRecord.getColumnCurrentField(columnNumber);
                bytesList.add(recordStartBytePosition);
                bytesList.add(currentRecord.getByteLength());
                if(!recordBytePosition.containsKey(key)){
                    List<List<Integer>> list = new ArrayList<>();
                    list.add(bytesList);
                    recordBytePosition.put(key, list);
                }
                else{
                    List<List<Integer>> list= recordBytePosition.get(key);
                    list.add(bytesList);
                    recordBytePosition.put(key, list);
                }
                recordStartBytePosition += currentRecord.getByteLength() + 2;
            }
        }
        catch (IOException e){
            System.err.println("File not found");
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String, List<List<Integer>>> getRecordBytePosition(){
        return recordBytePosition;
    }

    private String getExecutionPath(){
        String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.replaceAll("%20"," ");
        return absolutePath;
    }

    private String getResourcePath(String executionPath){
        return executionPath + "/classes" + fileName;
    }
}
