package org.cyberluca.cyberBan.utils;

public class BanUtils {


    public long parseDuration(String input) {
        if (input == null || input.isEmpty()) return -1L;

        try {
            long factor = 1000L; // Sekunden in ms
            char unit = Character.toLowerCase(input.charAt(input.length() - 1));
            long value;

            if (Character.isLetter(unit)) {
                value = Long.parseLong(input.substring(0, input.length() - 1));
            } else {
                value = Long.parseLong(input); // nur Zahl = Sekunden
                return value * factor;
            }

            switch (unit) {
                case 's': return value * 1000L;
                case 'm': return value * 60L * 1000L;
                case 'h': return value * 60L * 60L * 1000L;
                case 'd': return value * 24L * 60L * 60L * 1000L;
                default:  return -1L;
            }
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
