package com.deerwalk.HSPParser;

import com.deerwalk.HSPParser.Segment.Element;
import com.deerwalk.HSPParser.Segment.Segment;
import com.deerwalk.HSPParser.Utils.*;
import com.deerwalk.HSPParser.lib.Constants;
import com.deerwalk.HSPParser.lib.KeyGenerator;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.*;

public class HSPReaderV2 implements HSPReader {
    public String layoutType;

    public String inputFileName;

    CSVGenerator csvGenerator;

    //LayoutExtractor layoutExtractor = new LayoutExtractor();
    LayoutExtractor layoutExtractor;

    public static Logger logger = Logger.getLogger(HSPLayoutReader.class);

    public void setInputFileName(String name) {
        this.inputFileName = name;
    }

    public String getInputFileName() {
        return this.inputFileName;
    }


    public void setFooter(String footerMessage) {

        System.out.println(footerMessage);
    }


    /***
     *  This method sets the layout Type on the basis of Insured Begin Segment/ Dependent Begin Segment.
     * **/
    public void setLayoutType(String segment) {

        if (segment.equalsIgnoreCase(Constants.Insured_Begin_Segment)) {
            this.layoutType = Constants.Layout_Insured;
        } else if (segment.equalsIgnoreCase(Constants.Dependent_Begin_Segment)) {
            this.layoutType = Constants.Layout_DEPENDENT;
        }
    }

    public void parseInputFile(String inputFile, LayoutExtractor layoutExtractor, RuleExtractor ruleExtractor) {

        FileReader fileReader = new FileReader(inputFile);
        Scanner fileReaderScanner = fileReader.getScanner();

        //extractLayoutFields(layoutStream);
        System.out.println(":::: Layout Version :::::"+layoutExtractor.getLayoutVersion());

        csvGenerator = IOUtils.initializeCSVFile(inputFile);
        this.layoutExtractor = layoutExtractor;

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
        Map<String, Object> insuredDependentFieldsTemp = new HashMap<>();
        ArrayList<Map<String, Object>> insuredDependentFieldsTempList = new ArrayList<>();

        /***
         *  I1 and I2 collection Map
         * ***/
        Map<String,Object> insuredDependentRecordType = new HashMap<>();

        String line;
        int stringLength;
        //extractFooters(inputFile);
        String previousLayoutType = null;

        Map<String, Object> customLayoutCollection = new HashMap<>();

        while (fileReaderScanner.hasNextLine()) {

            line = fileReaderScanner.nextLine();

            logger.info(":::: line Content ::::\n" + line);

            stringLength = line.length();
            String contentSegment = line.substring(0, 2).trim();

            if (this.layoutType != null) {
                previousLayoutType = new String(this.layoutType);
            }

            System.out.println("::::content Segment :::::" + contentSegment);

            if (SegmentUtils.isBeginningSegment(contentSegment)) {
                setLayoutType(contentSegment);
            }

            if ( SegmentUtils.isBeginningSegment(contentSegment) && resultSet.size() > 0) {


                collectParsedData(resultSet,insuredDependentRecordType,loopResultSetCollection, insuredDependentFieldsTempList, previousLayoutType);

                if (SegmentUtils.isBeginOfInsuredSegment(contentSegment)) {
                    insuredDependentFieldsTempList.clear();
                    insuredDependentFieldsTemp.clear();
                }
                /***
                 * clear the resultSet and loopResultCollection
                 * ***/
                resultSet.clear();
                loopResultSetCollection.clear();
                keySet.clear();
                /**===================================================**/
            }

            /**
             * Header extraction
             * **/
            if (contentSegment.trim().equalsIgnoreCase(Constants.Header_Segment)) {

                Map rowMap = SegmentUtils.readSegmentElements(line,contentSegment);

                String[] rowArray  = IOUtils.convertCollectionToArray(rowMap.values());

                csvGenerator.writeRow(rowArray);
                continue;
            }

            /**
             *  Footer extraction
             * **/
            if (contentSegment.trim().equalsIgnoreCase(Constants.Footer_Segment)) {

                Map rowMap = SegmentUtils.readSegmentElements(line,contentSegment);

                String[] rowArray  = IOUtils.convertCollectionToArray(rowMap.values());

                collectParsedData(resultSet,insuredDependentRecordType, loopResultSetCollection,insuredDependentFieldsTempList, previousLayoutType);
                insuredDependentFieldsTemp.clear();
                insuredDependentRecordType.clear();
                csvGenerator.writeRow(rowArray);
                continue;
            }

                Map<String, Object> loopResultSet = new HashMap<String, Object>();

                List<Element> elements = ruleExtractor.getElements(contentSegment, layoutType);
                Element element;
                String key, value=null;
                int beginIndex, endIndex;

                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    key = element.getName();
                    beginIndex = element.getPosition() - 1;
                    endIndex = beginIndex + element.getLength();

                    if (stringLength < endIndex) {
                        endIndex = line.length() - 1;
                    }

                    if (beginIndex < (stringLength - 1)) {
                        value = line.substring(beginIndex, endIndex).trim();
                    }

                    String generatedKey = KeyGenerator.generateKey(layoutType, contentSegment, key, keySet);
                    keySet.add(generatedKey);

                    if (Constants.dependentInsuredRecordType.contains(contentSegment)) {
                        insuredDependentRecordType.put(generatedKey, value);
                    } else if (Constants.dependentInsuredFields.contains(key)) {
                        insuredDependentFieldsTemp.put(generatedKey, value);
                    }

                    if (Segment.loopRecordType.contains(contentSegment.trim())) {
                        loopResultSet.put(generatedKey, value);
                        //insuredDependentTemp.put(generatedKey,value);
                    } else {
                        resultSet.put(generatedKey, value);
                    }

                }
                // Completion after the Section.
                if (Segment.loopRecordType.contains(contentSegment.trim())) {
                    loopResultSetCollection.add(loopResultSet);
                }

            // =====================================================================
            if (contentSegment.trim().equalsIgnoreCase("C") && layoutType.equalsIgnoreCase(Constants.Layout_Insured)) {

                if (customLayoutCollection.isEmpty()) {
                    customLayoutCollection.putAll(insuredDependentFieldsTemp);
                    insuredDependentFieldsTempList.add(customLayoutCollection);
                    customLayoutCollection = new HashMap<>();
                }
            }

            //=======================================================================

        }
        csvGenerator.closeCSVWriter();
    }

    /**
     * @param resultSet contains the block record  of a specific layout.
     * @param loopSet   contains the loop contains, whose each record should be written in a different row along with
     *                  the @param resultSet values.
     * @param layout    is the type of layout i.e. either insured or dependent.
     *                  Usage: This functions combines the records of @param resultSet and @param loopSet and it triggers action to write on a file.
     **/
    void collectParsedData(Map<String, Object> resultSet,Map<String,Object> insuredDependentRecordType, ArrayList<Map<String, Object>> loopSet, ArrayList<Map<String,Object>>insuredDependentList, String layout) {

        Map<String, Object> objectMap = new HashMap<>();

        ArrayList<Map<String,Object>> mergedResult;
        if(layout.equalsIgnoreCase("dependent")){
            mergedResult = SegmentUtils.getMergedResult(loopSet,insuredDependentRecordType,insuredDependentList,60);
        } else {
            mergedResult = loopSet;
        }

        /**
         *
         * */
        System.out.println("=============================================================");
        System.out.println("====== source result ====="+new Gson().toJson(resultSet));
        System.out.println("====== merged result ====="+new Gson().toJson(resultSet));

        if (mergedResult.size() > 0) {
            for (int i = 0; i < mergedResult.size(); i++) {
                objectMap.clear();
                objectMap.putAll(resultSet);
                objectMap.putAll(mergedResult.get(i));

                System.out.println("::: each row  ::::"+new Gson().toJson(objectMap));
                SegmentUtils.findAndWriteMatchedLayoutFieldValue(layoutExtractor,csvGenerator,objectMap,layout,this.inputFileName);
            }
        }
        System.out.println("=========================end of writing each block ===========================");
    }
}
