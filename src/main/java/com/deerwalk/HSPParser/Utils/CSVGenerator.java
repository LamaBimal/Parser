package com.deerwalk.HSPParser.Utils;

import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by btamang on 5/23/18.
 */
public class CSVGenerator {

    public CSVWriter csvWriter;
    public FileWriter fileWriter;
    public String outputFile;
    public static Logger logger = Logger.getLogger(CSVGenerator.class);

    public CSVGenerator() {
    }

    public void setCSVWriter(String outputPath) {
        try {
            outputFile = outputPath;
            fileWriter = new FileWriter(outputPath);
            csvWriter = new CSVWriter(fileWriter,'|');
        } catch (Exception exception) {
            logger.info("===============Failed to create a csv file ========" + outputPath);
        }
    }

    public CSVWriter getCsvWriter() {
        return csvWriter;
    }

    public void writeRow(String[] row) {
        csvWriter.writeNext(row);
    }

    public void closeCSVWriter() {
        try {
            logger.info("====================complete CSV File generation================" + outputFile);
            csvWriter.close();
            fileWriter.close();
        } catch (IOException exception) {
            logger.info("===============Failure to close the csvWriter Resource =========" + exception.getCause());
        }
    }
}
