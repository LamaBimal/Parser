package com.deerwalk.HSPParser;

import com.deerwalk.HSPParser.Utils.StringUtils;

public class HSPReaderFactory {

    public static HSPReader getReader(String layoutVersion) {

        HSPReader reader = null;

        if (!StringUtils.isNullorEmpty(layoutVersion)) {

            switch (layoutVersion) {
                case "0.1":
                    reader = new HSPReaderV1();
                    break;
                case "0.2":
                    reader = new HSPReaderV2();
                    break;
            }
        }
        return reader;
    }
}
