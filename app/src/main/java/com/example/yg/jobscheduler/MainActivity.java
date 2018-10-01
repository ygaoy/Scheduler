package com.example.yg.jobscheduler;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String packageName;
    static final int PHOTOS_CONTENT_JOB = 10086;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        packageName = getPackageName();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                //debug purpose
                cancelJob();
                
                //call only greater than api 24
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isScheduled()){
                    
                    JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    ComponentName name = new ComponentName(MainActivity.packageName,PhotosContentJob.class.getName());
                    JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(PHOTOS_CONTENT_JOB, name);
                    jobInfoBuilder.addTriggerContentUri(new JobInfo.TriggerContentUri(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
    
                    JobInfo jobInfo = jobInfoBuilder.build();
                    if (mJobScheduler != null) {
                        int result = mJobScheduler.schedule(jobInfo);
                        if (result == JobScheduler.RESULT_SUCCESS) {
                            Log.d("yuan", "CameraUploadsService: Job scheduled SUCCESS");
                        } else {
                            Log.d("yuan", "CameraUploadsService: Job scheduled FAILED");
                        }
                    }
                }
            }
        });
    }
    
    // Cancel this job, if currently scheduled.
    private void cancelJob() {
        JobScheduler js = getSystemService(JobScheduler.class);
        js.cancelAll();
    }
    
    // Check whether this job is currently scheduled.
    private boolean isScheduled() {
        JobScheduler js = getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs == null) {
            return false;
        }
        for (int i = 0;i < jobs.size();i++) {
            if (jobs.get(i).getId() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
