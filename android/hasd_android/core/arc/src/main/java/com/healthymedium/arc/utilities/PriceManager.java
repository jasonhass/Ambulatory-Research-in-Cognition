//
// PriceManager.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriceManager {

    private static PriceManager instance;
    private List<List<PriceManager.Item>> priceSets = new ArrayList<>();

    private PriceManager() {
    }

    public static synchronized void initialize() {
        instance = new PriceManager();
    }

    public static synchronized PriceManager getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }

    public synchronized void clearCache() {
        priceSets.clear();
    }

    public List<PriceManager.Item> getPriceSet(){

        if(priceSets.size()==0) {
            priceSets = loadJson(Application.getInstance().getResources().openRawResource(R.raw.price_sets));
        }

        if(Config.ENABLE_LEGACY_PRICE_SETS)
        {
            return getLegacyPriceSet();
        }

        int index = Study.getCurrentTestSession().getId();
        int size = priceSets.size();

        if(size == 0)
        {
            return new ArrayList<>();
        }

        if(index>=size){
            index -= size;
        }
        return priceSets.get(index);
    }

    // Many apps have used this older version of the getPriceSet() method, which returns an incorrect
    // priceSet for later visits. This method is being maintained to provide consistent tests for
    // the participants using these applications.

    private List<PriceManager.Item> getLegacyPriceSet(){

        ParticipantState state = Study.getParticipant().getState();
        int size = priceSets.size();
        int index = 10*state.currentTestCycle +state.currentTestSession;
        if(index>=size){
            index -= size;
        }
        return priceSets.get(index);
    }


    public static List<List<PriceManager.Item>> loadJson(InputStream stream){
        long before = System.currentTimeMillis();
        List<List<PriceManager.Item>> priceSets = new ArrayList<>();
        try {

            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            Gson gson = new GsonBuilder().create();

            // Read file in stream mode
            reader.beginArray();
            while (reader.hasNext()) {
                // Read data into object model
                Item[] items = gson.fromJson(reader, Item[].class);
                priceSets.add(Arrays.asList(items));
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long after = System.currentTimeMillis();


        return priceSets;
    }

    public class Item {
        public String item;
        public String price;
        public String alt;

        Item(String item, String price, String alt){
            this.item = item;
            this.price = price;
            this.alt = alt;
        }
    }


}
