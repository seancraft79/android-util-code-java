package com.sean.lib_code_java;

import java.util.Iterator;
import java.util.Map;

public class StringUtil {
    public static boolean isNullOrEmpty(String str){

        if(str == null || str.isEmpty() || str.length() < 1)
            return true;

        str = removeSpace(str);
        if(str.isEmpty() || str.length() < 1)
            return true;

        return false;
    }

    public static String removeSpace(String s) {

        String withoutspaces = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ')
                withoutspaces += s.charAt(i);

        }
        return withoutspaces;
    }

    public static String getJsonString(Map<String, String> data) {
        String json = "{";

        Iterator<Map.Entry<String, String>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            json += "'" + entry.getKey() + "':'" + entry.getValue() + "'";

            if(iterator.hasNext()) json += ",";
        }

        json += "}";

        return json;
    }
}
