package com.daniel.friendcompass.util;

import android.text.TextUtils;
import android.util.Patterns;

public class VerifyUtil {
    public static boolean verifyEmail(String email) {
        return !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
