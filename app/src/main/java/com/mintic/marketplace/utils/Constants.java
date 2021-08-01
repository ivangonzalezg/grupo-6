package com.mintic.marketplace.utils;

/**
 * Created by Iván González on 31/07/21.
 */

public class Constants {

    private Constants() {
        throw new UnsupportedOperationException(
                "Should not create instance of Constants class. Please use as static.");
    }

    // Shared preferences
    public static final String userId = "user_id";
    public static final String firstName = "first_name";
    public static final String lastName = "last_name";
    public static final String email = "email";
    public static final String isUserLoggedIn = "is_user_logged_in";

    // Firebase firestore
    public static final String users = "users";
}
