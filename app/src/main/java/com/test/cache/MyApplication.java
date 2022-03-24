package com.test.cache;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static MyApplication mApplication = null;

    public static Context getContext() {
        return mApplication;
    }

    @TargetApi(28)
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }
}
