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
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.healthymedium.arc.core.Application;
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

    private PriceManager(Context context) {
        loadJson(context);
    }

    public static synchronized void initialize(Context context) {
        instance = new PriceManager(context);
    }

    public static synchronized PriceManager getInstance() {
        if (instance == null) {
            initialize(Application.getInstance().getApplicationContext());
        }
        return instance;
    }


    public List<PriceManager.Item> getPriceSet(){
        ParticipantState state = Study.getParticipant().getState();
        int size = priceSets.size();
        int index = 10*state.currentVisit+state.currentTestSession;
        if(index>=size){
            index -= size;
        }
        return priceSets.get(index);
    }

    public List<PriceManager.Item> getPriceSet(int set,int test){
        return priceSets.get(10*set+test);
    }

    private void loadJson(Context context){
        long before = System.currentTimeMillis();

        try {
            InputStream stream = context.getResources().openRawResource(R.raw.price_sets);
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
        Log.i("PriceManager", "loadJson took "+(after-before)+"ms");
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
