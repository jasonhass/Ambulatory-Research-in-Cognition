//
// GoogleDoc.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleDoc {

    private static final String APPLICATION_NAME = "HealthyMedium Translation Tool";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "/tokens";

    NetHttpTransport httpTransport;
    JsonFactory jsonFactory;
    Sheets sheetsService;

    String spreadsheetId;
    boolean valid;


    public GoogleDoc() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            jsonFactory = JacksonFactory.getDefaultInstance();

            // Load client secrets
            InputStream inputStream = GoogleDoc.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("credentials not found: " + CREDENTIALS_FILE_PATH);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
            List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

            // Build flow and trigger user authorization request
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            sheetsService = new Sheets.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            valid = true;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            valid = false;
        } catch (IOException e) {
            e.printStackTrace();
            valid = false;
        }
    }

    public void setSpreadsheetId(String id) {
        spreadsheetId = id;
    }

    public List<List<String>> get(String range)  {

        List<List<String>> results = new ArrayList<>();

        if(!valid) {
            return results;
        }

        System.out.println("requesting sheet data '"+range+"'");
        ValueRange response = null;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return results;
        }

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("no sheet data available");
            return results;
        }

        System.out.println("sheet data received");

        // copy the generic structure into something we expect
        for(List<Object> objectList : values){
            List<String> row = new ArrayList<>();
            for(Object obj : objectList){
                if(obj instanceof String){
                    row.add((String) obj);
                } else {
                    row.add("");
                }
            }
            results.add(row);
        }

        return results;
    }

}
