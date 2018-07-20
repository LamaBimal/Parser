package com.deerwalk.HSPParser;

import com.deerwalk.HSPParser.Utils.HSPLayoutReader;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by btamang on 5/22/18.
 */
public class HealthAxisParser {

    public static void main(String[] args) {

       String inputFile = "/home/btamang/Desktop/HealthAxisFile/ELIGFF.R.DWCPW_HSP_HAX_ELIG_SAMPLE_FILE_20180521";

        RuleExtractor ruleExtractor = new RuleExtractor();

        ClassLoader classLoader = HealthAxisParser.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("HSP_Layout.xlsx");
        InputStream ruleInputStream = classLoader.getResourceAsStream("hsp_elig_Insured_layout.txt");

        ruleExtractor.extractLayout(ruleInputStream);

        try {
            new HealthAxisParser().parse(inputFile,inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void parse(String inputFile, InputStream layoutStream) throws IOException {

        InputStream ruleInputStream = HealthAxisParser.class.getClassLoader().getResourceAsStream("hsp_elig_Insured_layout.txt");

        RuleExtractor ruleExtractor = new RuleExtractor();
        ruleExtractor.extractLayout(ruleInputStream);

        HSPLayoutReader reader = new HSPLayoutReader();
        reader.readFile(inputFile,layoutStream,ruleExtractor);

        ruleInputStream.close();
    }
}
