/*
 * Copyright 2017 - 2018 Vorlonsoft LLC
 *
 * Licensed under The MIT License (MIT)
 */

package com.vorlonsoft.android.rate;

import android.app.Dialog;
import android.content.Context;

public interface DialogManager {

    Dialog createDialog();

    interface Factory {
        DialogManager createDialogManager(final Context context, final DialogOptions dialogOptions, final StoreOptions storeOptions);
    }
}