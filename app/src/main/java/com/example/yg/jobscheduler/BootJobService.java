package com.example.yg.jobscheduler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;

import java.util.List;

import static com.example.yg.jobscheduler.MainActivity.PHOTOS_CONTENT_JOB;
import static com.example.yg.jobscheduler.MainActivity.logJobState;
import static com.example.yg.jobscheduler.PhotosContentJob.MEDIA_URI;

@TargetApi(Build.VERSION_CODES.N) //api 24
public class BootJobService extends JobService {
    
    Activity mActivity;
    
    public void setActivity(Activity activity){
        mActivity = activity;
    }
    
    @Override
    public boolean onStartJob(final JobParameters params) {
    
//        if(isScheduled(PHOTOS_CONTENT_JOB)){
//            return true;
//        }
        final JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    
        ComponentName name = new ComponentName("com.example.yg.jobscheduler", CameraSyncService.class.getName());
    
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(PHOTOS_CONTENT_JOB, name);
        jobInfoBuilder.addTriggerContentUri(new JobInfo.TriggerContentUri(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
        jobInfoBuilder.addTriggerContentUri(new JobInfo.TriggerContentUri(MEDIA_URI,0));
    
        final JobInfo jobInfo = jobInfoBuilder.build();
    
        Thread t = new Thread(){
            @Override
            public void run() {
                int result = mJobScheduler.schedule(jobInfo);
                logJobState(result);
            }
        };
        t.start();
        
        return true;
    }
    
    // Check whether this job is currently scheduled.
    private boolean isScheduled(int id) {
        JobScheduler js = getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i = 0;i < jobs.size();i++) {
            if (jobs.get(i).getId() == id) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
