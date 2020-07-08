package com.exarlabs.timescheduler;

import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.widget.Toast;

import com.exarlabs.timescheduler.business.RxManager;
import com.exarlabs.timescheduler.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {


    // ------------------------------------------------------------------------
    // TYPES
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // STATIC METHODS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // METHODS
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxManager.executeLaterOnMain(3, TimeUnit.SECONDS, aLong -> {
            Toast.makeText(this, "Heureka", Toast.LENGTH_SHORT).show();
        });
    }

    // ------------------------------------------------------------------------
    // GETTERS / SETTERS
    // ------------------------------------------------------------------------
}
