package com.twine.arca_app;

import com.activeandroid.ActiveAndroid;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

public class myAplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
