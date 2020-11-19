//
// PreferencesManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.PathSegmentTypeAdapter;
import com.healthymedium.arc.time.DateTimeTypeAdapter;
import com.healthymedium.arc.time.LocalTimeTypeAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private static PreferencesManager instance;
    private static final String tag = "PreferencesManager";
    Gson objectGson;

    private PreferencesManager(Context context) {
        buildObjectGson();
    }

    public static synchronized void initialize(Context context) {
        instance = new PreferencesManager(context);
    }

    public static synchronized PreferencesManager getInstance() {
        return instance;
    }

    private void buildObjectGson(){
        objectGson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .registerTypeAdapter(List.class, new ListTypeAdapter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                //.registerTypeAdapter(LocalDate.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(PathSegment.class,new PathSegmentTypeAdapter())
                .registerTypeAdapter(BaseData.class, new BaseDataTypeAdapter())
                .create();
    }

    public Gson getGson() {
        return objectGson;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {

    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {

    }

    public boolean contains(String key) {
        return false;
    }

    public void remove(String key) {

    }

    public void removeAll() {

    }

    public boolean getBoolean(String key, boolean defValue) {
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        return defValue;
    }

    public double getDouble(String key, double defValue) {
        return defValue;
    }

    public int getInt(String key, int defValue) {
        return defValue;
    }

    public long getLong(String key, long defValue) {
        return defValue;
    }

    public String getString(String key, String defValue) {
        return defValue;
    }

    public void putBoolean(String key, boolean value) {

    }

    public void putDouble(String key, double value) {

    }

    public void putFloat(String key, float value) {

    }

    public void putInt(String key, int value) {

    }

    public void putLong(String key, long value) {

    }

    public void putString(String key, String value) {
        if(value==null){
            Log.i(tag,"invalid string, failed to save");
            throw new RuntimeException("tried to save a null string");
        }
    }

    public void putObject(String key, Object object) {
        if(object==null){
            Log.i(tag,"invalid object, failed to save");
            throw new RuntimeException("tried to save a null object");
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String json = "{}";
        return objectGson.fromJson(json, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        List<T> list = null;
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        String json = "{}";
        if(!json.equals("{}")){
            list  =  objectGson.fromJson(json, listType);
        }
        if(list==null){
            list = new ArrayList<>();
        }
        return list;
    }

    /*
    public <T> void putList(String key, List<T> objects) {
        Type listType = new TypeToken<List<T>>(){}.getType();
        String json = objectGson.toJson(objects, listType);
        sharedPreferences.edit().putString(key, json).apply();
    }
    */

    public <T> T getObject(String key, Type type) {
        String json = "{}";
        return objectGson.fromJson(json, type);
    }

    private class UriAdapter extends TypeAdapter<Uri> {
        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            if (uri != null) {
                out.value(uri.toString());
            }
            else {
                out.nullValue();
            }
        }

        @Override
        public Uri read(JsonReader in) throws IOException {
            return Uri.parse(in.nextString());
        }
    }

    public class ListTypeAdapter implements JsonDeserializer<List> {

        @Override
        public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List list = new ArrayList();
            Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];

            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    list.add(context.deserialize(element,type));
                }
            }

            return list;
        }

    }

    public class BaseDataTypeAdapter implements JsonDeserializer<BaseData>, JsonSerializer<BaseData>
    {
        @Override
        public BaseData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject obj = json.getAsJsonObject();


            try {
                Class actualClass = null;
                actualClass = Class.forName(obj.get("actual_class").getAsString());
                Object data = objectGson.fromJson(obj.get("data"), actualClass);

                if(BaseData.class.isAssignableFrom(data.getClass()))
                {
                    return (BaseData) data;
                }

            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }


            return new BaseData();
        }

        @Override
        public JsonElement serialize(BaseData src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject result = new JsonObject();
            result.add("data", objectGson.toJsonTree(src));
            result.addProperty("actual_class",  src.getClass().getName());
            return result;
        }
    }

}
