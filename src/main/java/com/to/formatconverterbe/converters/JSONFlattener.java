package com.to.formatconverterbe.converters;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONFlattener {

    private static final Class<?> JSON_OBJECT = JSONObject.class;
    private static final Class<?> JSON_ARRAY = JSONArray.class;
    private static final Logger LOGGER = Logger.getLogger(JSONFlattener.class);

    public static List<Map<String, String>> parseJson(String json) {
        List<Map<String, String>> flatJson = null;

        try {
            JSONObject jsonObject = new JSONObject(json);
            flatJson = new ArrayList<Map<String, String>>();
            flatJson.add(parse(jsonObject));
        } catch (JSONException je) {
            LOGGER.info("Handle the JSON String as JSON Array");
            flatJson = handleAsArray(json);
        }

        return flatJson;
    }

    public static Map<String, String> parse(JSONObject jsonObject) {
        Map<String, String> flatJson = new LinkedHashMap<String, String>();
        flatten(jsonObject, flatJson, "");
        return flatJson;
    }

    public static List<Map<String, String>> parse(JSONArray jsonArray) {
        JSONObject jsonObject = null;
        List<Map<String, String>> flatJson = new ArrayList<Map<String, String>>();
        int length = jsonArray.length();

        for (int i = 0; i < length; i++) {
            jsonObject = jsonArray.getJSONObject(i);
            Map<String, String> stringMap = parse(jsonObject);
            flatJson.add(stringMap);
        }

        return flatJson;
    }

    private static void flatten(JSONObject obj, Map<String, String> flatJson, String prefix) {
        Iterator<?> iterator = obj.keys();
        String _prefix = prefix != "" ? prefix + "." : "";

        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            if (obj.get(key).getClass() == JSON_OBJECT) {
                JSONObject jsonObject = (JSONObject) obj.get(key);
                flatten(jsonObject, flatJson, _prefix + key);
            } else if (obj.get(key).getClass() == JSON_ARRAY) {
                JSONArray jsonArray = (JSONArray) obj.get(key);

                if (jsonArray.length() < 1) {
                    continue;
                }

                flatten(jsonArray, flatJson, _prefix + key);
            } else {
                String value = obj.get(key).toString();

                if (value != null && !value.equals("null")) {
                    flatJson.put(_prefix + key, value);
                }
            }
        }
    }

    private static void flatten(JSONArray obj, Map<String, String> flatJson, String prefix) {
        int length = obj.length();

        for (int i = 0; i < length; i++) {
            if (obj.get(i).getClass() == JSON_ARRAY) {
                JSONArray jsonArray = (JSONArray) obj.get(i);

                if (jsonArray.length() < 1) {
                    continue;
                }

                flatten(jsonArray, flatJson, prefix + "[" + i + "]");
            } else if (obj.get(i).getClass() == JSON_OBJECT) {
                JSONObject jsonObject = (JSONObject) obj.get(i);
                flatten(jsonObject, flatJson, prefix + "[" + (i + 1) + "]");
            } else {
                String value = obj.get(i).toString();

                if (value != null) {
                    flatJson.put(prefix + "[" + (i + 1) + "]", value);
                }
            }
        }
    }

    private static List<Map<String, String>> handleAsArray(String json) {
        List<Map<String, String>> flatJson = null;

        try {
            JSONArray jsonArray = new JSONArray(json);
            flatJson = parse(jsonArray);
        } catch (Exception e) {
            LOGGER.error("JSON might be malformed, Please verify that your JSON is valid");
        }
        return flatJson;
    }
}
