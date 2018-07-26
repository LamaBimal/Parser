package com.deerwalk.HSPParser.Utils;

import com.deerwalk.HSPParser.LayoutExtractor;
import com.deerwalk.HSPParser.RuleExtractor;
import com.deerwalk.HSPParser.Segment.Element;
import com.deerwalk.HSPParser.lib.Constants;
import com.google.gson.Gson;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;


import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class SegmentUtils {

    public static Logger  logger = Logger.getLogger(SegmentUtils.class);
    public static boolean isBeginOfInsuredSegment(String segment){
        if(segment.trim().equalsIgnoreCase(Constants.Insured_Begin_Segment)){
            return true;
        }
        return false;
    }

    public static boolean isBegionOfDependentSegment(String segment){
        if (Constants.dependentBeginSegment.containsKey(segment.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isBeginningSegment(String segment){
        if (segment.trim().equalsIgnoreCase(Constants.Insured_Begin_Segment) || Constants.dependentBeginSegment.containsKey(segment.trim())) {
            return true;
        }
        return false;
    }

    public static Map<String,Object> readSegmentElements(String line,String contentSegment){

        Map<String,Object> row = new LinkedHashMap<>();
        RuleExtractor ruleExtractor = RuleExtractor.getInstance();
        if (!StringUtils.isNullorEmpty(line)) {

            List<Element> elementList =  ruleExtractor.getElements(contentSegment);
            int beginIndex,endIndex;

            String value = "";

            for (Element element : elementList) {

                beginIndex = element.getPosition() - 1;
                endIndex = beginIndex + element.getLength();
                if (endIndex > line.length() - 1) {
                    endIndex = line.length()-1;
                }

                if (beginIndex < line.length()) {
                    value = line.substring(beginIndex,endIndex).trim();
                }

                row.put(element.getName(),value);
            }
        }
        return row;
    }

    /**
     * @param dependentList
     *
     * **/
    public static ArrayList<Map<String,Object>> getMergedResult(ArrayList<Map<String,Object>> dependentList, Map<String,Object>insuredDependentRecordType, ArrayList<Map<String,Object>> insuredList, int max){

        String DC_Insured_ID_SSN="",DC_Coverage_Type="",CR_Insured_ID_SSN="",CR_Coverage_Type="";

        int innerLoopSize = insuredList.size()>max?max:insuredList.size();

        int count;
        for (int outerLoop = 0; outerLoop < dependentList.size(); outerLoop++) {
            Map<String,Object> element = dependentList.get(outerLoop);
            DC_Insured_ID_SSN = element.get("DC_Insured_ID_SSN").toString();
            DC_Coverage_Type = element.get("DC_Coverage_Type").toString();

            count = 0;

            for (int innerLoop = 0; innerLoop < innerLoopSize; innerLoop++) {

                Map<String,Object> insuredElement = insuredList.get(innerLoop);
                if (insuredElement.containsKey("CR_Insured_ID_SSN")) {
                    CR_Insured_ID_SSN = insuredElement.get("CR_Insured_ID_SSN").toString();
                }

                if (insuredElement.containsKey("CR_Coverage_Type")) {
                    CR_Coverage_Type = insuredElement.get("CR_Coverage_Type").toString();
                }


                if (DC_Coverage_Type.equalsIgnoreCase(CR_Coverage_Type) && DC_Insured_ID_SSN.equalsIgnoreCase(CR_Insured_ID_SSN)) {
                    count++;
                    element.put("CR_Coverage_Effective_Date_"+count,insuredElement.containsKey("CR_Coverage_Effective_Date")?insuredElement.get("CR_Coverage_Effective_Date"):"");
                    element.put("CR_Coverage_Term_Date_"+count,insuredElement.containsKey("CR_Coverage_Term_Date")?insuredElement.get("CR_Coverage_Term_Date"):"");
                    element.put("CR_Coverage_Plan_Code_"+count,insuredElement.containsKey("CR_Coverage_Plan_Code")?insuredElement.get("CR_Coverage_Plan_Code"):"");
                    element.put("CR_Coverage_Dep_Status_Code_"+count,insuredElement.containsKey("CR_Coverage_Dep_Status_Code")?insuredElement.get("CR_Coverage_Dep_Status_Code"):"");
                    element.putAll(insuredDependentRecordType);
                }
            }
        }

        System.out.println("================= returning merge dependent resultSet ======================");
        System.out.println(new Gson().toJson(dependentList));
        System.out.println("============================================================================");
        return dependentList;
    }

    /**
     * USAGE: This function is used to write a row in a file.
     *
     * @param mapRecord contains the key, value pair of the records.
     * @param layout    is the layoutType which is used to extract the layoutFields.
     **/
    public static void findAndWriteMatchedLayoutFieldValue(LayoutExtractor layoutExtractor, CSVGenerator csvGenerator, Map<String, Object> mapRecord, String layout,String inputFileName) {

        ArrayList<String> layoutFields = layoutExtractor.getLayoutFieldCollection(layout);

        mapRecord.put("Type", layout);

        mapRecord.put("Version", layoutExtractor.getLayoutVersion());
        mapRecord.put("Parsed_date", IOUtils.getCurrentDate());
        mapRecord.put("Raw_filename", inputFileName);

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

    /**
     * This method is used to extract the footers contents of a file.
     *
     * @param inputFile is the file path.
     **/
    public static String extractFooters(String inputFile) {
        try {
            File file = new File(inputFile);
            ReversedLinesFileReader reverseReader = new ReversedLinesFileReader(file, Charset.defaultCharset());
            String lastLine = reverseReader.readLine();
            String segment = lastLine.substring(0, 2).trim();
            if (segment.equalsIgnoreCase(Constants.Footer_Segment)) {
                //setFooter(lastLine.substring(2));
                return lastLine.substring(2);
            }
        } catch (Exception ex) {
            logger.info(":::: Failed to extract footers with Cause::::::::  " + ex.getCause());
            ex.printStackTrace();
        }
        return null;
    }
}
