package com.dian.arc.libs.utilities;

import android.content.Context;

public class ContextSingleton {
    private static ContextSingleton instance;
    private Context context;

    // singleton methods ---------------------------------------------------------------------------

    public static synchronized void initialize(Context context) {
        instance = new ContextSingleton(context);
    }

    public static synchronized Context getContext() {
        return instance.context;
    }

    public ContextSingleton(Context context){
        this.context = context;
    }
}