package com.example.tryall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;

import java.util.Random;

public class SecondActivity  extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    boolean isGgPriority = true;
    public static InterstitialAd ggInterstitialAd;
    public static com.facebook.ads.InterstitialAd fbInterstitialAd;
    NavigationView navigationView;
    BroadcastReceiver receiverExitFromRateApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initView();

        initInterstitialAd();
        //View headerView = navigationView.getHeaderView(0);
        //Ads.initNativeGg((LinearLayout) headerView.findViewById(R.id.lnNative), this, true, true);
        Ads.initNativeGg((LinearLayout) findViewById(R.id.lnNative), this, false, true);
        Ads.initBanner((LinearLayout) findViewById(R.id.lnNative), this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*receiverExitFromRateApp = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        IntentFilter i2 = new IntentFilter("exitFromRateApp");
        registerReceiver(receiverExitFromRateApp, i2);*/
    }

    private void initInterstitialAd() {
        //Chia tỉ lệ 70/30 giữa GG và FB
        int percent = new Random().nextInt(100);
        if (percent <= 70) {
            isGgPriority = true;
            Log.e("123", "initInterstitialAd: " + "gg");
            initGgInterstitialAd();
        } else {
            isGgPriority = false;
            Log.e("123", "initInterstitialAd: " + "fb");
            initFbInterstitialAd();
        }
        //Dành cho game
        //initUnityInterstitialAd();
    }

    private void initGgInterstitialAd() {
        try {
            //Inter ads : Quảng cáo xen kẽ
            ggInterstitialAd = new InterstitialAd(this);
            //Truyền ID
            ggInterstitialAd.setAdUnitId(getString(R.string.INTER_G));
            // xử lí action

            ggInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //loadAds
                    requestNewInterstitial();
                    Log.e("123", "onAdClosed: Load New ADS" );
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e("123", "onAdLoaded: " );
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    //Fail thì load FB
                    /*try {
                        if (isGgPriority) initFbInterstitialAd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    Log.e("123", "onAdFailedToLoad: Load FB ADS" );
                }

                @Override
                public void onAdOpened() {
                    Log.e("123", "onAdOpened: " );
                    super.onAdOpened();
                    try {
                        //Dành cho example Game
                        //Constant.pushEventFirebase(MainActivity.this, Constant.ADS_VIEW_INTER_GG, Constant.POSITION, MainActivity.this.getClass().getSimpleName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //LoadAds
            requestNewInterstitial();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNewInterstitial() {
        try {
            //Nếu không có gỡ ứng ụng .
            if (!MySetting.isRemoveAds(this)) {
                AdRequest adRequest = null;
                //Check Yêu cầu sự đồng ý của người dùng ở Châu Âu
                if (ConsentInformation.getInstance(this).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                        !ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown()) {
                    adRequest = new AdRequest.Builder().build();
                } else {
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, Ads.getNonPersonalizedAdsBundle())
                            .build();
                }
                //Load Ads
                ggInterstitialAd.loadAd(adRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFbInterstitialAd() {
        try {
            int percentInterIn = 30;
            try {
                //Lấy và gán giá trị tỉ lệ
                //percentInterIn = Integer.parseInt(Ads.getValueConfig(Ads.getJsonConfig(this, Ads.CONFIG_I_N_I_O), "percent_1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Chia tỉ lệ show giữa 2 fb
            if (new Random().nextInt(100) < percentInterIn) {
                fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.INTER_FB));
            } else {
                fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.INTER_FB_O));
            }
            //Xử lí action
            fbInterstitialAd.setAdListener(new InterstitialAdListener() {

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    try {
                        //Constant.pushEventFirebase(MainActivity.this, Constant.ADS_VIEW_INTER_FB, Constant.POSITION, MainActivity.this.getClass().getSimpleName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("123", "onInterstitialDisplayed: " );
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    //Load lại ads
                    Log.e("123", "onInterstitialDismissed: " );
                    requestNewFBInterstitial();
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e("123", "onError: load GG Inter" );
                    if (!isGgPriority) initGgInterstitialAd();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e("123", "onAdLoaded: " );
                    fbInterstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e("123", "onAdClicked: " );
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("123", "onLoggingImpression: " );
                }
            });
            //Check remove và load lại Ads
            requestNewFBInterstitial();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestNewFBInterstitial() {
        try {
            if (!MySetting.isRemoveAds(this)) {
                fbInterstitialAd.loadAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_setting){
            ggInterstitialAd.show();
        }
        if(id==R.id.nav_tutorial){
            if(fbInterstitialAd!=null&&fbInterstitialAd.isAdLoaded()){
                fbInterstitialAd.show();
            }
        }
        if (id == R.id.nav_policy) {
            //startActivity(new Intent(SecondActivity.this, PolicyActivity.class));
            /*if (!SecondActivity.showInterstitial()) {
                if (UnityAds.isInitialized() && UnityAds.isReady(getString(R.string.INTER_UNI))) {
                    UnityAds.show(SecondActivity.this, getString(R.string.INTER_UNI));
                }
            }*/
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
