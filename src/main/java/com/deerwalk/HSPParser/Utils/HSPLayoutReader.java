package com.deerwalk.HSPParser.Utils;

import com.deerwalk.HSPParser.LayoutExtractor;
import com.deerwalk.HSPParser.RuleExtractor;
import com.deerwalk.HSPParser.Segment.Element;

import com.deerwalk.HSPParser.Segment.Segment;
import com.deerwalk.HSPParser.lib.Constants;
import com.deerwalk.HSPParser.lib.KeyGenerator;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by btamang on 5/28/18.
 */
public class HSPLayoutReader {

    public String header;
    public String footer;
    public String layoutType;

    public String inputFileName;

    CSVGenerator csvGenerator;

    LayoutExtractor layoutExtractor = new LayoutExtractor();

    public static Logger logger = Logger.getLogger(HSPLayoutReader.class);

    public void setInputFileName(String name){
        this.inputFileName = name;
    }
    public String getInputFileName(){
        return this.inputFileName;
    }
    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setFooter(String footerMessage) {
        this.footer = footerMessage;
    }

    public String getFooter() {
        return this.footer;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public String getLayoutType() {
        return this.layoutType;
    }

    public void readFile(String inputFile, InputStream layoutStream, RuleExtractor ruleExtractor) {

        FileReader fileReader = new FileReader(inputFile);
        Scanner fileReaderScanner = fileReader.getScanner();

        extractLayoutFields(layoutStream);
        initializeCSVFile(inputFile);
        setInputFileName(inputFile);

        Map<String, Object> resultSet = new HashMap<String, Object>();

        /**
         *  This variable contains the record Type which multiple occur has to be recorded unique in a different line.
         * **/
        ArrayList<Map<String, Object>> loopResultSetCollection = new ArrayList<>();

        /**
         *  This is the datasource for the generated Key.
         * **/
        ArrayList<String> keySet = new ArrayList<>();

        /**
         *  This @variable insuredDependentTemp collects the records of the insured records which should
         *  be mentioned in the dependent records too.
         * **/
        Map<String, Object> insuredDependentTemp = new HashMap<>();

        String line;
        int stringLength;
        //extractFooters(inputFile);
        String previousLayoutType = null;

        while (fileReaderScanner.hasNextLine()) {

            line = fileReaderScanner.nextLine();

            logger.info(":::: line Content ::::\n" + line);

            stringLength = line.length();
            String contentSegment = line.substring(0, 2).trim();

            if (getLayoutType() != null) {
                previousLayoutType = new String(getLayoutType());
            }

            if (contentSegment.equalsIgnoreCase(Constants.Insured_Begin_Segment)) {
                setLayoutType(Constants.Layout_Insured);
            } else if (contentSegment.equalsIgnoreCase(Constants.Dependent_Begin_Segment) || Constants.dependentBeginSegment.containsKey(contentSegment.trim())) {
                setLayoutType(Constants.Layout_DEPENDENT);
            }

            if ((contentSegment.equalsIgnoreCase(Constants.Insured_Begin_Segment)
                    || contentSegment.equalsIgnoreCase(Constants.Dependent_Begin_Segment) || Constants.dependentBeginSegment.containsKey(contentSegment.trim())) && resultSet.size() > 0) {

                if (contentSegment.equalsIgnoreCase(Constants.Insured_Begin_Segment)) {
                    if(insuredDependentTemp.size()>0) {
                        resultSet.putAll(insuredDependentTemp);
                        insuredDependentTemp.clear();
                    }
                }
                /**
                 *  This case is for the subsequent dependents of the same insured layout.
                 * ***/
                if (previousLayoutType.equalsIgnoreCase(Constants.Layout_DEPENDENT)
                        && (Constants.dependentBeginSegment.containsKey(contentSegment.trim()))) {
                    resultSet.putAll(insuredDependentTemp);
                }

                collectParsedData(resultSet, loopResultSetCollection, previousLayoutType);

                resultSet.clear();
                loopResultSetCollection.clear();
            }

            /**
             * Header extraction
             * **/
            if (contentSegment.trim().equalsIgnoreCase(Constants.Header_Segment)) {
                //String header = line.substring(2);
                csvGenerator.writeRow(new String[]{line});
                //setHeader(header);
            }

            /**
             *  Footer extraction
             * **/
            if (contentSegment.trim().equalsIgnoreCase(Constants.Footer_Segment)) {
                resultSet.putAll(insuredDependentTemp);
                collectParsedData(resultSet, loopResultSetCollection, previousLayoutType);
                insuredDependentTemp.clear();
                //String footer = line.substring(2);
                csvGenerator.writeRow(new String[]{line});
            }

            int repetition = 0;
            do {
                Map<String, Object> loopResultSet = new HashMap<String, Object>();
                repetition++;
                List<Element> elements = ruleExtractor.getElements(contentSegment, layoutType);
                Element element;
                String key, value="";
                int beginIndex, endIndex;

                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    key = element.getName();
                    beginIndex = element.getPosition() - 1;
                    endIndex = beginIndex + element.getLength();
                    if (stringLength < endIndex) {
                        endIndex = line.length() - 1;
                    }

                    if (beginIndex < line.length() - 1) {
                        value = line.substring(beginIndex, endIndex).trim();
                    }

                    String generatedKey = KeyGenerator.generateKey(layoutType, contentSegment, key, keySet);
                    keySet.add(generatedKey);

                    if (Constants.dependentInsuredRecordType.contains(contentSegment)) {
                        insuredDependentTemp.put(generatedKey, value);
                    /*} else if (Constants.dependentInsuredFields.contains(key)) {
                        insuredDependentTemp.put(generatedKey,value);*/
                    }

                    if (Segment.loopRecordType.contains(contentSegment.trim())) {
                        loopResultSet.put(generatedKey, value);
                    } else {
                        resultSet.put(generatedKey, value);
                    }
                }
                if (Segment.loopRecordType.contains(contentSegment.trim())) {
                    loopResultSetCollection.add(loopResultSet);
                }
            } while (repetition < KeyGenerator.getSegmentRepetition(contentSegment));

        }
        csvGenerator.closeCSVWriter();
    }

    /**
     *  @param resultSet  contains the block record  of a specific layout.
     *  @param loopSet contains the loop contains, whose each record should be written in a different row along with
     *                 the @param resultSet values.
     *  @param layout  is the type of layout i.e. either insured or dependent.
     *  Usage: This functions combines the records of @param resultSet and @param loopSet and it triggers action to write on a file.
     * **/
    void collectParsedData(Map<String, Object> resultSet, ArrayList<Map<String, Object>> loopSet, String layout) {

        Map<String, Object> objectMap = new HashMap<>();
        if (loopSet.size() > 0) {
            for (int i = 0; i < loopSet.size(); i++) {
                objectMap.clear();
                objectMap.putAll(loopSet.get(i));
                objectMap.putAll(resultSet);
                findAndWriteMatchedLayoutFieldValue(objectMap, layout);
            }
        }
    }

    /**
     * This method is used to extract the footers contents of a file.
     * @param inputFile  is the file path.
     **/
    void extractFooters(String inputFile) {
        try {
            File file = new File(inputFile);
            ReversedLinesFileReader reverseReader = new ReversedLinesFileReader(file, Charset.defaultCharset());
            String lastLine = reverseReader.readLine();
            String segment = lastLine.substring(0, 2).trim();
            if (segment.equalsIgnoreCase(Constants.Footer_Segment)) {
                setFooter(lastLine.substring(2));
            }
        } catch (Exception ex) {
            logger.info(":::: Failed to extract footers with Cause::::::::  " + ex.getCause());
            ex.printStackTrace();
        }
    }

    /**
     * USAGE: This function is used to create a input Stream of output File.
     * @param inputFile is used to generate the output file name.
     * PROCESS: This function also writes the layout field as the first row of the output file.
     * **/
    void initializeCSVFile(String inputFile) {

        String inputFileName = IOUtils.getFileName(inputFile);

        String outputFileName = IOUtils.getOutputFileName(inputFile, inputFileName);
        csvGenerator = new CSVGenerator();
        csvGenerator.setCSVWriter(outputFileName);
    }

    void extractLayoutFields(InputStream layoutStream) {
        String[] arrays = new String[]{Constants.Layout_Insured, Constants.Layout_DEPENDENT};
        layoutExtractor.extractLayoutField(layoutStream, arrays);
    }

    /**
     *  USAGE: This function is used to write a row in a file.
     *  @param mapRecord contains the key, value pair of the records.
     *  @param layout is the layoutType which is used to extract the layoutFields.
     * **/
    public void findAndWriteMatchedLayoutFieldValue(Map<String, Object> mapRecord, String layout) {

        ArrayList<String> layoutFields = layoutExtractor.getLayoutFieldCollection(layout);

        mapRecord.put("Type",layout);

        mapRecord.put("Version", layoutExtractor.getLayoutVersion());
        mapRecord.put("Parsed_date", IOUtils.getCurrentDate());
        mapRecord.put("Raw_filename", getInputFileName());

        ArrayList<String> records = new ArrayList<>();
        String value;
        for (int i = 0; i < layoutFields.size(); i++) {
            if (mapRecord.get(layoutFields.get(i)) != null) {
                value = mapRecord.get(layoutFields.get(i)).toString();
            } else {
                value = "";
            }
            records.add(value);
        }
        String[] stringValues = IOUtils.convertListToArray(records);
        csvGenerator.writeRow(stringValues);
    }
}
