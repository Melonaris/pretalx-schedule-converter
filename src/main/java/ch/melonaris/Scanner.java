package ch.melonaris;

import java.io.File;

public class Scanner {
    private static java.util.Scanner inputScanner;

    public static void openScanner() {
        inputScanner = new java.util.Scanner(System.in);
    }

    public static void closeScanner() {
        inputScanner.close();
    }

    public static java.util.Scanner getScanner() {
        return inputScanner;
    }
}
