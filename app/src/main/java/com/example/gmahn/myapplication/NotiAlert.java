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

    public void createNotificationChannel(String channelID, String title, String description){
        // NotificationChannel을 만들지만 API 26+에서만 만들기 때문에
        // NotificationChannel 클래스를 지원하는 새로운 라이브러리는 없다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // 추가 작업이 필요한 경우 : NotificationManager를 구현해야한다.
            // 채널 아이디, 노티 제목, 알림 중요도 를 설정한다.
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, importance);
            // setDescription(노티 내용)을 세팅한다.
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            callNotification();
        } else{
            sendNotification(); //버젼이 낮은경우 실행
        }
    }

    public void callNotification(){ //채널아이디를 얻어 사용

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(mChannelName)
                .setContentText(mChannelDescription)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendNotification() { //구버전

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher) //알림 아이콘
                .setWhen(System.currentTimeMillis()) //알림시간
                .setContentTitle(mChannelName) // 알림제목
                .setContentText(mChannelDescription) // 알림내용+
                .setContentIntent(pendingIntent) // 클릭시 이벤트 발생
                .setAutoCancel(true); //취소가능여부

        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
    }
}
