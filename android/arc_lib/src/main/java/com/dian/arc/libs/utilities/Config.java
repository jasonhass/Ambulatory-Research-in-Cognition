package com.dian.arc.libs.utilities;

public class Config {

    // Core
    public static boolean CHOOSE_LOCALE = false;
    public static String FOLDER_NAME = "arc"; // if a file fails to send, you can find it here
    public static int ARC_INTERVALS[] = new int[]{};
    public static boolean SKIP_BASELINE = false;

    // Rest API
    public static int REST_API_VERSION = 0;
    public static String REST_ENDPOINT = ""; // where we send the data
    public static boolean REST_BLACKHOLE = true; // used for debugging, keeps all rest calls from reaching the outside world
    public static boolean REST_HEARTBEAT = false; // heartbeat will fail if blackhole is enabled

    // Authentication
    public static boolean AUTHENTICATION_TWO_FACTOR = false; // not currently implemented
    public static boolean AUTHENTICATION_RATER_ID = false;

    //Debug
    public static boolean DEBUG_DIALOGS = false; // click the top left header on most screens a couple times and a debug dialog will appear
    public static boolean RECORD_PERFORMANCE = true;


}
