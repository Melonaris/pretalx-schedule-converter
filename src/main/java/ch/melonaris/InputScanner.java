package ch.melonaris;

public class InputScanner {
    private static java.util.Scanner inputScanner;

    public static void Open() {
        inputScanner = new java.util.Scanner(System.in);
    }

    public static String getInput() {
        return inputScanner.nextLine();
    }

    public static void closeScanner() {
        inputScanner.close();
    }
}
