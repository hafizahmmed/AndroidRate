/*
 * Copyright 2017 - 2018 Vorlonsoft LLC
 *
 * Licensed under The MIT License (MIT)
 */

package com.vorlonsoft.android.rate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static com.vorlonsoft.android.rate.UriHelper.getGooglePlay;
import static com.vorlonsoft.android.rate.UriHelper.getGooglePlayWeb;
import static com.vorlonsoft.android.rate.UriHelper.getAmazonAppstore;
import static com.vorlonsoft.android.rate.UriHelper.getAmazonAppstoreWeb;
import static com.vorlonsoft.android.rate.UriHelper.getSamsungGalaxyApps;
import static com.vorlonsoft.android.rate.UriHelper.getSamsungGalaxyAppsWeb;
import static com.vorlonsoft.android.rate.UriHelper.getTencentAppStore;
import static com.vorlonsoft.android.rate.UriHelper.getMiAppstore;
import static com.vorlonsoft.android.rate.UriHelper.isPackageExists;

final class IntentHelper {

    static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
    static final String AMAZON_APPSTORE_PACKAGE_NAME = "com.amazon.venezia";
    static final String SAMSUNG_GALAXY_APPS_PACKAGE_NAME = "com.sec.android.app.samsungapps";

    private IntentHelper() {
    }

    static Intent createIntentForGooglePlay(Context context) {
        Intent intent;
        String packageName = context.getPackageName();
        if (isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME)) {
            intent = new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName));
            // Make sure it DOESN'T open in the stack of packageName activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Task reparenting if needed
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            // If the Google Play was already open in a search result
            // this make sure it still go to the app page you requested
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, getGooglePlayWeb(packageName));
        }
        return intent;
    }

    static Intent createIntentForAmazonAppstore(Context context) {
        Intent intent;
        String packageName = context.getPackageName();
        if (isPackageExists(context, AMAZON_APPSTORE_PACKAGE_NAME)) {
            intent = new Intent(Intent.ACTION_VIEW, getAmazonAppstore(packageName));
            // Make sure it DOESN'T open in the stack of packageName activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Task reparenting if needed
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            // If the Amazon Appstore was already open in a search result
            // this make sure it still go to the app page you requested
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setPackage(AMAZON_APPSTORE_PACKAGE_NAME);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, getAmazonAppstoreWeb(packageName));
        }
        return intent;
    }

    static Intent createIntentForSamsungGalaxyApps(Context context) {
        Intent intent;
        String packageName = context.getPackageName();
        if (isPackageExists(context, SAMSUNG_GALAXY_APPS_PACKAGE_NAME)) {
            intent = new Intent(Intent.ACTION_VIEW, getSamsungGalaxyApps(packageName));
            // Make sure it DOESN'T open in the stack of packageName activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Task reparenting if needed
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            // If the Samsung Galaxy Apps was already open in a search result
            // this make sure it still go to the app page you requested
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setPackage(SAMSUNG_GALAXY_APPS_PACKAGE_NAME);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, getSamsungGalaxyAppsWeb(packageName));
        }
        return intent;
    }

    static Intent createIntentForTencentAppStore(Context context) {
        String packageName = context.getPackageName();
        return new Intent(Intent.ACTION_VIEW, getTencentAppStore(packageName));
    }

    static Intent createIntentForMiAppstore(Context context) {
        String packageName = context.getPackageName();
        return new Intent(Intent.ACTION_VIEW, getMiAppstore(packageName));
    }

    static Intent createIntentForOther(Uri uri) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }

}
