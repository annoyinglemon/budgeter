package com.lemond.kurt.budgeter;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by kurt_capatan on 8/18/2016.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Comfortaa-Light.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}