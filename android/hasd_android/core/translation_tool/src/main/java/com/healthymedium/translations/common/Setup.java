//
// Setup.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations.common;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

public class Setup {

    @Key
    public String coreResourcePath;

    @Key
    public String exrResourcePath;

    @Key
    public String hasdResourcePath;

    public static Setup load(JsonFactory jsonFactory, Reader reader) throws IOException {
        return jsonFactory.fromReader(reader, Setup.class);

    }

    public static boolean createDefault(JsonFactory jsonFactory, String filename) {
        try {

            File file = new File(filename);
            file.createNewFile();

            if(!file.canWrite()){
                file.setWritable(true);
            }

            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file),
                    Charset.forName("UTF-8").newEncoder()
            );

            Setup setup = new Setup();
            setup.coreResourcePath = "";
            setup.exrResourcePath = "";
            setup.hasdResourcePath = "";

            writer.write(jsonFactory.toPrettyString(setup));
            writer.close();

            System.out.println("default setup file written");
            System.out.println("here's a sample of what it might look like: ");
            System.out.println("{\n" +
                    "  \"coreResourcePath\": \"D:\\\\healthyMedium\\\\core\\\\arc\\\\src\\\\main\\\\res\\\\\",\n" +
                    "  \"exrResourcePath\": \"D:\\\\healthyMedium\\\\exr\\\\app\\\\src\\\\main\\\\res\\\\\",\n" +
                    "  \"hasdResourcePath\": \"D:\\\\healthyMedium\\\\hasd\\\\app\\\\src\\\\main\\\\res\\\\\"\n" +
                    "}");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
