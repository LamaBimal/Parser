package com.deerwalk.HSPParser.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by btamang on 5/28/18.
 */
public class IOUtils {

    static String inputFile;

    public static String getFileName(String filePath) {
        if (!StringUtils.isNullorEmpty(filePath)) {
            inputFile = filePath;
            Path InputFilePath = Paths.get(filePath);
            return InputFilePath.getFileName().toString();
        } else {
            return null;
        }
    }

    /**
     * This function returns the outputPath of the csv file with (fileName).
     **/
    public static String getOutputFileName(String inputFile, String fileName) {
        if (!StringUtils.isNullorEmpty(inputFile)) {
            Path InputFilePath = Paths.get(inputFile);
            Path OutputFilePath = InputFilePath.getParent();
            String FileNameWithoutExtension = "",finalOutputLocation;
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".txt") || fileName.endsWith(".dat") || fileName.endsWith(".xls")) {
                FileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            if (!StringUtils.isNullorEmpty(FileNameWithoutExtension)) {
                finalOutputLocation = OutputFilePath.toString() + "/" + FileNameWithoutExtension  + ".csv";
            } else {
                finalOutputLocation = OutputFilePath.toString()+"/"+fileName+".csv";
            }

            return finalOutputLocation;
        } else {
            return null;
        }
    }

    /**
     * This function is used to change the String ArrayList to String Array.
     *
     * @param list is the String ArrayList.
     **/
    public static String[] convertListToArray(ArrayList<String> list) {

        String[] strings = list.toArray(new String[list.size()]);
        return strings;
    }

    /**
     * function which returns the current date.
     **/
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String currentDate = dateFormat.format(new Date()).toString();
        return currentDate;
    }
}
