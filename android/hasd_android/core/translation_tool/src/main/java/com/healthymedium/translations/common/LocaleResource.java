//
// LocaleResource.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

public class LocaleResource {

    private static final String LANGUAGE_KEY = "language_key";
    private static final String COUNTRY_KEY = "country_key";
    private static final String FILE_NAME = "strings.xml";
    private static final String APP_NAME = "app_name";

    public Map<String,String> map;
    public String languageKey;
    public String countryKey;
    public String resource;

    public LocaleResource(Map<String,String> map) {
        this.map = map;

        if(map.containsKey(LANGUAGE_KEY)) {
            languageKey = map.get(LANGUAGE_KEY);
            map.remove(LANGUAGE_KEY);
        }

        if(map.containsKey(COUNTRY_KEY)) {
            countryKey = map.get(COUNTRY_KEY);
            map.remove(COUNTRY_KEY);
        }

        if(map.containsKey(APP_NAME)) {
            map.remove(APP_NAME);
        }

        if(languageKey==null || countryKey==null) {
            return;
        }

        if(countryKey.equals("US") && languageKey.equals("en")) {
            resource = "values" + File.separator;
        } else {
            resource = "values-" + languageKey + "-r" + countryKey + File.separator;
        }

        sanitize();
    }

    public boolean isValid() {
        if(resource==null) {
            return false;
        }
        // android does not support region 419 as a locale resource.
        if(countryKey.equals("419")) {
            return false;
        }
        if(map.isEmpty()){
            return false;
        }
        return true;
    }

    private void sanitize() {
        for(Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();

            // replace less than signs, escape single quotes, replace line breaks with \n (some breaks have odd spaces around them)
            entry.setValue(value
                    .replace("<", "&lt;")
                    .replace("'", "\\'")
                    .replace("\"", "\\\"")
                    .replace(" \n ", "\\n")
                    .replace("\n ", "\\n")
                    .replace("\n", "\\n"));
        }
    }


    public boolean write(String resourcePath) {
        if(!isValid()){
            return false;
        }

        File dir = new File(resourcePath + resource);
        String filename = resourcePath + resource + FILE_NAME;

        if(!dir.exists()){
            System.out.println("'" + dir.getName() + "' does not exist. trying to create...");
            if(dir.mkdir()) {
                System.out.println("directory created");
            } else {
                System.out.println("unable to create directory");
                return false;
            }
        }

        try {
            // write to strings.xml in directory
            File file = new File(filename);
            if(!file.exists()){
                System.out.println("'" + dir.getName() + "' does not exist. trying to create...");
                if(file.createNewFile()) {
                    System.out.println("file created");
                } else {
                    System.out.println("unable to create file");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("["+resource+"] writing xml file...");

        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(filename),
                    Charset.forName("UTF-8").newEncoder()
            );

            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");

            if(countryKey.equals("US") && languageKey.equals("en")) {
                writer.write("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n");
            } else {
                writer.write("<resources>\n");
            }


            for(Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                writer.write("\t<string name=\"" + key + "\">" + value + "</string>\n");
            }

            writer.write("</resources>");
            writer.close();

            System.out.println("["+resource+"] file written");
        } catch (IOException e) {
            System.out.println("["+resource+"] failed");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
