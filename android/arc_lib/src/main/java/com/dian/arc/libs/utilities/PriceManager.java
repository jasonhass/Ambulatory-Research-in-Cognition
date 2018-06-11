package com.dian.arc.libs.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;

import com.dian.arc.libs.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriceManager {

    private static PriceManager instance;
    private Context context;
    private List<PriceSet> priceSets = new ArrayList<>();

    private PriceManager(Context context) {
        this.context = context;
        loadJson();
    }

    public static synchronized void initialize(Context context) {
        instance = new PriceManager(context);
    }

    public static synchronized PriceManager getInstance() {
        if (instance == null) {
            initialize(ContextSingleton.getContext());
        }
        return instance;
    }


    public PriceSet getPriceSet(int set,int test){
        return priceSets.get(10*set+test);
    }

    private void loadJson(){
        try {
            InputStream stream = context.getResources().openRawResource(R.raw.price_sets);
            context.getResources().openRawResource(R.raw.price_sets);

            TypedValue returnedValue = new TypedValue();
            context.getResources().openRawResource(R.raw.price_sets, returnedValue);
            Log.d("Example", "Path to loaded resource: " + returnedValue.string);

            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            Gson gson = new GsonBuilder().create();

            // Read file in stream mode
            reader.beginArray();
            while (reader.hasNext()) {
                // Read data into object model
                Item[] items = gson.fromJson(reader, Item[].class);
                PriceSet priceSet = new PriceSet();
                priceSet.items = Arrays.asList(items);
                priceSets.add(priceSet);
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public class JsonLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loaded = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InputStream stream = context.getResources().openRawResource(R.raw.price_sets);
                JsonReader reader = new JsonReader(new InputStreamReader(stream));
                Gson gson = new GsonBuilder().create();

                // Read file in stream mode
                reader.beginArray();
                while (reader.hasNext()) {
                    // Read data into object model
                    Item[] items = gson.fromJson(reader, Item[].class);
                    PriceSet priceSet = new PriceSet();
                    priceSet.items = Arrays.asList(items);
                    priceSets.add(priceSet);
                }
                reader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void etc) {
            //loaded = true;
        }
    }

    public class PriceSet {
        public List<Item> items = new ArrayList<>();
    }

    public class Item {
        public String item;
        public String price;
        public String alt;

        Item(String item,String price, String alt){
            this.item = item;
            this.price = price;
            this.alt = alt;
        }
    }


}
