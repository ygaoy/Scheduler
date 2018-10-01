package com.example.yg.jobscheduler;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.N) //api 24
public class CameraSyncService extends JobService {
    
    private int notificationId = 10086;
    private int notificationIdFinal = 10087;
    private String notificationChannelId = "10088";
    private String notificationChannelName = "Notification";
    
    @Override
    public boolean onStartJob(final JobParameters params) {
        Thread task = new Thread() {
            @Override
            public void run() {
                
                startForeground(notificationId,createNotification());
                for (int i = 0;i < 10;i++) {
                    Log.d("yuan","i is: " + i);
                    //appendLog(i + "");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jobFinished(params,true);
                Log.d("yuan","Job Done ");
            }
        };
        task.start();
        return true;
    }
    
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
    
    private Notification createNotification() {
        Notification notification;
        Notification.Builder mBuilder = new Notification.Builder(CameraSyncService.this);
        PendingIntent pendingIntent = PendingIntent.getActivity(CameraSyncService.this, 0, new Intent(), 0);
    
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setSound(null, null);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        
            NotificationCompat.Builder mBuilderCompat = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);
            mBuilderCompat
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setProgress(100, 50, false)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setContentTitle("working")
                    .setSubText("working working")
                    .setContentText("Content")
                    .setOnlyAlertOnce(true);
        
            notification = mBuilderCompat.build();
        }
        else {
            mBuilder
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setProgress(100, 50, false)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setContentTitle("working")
                    .setSubText("working working")
                    .setContentText("Content")
                    .setOnlyAlertOnce(true);
            notification = mBuilder.getNotification();
        }
        
        return notification;
    }
}
