package com.example.yg.jobscheduler;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@TargetApi(Build.VERSION_CODES.N) //api 24
public class PhotosContentJob extends JobService {
    // The root URI of the media provider, to monitor for generic changes to its content.
    static final Uri MEDIA_URI = Uri.parse("content://" + MediaStore.AUTHORITY + "/");
    
    static final int PHOTOS_CONTENT_JOB = 1086;
    static int count = 0;
    
    public static Context mContext;
    JobParameters mRunningParams;
    
    // Schedule this job, replace any existing one.
    public static void scheduleJob(Context context) {
        final JobScheduler js = context.getSystemService(JobScheduler.class);
        
        JobInfo.Builder builder = new JobInfo.Builder(PHOTOS_CONTENT_JOB,
                new ComponentName(MainActivity.packageName,PhotosContentJob.class.getName()));
        builder.setRequiresCharging(true);
        // Look for specific changes to images in the provider.
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
        // Also look for general reports of changes in the overall provider.
        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(MEDIA_URI,0));
        final JobInfo jobInfo = builder.build();
        Thread t = new Thread(){
            @Override
            public void run() {
                int result = js.schedule(jobInfo);
                if (result == JobScheduler.RESULT_SUCCESS) {
                    Log.d("PhotosContentJob","JOB SCHEDULED!");
                }else{
                    Log.d("PhotosContentJob","FAILED TO SCHEDULE!");
                }
            }
        };
        
        t.start();
        
    }
    
    // Check whether this job is currently scheduled.
    public static boolean isScheduled(Context context) {
        mContext = context;
        JobScheduler js = context.getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i = 0;i < jobs.size();i++) {
            if (jobs.get(i).getId() == PHOTOS_CONTENT_JOB) {
                return true;
            }
        }
        return false;
    }
    
    // Cancel this job, if currently scheduled.
    public static void cancelJob(Context context) {
        mContext = context;
        JobScheduler js = context.getSystemService(JobScheduler.class);
        js.cancel(PHOTOS_CONTENT_JOB);
    }
    
    public static void sendNotification(String message) {
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1006,emptyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("My notification")
                        .setContentText("round: " + message)
                        .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10087,mBuilder.build());
    }
    
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("PhotosContentJob","job stopped");
        return true;
    }
    
    public void appendLog(String message) {
        
        File logFile = new File(Environment.getExternalStorageDirectory() + "/TestLog.txt");
        
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,true));
            Date date = new Date();
            buf.append("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + " " + message + " \n"));
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d("PhotosContentJob","JOB STARTED!");
        count++;
        //sendNotification();
        mRunningParams = params;
        
        Thread task = new Thread() {
            @Override
            public void run() {
                
                for (int i = 0;i < 10;i++) {
                    Log.d("PhotosContentJob","i is: " + i);
                    appendLog(i + "");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jobFinished(params, true);
                Log.d("PhotosContentJob","Job Done ");
            }
        };
        task.start();
        return true;
    }
    
   
}
