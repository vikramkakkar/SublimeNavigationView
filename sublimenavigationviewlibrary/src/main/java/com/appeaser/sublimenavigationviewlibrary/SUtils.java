package com.appeaser.sublimenavigationviewlibrary;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by Admin on 18/02/2016.
 */
public class SUtils {

    public final static boolean ON_JB_OR_HIGHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBackground(View v, Drawable drawable) {
        if (v != null) {
            int pLeft = v.getPaddingLeft(),
                    pTop = v.getPaddingTop(),
                    pRight = v.getPaddingRight(),
                    pBottom = v.getPaddingBottom();

            if (ON_JB_OR_HIGHER) {
                v.setBackground(drawable);
            } else {
                v.setBackgroundDrawable(drawable);
            }

            v.setPadding(pLeft, pTop, pRight, pBottom);
        }
    }
}
