package com.deerwalk.HSPParser;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by btamang on 5/22/18.
 */
public class HealthAxisParser {

    public static void main(String[] args) {

       String inputFile = "/home/bimal/Desktop/HealthAxisFile/ELIGFF.R.DWCPW_HSP_HAX_ELIG_SAMPLE_FILE_20180726";

        ClassLoader classLoader = HealthAxisParser.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("HspLayout.xlsx");

        try {
            new HealthAxisParser().parse(inputFile,inputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void parse(String inputFile, InputStream layoutStream) throws IOException {

        InputStream ruleInputStream = HealthAxisParser.class.getClassLoader().getResourceAsStream("hsp_elig_Insured_layout.txt");

        RuleExtractor ruleExtractor = RuleExtractor.getInstance();
        ruleExtractor.extractLayout(ruleInputStream);

        LayoutExtractor layoutExtractor = new LayoutExtractor();
        layoutExtractor.extractLayoutFields(layoutStream);

        HSPReader hspReader = HSPReaderFactory.getReader(layoutExtractor.getLayoutVersion());
        hspReader.parseInputFile(inputFile,layoutExtractor,ruleExtractor);

        ruleInputStream.close();
    }
}
