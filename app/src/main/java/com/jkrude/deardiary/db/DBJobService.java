package com.jkrude.deardiary.db;

import android.app.job.JobParameters;

public class DBJobService extends android.app.job.JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
