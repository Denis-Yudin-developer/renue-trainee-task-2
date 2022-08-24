import search.Searcher;

public class Main {
    public static void main(String[] args) {
        int columnNumber = Integer.parseInt(args[0]);
        if(columnNumber < 0 || columnNumber > 14){
            throw new IllegalArgumentException("The column in the CSV file is missing");
        }
        Searcher searcher = new Searcher(columnNumber);
        searcher.run();
    }
}
