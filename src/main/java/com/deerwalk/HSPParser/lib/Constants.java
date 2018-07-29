package com.deerwalk.HSPParser.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by btamang on 5/25/18.
 */
public class Constants {

    public static String Layout_Insured = "Insured";
    public static String Layout_DEPENDENT = "dependent";

    public static String Insured_Begin_Segment = "I1";
    public static String Dependent_Begin_Segment = "D1";

    public static String Header_Segment = "H";
    public static String Footer_Segment = "T";

    public static String LayoutField_Version = "version";
    public static String LayoutField_Title = "Field Name";

    public static ArrayList<String> dependentInsuredRecordType = new ArrayList<>();
    public static ArrayList<String> metaDataFields = new ArrayList<>();
    public static Map<String,String> dependentBeginSegment = new HashMap<>();
    public static ArrayList<String> dependentInsuredFields = new ArrayList<>();

    static {
        /**
         *  While generating the dependent file, Insured Record, Part1 and Part2 should be recoreded to
         *  distinguish the dependent record of which Insured record.
         */
        dependentInsuredRecordType.add("I1");
        dependentInsuredRecordType.add("I2");

        /**
         *  while generating the dependent records, the dependency of the Coverage Record elements.
         * **/
        dependentInsuredFields.add("CR_Coverage_Effective_Date");
        dependentInsuredFields.add("CR_Coverage_Term_Date");
        dependentInsuredFields.add("CR_Coverage_Plan_Code");
        dependentInsuredFields.add("CR_Coverage_Dep_Status_Code");
        dependentInsuredFields.add("CR_Insured_ID_SSN");
        dependentInsuredFields.add("CR_Coverage_Type");

        /***
         *
         * **/
        metaDataFields.add("Header");
        metaDataFields.add("Footer");
        metaDataFields.add("Version");
        metaDataFields.add("Parsed_date");
        metaDataFields.add("raw_filename");

        dependentBeginSegment.put("D","For dependent record (V2)");
        dependentBeginSegment.put("D1","For dependent record (V3)");
    }
}
