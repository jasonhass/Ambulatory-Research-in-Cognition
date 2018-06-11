package com.dian.arc.libs.utilities;

import android.content.Context;
import android.graphics.Typeface;
import java.lang.reflect.Field;

public class FontFactory {

    public static Typeface georgia;
    public static Typeface georgiaBold;
    public static Typeface georgiaBoldItalic;
    public static Typeface georgiaItalic;
    public static Typeface tahoma;
    public static Typeface tahomaBold;

    static FontFactory instance;

    FontFactory(Context context){
        instance = this;
        georgia = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Georgia.ttf");
        georgiaBold = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Georgia-Bold.ttf");
        georgiaBoldItalic = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Georgia-BoldItalic.ttf");
        georgiaItalic = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Georgia-Italic.ttf");
        tahoma = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Tahoma.ttf");
        tahomaBold = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Tahoma-Bold.ttf");
    }

    public static synchronized void initialize(Context context) {
        instance = new FontFactory(context);
    }

    public static FontFactory getInstance(){
        if(instance==null){
            instance = new FontFactory(ContextSingleton.getContext());
        }
        return instance;}

    public void setDefaultFont(Typeface typeface) {
        replaceFont("SANS_SERIF", typeface);
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



}
