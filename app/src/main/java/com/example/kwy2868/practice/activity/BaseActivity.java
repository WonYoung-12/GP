package com.example.kwy2868.practice.activity;

import android.app.Activity;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends Activity{

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
