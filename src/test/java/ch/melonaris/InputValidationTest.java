package ch.melonaris;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.lang.System.in;

public class InputValidationTest {
    private ByteArrayOutputStream output;

    @BeforeAll
    public static void beforeAll() {
        InputScanner.open();
    }

    @BeforeEach
    public void beforeEach() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void afterEach() {
        Settings.setTimeFormat(TimeFormat.MILITARY);
        System.setIn(in);
    }

    @AfterAll
    public static void afterAll() {
        InputScanner.close();
    }


    @Test
    public void dateTimeReturnsLocalVerifyDateVerifyTimeObject() {
        Assertions.assertInstanceOf(LocalDateTime.class, InputValidation.dateTime("17/05/2003", "3 45 PM"));
    }

    @Test
    public void dateReturnsLocalVerifyDateObject() {
        Assertions.assertInstanceOf(LocalDate.class, InputValidation.verifyDate("17/05/2003"));
    }

    @Test
    public void dateReturnsValidLocalDateObjectIndependentOfVerifyDateInputFormat() {
        Assertions.assertEquals(InputValidation.verifyDate("22 08 2021"), InputValidation.verifyDate("2021-22-8"));
    }

    @Test
    public void dateAsksForNewInputIfVerifyDateFormatIsIncorrect() {
        String input = "3/13/2005", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyDate("28072022");
        expectedOutput = "Error: Invalid date format!\r\nEnter a valid date (yyyy/mm/dd):";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void dateInvalidVerifyDateReturnsError() {
        String input = "1983\n02\n13", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyDate("30.02.2012");
        expectedOutput = "Error: February only has 29 days!\r\nReenter Year:\r\nReenter Month:\r\nReenter Day:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyDateInvalidDaysAndMonthsReturnError() {
        String input = "2024\n08\n21", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyDate("30.13.1903");
        expectedOutput = "Error: Month and day numbers are invalid!\r\nReenter Year:\r\nReenter Month:\r\nReenter Day:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyDateSecondNumbersDetermineLogicPath() {
        Assertions.assertEquals(17, InputValidation.verifyDate("2001.17.5").getDayOfMonth());
        Assertions.assertEquals(23, InputValidation.verifyDate("2001.11.23").getDayOfMonth());

        Assertions.assertEquals(15, InputValidation.verifyDate("2001.11.15").getDayOfMonth());

        String input = "1\n2";
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        Assertions.assertEquals(3, InputValidation.verifyDate("2001.11.3").getDayOfMonth());
    }

    @Test
    public void verifyDateChoiceInvalidNumberInputReturnsErrorMessage() {
        String input = "0\n1", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyDate("1763.5.3");
        expectedOutput = "Confirm your Date Input:\r\n1) 3. May\r\n2) 5. March\r\n3) enter another date\r\nError: expected real number input!\r\nPlease enter a Number between 1 and 3.";

        Assertions.assertEquals(expectedOutput, output.toString().trim());

        input = "4\n1";
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        output.reset();
        InputValidation.verifyDate("1763.5.3");
        expectedOutput = "Confirm your Date Input:\r\n1) 3. May\r\n2) 5. March\r\n3) enter another date\r\nError: expected real number input!\r\nPlease enter a Number between 1 and 3.";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyDateChoiceCharInputThrowsNumberFormatException() {
        String input = "x";
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        Assertions.assertThrows(NumberFormatException.class, () -> InputValidation.verifyDate("2001.11.3"));
    }

    @Test
    public void dateChoiceDefaultReturnsPromptForNewVerifyDate() {
        String input = "3\n2003\n5\n13", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyDate("1763.5.3");
        expectedOutput = "Confirm your Date Input:\r\n1) 3. May\r\n2) 5. March\r\n3) enter another date\r\nReenter Year:\r\nReenter Month:\r\nReenter Day:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void timeReturnsLocalVerifyTimeObject() {
        Assertions.assertInstanceOf(LocalTime.class, InputValidation.verifyTime("23:45"));
    }

    @Test
    public void verifyTimeReturnsValidLocalDateObjectIndependentOfVerifyDateInputFormat() {
        LocalTime time_1 = InputValidation.verifyTime("22.55");

        Settings.setTimeFormat(TimeFormat.STANDARD);
        LocalTime time_2 = InputValidation.verifyTime("10.55 PM");

        Assertions.assertEquals(time_1, time_2);
    }

    @Test
    public void timeReturnsCorrectStandardLocalVerifyTimeObject() {
        Settings.setTimeFormat(TimeFormat.STANDARD);
        Assertions.assertEquals(0, InputValidation.verifyTime("12 30 AM").getHour());
        Assertions.assertEquals(12, InputValidation.verifyTime("12 30 PM").getHour());
        Assertions.assertEquals(7, InputValidation.verifyTime("7 30 AM").getHour());
        Assertions.assertEquals(19, InputValidation.verifyTime("7 30 PM").getHour());
    }

    @Test
    public void verifyTimeAsksForNewInputIfVerifyDateFormatIsIncorrect() {
        String input = "05:33", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyTime("0533");
        expectedOutput = "Error: Invalid time format!\r\nEnter a valid date (hh:mm):";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyTimeInvalidMilitaryHourNumberReturnsError() {
        String input = "05\n33", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyTime("25:23");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 00:00 and 23:59.\r\nReenter Hour:\r\nReenter Minute:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyTimeInvalidStandardHourNumberReturnsError() {
        String input = "05\n33\nPM", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        Settings.setTimeFormat(TimeFormat.STANDARD);
        InputValidation.verifyTime("13 20 PM");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 12:00 AM and 11:59 PM.\r\nReenter Hour:\r\nReenter Minute:\r\nReenter Time Appendix:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyTimeInvalidHourReturnsError() {
        String input = "0\n20", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyTime("24 20");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 00:00 and 23:59.\r\nReenter Hour:\r\nReenter Minute:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());

        output.reset();
        input = "1\n20\nPM";
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        Settings.setTimeFormat(TimeFormat.STANDARD);
        InputValidation.verifyTime("13 20 AM");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 12:00 AM and 11:59 PM.\r\nReenter Hour:\r\nReenter Minute:\r\nReenter Time Appendix:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyTimeInvalidMinuteReturnsError() {
        String input = "14\n10", expectedOutput;
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        InputValidation.verifyTime("13 70");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 00:00 and 23:59.\r\nReenter Hour:\r\nReenter Minute:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());

        output.reset();
        input = "6\n10\nPM";
        InputScanner.open(new ByteArrayInputStream(input.getBytes()));

        Settings.setTimeFormat(TimeFormat.STANDARD);
        InputValidation.verifyTime("5 70 PM");
        expectedOutput = "Error: Invalid time was entered!\r\nPlease enter a time between 12:00 AM and 11:59 PM.\r\nReenter Hour:\r\nReenter Minute:\r\nReenter Time Appendix:";

        Assertions.assertEquals(expectedOutput, output.toString().trim());
    }

    @Test
    public void verifyTimeConverts12AMTo0Hours() {
        Settings.setTimeFormat(TimeFormat.STANDARD);
        Assertions.assertEquals(0, InputValidation.verifyTime("12:05 AM").getHour());
    }

    @Test
    public void verifyTimeDoesNotConvert12MPTo0Hours() {
        Settings.setTimeFormat(TimeFormat.STANDARD);
        Assertions.assertEquals(12, InputValidation.verifyTime("12:05 PM").getHour());
    }
}
