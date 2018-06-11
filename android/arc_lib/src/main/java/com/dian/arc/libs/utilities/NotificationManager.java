package com.dian.arc.libs.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.dian.arc.libs.MainActivity;
import com.dian.arc.libs.NotificationReceiver;
import com.dian.arc.libs.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationManager {

    static public final String NOTIFICATION_ID = "notification_id";
    static public final String NOTIFICATION_TYPE = "notification_type";
    static public final String NOTIFICATION_NODES = "notification_nodes";

    public static final int TEST_TAKE = 1;
    public static final int TEST_MISSED = 30;
    public static final int TEST_CONFIRM = 900;
    public static final int TEST_NEXT = 901;
    private List<Node> nodes;

    private static NotificationManager instance;
    private Context context;

    private NotificationManager(Context context) {
        this.context = context;
        if(PreferencesManager.getInstance()==null){
            PreferencesManager.initialize(context);
        }
        if(PreferencesManager.getInstance().contains(NOTIFICATION_NODES)) {
            Node[] nodeArrays = PreferencesManager.getInstance().getObject(NOTIFICATION_NODES,Node[].class);
            nodes = new ArrayList<>(Arrays.asList(nodeArrays));
        } else {
            nodes = new ArrayList<>();
        }
    }

    public static synchronized void initialize(Context context) {
        instance = new NotificationManager(context);
    }

    public static synchronized NotificationManager getInstance() {
        if(instance==null){
            initialize(ContextSingleton.getContext());
        }
        return instance;
    }

    private void saveNodes(){
        PreferencesManager.getInstance().putObject(NOTIFICATION_NODES,nodes.toArray());
    }

    public Notification buildNotification(String content) {
        Intent main = new Intent(context, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,main, 0);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pluck);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("ARC")
                .setContentText(content)
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    public Notification buildNotification(Node content) {
        Intent main = new Intent(context, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,main, 0);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pluck);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("ARC")
                .setContentText(getContent(content.type))
                .setVibrate(new long[]{500,250,125,250})
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.primary))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(getContent(content.type)))
                .setSound(sound)
                .setSmallIcon(R.drawable.notification);
        return builder.build();
    }

    public void scheduleAllNotifications(){
        int size = nodes.size();
        for(int i=0;i<size;i++){
            DateTime time = new DateTime(nodes.get(i).time.longValue()*1000);
            if(time.isAfterNow()) {
                scheduleNotification(nodes.get(i).id,nodes.get(i).type,time);
            } else {
                nodes.remove(i);
            }
        }
    }

    public void scheduleNotification(int sessionId, int type, DateTime timeStamp) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        Node node = new Node(sessionId, type, timeStamp);

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (node.id+1)*node.type, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeStamp.getMillis(), pendingIntent);

        addNode(node);
        saveNodes();
    }

    public boolean removeNotification(int sessionId,int type) {
        Node node = getNode(type, sessionId);
        if(node==null){
            return false;
        }
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (node.id+1)*node.type, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        removeNode(node);
        saveNodes();
        return true;
    }

    public void removeNotification(Node node) {
        if(!removeNode(node)){
            return;
        }
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (node.id+1)*node.type, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private String getContent(int type){
        String content = new String();
        switch (type){
            case TEST_TAKE:
                //content = "It’s wakeTime to take a quick test!";
                content = context.getString(R.string.notification_take);
                break;
            case TEST_CONFIRM:
                //content = "Please confirm your next test date.";
                content = context.getString(R.string.notification_confirm);
                break;
            case TEST_MISSED:
                //content = "You’ve missed your tests. If you're unable to finish this week, please contact your site coordinator.";
                content = context.getString(R.string.notification_missed);
                break;
            case TEST_NEXT:
                //content = "Your next test will be on "+ArcManager.getInstance().getCurrentVisit().getUserStartDate().toString("MM/dd/yy");
                content = context.getString(R.string.notification_next).replace("{DATE}",ArcManager.getInstance().getCurrentVisit().getUserStartDate().toString(context.getString(R.string.format_date)));
                break;
        }
        return content;
    }

    public Node getNode(int type, int sessionId){
        int size = nodes.size();
        for(int i=0;i<size;i++){
            if(nodes.get(i).id==sessionId && nodes.get(i).type==type){
                return nodes.get(i);
            }
        }
        return null;
    }

    public boolean removeNode(int type, int sessionId){
        Node node = getNode(type,sessionId);
        boolean result = nodes.remove(node);
        saveNodes();
        return result;
    }

    public boolean removeNode(Node node){
        boolean result = nodes.remove(node);
        saveNodes();
        return result;
    }

    public void addNode(Node node){
        nodes.add(node);
        saveNodes();
    }

    public class Node{
        public Integer id;
        public Integer type;
        public Double time;

        Node(Integer id, Integer type, DateTime time){
            this.id = id;
            this.type = type;
            this.time = JodaUtil.toUtcDouble(time);
        }
    }

    public void notifyUser(int id,int type){
        Node node = getNode(type,id);
        if(node != null) {
            removeNode(node);
            Notification notification = buildNotification(node);
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(node.id, notification);
        }
    }
}
