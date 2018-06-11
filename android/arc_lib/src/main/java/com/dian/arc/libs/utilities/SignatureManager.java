package com.dian.arc.libs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignatureManager {

    private List<Signature> signatures;
    private static SignatureManager instance;
    private Context context;
    private File directory;

    private SignatureManager(Context context) {
        this.context = context;
        if(PreferencesManager.getInstance().contains("signatures")) {
            Signature[] array = PreferencesManager.getInstance().getObject("signatures", Signature[].class);
            signatures = new ArrayList<>(Arrays.asList(array));
        }
        directory = context.getDir("signatures", Context.MODE_PRIVATE);
        if(signatures==null){
            signatures = new ArrayList<>();
        }
    }

    public static synchronized void initialize(Context context) {
        instance = new SignatureManager(context);
    }

    public static synchronized SignatureManager getInstance() {
        if (instance == null) {
            initialize(ContextSingleton.getContext());
        }
        return instance;
    }

    public boolean put(Bitmap bitmap, boolean start){
        Signature signature = new Signature();
        signature.sessionId = ArcManager.getInstance().getCurrentTestSession().getSessionId();
        signature.bitmap = storeImage(bitmap,start);
        signature.start = start;
        signatures.add(signature);

        PreferencesManager.getInstance().putObject("signatures", signatures);
        return true;
    }

    private String storeImage(Bitmap image,boolean start) {
        int visit =  ArcManager.getInstance().getState().currentVisit;
        int test = ArcManager.getInstance().getState().currentTestSession;
        String suffix = (start) ?" start":" end";
        String filePath = directory.getPath()+File.separator+"Visit-"+visit+" Session-"+test+suffix;
        File imageFile = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public Bitmap get(int sessionId, boolean preTest){
        for(Signature object: signatures){
            if(object.sessionId == sessionId && object.start ==preTest){
                return BitmapFactory.decodeFile(object.bitmap);
            }
        }
        return null;
    }

    public void purge(){
        for (File child : directory.listFiles()) {
            purgeRecursive(child);
        }
        signatures = new ArrayList<>();
        PreferencesManager.getInstance().putObject("signatures", signatures);
    }

    private void purgeRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                purgeRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    private class Signature {
        boolean start;
        String bitmap;
        int sessionId;
    }

}
