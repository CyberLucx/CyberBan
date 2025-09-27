package org.cyberluca.cyberBan.utils;

public class Utils {


    public static long parseDuration(String s) {
        if (s == null) return -1L;
        s = s.trim().toLowerCase();
        if (s.equals("perm") || s.equals("permanent") || s.equals("p") || s.equals("-1")) return -1L;
        try {
            long now = System.currentTimeMillis();
            long totalMs = 0L;
            int i = 0;
            while (i < s.length()) {
                int j = i;
                while (j < s.length() && Character.isDigit(s.charAt(j))) j++;
                if (j == i) return -1L;
                long num = Long.parseLong(s.substring(i, j));
                if (j >= s.length()) return -1L;
                char unit = s.charAt(j);
                j++;
                long ms;
                switch (unit) {
                    case 'd':
                        ms = num * 24L * 60L * 60L * 1000L;
                        break;
                    case 'h':
                        ms = num * 60L * 60L * 1000L;
                        break;
                    case 'm':
                        ms = num * 60L * 1000L;
                        break;
                    case 's':
                        ms = num * 1000L;
                        break;
                    default:
                        return -1L;
                }
                totalMs += ms;
                i = j;
            }
            if (totalMs <= 0) return -1L;
            return now + totalMs;
        } catch (Exception e) {
            return -1L;
        }
    }
}
