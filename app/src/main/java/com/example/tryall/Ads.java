package com.example.tryall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ads {

    /*public static String getJsonConfig(Context context, String keyConfig) {
        String json = TransConfigs.getInstance().getConfig().getString(keyConfig);
        if (json.equals("false")) {
            return "";
        } else {
            return json;
        }
    }*/

    public static Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return extras;
    }
    //Tuyền vào View , context ,...
    public static void initNativeGg(final LinearLayout lnNative, final Activity activity, final boolean isSmall, final boolean isBackfill) {
        try {
            //Kiểm tra Internet
            if (Helper.isConnectedInternet(activity)) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//gọi inflater từ context
                View view = null;
                if (isSmall) view = inflater.inflate(R.layout.layout_ad_banner, null);//Size nhỏ
                else view = inflater.inflate(R.layout.layout_ad_native_large, null);//Size to
                lnNative.removeAllViews();//Xóa dữ liệu của view truyền vào
                lnNative.addView(view);//Set dữ liệu mới
                lnNative.setVisibility(View.VISIBLE);
            }else lnNative.setVisibility(View.GONE); //Mất mạng thì ẩn luôn
            //Nếu chưa Remove Ads
            if (!MySetting.isRemoveAds(activity)) {
                //Todo Khởi tạo và truyền id Ads
                AdLoader.Builder builder = null;
                builder = new AdLoader.Builder(activity, activity.getString(R.string.NATIVE_L_G));

                builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        try {
                            //Todo Gắn quảng cáo vào View

                            UnifiedNativeAdView adView;
                            adView = (UnifiedNativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.native_gg, null);

                            populateAppInstallAdView(unifiedNativeAd, adView, isSmall);
                            lnNative.removeAllViews();
                            lnNative.addView(adView);
                            lnNative.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //setup video
                VideoOptions videoOptions = new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build();
                //Set video
                NativeAdOptions adOptions = new NativeAdOptions.Builder()
                        .setVideoOptions(videoOptions)
                        .build();

                builder.withNativeAdOptions(adOptions);

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        try {
                            //Constant.pushEventFirebase(activity, Constant.ADS_VIEW_NATIVE_GG, Constant.POSITION, activity.getClass().getSimpleName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        //Loadfail
                        lnNative.setVisibility(View.GONE);
                        if (!isBackfill) {
                            if (isSmall) {
                                //Mở baner
                                initNativeBannerFB(lnNative, activity, isSmall, true);
                            } else {
                                //Mở native
                                initNativeFB(lnNative, activity, isSmall, true);
                            }
                        }
                    }
                }).build();
                // Check người châu Âu
                AdRequest adRequest = null;
                if (ConsentInformation.getInstance(activity).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                        !ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown()) {
                    adRequest = new AdRequest.Builder().addTestDevice("36ECCE4B775EB700983E06DAB2A90F99").build();
                } else {
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                            .build();
                }
                //Load ads
                adLoader.loadAd(adRequest);
            } else lnNative.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initNativeBannerFB(final LinearLayout lnNative, final Activity context, final boolean isSmall, final boolean isBackfill) {
        try {
            if (Helper.isConnectedInternet(context)){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.layout_ad_banner, null);
                lnNative.removeAllViews();
                lnNative.addView(view);
            }else lnNative.setVisibility(View.GONE);
            if (!MySetting.isRemoveAds(context)) {
                final com.facebook.ads.NativeBannerAd nativeAd = new com.facebook.ads.NativeBannerAd(context, context.getString(R.string.NATIVE_BANNER_FB));

                nativeAd.setAdListener(new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        lnNative.setVisibility(View.GONE);
                        if (!isBackfill) initNativeGg(lnNative, context, isSmall, true);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        try {
                            LayoutInflater inflater = LayoutInflater.from(context);
                            final LinearLayout lnContentAd = (LinearLayout) inflater.inflate(R.layout.native_fb_banner, lnNative, false);

                            MediaView nativeAdIcon = lnContentAd.findViewById(R.id.native_ad_icon);
                            TextView nativeAdTitle = (TextView) lnContentAd.findViewById(R.id.native_ad_title);
                            TextView nativeAdSocialContext = (TextView) lnContentAd.findViewById(R.id.native_ad_social_context);
                            Button nativeAdCallToAction = (Button) lnContentAd.findViewById(R.id.native_ad_call_to_action);

                            TextView sponsoredLabel = lnContentAd.findViewById(R.id.native_ad_sponsored_label);
                            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                            nativeAdTitle.setText(nativeAd.getAdvertiserName());
                            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                            LinearLayout adChoicesContainer = (LinearLayout) lnContentAd.findViewById(R.id.ad_choices_container);
                            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, null);
                            adChoicesContainer.removeAllViews();
                            adChoicesContainer.addView(adOptionsView, 0);

                            List<View> clickableViews = new ArrayList<>();
                            clickableViews.add(nativeAdCallToAction);
                            nativeAdCallToAction.setEnabled(false);
                            int percentClick = 30;
                            try {
                                //percentClick = Integer.parseInt(getValueConfig(getJsonConfig(context, CONFIG_NA_CL_S_L), "percent_1"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (new Random().nextInt(100) < percentClick) {
                                nativeAdCallToAction.setEnabled(true);
                            }
                            nativeAd.registerViewForInteraction(lnContentAd, nativeAdIcon, clickableViews);
                            lnNative.removeAllViews();
                            lnNative.addView(lnContentAd);
                            lnNative.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        try {
                            //Constant.pushEventFirebase(context, Constant.ADS_VIEW_NATIVE_FB, Constant.POSITION, context.getClass().getSimpleName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                nativeAd.loadAd();
            } else lnNative.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initNativeFB(final LinearLayout lnNative, final Activity context, final boolean isSmall, final boolean isBackfill) {
        try {
            if (Helper.isConnectedInternet(context)){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.layout_ad_native_large, null);
                lnNative.removeAllViews();
                lnNative.addView(view);
            }else lnNative.setVisibility(View.GONE);
            if (!MySetting.isRemoveAds(context)) {
                int percentNativeIn = 30;
                try {
                    //percentNativeIn = Integer.parseInt(getValueConfig(getJsonConfig(context, CONFIG_I_N_I_O), "percent_3"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final com.facebook.ads.NativeAd nativeAd;
                if (new Random().nextInt(100) < percentNativeIn) {
                    nativeAd = new com.facebook.ads.NativeAd(context, context.getString(R.string.NATIVE_L_FB));
                } else {
                    nativeAd = new com.facebook.ads.NativeAd(context, context.getString(R.string.NATIVE_L_FB_O));
                }

                nativeAd.setAdListener(new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        lnNative.setVisibility(View.GONE);
                        if (!isBackfill) initNativeGg(lnNative, context, isSmall, true);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        try {
                            LayoutInflater inflater = LayoutInflater.from(context);
                            final LinearLayout lnContentAd = (LinearLayout) inflater.inflate(R.layout.native_fb_large, lnNative, false);

                            MediaView nativeAdIcon = lnContentAd.findViewById(R.id.native_ad_icon);
                            TextView nativeAdTitle = (TextView) lnContentAd.findViewById(R.id.native_ad_title);
                            MediaView nativeAdMedia = (MediaView) lnContentAd.findViewById(R.id.native_ad_media);
                            TextView nativeAdSocialContext = (TextView) lnContentAd.findViewById(R.id.native_ad_social_context);
                            Button nativeAdCallToAction = (Button) lnContentAd.findViewById(R.id.native_ad_call_to_action);
                            nativeAdTitle.setText(nativeAd.getAdvertiserName());
                            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                            LinearLayout adChoicesContainer = (LinearLayout) lnContentAd.findViewById(R.id.ad_choices_container);
                            AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, null);
                            adChoicesContainer.removeAllViews();
                            adChoicesContainer.addView(adOptionsView, 0);

                            List<View> clickableViews = new ArrayList<>();
                            clickableViews.add(nativeAdCallToAction);
                            nativeAdCallToAction.setEnabled(false);
                            int percentClick = 30;
                            /*try {
                                percentClick = Integer.parseInt(getValueConfig(getJsonConfig(context, CONFIG_NA_CL_S_L), "percent_2"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                            if (new Random().nextInt(100) < percentClick) {
                                nativeAdCallToAction.setEnabled(true);
                            }
                            nativeAd.registerViewForInteraction(lnContentAd, nativeAdMedia, nativeAdIcon, clickableViews);
                            lnNative.removeAllViews();
                            lnNative.addView(lnContentAd);
                            lnNative.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        try {
                            //Constant.pushEventFirebase(context, Constant.ADS_VIEW_NATIVE_FB, Constant.POSITION, context.getClass().getSimpleName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                nativeAd.loadAd();
            } else lnNative.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initBanner(final LinearLayout lnNative, final Activity activity) {
        try {
            if (Helper.isConnectedInternet(activity)){
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.layout_ad_banner, null);
                lnNative.removeAllViews();
                lnNative.addView(view);
            }else lnNative.setVisibility(View.GONE);
            if (!MySetting.isRemoveAds(activity)) {
                try {
                    final AdView adView = new AdView(activity);
                    adView.setAdSize(getAdSize(activity));
                    adView.setAdUnitId(activity.getString(R.string.BANNER_G));
                    AdRequest adRequest = null;
                    if (ConsentInformation.getInstance(activity).getConsentStatus().toString().equals(ConsentStatus.PERSONALIZED) ||
                            !ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown()) {
                        adRequest = new AdRequest.Builder().build();
                    } else {
                        adRequest = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                                .build();
                    }
                    adView.loadAd(adRequest);

                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            try {
                                super.onAdLoaded();
                                lnNative.removeAllViews();
                                lnNative.addView(adView);
                                lnNative.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            lnNative.setVisibility(View.GONE);
                            initBannerFB(lnNative, activity);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else lnNative.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AdSize getAdSize(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public static void initBannerFB(final LinearLayout lnNative, Activity activity) {
        try {
            if (Helper.isConnectedInternet(activity)){
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.layout_ad_banner, null);
                lnNative.removeAllViews();
                lnNative.addView(view);
            }else lnNative.setVisibility(View.GONE);
            if (!MySetting.isRemoveAds(activity)) {
                final com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, activity.getString(R.string.BANNER_FB), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                adView.setAdListener(new com.facebook.ads.AdListener() {
                    @Override
                    public void onError(Ad ad, AdError adError) {
                        lnNative.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        try {
                            lnNative.removeAllViews();
                            lnNative.addView(adView);
                            lnNative.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });
            } else lnNative.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void populateAppInstallAdView(UnifiedNativeAd nativeAppInstallAd,
                                                 UnifiedNativeAdView adView, boolean isSmall) {
        try {
            VideoController vc = nativeAppInstallAd.getVideoController();
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
            adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
            adView.setBodyView(adView.findViewById(R.id.appinstall_body));
            adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
            adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
            adView.setPriceView(adView.findViewById(R.id.appinstall_price));
            adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
            adView.setStoreView(adView.findViewById(R.id.appinstall_store));

            try {
                ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
                ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
                ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

                if (nativeAppInstallAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    adView.getIconView().setVisibility(View.VISIBLE);
                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            com.google.android.gms.ads.formats.MediaView mediaView = adView.findViewById(R.id.appinstall_media);
            adView.setImageView(adView.findViewById(R.id.appinstall_image));

            if (vc.hasVideoContent()) {
                try {
                    adView.setMediaView(mediaView);
                    adView.getImageView().setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mediaView.setVisibility(View.GONE);

                    // At least one image is guaranteed.
                    List<NativeAd.Image> images = nativeAppInstallAd.getImages();
                    if (images != null && images.size() > 0) {
                        ((ImageView) adView.getImageView()).setVisibility(View.VISIBLE);
                        ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
                    } else {
                        ((ImageView) adView.getImageView()).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isSmall) {
                try {
                    ((ImageView) adView.getImageView()).setVisibility(View.GONE);
                    mediaView.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                if (nativeAppInstallAd.getPrice() == null) {
                    adView.getPriceView().setVisibility(View.GONE);
                } else {
                    adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (nativeAppInstallAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.GONE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (nativeAppInstallAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.GONE);
                } else {
                    ((RatingBar) adView.getStarRatingView())
                            .setRating(nativeAppInstallAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adView.setNativeAd(nativeAppInstallAd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
