package com.exarlabs.timescheduler.business.dagger.modules;

import android.content.Context;

import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.business.dagger.AppScope;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final TimeSchedulerApplication mApplication;

    public AppModule(TimeSchedulerApplication application) {
        mApplication = application;
    }

    @AppScope
    @Provides
    TimeSchedulerApplication provideApplication() {
        return mApplication;
    }

    @AppScope
    @Provides
    Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

}
