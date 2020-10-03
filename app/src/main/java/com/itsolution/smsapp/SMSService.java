package com.itsolution.smsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class SMSService extends Service {
    public static final String CHANNEL_ID = "SMSServiceChannel";
    private static final CharSequence CHANNEL_NAME = "Foreground SMS Service Channel";

    String msg;
    ArrayList<String> numbers;
    Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        numbers = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        msg  = intent.getStringExtra("msg");
        numbers = intent.getStringArrayListExtra("list");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

         notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                 .setContentText("sending SMS")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        startSMSSending();

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    void  startSMSSending(){

        for (int i = 0; i<numbers.size(); i++) {
            final int finalI = i;

            /*SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numbers.get(finalI), null,"I Love You" , null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();*/

            Log.d("sent SMS to: ",+finalI+ " "+ numbers.get(finalI));


            sleep(1500);

        }
        stopForeground(true);
    }

    private void sleep(long n) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }
}
