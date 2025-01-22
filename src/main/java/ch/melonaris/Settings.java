package ch.melonaris;

import java.util.Locale;

public class Settings {
    static Locale localLanguage = new Locale.Builder().setLanguage("en").setRegion("US").build();
    static TimeFormat timeFormat = TimeFormat.MILITARY;
}