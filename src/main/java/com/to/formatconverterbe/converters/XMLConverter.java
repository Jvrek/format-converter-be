package com.to.formatconverterbe.converters;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XMLConverter {

    private static final Logger LOGGER = Logger.getLogger(CSVConverter.class);

    public static String getXML(List<Map<String, String>> flatJson) {
        // Use the default separator
        return getXML(flatJson, ",");
    }

    public static String getXML(List<Map<String, String>> flatJson, String separator) {
        String xmlString = StringUtils.join(separator) + "\n";

        return xmlString;
    }


    public static void writeToFile(String xmlString, String fileName) {
        try {
            FileUtils.write(new File(fileName), xmlString);
        } catch (IOException e) {
            LOGGER.error("XMLWriter#writeToFile(xmlString, fileName) IOException: ", e);
        }
    }

}
