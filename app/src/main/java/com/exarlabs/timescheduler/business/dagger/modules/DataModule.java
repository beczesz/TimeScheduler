package com.exarlabs.timescheduler.business.dagger.modules;

import com.exarlabs.timescheduler.business.EventManager;
import com.exarlabs.timescheduler.business.SessionManager;
import com.exarlabs.timescheduler.business.dagger.AppScope;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {


    @AppScope
    @Provides
    SessionManager provideSessionManager() {
        return new SessionManager();
    }

    @AppScope
    @Provides
    EventManager provideEventManager() {
        return new EventManager();
    }

}
