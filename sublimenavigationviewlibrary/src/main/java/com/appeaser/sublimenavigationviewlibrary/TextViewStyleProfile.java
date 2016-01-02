/*
 * Copyright 2015 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appeaser.sublimenavigationviewlibrary;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;

/**
 * Keeps track of TextView styling based
 * on its role: Item, Hint, Group header etc.
 *
 * Created by Vikram
 */
public class TextViewStyleProfile {

    private static final String TAG = TextViewStyleProfile.class.getSimpleName();

    private static final int[] EMPTY_STATE_SET = new int[]{};
    private static final int[] CHECKED_STATE_SET = new int[]{R.attr.state_item_checked};
    private static final int[] DISABLED_STATE_SET = new int[]{-android.R.attr.state_enabled};

    private ColorStateList mTextColor;
    private Typeface mTypeface;
    private int mTypefaceStyle = Typeface.NORMAL;

    private Context mContext;
    // Used to create the default colors.
    private SublimeThemer.DefaultTheme mDefaultTheme;

    public TextViewStyleProfile(@NonNull Context context, @NonNull SublimeThemer.DefaultTheme defaultTheme) {
        mContext = context;
        mDefaultTheme = defaultTheme;
    }

    /**
     * Sets the text color to the given {@link ColorStateList}.
     * 'null' is a valid argument, in which case, a default
     * text color will be used.
     * See {@link TextViewStyleProfile#setDefaultItemTextColor()}
     * for details about the default text color.
     *
     * @param textColor The text color to use.
     * @return This {@link TextViewStyleProfile} for chaining.
     */
    public TextViewStyleProfile setTextColor(ColorStateList textColor) {
        if (textColor == null) {
            Log.e(TAG, "'setTextColor(ColorStateList)' was called with a 'null' value");
        }

        mTextColor = textColor;
        return this;
    }

    /**
     * Sets the typeface to the given value. 'null' is valid argument,
     * in which case, the system default typeface will be used.
     *
     * @param typeface The typeface to use.
     * @return This {@link TextViewStyleProfile} for chaining.
     */
    public TextViewStyleProfile setTypeface(Typeface typeface) {
        if (typeface == null) {
            Log.e(TAG, "'setTypeface(Typeface)' was called with a 'null' value");
        }

        mTypeface = typeface;
        return this;
    }

    /**
     * Sets the typeface style to the given value. The typeface style
     * defaults to Typeface.NORMAL if the value isn't one of
     * Typeface.NORMAL, Typeface.BOLD, Typeface.ITALIC, Typeface.BOLD_ITALIC.
     *
     * @param typefaceStyle The style to set.
     * @return This {@link TextViewStyleProfile} for chaining.
     */
    public TextViewStyleProfile setTypefaceStyle(int typefaceStyle) {
        if (typefaceStyle < Typeface.NORMAL || typefaceStyle > Typeface.BOLD_ITALIC) {
            Log.e(TAG, "'setTypefaceStyle(int)' was called with a invalid value " +
                    "- allowed values are Typeface.NORMAL, Typeface.BOLD, " +
                    "Typeface.ITALIC and Typeface.BOLD_ITALIC.");
            mTypefaceStyle = Typeface.NORMAL;
        } else {
            mTypefaceStyle = typefaceStyle;
        }

        return this;
    }

    /**
     * Returns the currently set text color.
     *
     * @return {@link ColorStateList} that will be used
     *          as the text color.
     */
    public ColorStateList getTextColor() {
        if (mTextColor == null) {
            setDefaultItemTextColor();
        }

        return mTextColor;
    }

    /**
     * Returns the (custom) Typeface.
     *
     * @return given Typeface. May be 'null'.
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * Returns the currently set text style: normal, bold,
     * italic, bold_italic.
     *
     * @return one of Typeface.NORMAL, Typeface.BOLD,
     *         Typeface.ITALIC, Typeface.BOLD_ITALIC.
     */
    public int getTypefaceStyle() {
        return mTypefaceStyle;
    }

    /**
     * Creates and sets the default item text color.
     */
    private void setDefaultItemTextColor() {
        TypedValue value = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.textColorPrimary, value, true)) {
            ColorStateList baseColor = mContext.getResources().getColorStateList(value.resourceId);
            if (mContext.getTheme().resolveAttribute(R.attr.colorPrimary, value, true)) {
                int colorPrimary = value.data;
                int defaultColor = baseColor.getDefaultColor();
                mTextColor = new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET},
                        new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
            }
        }

        if (mTextColor == null) {
            // Defaults
            boolean isLightTheme = mDefaultTheme == SublimeThemer.DefaultTheme.LIGHT;

            int defDisabled = isLightTheme ?
                    mContext.getResources().getColor(R.color.snv_primary_text_disabled_material_light)
                    : mContext.getResources().getColor(R.color.snv_primary_text_disabled_material_dark);
            int defChecked = isLightTheme ?
                    mContext.getResources().getColor(R.color.snv_primary_material_light)
                    : mContext.getResources().getColor(R.color.snv_primary_material_dark);
            int defEmptySet = isLightTheme ?
                    mContext.getResources().getColor(R.color.snv_primary_text_default_material_light)
                    : mContext.getResources().getColor(R.color.snv_primary_text_default_material_dark);
            mTextColor = new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET},
                    new int[]{defDisabled, defChecked, defEmptySet});
        }
    }
}
