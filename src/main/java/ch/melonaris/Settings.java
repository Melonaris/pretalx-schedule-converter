package ch.melonaris;

import java.util.Locale;

public class Settings {
    private static Locale localLanguage = new Locale.Builder().setLanguage("en").setRegion("US").build();
    private static TimeFormat timeFormat = TimeFormat.MILITARY;

    public static Locale getLocalLanguage() {
        return localLanguage;
    }

    public static void setLocalLanguage(Locale localLanguage) {
        Settings.localLanguage = localLanguage;
    }

    public static TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public static void setTimeFormat(TimeFormat timeFormat) {
        Settings.timeFormat = timeFormat;
    }
}