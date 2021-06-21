package com.to.formatconverterbe.converters;

import org.json.JSONArray;
import org.json.JSONObject;

public class StringToJson {

    public static Object toJson(String string){

        if(string.startsWith("{")){
            return new JSONObject(string);
        }
        else if(string.startsWith("["))
        {
            return new JSONArray(string);
        }
        else{
            return null;
        }
    }
}
