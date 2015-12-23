package com.jymf.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Object和String互转工具类
 */
public class JsonTool {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    public static String toJson (Object data){
        return gson.toJson(data);
    }

    public static Object toObject (String json, Class clazz) throws JsonSyntaxException{
        return gson.fromJson(json,clazz);
    }
}
