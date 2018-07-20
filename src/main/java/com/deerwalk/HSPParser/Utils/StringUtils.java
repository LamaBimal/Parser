package com.deerwalk.HSPParser.Utils;

/**
 * Created by btamang on 5/23/18.
 */
public class StringUtils {

    public static boolean isNullorEmpty(Object stringSegment) {
        if (stringSegment == null  || stringSegment.equals("")) {
            return true;
        } else {
            return false;
        }
    }
    public static String removeEscapeDelimiter(String string) {
        if (!isNullorEmpty(string)) {
            string = string.replaceAll("\r","");
            string = string.replaceAll("\n","");
        }
        return string;
    }
}
