package com.example.gmahn.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotiAlert {
    public static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "Notification Channel ID";

    private Context mContext;
    private String mChannelName = "충격감지";
    private String mChannelDescription = "캡쳐 되었습니다.";

    public NotiAlert(Context context){
        mContext = context;
    }

    public void createNotificationChannel(String title, String description){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            callNotification();
        } else{
            sendNotification();
        }
    }

    public void callNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(mChannelName)
                .setContentText(mChannelDescription)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(mChannelName)
                .setContentText(mChannelDescription)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
    }
}
