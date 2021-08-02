package com.mintic.marketplace.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Iván González on 2/08/21.
 */

public class Keyboard {
    private static final String TAG = "Keyboard";

    public static void hideSoft(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}
