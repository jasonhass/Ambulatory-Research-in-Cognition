//
// OldPreferences.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class OldPreferences {

    private SharedPreferences sharedPreferences;

    public OldPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName()+".PREF_NAME", Context.MODE_PRIVATE);
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public double getDouble(String key, double defValue) {
        if(contains(key)){
            return getObject(key, double.class);
        }
        return defValue;
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }
    
    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public void putDouble(String key, double value) {
        putObject(key,value);
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).commit();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void putObject(String key, Object object) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        String json = gson.toJson(object);
        sharedPreferences.edit().putString(key, json).commit();
    }

    public <T> T getObject(String key, Class<T> clazz) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        String json = sharedPreferences.getString(key, "{}");
        return gson.fromJson(json, clazz);
    }

    public <T> T getObject(String key, Type type) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, "{}");
        return gson.fromJson(json, type);
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

}
