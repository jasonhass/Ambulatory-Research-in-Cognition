//
// LocaleResource.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocaleResource {

    // replace with your local path to the resources folder in translation_tool. for windows users, use \\ as path separator (ex: C:\\Users\\MyName\\...)
//    private static final String PATH_TO_RESOURCES = "/Users/danny/Android/HealthyMedium/core/translation_tool/src/main/resources/";
//    private static final String PATH_SEPARATOR = "/";
    private static final String PATH_TO_RESOURCES = null;
    private static final String PATH_SEPARATOR = null;
    private static final String FILE_NAME = "strings.xml";

    public LocaleResource() {
    }

    public Map<String,String> map = new LinkedHashMap<>();
    public String ARC2key = new String();
    public String app_name = new String();
    public String country_key = new String();
    public String language_key = new String();

    public String getFilePath() {
        if(country_key.equals("US") && language_key.equals("en")) {
            return PATH_TO_RESOURCES + "values";
        }
        return PATH_TO_RESOURCES + "values-" + language_key + "-r" + country_key;
    }

    public String getFileName() {
        return FILE_NAME;
    }

    public String getFileNameWithPath() {
        return getFilePath() + PATH_SEPARATOR + FILE_NAME;
    }

    public static boolean localPathsSet() {
        return PATH_TO_RESOURCES != null && PATH_SEPARATOR != null && FILE_NAME != null;
    }
}
