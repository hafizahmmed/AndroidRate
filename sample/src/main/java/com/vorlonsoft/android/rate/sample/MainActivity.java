/*
 * Copyright 2017 - 2018 Vorlonsoft LLC
 *
 * Licensed under The MIT License (MIT)
 */

package com.vorlonsoft.android.rate.sample;

import android.app.Activity;
/* uncomment to test other locales - start */
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.os.Build;
/* uncomment to test other locales - end */

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.vorlonsoft.android.rate.AppRate;
import com.vorlonsoft.android.rate.StoreType;

/* uncomment to test other locales */
//import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = "ANDROIDRATE_SAMPLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* uncomment to test other locales - start */
        //if ((Build.VERSION.SDK_INT >= 17)&&(Build.VERSION.SDK_INT < 25)) {
        //    String mLang = "fr";    // change to your test language
        //    String mCountry = "FR"; // change to your test country
        //    Configuration mConfig;
        //    Locale mLocale;
        //
        //    if (mCountry.hashCode() == "".hashCode()) {
        //        mLocale = new Locale(mLang);
        //    } else {
        //        mLocale = new Locale(mLang, mCountry);
        //    }
        //    Locale.setDefault(mLocale);
        //
        //    mConfig = getBaseContext().getResources().getConfiguration();
        //    mConfig.setLocale(mLocale);
        //    mConfig.setLayoutDirection(mLocale);
        //
        //    Resources resources = getBaseContext().getResources();
        //    resources.updateConfiguration(mConfig, resources.getDisplayMetrics());
        //}
        /* uncomment to test other locales - end */

        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY) /* default GOOGLEPLAY (Google Play), other options are AMAZON (Amazon Appstore), BAZAAR (Cafe Bazaar),
                                                     *         CHINESESTORES (19 chinese app stores), MI (Mi Appstore (Xiaomi Market)), SAMSUNG (Samsung Galaxy Apps),
                                                     *         SLIDEME (SlideME Marketplace), TENCENT (Tencent App Store), YANDEX (Yandex.Store),
                                                     *         setStoreType(BLACKBERRY, long) (BlackBerry World, long - your application ID),
                                                     *         setStoreType(APPLE, long) (Apple App Store, long - your application ID) and
                                                     *         setStoreType(String) (Any other store, String - a full URI to your app) */
                .setInstallDays((byte) 3)           // default 10, 0 means install day.
                .setLaunchTimes((byte) 10)          // default 10 times.
                .setRemindInterval((byte) 2)        // default 1 day.
                .setRemindLaunchTimes((byte) 4)     // default 1 (each launch).
                .setShowLaterButton(true)           // default true.
                .setDebug(true)                     // default false.
                .setCancelable(false)               // default false.
                .setOnClickButtonListener(which -> Log.d(TAG, "RateButton: " + Byte.toString(which)))
                /* comment to use library strings instead app strings - start */
                .setTitle(R.string.new_rate_dialog_title)
                .setTextLater(R.string.new_rate_dialog_later)
                /* comment to use library strings instead app strings - end */
                /* uncomment to use app string instead library string */
                //.setMessage(R.string.new_rate_dialog_message)
                /* comment to use library strings instead app strings - start */
                .setTextNever(R.string.new_rate_dialog_never)
                .setTextRateNow(R.string.new_rate_dialog_ok)
                /* comment to use library strings instead app strings - end */
                .monitor();

        if (AppRate.with(this).getStoreType() == StoreType.GOOGLEPLAY) {
            //Check that Google Play is available
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SERVICE_MISSING) {
                // Show a dialog if meets conditions
                AppRate.showRateDialogIfMeetsConditions(this);
            }
        } else {
            // Show a dialog if meets conditions
            AppRate.showRateDialogIfMeetsConditions(this);
        }
    }
}