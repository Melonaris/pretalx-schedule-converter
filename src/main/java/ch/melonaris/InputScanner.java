package ch.melonaris;

import java.io.InputStream;
import java.util.Scanner;

public class InputScanner {
    private static Scanner inputScanner;

    public static void open() {
        inputScanner = new Scanner(System.in);
    }

    public static void open(InputStream inputStream) {
        inputScanner = new Scanner(inputStream);
    }

    public static String getInput() {
        return inputScanner.nextLine();
    }

    public static void close() {
        inputScanner.close();
    }
}
