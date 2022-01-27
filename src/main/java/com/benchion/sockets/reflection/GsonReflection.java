package com.benchion.sockets.reflection;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * That class adds version compatibility to the Gson Library
 */
public final class GsonReflection {
    /**
     * Converts string to JsonElement
     *
     * @param str text to be converted
     * @return JsonElement
     */
    public static JsonElement parseJson(String str) {
        JsonElement element;
        try {
            JsonParser.class.getDeclaredMethod("parseString", String.class);
            element = JsonParser.parseString(str);
        } catch (NoSuchMethodException e) {
            element = new JsonParser().parse(str);
        }
        return element;
    }
}
