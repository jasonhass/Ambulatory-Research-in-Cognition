//
// CacheManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.PathSegmentTypeAdapter;
import com.healthymedium.arc.time.DateTimeTypeAdapter;
import com.healthymedium.arc.time.LocalTimeTypeAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager {

    private static final String TYPE_OBJECT = "";
    private static final String TYPE_BITMAP = "png";
    private static final String TYPE_ZIP = "zip";

    private static final String tag = "CacheManager";
    private static CacheManager instance;

    private class Cache {
        String content;
        File file;
        String type;
        boolean isInMemory;
        boolean isPersistent;

        Cache(){
            content = "{}";
            file = null;
            isInMemory = false;
            isPersistent = false;
        }

        public boolean isObject() {
            return type.equals(TYPE_OBJECT);
        }

        public boolean isBitmap() {
            return type.equals(TYPE_BITMAP);
        }

        public boolean isZip() {
            return type.equals(TYPE_ZIP);
        }

    }

    private Map<String, Cache> map = new HashMap<>();

    private File cacheDir;
    private Gson objectGson;

    private CacheManager(Context context) {
        cacheDir = context.getCacheDir();
        resetCache();
        buildObjectGson();
    }

    public void resetCache() {
        map.clear();
        File[] files = cacheDir.listFiles();
        int count = files.length;

        for(int i=0;i<count;i++) {
            File file = files[i];
            Cache cache = new Cache();
            cache.file = file;
            cache.type = MimeTypeMap.getFileExtensionFromUrl(file.toString());
            cache.isPersistent = true;
            cache.isInMemory = false;

            String name = file.getName();
            if (!cache.type.isEmpty()) {
                name = name.replace("." + cache.type, "");
            }


            map.put(name, cache);
        }
    }


    public static synchronized void initialize(Context context) {
        instance = new CacheManager(context);
    }

    public static synchronized CacheManager getInstance() {
        return instance;
    }

    private void buildObjectGson(){
        objectGson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .registerTypeAdapter(List.class, new ListTypeAdapter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(PathSegment.class,new PathSegmentTypeAdapter())
                .registerTypeAdapter(BaseData.class, new BaseDataTypeAdapter())
                .create();
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public Gson getGson() {
        return objectGson;
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public void remove(String key) {

        key = sanitizeKey(key);
        if(map.containsKey(key)){
            File file = map.get(key).file;
            if(file!=null){
                file.delete();
            }
            map.remove(key);
        }
    }

    public void removeAll() {

        for(Map.Entry<String,Cache> entry : map.entrySet()) {
            File file = entry.getValue().file;
            if(file!=null){
                file.delete();
            }
        }
        map.clear();
    }

    public boolean save(String key) {
        key = sanitizeKey(key);
        if(!map.containsKey(key)) {
            return false;
        }
        Cache cache = map.get(key);
        if(!cache.isPersistent && cache.isObject()) {
            cache.isPersistent =  FileUtil.writeTextFile(cache.file,cache.content);

        }
        return cache.isPersistent;
    }

    public void saveAll() {

        for(Map.Entry<String,Cache> entry : map.entrySet()) {
            Cache cache = entry.getValue();
            if(!cache.isPersistent && cache.isObject()) {
                cache.isPersistent = FileUtil.writeTextFile(cache.file, cache.content);

            }
        }
    }

    public boolean putObject(String key, Object object) {

        if(object==null){

            return false;
        }
        Cache cache;
        if(!map.containsKey(key)){
            File file = new File(cacheDir, key);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            cache = new Cache();
            cache.file = file;
            cache.type = TYPE_OBJECT;
            map.put(key,cache);
        } else {
            cache = map.get(key);
        }

        String content = objectGson.toJson(object);
        if(!cache.content.equals(content)){
            cache.content = content;
            cache.isInMemory = true;
            cache.isPersistent = false;
        }
        return true;
    }

    public <T> T getObject(String key, Class<T> clazz) {

        if(!map.containsKey(key)){
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException(e.getMessage());
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
        Cache cache = map.get(key);
        if(!cache.isInMemory && cache.isObject()){
            cache.content = FileUtil.readTextFile(cache.file);
            cache.isInMemory = true;
        }
        return objectGson.fromJson(cache.content, clazz);
    }

    public File getFile(String key) {

        key = sanitizeKey(key);
        if(map.containsKey(key)){
            return map.get(key).file;
        }

        File file = new File(cacheDir, key);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public Bitmap getBitmap(String key) {

        key = sanitizeKey(key);
        if(!map.containsKey(key)){
            return null;
        }
        Cache cache = map.get(key);
        if(!cache.isBitmap()) {
            return null;
        }
        return FileUtil.readBitmap(cache.file);
    }

    public boolean putBitmap(String key, Bitmap bitmap, int quality) {

        key = sanitizeKey(key);
        Cache cache;
        if (!map.containsKey(key)) {
            File file = new File(cacheDir, key+"."+TYPE_BITMAP);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            cache = new Cache();
            cache.file = file;
            cache.type = TYPE_BITMAP;
            map.put(key, cache);
        } else {
            cache = map.get(key);
        }
        return FileUtil.writeBitmap(cache.file, bitmap, quality);
    }

    private String sanitizeKey(String key) {
        String type = MimeTypeMap.getFileExtensionFromUrl(key);
        if(!type.isEmpty()) {
            key = key.replace("." + type, "");
        }
        return key;
    }

    private class UriAdapter extends TypeAdapter<Uri> {
        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            if (uri != null) {
                out.value(uri.toString());
            } else {
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

                if(BaseData.class.isAssignableFrom(data.getClass())) {
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
