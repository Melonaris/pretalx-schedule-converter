package ch.melonaris;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {

    public static String date(String dateString) {
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
                dateString = InputScanner.getInput();
            }
        } while (!isValidInput);
        return dateString;
    }

    public static LocalDateTime dateTime(String dateString, String timeSting) {
        Pattern y_md_md_Format = Pattern.compile("^(\\d{4})[,/\\-.](\\d{2})[,/\\-.](\\d{2})$");
        Pattern md_md_y_Format = Pattern.compile("^(\\d{2})[,/\\-.](\\d{2})[,/\\-.](\\d{4})$");
        Matcher y_md_md, md_md_y;

        do {
            y_md_md = y_md_md_Format.matcher(dateString);
            md_md_y = md_md_y_Format.matcher(dateString);

            if (md_md_y.find()) {
                dateString = date(md_md_y.group(3), md_md_y.group(1), md_md_y.group(2));
            } else if (y_md_md.find()) {
                dateString = date(y_md_md.group(1), md_md_y.group(2), md_md_y.group(3));
            } else {
                dateString = returnDateFormatErrorGetNewInput();
            }
        } while (true);
    }

    private static String date(String year, String numString1, String numString2) {

    private static String validateMonthAndDay(String numString1, String numString2, String year) {
        boolean isInputConfirmedByUser = false;

        do {
            int num1 = Integer.parseInt(numString1);
            int num2 = Integer.parseInt(numString2);

            if (num1 > 12 && num2 > 12) {
                throwInvalidMonthAndDaysError(Integer.parseInt(year));
            }

            if (num1 > 12 && num2 < 13) {
                return validateDayNumber(Integer.parseInt(year), num2, num1);
            }

            if (num2 > 12 && num1 < 13) {
                return validateDayNumber(Integer.parseInt(year), num1, num2);
            }

            // if non higher than 12 -> confirm day and month

            System.out.println("Confirm your Date Input:");
            System.out.println("1) " + convertToMonthString(num1) + num2);
            System.out.println("2) " + convertToMonthString(num2) + num1);


        } while (isInputConfirmedByUser);

        return null;
    }

    private static String validateDayNumber(int year, int month, int day) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day > 31) {
                    throwInvalidDaysError(year, month, 31);
                }
                // needs 31
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day > 30) {
                    throwInvalidDaysError(year, month, 30);
                }
                break;
            case 2:
                if (isLeapYear(year) && day > 29) {
                    throwInvalidDaysError(year, month, 29);
                } else if (!isLeapYear(year) && day > 28) {
                    throwInvalidDaysError(year, month, 28);
                }
                break;
        }

        return month + "-" + day;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }

    private static void throwInvalidDaysError(int year, int month, int maxDays) {
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Settings.localLanguage);
        System.out.println("Error: " + monthName + "only has " + maxDays + " days!");
        reenterDate();
    }

    private static void throwInvalidMonthAndDaysError(int year) {
        System.out.println("Error: Month and Day numbers are invalid!");
        reenterDate();
    }

    private static String returnDateFormatErrorGetNewInput() {
        System.out.println("Error: Invalid date format!");
        System.out.println("(yyyy/mm/dd -> 2005/05/21)");
        return InputScanner.getInput();
    }

    private static void reenterDate() {
        System.out.println("Reenter Month:");
        numString2 = InputScanner.getInput();
        System.out.println("Reenter Day:");
        numString2 = ch.melonaris.Scanner.getScanner().nextLine();
        validateMonthAndDay(String.valueOf(year), numString1, numString2);
        numString3 = InputScanner.getInput();
        date(numString1, numString2, numString3);
    }
}
