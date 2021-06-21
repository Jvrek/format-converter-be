package com.to.formatconverterbe.converters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONtoXML {

    public static String toString(Object object) throws JSONException {
        return toString(object, (String)null);
    }

    public static String toString(Object object, String tagName) throws JSONException {
        StringBuffer sb = new StringBuffer();
        int i;
        JSONArray ja;
        int length;
        String string;
        if (!(object instanceof JSONObject)) {
            if (object.getClass().isArray()) {
                object = new JSONArray(object);
            }

            if (object instanceof JSONArray) {
                ja = (JSONArray)object;
                length = ja.length();

                for(i = 0; i < length; ++i) {
                    sb.append(toString(ja.opt(i), tagName == null ? "array" : tagName));
                }

                return sb.toString();
            } else {
                //escape
                string = object == null ? "null" : object.toString();
                return tagName == null ? "\"" + string + "\"" : (string.length() == 0 ? "<" + tagName + "/>" : "<" + tagName + ">" + string + "</" + tagName + ">");
            }
        } else {
            if (tagName != null) {
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
            }

            JSONObject jo = (JSONObject)object;
            Iterator keys = jo.keys();

            while(true) {
                while(true) {
                    while(keys.hasNext()) {
                        String key = keys.next().toString();
                        Object value = jo.opt(key);
                        if (value == null) {
                            value = "";
                        }

                        if (value instanceof String) {
                            string = (String)value;
                        } else {
                            string = null;
                        }

                        if ("content".equals(key)) {
                            if (value instanceof JSONArray) {
                                ja = (JSONArray)value;
                                length = ja.length();

                                for(i = 0; i < length; ++i) {
                                    if (i > 0) {
                                        sb.append('\n');
                                    }
                                    //escape
                                    sb.append(ja.get(i).toString());
                                }
                            } else {
                                //escape
                                sb.append(value.toString());
                            }
                        } else if (value instanceof JSONArray) {
                            ja = (JSONArray)value;
                            length = ja.length();

                            for(i = 0; i < length; ++i) {
                                value = ja.get(i);
                                if (value instanceof JSONArray) {
                                    sb.append('<');
                                    sb.append(key);
                                    sb.append('>');
                                    sb.append(toString(value));
                                    sb.append("</");
                                    sb.append(key);
                                    sb.append('>');
                                } else {
                                    sb.append(toString(value, key));
                                }
                            }
                        } else if ("".equals(value)) {
                            sb.append('<');
                            sb.append(key);
                            sb.append("/>");
                        } else {
                            sb.append(toString(value, key));
                        }
                    }

                    if (tagName != null) {
                        sb.append("</");
                        sb.append(tagName);
                        sb.append('>');
                    }

                    return sb.toString();
                }
            }
        }
    }

//    public static String escape(String string) {
//        StringBuffer sb = new StringBuffer();
//        int i = 0;
//
//        for(int length = string.length(); i < length; ++i) {
//            char c = string.charAt(i);
//            switch(c) {
//                case '"':
//                    sb.append("&quot;");
//                    break;
//                case '&':
//                    sb.append("&amp;");
//                    break;
//                case '\'':
//                    sb.append("&apos;");
//                    break;
//                case '<':
//                    sb.append("&lt;");
//                    break;
//                case '>':
//                    sb.append("&gt;");
//                    break;
//                default:
//                    sb.append(c);
//            }
//        }
//
//        return sb.toString();
//    }
}
