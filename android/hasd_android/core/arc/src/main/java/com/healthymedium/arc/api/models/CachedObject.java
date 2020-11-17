//
// CachedObject.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;


import com.healthymedium.arc.utilities.CacheManager;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CachedObject {

    public String filename;
    public String mediaType;

    public RequestBody getRequestBody(){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        File object = CacheManager.getInstance().getFile(filename,false);
        if(object==null){

        } else {
            RequestBody uploadFile = RequestBody.create(MediaType.parse(mediaType), object);
            builder.addFormDataPart("file", filename, uploadFile);
        }

        Field[] fields = getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            String value = fieldToString(fields[i]);
            if(value.length()>0) {
                builder.addFormDataPart(key, value);
            }
        }

        return builder.build();
    }


    public String fieldToString(Field field) {
        String name = field.getName();
        if(name.equals("serialVersionUID")){
            return "";
        }
        if(name.equals("filename")){
            return "";
        }
        if(name.equals("mediaType")){
            return "";
        }

        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(value==null){
            return "";
        }

        return String.valueOf(value);
    }
}
