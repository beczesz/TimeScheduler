package com.exarlabs.timescheduler;

import android.app.Application;

import com.exarlabs.timescheduler.business.dagger.components.AppComponent;
import com.exarlabs.timescheduler.business.dagger.components.DaggerComponentGraph;
import com.exarlabs.timescheduler.business.utils.logging.TimberPlanter;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;

/**
 * Application base class implementation
 */
public class TimeSchedulerApplication extends Application {

    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------


    private static TimeSchedulerApplication sInstance;

    public static final String TAG = TimeSchedulerApplication.class.getSimpleName();

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------


    public static TimeSchedulerApplication getInstance() {
        return sInstance;
    }

    private static void setInstance(TimeSchedulerApplication instance) {
        sInstance = instance;
    }

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    /**
     * Dagger component graph
     */
    protected DaggerComponentGraph graph;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();

        setInstance(this);
        initLibs();
    }

    private void initLibs() {
        TimberPlanter.replant(this);

        // Initialize dagger
        buildComponentAndInject(this);

        // Register the font icons
        Iconics.registerFont(new FontAwesome());

    }

    /**
     * Rebuilds the dagger generated object graph
     */
    protected void buildComponentAndInject(TimeSchedulerApplication app) {
        graph = AppComponent.Initializer.init(app);
        graph.inject(app);
    }


    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------

    /**
     * @return the Dagger generate graph
     */
    public static DaggerComponentGraph component() {
        return getInstance().graph;
    }
}
