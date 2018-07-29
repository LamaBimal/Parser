package com.deerwalk.HSPParser;

import com.deerwalk.HSPParser.Utils.StringUtils;
import com.deerwalk.HSPParser.lib.Constants;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by btamang on 5/25/18.
 */
public class LayoutExtractor {

    public Map<String, ArrayList<String>> layoutFieldCollection = new HashMap<>();
    public static Logger logger = Logger.getLogger(LayoutExtractor.class);
    String layoutVersion;

    public ArrayList<String> getLayoutFieldCollection(String layout) {
        return layoutFieldCollection.get(layout);
    }

    /***
     * This method extracts layout of the given sheet.
     */
    public void extractLayoutField(InputStream layoutFileStream, String[] sheetNames) {

        String Version = null;
        try {

            XSSFWorkbook layoutFileSheet = new XSSFWorkbook(layoutFileStream);

            String sheetName;
            for (int i = 0; i < sheetNames.length; i++) {
                ArrayList<String> collection = new ArrayList<>();
                sheetName = sheetNames[i];
                XSSFSheet layoutSheet = layoutFileSheet.getSheet(sheetName);

                Iterator<Row> rowIterator = layoutSheet.iterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Cell cell = row.getCell(0);
                    if (cell.toString().equalsIgnoreCase(Constants.LayoutField_Version)) {
                        Cell versionCell = row.getCell(1);
                        if (!StringUtils.isNullorEmpty(versionCell)) {
                            Version = versionCell.toString();
                        }

                    }
                    if (!cell.toString().equalsIgnoreCase(Constants.LayoutField_Title)) {
                        collection.add(cell.toString());
                    }
                }
                layoutFieldCollection.put(sheetName, collection);
            }
        } catch (IOException ex) {
            logger.info("Failed to extract the layout Fields..");
            ex.getStackTrace();
        } catch (Exception ex) {
            logger.info("=========Failed to extract layout File ========" + ex.getCause());
            ex.printStackTrace();
        }
        if (!StringUtils.isNullorEmpty(Version)) {
            setLayoutVersion(Version);
        }
    }

    public void setLayoutVersion(String version) {
        layoutVersion = version;
    }

    public String getLayoutVersion() {
        return layoutVersion;
    }


    void extractLayoutFields(InputStream layoutStream) {
        String[] arrays = new String[]{Constants.Layout_Insured, Constants.Layout_DEPENDENT};
        extractLayoutField(layoutStream, arrays);
    }
}
