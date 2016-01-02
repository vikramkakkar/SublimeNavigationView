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
 * View implementation for Text menu item.
 *
 * Created by Vikram.
 */
public class SublimeTextItemView extends SublimeBaseItemView {


    public SublimeTextItemView(Context context) {
        this(context, null);
    }

    public SublimeTextItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeTextItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.sublime_menu_text_item_content, this, true);
        initializeViews();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
    }

    @Override
    public void initialize(SublimeBaseMenuItem itemData, SublimeThemer themer) {
        super.initialize(itemData, themer);
    }
}
