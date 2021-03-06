/*
 * Copyright 2017 - 2018 Vorlonsoft LLC
 *
 * Licensed under The MIT License (MIT)
 */

package com.vorlonsoft.android.rate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.vorlonsoft.android.rate.PreferenceHelper.get365DayPeriodDialogLaunchTimes;
import static com.vorlonsoft.android.rate.PreferenceHelper.getCustomEventCount;
import static com.vorlonsoft.android.rate.PreferenceHelper.getDialogFirstLaunchTime;
import static com.vorlonsoft.android.rate.PreferenceHelper.getInstallDate;
import static com.vorlonsoft.android.rate.PreferenceHelper.getIsAgreeShowDialog;
import static com.vorlonsoft.android.rate.PreferenceHelper.getLaunchTimes;
import static com.vorlonsoft.android.rate.PreferenceHelper.getRemindInterval;
import static com.vorlonsoft.android.rate.PreferenceHelper.increment365DayPeriodDialogLaunchTimes;
import static com.vorlonsoft.android.rate.PreferenceHelper.isFirstLaunch;
import static com.vorlonsoft.android.rate.PreferenceHelper.setCustomEventCount;
import static com.vorlonsoft.android.rate.PreferenceHelper.setDialogFirstLaunchTime;
import static com.vorlonsoft.android.rate.PreferenceHelper.setFirstLaunchSharedPreferences;
import static com.vorlonsoft.android.rate.PreferenceHelper.setIsAgreeShowDialog;
import static com.vorlonsoft.android.rate.StoreType.AMAZON;
import static com.vorlonsoft.android.rate.StoreType.APPLE;
import static com.vorlonsoft.android.rate.StoreType.BLACKBERRY;
import static com.vorlonsoft.android.rate.StoreType.INTENT;
import static com.vorlonsoft.android.rate.StoreType.OTHER;
import static com.vorlonsoft.android.rate.StoreType.YANDEX;
import static com.vorlonsoft.android.rate.Utils.DAY_IN_MILLIS;
import static com.vorlonsoft.android.rate.Utils.TAG;

public final class AppRate {

    @SuppressLint("StaticFieldLeak")
    private static volatile AppRate singleton = null;
    private final Map<String, Short> customEventsCounts;
    private final Context context;
    private final DialogOptions dialogOptions = new DialogOptions();
    private final StoreOptions storeOptions = new StoreOptions();
    private boolean isDebug = false;
    private byte installDate = (byte) 10;
    private byte appLaunchTimes = (byte) 10;
    private byte remindInterval = (byte) 1;
    private byte remindLaunchTimes = (byte) 1;
    /**
     * Short.MAX_VALUE - unlimited occurrences of the display of the dialog within a 365-day period
     */
    private short dialogLaunchTimes = Short.MAX_VALUE;
    private DialogManager.Factory dialogManagerFactory = new DefaultDialogManager.Factory();

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            customEventsCounts = new ArrayMap<>();
        } else {
            customEventsCounts = new HashMap<>();
        }
    }

    private AppRate(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AppRate with(Context context) {
        if (singleton == null) {
            synchronized (AppRate.class) {
                if (singleton == null) {
                    singleton = new AppRate(context);
                }
            }
        }
        return singleton;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean showRateDialogIfMeetsConditions(Activity activity) {
        boolean isMeetsConditions = (singleton.isDebug() || singleton.shouldShowRateDialog());
        if (isMeetsConditions) {
            singleton.showRateDialog(activity);
        }
        return isMeetsConditions;
    }

    private static boolean isOverDate(long targetDate, byte threshold) {
        return new Date().getTime() - targetDate >= threshold * DAY_IN_MILLIS;
    }

    private boolean isBelow365DayPeriodMaxNumberDialogLaunchTimes() {
        return ((dialogLaunchTimes == Short.MAX_VALUE) || (get365DayPeriodDialogLaunchTimes(context) < dialogLaunchTimes));
    }

    /**
     * Set Short.MAX_VALUE for unlimited occurrences of the display of the dialog within a 365-day period
     */
    @SuppressWarnings({"unused"})
    public AppRate set365DayPeriodMaxNumberDialogLaunchTimes(short dialogLaunchTimes) {
        this.dialogLaunchTimes = dialogLaunchTimes;
        return this;
    }

    public AppRate setLaunchTimes(@SuppressWarnings("SameParameterValue") byte appLaunchTimes) {
        this.appLaunchTimes = appLaunchTimes;
        return this;
    }

    public AppRate setInstallDays(@SuppressWarnings("SameParameterValue") byte installDate) {
        this.installDate = installDate;
        return this;
    }

    public AppRate setRemindInterval(@SuppressWarnings("SameParameterValue") byte remindInterval) {
        this.remindInterval = remindInterval;
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setMinimumEventCount(String eventName, short minimumCount) {
        this.customEventsCounts.put(eventName, minimumCount);
        return this;
    }

    public AppRate setRemindLaunchTimes(@SuppressWarnings("SameParameterValue") byte remindLaunchTimes) {
        this.remindLaunchTimes = remindLaunchTimes;
        return this;
    }

    public AppRate setShowLaterButton(@SuppressWarnings("SameParameterValue") boolean isShowNeutralButton) {
        dialogOptions.setShowNeutralButton(isShowNeutralButton);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setShowNeverButton(boolean isShowNeverButton) {
        dialogOptions.setShowNegativeButton(isShowNeverButton);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setShowTitle(boolean isShowTitle) {
        dialogOptions.setShowTitle(isShowTitle);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate clearAgreeShowDialog() {
        setIsAgreeShowDialog(context, true);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate clearSettingsParam() {
        PreferenceHelper.clearSharedPreferences(context);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setAgreeShowDialog(boolean isAgree) {
        setIsAgreeShowDialog(context, isAgree);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setView(View view) {
        dialogOptions.setView(view);
        return this;
    }

    public AppRate setOnClickButtonListener(OnClickButtonListener listener) {
        dialogOptions.setListener(listener);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTitle(@SuppressWarnings("SameParameterValue") int resourceId) {
        dialogOptions.setTitleResId(resourceId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTitle(String title) {
        dialogOptions.setTitleText(title);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setMessage(int resourceId) {
        dialogOptions.setMessageResId(resourceId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setMessage(String message) {
        dialogOptions.setMessageText(message);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextRateNow(@SuppressWarnings("SameParameterValue") int resourceId) {
        dialogOptions.setTextPositiveResId(resourceId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextRateNow(String positiveText) {
        dialogOptions.setPositiveText(positiveText);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextLater(@SuppressWarnings("SameParameterValue") int resourceId) {
        dialogOptions.setTextNeutralResId(resourceId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextLater(String neutralText) {
        dialogOptions.setNeutralText(neutralText);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextNever(@SuppressWarnings("SameParameterValue") int resourceId) {
        dialogOptions.setTextNegativeResId(resourceId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setTextNever(String negativeText) {
        dialogOptions.setNegativeText(negativeText);
        return this;
    }

    public AppRate setCancelable(@SuppressWarnings("SameParameterValue") boolean cancelable) {
        dialogOptions.setCancelable(cancelable);
        return this;
    }

    public AppRate setStoreType(@StoreType.StoreWithoutApplicationId final int storeType) {
        if ((storeType == APPLE) || (storeType == BLACKBERRY)) {
            throw new IllegalArgumentException("For StoreType.APPLE/StoreType.BLACKBERRY you must use setStoreType(StoreType.APPLE/StoreType.BLACKBERRY, long applicationId)");
        } else if ((storeType < AMAZON) || (storeType > YANDEX)) {
            throw new IllegalArgumentException("StoreType must be one of: AMAZON, APPLE, BAZAAR, BLACKBERRY, CHINESESTORES, GOOGLEPLAY, MI, SAMSUNG, SLIDEME, TENCENT, YANDEX");
        }
        return setStoreType(storeType, null, null);
    }

    @SuppressWarnings("unused")
    public AppRate setStoreType(@StoreType.StoreWithApplicationId final int storeType, final long applicationId) {
        if ((storeType < AMAZON) || (storeType > YANDEX)) {
            throw new IllegalArgumentException("StoreType must be one of: AMAZON, APPLE, BAZAAR, BLACKBERRY, CHINESESTORES, GOOGLEPLAY, MI, SAMSUNG, SLIDEME, TENCENT, YANDEX");
        }
        return ((storeType != APPLE) && (storeType != BLACKBERRY)) ? setStoreType(storeType, null, null) : setStoreType(storeType, new String[]{String.valueOf(applicationId)}, null);
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess", "unused"})
    public AppRate setStoreType(@NonNull final String... uris) {
        if (uris == null) {
            throw new IllegalArgumentException("setStoreType(String... uris): 'uris' must be != null");
        }
        return setStoreType(OTHER, uris, null);
    }

    private AppRate setStoreType(final int storeType, final String[] stringParam, final Intent[] intentParaam) {
        storeOptions.setStoreType(storeType, stringParam, intentParaam);
        return this;
    }

    @StoreType.AnyStoreType
    public int getStoreType() {
        return storeOptions.getStoreType();
    }

    @SuppressWarnings({"ConstantConditions", "WeakerAccess", "unused"})
    public AppRate setStoreType(@NonNull final Intent... intents) {
        if (intents == null) {
            throw new IllegalArgumentException("setStoreType(Intent... intents): 'intents' must be != null");
        }
        return setStoreType(INTENT, null, intents);
    }

    @SuppressWarnings("unused")
    public AppRate incrementEventCount(String eventName) {
        return setEventCountValue(eventName, (short) (getCustomEventCount(context, eventName) + 1));
    }

    @SuppressWarnings("WeakerAccess")
    public AppRate setEventCountValue(String eventName, short countValue) {
        setCustomEventCount(context, eventName, countValue);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setThemeResId(int themeResId) {
        dialogOptions.setThemeResId(themeResId);
        return this;
    }

    @SuppressWarnings("unused")
    public AppRate setDialogManagerFactory(DialogManager.Factory dialogManagerFactory) {
        this.dialogManagerFactory = dialogManagerFactory;
        return this;
    }

    public void monitor() {
        if (isFirstLaunch(context)) {
            setFirstLaunchSharedPreferences(context);
        } else {
            PreferenceHelper.setLaunchTimes(context, (short) (getLaunchTimes(context) + 1));
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void showRateDialog(Activity activity) {
        if (!activity.isFinishing()) {
            Dialog dialog = dialogManagerFactory.createDialogManager(activity, dialogOptions, storeOptions).createDialog();
            if (dialog != null) {
                if (getDialogFirstLaunchTime(context) == 0L) {
                    setDialogFirstLaunchTime(context);
                }
                increment365DayPeriodDialogLaunchTimes(context);
                dialog.show();
            } else {
                Log.w(TAG, "Failed to rate app, can't create rate dialog");
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean shouldShowRateDialog() {
        return getIsAgreeShowDialog(context) &&
                isOverLaunchTimes() &&
                isOverRemindLaunchTimes() &&
                isOverInstallDate() &&
                isOverRemindDate() &&
                isOverCustomEventsRequirements() &&
                isBelow365DayPeriodMaxNumberDialogLaunchTimes();
    }

    private boolean isOverLaunchTimes() {
        return ((appLaunchTimes == 0) || (getLaunchTimes(context) >= appLaunchTimes));
    }

    private boolean isOverRemindLaunchTimes() {
        return ((remindLaunchTimes == 1) || ((remindLaunchTimes != 0) && ((getLaunchTimes(context) % remindLaunchTimes) == 0)));
    }

    private boolean isOverInstallDate() {
        return ((installDate == 0) || isOverDate(getInstallDate(context), installDate));
    }

    private boolean isOverRemindDate() {
        return ((remindInterval == 0) || isOverDate(getRemindInterval(context), remindInterval));
    }

    private boolean isOverCustomEventsRequirements() {
        if (customEventsCounts.isEmpty()) {
            return true;
        } else {
            Short currentCount;
            for (Map.Entry<String, Short> eventRequirement : customEventsCounts.entrySet()) {
                currentCount = getCustomEventCount(context, eventRequirement.getKey());
                if (currentCount < eventRequirement.getValue()) {
                    return false;
                }
            }
            return true;
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public boolean isDebug() {
        return isDebug;
    }

    public AppRate setDebug(@SuppressWarnings("SameParameterValue") boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

}
