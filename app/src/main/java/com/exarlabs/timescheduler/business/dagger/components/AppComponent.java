package com.exarlabs.timescheduler.business.dagger.components;

import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.business.dagger.AppScope;
import com.exarlabs.timescheduler.business.dagger.modules.AppModule;
import com.exarlabs.timescheduler.business.dagger.modules.DataModule;

import dagger.Component;

@AppScope
@Component (modules = { AppModule.class, DataModule.class, })
public interface AppComponent extends DaggerComponentGraph {

    final class Initializer {

        public static AppComponent init(TimeSchedulerApplication app) {

            //@formatter:off
            return DaggerAppComponent.builder()
                            .appModule(new AppModule(app))
                            .dataModule(new DataModule())
                            .build();
            //@formatter:on
        }

    }
}
