package com.deerwalk.HSPParser.lib;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by btamang on 5/24/18.
 */
public class KeyGenerator {

    public static Map<String, Integer> insuredRepetitionRecordType = new HashMap<String, Integer>();
    public static ArrayList<String> commonRecordType = new ArrayList<>();
    public static Logger logger = Logger.getLogger(KeyGenerator.class);

    static {

        /***
         * Repetition Record Type contains the maximum number of repetition of record Type.
         * **/
        insuredRepetitionRecordType.put("OI", 3);
        insuredRepetitionRecordType.put("OM", 3);
        insuredRepetitionRecordType.put("O2", 3);

        /**
         *  Following record Type are common between insured and dependent layout.
         *  This record Type has been  tracked which is used to generated the output key.
         * **/
        commonRecordType.add("S*");
        commonRecordType.add("SC");
        commonRecordType.add("OI");
        commonRecordType.add("OM");
        commonRecordType.add("O2");
        commonRecordType.add("PP");
        commonRecordType.add("PS");
        commonRecordType.add("PC");
        commonRecordType.add("P1");
        commonRecordType.add("P2");
    }


    /**
     *  This function is used to generate the key
     *  as per the layout field generation.
     *  @param layoutType is the layout of the content i.e. insured or dependent.
     *  @param recordType
     *  @param element
     *  @param totalFields is the collection of previously generated keys.
     ***/
    public static String generateKey(String layoutType, String recordType, String element, List<String> totalFields) {

        String generatedKey = "";
        String attachKey = "";
        if (layoutType.equalsIgnoreCase(Constants.Layout_Insured)) {
            attachKey = "I_";
        } else if (layoutType.equalsIgnoreCase(Constants.Layout_DEPENDENT)) {
            attachKey = "D_";
        }

        if (commonRecordType.contains(recordType) || (recordType.startsWith("S") && recordType.length() == 2)) {
            generatedKey = attachKey + element;
        }

        if (insuredRepetitionRecordType.containsKey(recordType)) {

            int i = 0;
            int index = StringUtils.ordinalIndexOf(element, "_", 1);

            String prefix = element.substring(0, index);
            String suffix = element.substring(index + 1, element.length());

            do {
                i++;
                if (i > insuredRepetitionRecordType.get(recordType)) {
                    logger.info(":::: found ::::: out of Index ::: for record Type :::"+recordType);
                    break;
                }
                generatedKey = attachKey + prefix + "_" + i + "_" + suffix;

            } while (totalFields.contains(generatedKey));
        } else {
            generatedKey = element;
        }
        return generatedKey;
    }

    /****
     *  This function is used to get the number of repetition of the @param record Type.
     * **/
    public static int getSegmentRepetition(String recordType) {

        if (insuredRepetitionRecordType.containsKey(recordType)) {
            return insuredRepetitionRecordType.get(recordType);
        }
        return 1;
    }

}