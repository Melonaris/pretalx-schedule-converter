package ch.melonaris;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
    private static Scanner scanner;

    public static void openScanner() {
        scanner = new Scanner(System.in);
    }

    public static void closeScanner() {
        scanner.close();
    }

    public static Scanner getScanner() {
        return scanner;
    }

    public static String date(String dateString) {
        Scanner scanner = new Scanner(System.in);
        boolean isValidInput = true;
        Pattern datePattern = Pattern.compile("^\\d{2}(-|/|.)\\d{2}(-|/|.)\\d{4}$");
        Matcher dateFormat;
        do {
            dateFormat = datePattern.matcher(dateString);

            if (dateFormat.find()) {
                isValidInput = true;
            } else {
                isValidInput = false;
                System.out.println("Invalid input please use format yyyy-MM-dd!");
                dateString = scanner.nextLine();
            }
        } while (!isValidInput);
        return dateString;
    }
}
