package com.deerwalk.HSPParser.Utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by btamang on 5/23/18.
 */
public class ExcelFileReader {

    public static void readFile() throws IOException{

        InputStream inputStream = new FileInputStream("");
        XSSFWorkbook layoutFileSheet = new XSSFWorkbook(inputStream);

        XSSFSheet layoutSheet = layoutFileSheet.getSheetAt(0);
        Iterator<Row> rowIterator = layoutSheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell segment = row.getCell(0);
            Cell fieldName = row.getCell(1);
            Cell position = row.getCell(2);
            Cell length = row.getCell(3);

            String [] records = new String[]{segment.toString(),fieldName.toString(),position.toString(),length.toString()};
            //CSVGenerator.setCSVWriter("/home/btamang/project1/HSP_Parser/src/main/resources/hsp_elig_Insured_layout1.txt");

            /*if (cell.toString().equalsIgnoreCase("version")) {
                Cell versionCell = row.getCell(2);
                *//*Version = versionCell.toString();*//*
            }
            layoutFieldCollection.add(cell.toString());*/
        }
    }
}
