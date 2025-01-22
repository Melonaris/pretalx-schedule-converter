package ch.melonaris;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {

    public static LocalDateTime dateTime(String dateString, String timeString) {
        return LocalDateTime.of(date(dateString), time(timeString));
    }

    public static LocalDate date(String dateString) {
        int year, month, day;
        Pattern y_md_md_Format = Pattern.compile("^(\\d{4})[,/\\-.\\s](\\d{1,2})[,/\\-.\\s](\\d{1,2})$");
        Pattern md_md_y_Format = Pattern.compile("^(\\d{1,2})[,/\\-.\\s](\\d{1,2})[,/\\-.\\s](\\d{4})$");
        Matcher y_md_md, md_md_y;

        do {
            y_md_md = y_md_md_Format.matcher(dateString);
            md_md_y = md_md_y_Format.matcher(dateString);

            if (y_md_md.find()) {
                dateString = date(y_md_md.group(1), y_md_md.group(2), y_md_md.group(3));
                break;
            } else if (md_md_y.find()) {
                dateString = date(md_md_y.group(3), md_md_y.group(1), md_md_y.group(2));
                break;
            } else {
                dateString = returnDateFormatErrorGetNewInput();
            }
        } while (true);

        year = extractYear(dateString);
        month = extractMonth(dateString);
        day = extractDay(dateString);

        return LocalDate.of(year, month, day);
    }

    public static LocalTime time(String timeString) {
        int hour, minute;

        Pattern tt_tt_format = Pattern.compile("^(\\d{1,2})[:/-|\\s](\\d{1,2}).*(AM|PM)?");
        Matcher tt;

        do {
            tt = tt_tt_format.matcher(timeString);

            if (tt.find()) {
                timeString = time(tt.group(1), tt.group(2), tt.group(3));
                break;
            } else {
                timeString = returnTimeFormatErrorGetNewInput();
            }
        } while (true);

        hour = extractHour(timeString);
        minute = extractMinute(timeString);

        return LocalTime.of(hour, minute);
    }

    private static String returnTimeFormatErrorGetNewInput() {
        System.out.println("Error: Invalid time format!");
        System.out.println("Enter a valid date (hh:mm):");
        return InputScanner.getInput();
    }

    private static String time(String hourString, String minuteString, String timeAppendix) {
        TimeFormat timeformat = Settings.getTimeFormat();

        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        if (timeformat == TimeFormat.MILITARY) {
            return militaryTime(hour, minute);
        } else {
            return amPmTime(hour, minute, timeAppendix);
        }
    }

    private static String militaryTime(int hour, int minute) {
        if (hour > 23 || minute > 59) {
            throwInvalidTimeError();
        }
        return hour + ":" + minute;
    }

    private static String amPmTime(int hour, int minute, String timeAppendix) {
        if (hour > 13 || minute > 59) {
            throwInvalidTimeError();
        }
        if (Objects.equals(timeAppendix, "AM") && hour == 12) {
            hour = 0;
        } else {
            hour += 12;
        }
        return hour + ":" + minute;
    }


    private static void throwInvalidTimeError() {
        String startTime, endTime;

        System.out.println("Error: Invalid time was entered!");

        if (Settings.getTimeFormat() == TimeFormat.MILITARY) {
            startTime = "00:00";
            endTime = "23:59";
        } else {
            startTime = "12:00 AM";
            endTime = "11:59 PM";
        }

        System.out.printf("Please enter a time between %s and %s.", startTime, endTime);
        reenterTime();
    }

    private static void reenterTime() {
        String hours, minutes, timeAppendix = "";

        System.out.println("Reenter Hour:");
        hours = InputScanner.getInput();
        System.out.println("Reenter Minute:");
        minutes = InputScanner.getInput();

        if (Settings.getTimeFormat() == TimeFormat.STANDARD) {
            System.out.println("Reenter Time Appendix:");
            timeAppendix = InputScanner.getInput();
        } else {
            timeAppendix = null;
        }
        time(hours, minutes, timeAppendix);
    }

    private static String date(String year, String numString1, String numString2) {
        String date = "";
        int formatOption;

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

        System.out.println("Confirm your Date Input:");
        System.out.printf("1) %d. %s%n", num2, Month.of(num1).getDisplayName(TextStyle.FULL, Settings.getLocalLanguage()));
        System.out.printf("2) %d. %s%n", num1, Month.of(num2).getDisplayName(TextStyle.FULL, Settings.getLocalLanguage()));
        System.out.println("3) enter another date");

        do {
            try {
                formatOption = Integer.parseInt(InputScanner.getInput());

                if (formatOption < 4 && formatOption > 0) {
                    break;
                }
            } catch (NumberFormatException e) {

                throw new RuntimeException(e);
            }

            System.out.println("Error: expected real number input!");
            System.out.println("Please enter a Number between 1 and 3.");
        } while (true);

        switch (formatOption) {
            case 1:
                date = String.format("%s-%d-%d", year, num1, num2);
            case 2:
                date = String.format("%s-%d-%d", year, num2, num1);
            default:
                reenterDate();
        }
        return date;
    }

    private static boolean isMilitaryTime(String timeAppendix) {
        return timeAppendix == null;
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

        return String.format("%d-%d-%d", year, month, day);
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }

    private static void throwInvalidDaysError(int year, int month, int maxDays) {
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Settings.getLocalLanguage());
        System.out.printf("Error: %s only has %d days.%n", monthName, maxDays);
        reenterDate();
    }

    private static void throwInvalidMonthAndDaysError(int year) {
        System.out.println("Error: Month and day numbers are invalid!");
        reenterDate();
    }

    private static String returnDateFormatErrorGetNewInput() {
        System.out.println("Error: Invalid date format!");
        System.out.println("Enter a valid date (yyyy/mm/dd):");
        return InputScanner.getInput();
    }

    private static void reenterDate() {
        String numString1, numString2, numString3;

        System.out.println("Reenter Year:");
        numString1 = InputScanner.getInput();
        System.out.println("Reenter Month:");
        numString2 = InputScanner.getInput();
        System.out.println("Reenter Day:");
        numString3 = InputScanner.getInput();
        date(numString1, numString2, numString3);
    }

    private static int extractYear(String dateString) {
        Pattern yearFormat = Pattern.compile("^(\\d{4}).*");
        Matcher year = yearFormat.matcher(dateString);

        year.find();
        return Integer.parseInt(year.group(1));
    }

    private static int extractMonth(String dateString) {
        Pattern monthFormat = Pattern.compile("^\\d{4}-(\\d{1,2}).*");
        Matcher month = monthFormat.matcher(dateString);

        month.find();
        return Integer.parseInt(month.group(1));
    }

    private static int extractDay(String dateString) {
        Pattern dayFormat = Pattern.compile("^\\d{4}-\\d{1,2}-(\\d{1,2})");
        Matcher day = dayFormat.matcher(dateString);

        day.find();
        return Integer.parseInt(day.group(1));
    }

    private static int extractHour(String timeString) {
        Pattern hourFormat = Pattern.compile("^\\d{2}.*");
        Matcher hour = hourFormat.matcher(timeString);

        hour.find();
        return Integer.parseInt(hour.group(1));
    }

    private static int extractMinute(String timeString) {
        Pattern minuteFormat = Pattern.compile("^\\d{2}:(\\d{1,2}).*");
        Matcher minute = minuteFormat.matcher(timeString);

        minute.find();
        return Integer.parseInt(minute.group(1));
    }
}
