package it.rattly.duels.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private Utils() {
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
}
