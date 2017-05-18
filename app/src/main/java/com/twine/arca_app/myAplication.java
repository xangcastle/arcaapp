package com.twine.arca_app;

import com.activeandroid.ActiveAndroid;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */

public class myAplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        Iconify.with(new FontAwesomeModule());
    }
}
