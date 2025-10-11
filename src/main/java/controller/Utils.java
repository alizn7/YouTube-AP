package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Utils {
    public static java.util.Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            return new java.util.Date();
        }
    }
}