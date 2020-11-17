//
// CopyDoc.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations.common;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CopyDoc {

    private static final String SPREADSHEET_ID = "16YqR3rxQMK5Xg8KnX4_H99gVxMeZEol_bHSbhxYakms";
    private static final String SETUP_FILE = "setup.json";

    GoogleDoc googleDoc;
    Setup setup;
    String path;

    public CopyDoc() {
    }

    public boolean init() {

        path = getPath();
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdir();
        }

        // load setup json
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(path + SETUP_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("setup file not found");
            Setup.createDefault(jsonFactory,path+SETUP_FILE);
            try {
                inputStream = new FileInputStream(path + SETUP_FILE);
            } catch (FileNotFoundException fileException) {
                System.out.println("failed to create file");
            }
            return false;
        }

        if (inputStream == null) {
            System.out.println("invalid input stream");
            return false;
        }

        try {
            setup = Setup.load(jsonFactory, new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(setup==null){
            return false;
        }

        googleDoc = new GoogleDoc();
        googleDoc.setSpreadsheetId(SPREADSHEET_ID);
        return googleDoc.valid;
    }

    public boolean updateCore() {
        return update("Core!A2:Y999",setup.coreResourcePath,true);
    }

    public boolean updateEXR() {
        return update("EXR!A2:Y999",setup.exrResourcePath,false);
    }

    public boolean updateHASD() {
        return update("HASD!A2:Y999",setup.hasdResourcePath,false);
    }

    public boolean update(String range, String resourcePath, boolean includeEmptyFields) {
        if(!googleDoc.valid){
            return false;
        }

        List<List<String>> data = googleDoc.get(range);
        if(data.isEmpty()){
            return false;
        }

        int max = 0;
        for(List<String> row : data) {
            int size = row.size();
            if(size > max){
                max = size;
            }
        }

        List<LocaleResource> localeResources = new ArrayList<>();
        for(int i=1; i<max; i++) {
            LocaleResource resource = parseForLocale(data,i,includeEmptyFields);
            if(resource.isValid()){
                localeResources.add(resource);
            }
        }

        if(localeResources.isEmpty()) {
            return false;
        }

        if(!resourcePath.endsWith(File.separator)) {
            resourcePath = resourcePath + File.separator;
        }

        for(LocaleResource resource : localeResources) {
            boolean written = resource.write(resourcePath);
            if(!written){
                return false;
            }
        }

        return true;
    }

    private LocaleResource parseForLocale(List<List<String>> data, int index, boolean includeEmptyFields) {
        Map<String,String> map = new LinkedHashMap<>();
        for(List<String> row : data){
            int size = row.size();
            if(row.isEmpty()) {
                continue;
            }

            if((size <= index) && !includeEmptyFields){
                continue;
            }

            String key = row.get(0);

            if(!key.isEmpty()){
                if(row.size() <= index){
                    map.put(key,"");
                } else {
                    map.put(key,row.get(index));
                }
            }
        }
        LocaleResource locale = new LocaleResource(map);
        return locale;
    }

    private String getPath() {
        String moduleName = "translation_tool";
        String sep = File.separator;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("").getFile());
        String firstHalf = file.getAbsolutePath().split(moduleName)[0];
        String path = firstHalf + moduleName + sep + "src" + sep + "main" + sep + "resources" + sep;
        return path;


    }

}
