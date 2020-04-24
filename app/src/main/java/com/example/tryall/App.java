package com.example.tryall;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class App extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            AudienceNetworkAds.initialize(this);
        }
}
