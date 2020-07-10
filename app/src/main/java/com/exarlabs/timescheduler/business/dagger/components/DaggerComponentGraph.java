package com.exarlabs.timescheduler.business.dagger.components;

import android.content.Context;

import com.exarlabs.timescheduler.MainActivity;
import com.exarlabs.timescheduler.TimeSchedulerApplication;
import com.exarlabs.timescheduler.business.EventManager;

/**
 * Here are listed all the locations where injection is needed.
 * Created by becze on 9/17/2015.
 */
public interface DaggerComponentGraph {

    TimeSchedulerApplication application();

    Context context();

    void inject(MainActivity app);

    void inject(TimeSchedulerApplication app);

    void inject(EventManager eventManager);
}
