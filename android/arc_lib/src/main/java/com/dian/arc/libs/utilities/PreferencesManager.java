package com.dian.arc.libs.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private static PreferencesManager instance;
    private final SharedPreferences sharedPreferences;

    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName()+".PREF_NAME", Context.MODE_PRIVATE);
    }

    public static synchronized void initialize(Context context) {
        instance = new PreferencesManager(context);
    }

    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            initialize(ContextSingleton.getContext());
        }
        return instance;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
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
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void putObject(String key, Object object) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        String json = gson.toJson(object);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public <T> T getObject(String key, Class<T> clazz) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        String json = sharedPreferences.getString(key, "{}");
        return gson.fromJson(json, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        List<T> list = null;
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        String json = sharedPreferences.getString(key, "{}");
        if(!json.equals("{}")){
            list  =  gson.fromJson(json, listType);
        }
        if(list==null){
            list = new ArrayList<>();
        }
        return list;
    }

    public <T> void putList(String key, List<T> objects) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .create();
        Type listType = new TypeToken<List<T>>(){}.getType();
        String json = gson.toJson(objects, listType);
        sharedPreferences.edit().putString(key, json).apply();
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
