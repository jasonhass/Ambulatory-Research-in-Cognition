package com.dian.arc.libs.rest;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dian.arc.libs.rest.models.VisitStartDateChange;
import com.dian.arc.libs.rest.models.MissedTestSession;
import com.dian.arc.libs.rest.models.TestSession;
import com.dian.arc.libs.rest.models.TestSessionSchedule;
import com.dian.arc.libs.rest.models.WakeSleepSchedule;
import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.ContextSingleton;
import com.dian.arc.libs.utilities.NavigationManager;
import com.dian.arc.libs.utilities.PreferencesManager;
import com.dian.arc.libs.utilities.SignatureManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static RestAPI service;
    private static List<String> uploadQueue = new ArrayList<>();
    private static String fileBeingUploaded = null;
    private static boolean uploading = false;

    private RestClient() {

    }

    protected static synchronized void initialize(String baseUrl) {
        Log.i("RestClient","initialize");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl httpUrl = request.url().newBuilder().build();
                request = request.newBuilder().url(httpUrl).build();
                return chain.proceed(request);
            }
        }).build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                    @Override
                    public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                        MathContext mc = new MathContext(15);
                        BigDecimal value = BigDecimal.valueOf(src);
                        return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                    }
                })
                .setPrettyPrinting()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(RestAPI.class);
        uploadQueue = PreferencesManager.getInstance().getList("uploadQueue",String.class);
        if(PreferencesManager.getInstance().contains("uploadQueue")) {
            String[] nodeArrays = PreferencesManager.getInstance().getObject("uploadQueue",String[].class);
            Log.i("RestClient","uploadQueue: "+ Arrays.toString(nodeArrays));
            uploadQueue = new ArrayList<>(Arrays.asList(nodeArrays));
        } else {
            uploadQueue = new ArrayList<>();
        }
    }

    public static boolean isUploading(){
        return uploading;
    }

    public static synchronized RestAPI getService() {
        if (service == null) {
            initialize(Config.REST_ENDPOINT);
        }
        return service;
    }

    public static synchronized boolean sendWakeSleepData(WakeSleepSchedule schedule){
        Log.i("RestClient","sendWakeSleepData()");
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();

            String name = schedule.getArcId()+" Wake Sleep Schedule "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";

            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);
            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);

            zos.write(gson.toJson(schedule).getBytes());
            zos.closeEntry();
            zos.close();
            submitData(schedule.getDeviceId(),path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean sendTestSessionSchedule(TestSessionSchedule schedule){
        Log.i("RestClient","sendTestSessionSchedule()");
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();

            String name = schedule.getArcId()+" Test Session Schedule "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";
            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);
            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);
            zos.write(gson.toJson(schedule).getBytes());
            zos.closeEntry();
            zos.close();
            submitData(schedule.getDeviceId(),path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean updateArcDate(VisitStartDateChange dateChange){
        Log.i("RestClient","updateArcDate");

        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();

            String name = dateChange.getArcId()+" Visit DateTime Update "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";
            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);
            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);
            zos.write(gson.toJson(dateChange).getBytes());
            zos.closeEntry();
            zos.close();
            submitData(dateChange.getDeviceId(),path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean sendMissedTestSession(MissedTestSession missedSession){
        Log.i("RestClient","sendMissedTestSession");
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();
            String name = missedSession.getArcId()+" Visit-"+missedSession.getVisitId()+" Session-"+missedSession.getSessionId()+" "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";
            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);
            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);
            zos.write(gson.toJson(missedSession).getBytes());
            zos.closeEntry();
            zos.close();
            submitData(missedSession.getDeviceId(),path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean sendAbandonedTestSession(String deviceId, TestSession session){
        Log.i("RestClient","sendTestSession");
        ByteArrayOutputStream startStream = new ByteArrayOutputStream();
        ByteArrayOutputStream endStream = new ByteArrayOutputStream();
        session.setDeviceId(deviceId);
        session.ensureInit();
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();

            String name = session.getArcId()+" Visit-"+session.getVisitId()+" Session-"+session.getSessionId()+" "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";
            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);

            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);
            zos.write(gson.toJson(session).getBytes());
            zos.closeEntry();

            Bitmap imageStart = SignatureManager.getInstance().get(session.getSessionId(),true);
            if(imageStart != null) {
                imageStart.compress(Bitmap.CompressFormat.PNG, 100, startStream);
                ZipEntry start = new ZipEntry(name + " start.png");
                zos.putNextEntry(start);
                zos.write(startStream.toByteArray());
                zos.closeEntry();
            }

            Bitmap imageEnd = SignatureManager.getInstance().get(session.getSessionId(),false);
            if(imageEnd != null) {
                imageEnd.compress(Bitmap.CompressFormat.PNG, 100, endStream);
                ZipEntry end = new ZipEntry(name + " end.png");
                zos.putNextEntry(end);
                zos.write(endStream.toByteArray());
                zos.closeEntry();
            }

            zos.close();
            submitData(deviceId,path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static synchronized boolean sendTestSession(String deviceId, TestSession session){
        Log.i("RestClient","sendTestSession");
        ByteArrayOutputStream startStream = new ByteArrayOutputStream();
        ByteArrayOutputStream endStream = new ByteArrayOutputStream();
        session.setDeviceId(deviceId);
        session.ensureInit();
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(final Double src, final Type typeOfSrc, final JsonSerializationContext context) {
                            MathContext mc = new MathContext(15);
                            BigDecimal value = BigDecimal.valueOf(src);
                            return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
                        }
                    })
                    .setPrettyPrinting()
                    .setLenient()
                    .create();

            String name = session.getArcId()+" Visit-"+session.getVisitId()+" Session-"+session.getSessionId()+" "+ getDateTime();
            String path = getBasePath()+ File.separator + name + ".zip";
            FileOutputStream dest = new FileOutputStream(path);
            ZipOutputStream zos = new ZipOutputStream(dest);

            ZipEntry entry = new ZipEntry(name+".json");
            zos.putNextEntry(entry);
            zos.write(gson.toJson(session).getBytes());
            zos.closeEntry();

            Bitmap imageStart = SignatureManager.getInstance().get(session.getSessionId(),true);
            if(imageStart != null) {
                imageStart.compress(Bitmap.CompressFormat.PNG, 100, startStream);
                ZipEntry start = new ZipEntry(name + " start.png");
                zos.putNextEntry(start);
                zos.write(startStream.toByteArray());
                zos.closeEntry();
            }

            Bitmap imageEnd = SignatureManager.getInstance().get(session.getSessionId(),false);
            if(imageEnd != null) {
                imageEnd.compress(Bitmap.CompressFormat.PNG, 100, endStream);
                ZipEntry end = new ZipEntry(name + " end.png");
                zos.putNextEntry(end);
                zos.write(endStream.toByteArray());
                zos.closeEntry();
            }

            zos.close();
            submitData(deviceId,path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    private static synchronized String getBasePath(){
        String path = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(path+File.separator+Config.FOLDER_NAME);
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir.getPath();
    }

    private static String getDateTime() {
        return DateTime.now(DateTimeZone.UTC).toString("yyyy-MM-dd-hh-mm-ss");
    }

    public static String popUploadQueue() {
        Log.i("RestClient","popUploadQueue");
        int last = uploadQueue.size()-1;
        String filename = null;
        if(last>-1) {
            filename = uploadQueue.remove(last);
            Log.i("RestClient","popUploadQueue: "+filename);
            PreferencesManager.getInstance().putObject("uploadQueue", uploadQueue.toArray());
        }
        return filename;
    }

    private static void pushUploadQueue(String filename) {
        Log.i("RestClient","pushUploadQueue: "+filename);
        if(filename!=null){
            uploadQueue.add(filename);
        }
        PreferencesManager.getInstance().putObject("uploadQueue",uploadQueue.toArray());
    }

    private static Callback dataCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.d("onFailure", t.getMessage());
            pushUploadQueue(fileBeingUploaded);
        }
    };

    public static void deleteData(String filename) {
        File file = new File(filename);
        boolean deleted = false;
        if (file.exists()) {
            deleted = file.delete();
        }
        Log.i("dataCallback", "deleted: " + deleted);
    }

    public static void submitData(String deviceId, String filename) {
        uploading = true;
        fileBeingUploaded = filename;
        Log.i("RestClient", "submitData: " + filename);
        if(Config.REST_BLACKHOLE){
            return;
        }
        if(filename == null){
            return;
        }
        File assetFile = new File(filename);
        if(!assetFile.exists()){
            return;
        }
        RequestBody asset = RequestBody.create(MediaType.parse("application/octet-stream"), assetFile);
        Call<ResponseBody> call = getService().submitData(deviceId,asset);
        try {
            retrofit2.Response<ResponseBody> response = call.execute();
            parseResponse(response);
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
            pushUploadQueue(fileBeingUploaded);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void submitDataAsync(String deviceId, String filename) {
        fileBeingUploaded = filename;
        Log.i("RestClient", "submitDataAsync: " + filename);
        if(Config.REST_BLACKHOLE){
            return;
        }
        if(filename == null){
            return;
        }
        File assetFile = new File(filename);
        if(!assetFile.exists()){
            return;
        }
        RequestBody asset = RequestBody.create(MediaType.parse("application/octet-stream"), assetFile);
        Call<ResponseBody> call = getService().submitData(deviceId,asset);
        call.enqueue(submitDataCallback);
    }

    private static Callback submitDataCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            parseResponse(response);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i("verificationCodeRequest","request failed: "+t.getMessage());
            pushUploadQueue(fileBeingUploaded);
        }
    };


    private static void parseResponse(retrofit2.Response<ResponseBody> response){
        if (response != null) {
            int code = response.raw().code();
            switch (code) {
                case 200:
                    Log.i("dataCallback", "200: ok");
                    if (fileBeingUploaded != null) {
                        Log.i("dataCallback", "deleting: " + fileBeingUploaded);
                        deleteData(fileBeingUploaded);
                    }
                    fileBeingUploaded = popUploadQueue();
                    Log.i("dataCallback", "popUploadQueue: "+fileBeingUploaded);
                    if(fileBeingUploaded != null){
                        submitData(ArcManager.getInstance().getDeviceId(),fileBeingUploaded);
                    } else {
                        uploading = false;
                    }
                    return;
                case 201:
                    Log.i("dataCallback", "201: created");
                    if (fileBeingUploaded != null) {
                        Log.i("dataCallback", "deleting: " + fileBeingUploaded);
                        deleteData(fileBeingUploaded);
                    }
                    fileBeingUploaded = popUploadQueue();
                    Log.i("dataCallback", "popUploadQueue: "+fileBeingUploaded);
                    if(fileBeingUploaded != null){
                        submitData(ArcManager.getInstance().getDeviceId(),fileBeingUploaded);
                    } else {
                        uploading = false;
                    }
                    return;
                case 400:
                    Log.i("dataCallback", "400: bad request, malformed json body content");
                    break;
                case 404:
                    Log.i("dataCallback", "404: subject not found");
                    break;
                case 429:
                    Log.i("dataCallback", "429: too many requests");
                    break;
                case 500:
                    Log.i("dataCallback", "500: system failure on server side");
                    break;
                default:
                    Log.i("dataCallback", String.valueOf(code));
                    break;
            }
        } else {
            Log.i("dataCallback", "null: invalid response");
        }

        // if not going somewhere else, push the queue
        pushUploadQueue(fileBeingUploaded);
        uploading = false;
    }
}
