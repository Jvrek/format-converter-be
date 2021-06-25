package com.to.formatconverterbe.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;

public class CSVConverter {

    private static final Logger LOGGER = Logger.getLogger(CSVConverter.class);

    public static String getCSV(List<Map<String, String>> flatJson) {
        return getCSV(flatJson, ",");
    }

    public static String getCSV(List<Map<String, String>> flatJson, String separator) {
        Set<String> headers = collectHeaders(flatJson);
        String csvString = StringUtils.join(headers.toArray(), separator) + "\n";

        for (Map<String, String> map : flatJson) {
            csvString = csvString + getSeparatedColumns(headers, map, separator) + "\n";
        }

        return csvString;
    }

    private static Set<String> collectHeaders(List<Map<String, String>> flatJson) {
        Set<String> headers = new LinkedHashSet<String>();

        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }

        return headers;
    }

    public static void writeToFile(String csvString, String fileName) {
        try {
            FileUtils.write(new File(fileName), csvString);
        } catch (IOException e) {
            LOGGER.error("CSVWriter#writeToFile(csvString, fileName) IOException: ", e);
        }
    }

    private static String getSeparatedColumns(Set<String> headers, Map<String, String> map, String separator) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replaceAll("[\\,\\;\\r\\n\\t\\s]+", " ");
            items.add(value);
        }

        return StringUtils.join(items.toArray(), separator);
    }

}
