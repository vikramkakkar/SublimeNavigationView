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
import android.util.AttributeSet;
import android.view.LayoutInflater;

/**
 * View implementation for Separator menu item.
 *
 * Created by Vikram.
 */
public class SublimeSeparatorItemView extends SublimeBaseItemView {
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
    }
}
