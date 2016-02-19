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
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;

/**
 * View implementation for Separator menu item.
 *
 * Created by Vikram.
 */
public class SublimeSeparatorItemView extends SublimeBaseItemView {

    private static final String TAG = SublimeSeparatorItemView.class.getSimpleName();

    public SublimeSeparatorItemView(Context context) {
        this(context, null);
    }

    public SublimeSeparatorItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeSeparatorItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sublime_menu_separator_item_content,
                this, true);

        // Support for providing separator/divider color
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.snvDividerColor, typedValue, true);

        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            if (Config.DEBUG) {
                Log.i(TAG, "Given divider color: " + Integer.toHexString(typedValue.data));
            }

            findViewById(R.id.snvSeparator).setBackgroundColor(typedValue.data);
        } else {
            if (Config.DEBUG) {
                Log.i(TAG, "Divider color not provided, using default");
            }

            findViewById(R.id.snvSeparator)
                    .setBackgroundColor(ContextCompat.getColor(context,
                            R.color.snv_default_divider_color));
        }
    }
}
