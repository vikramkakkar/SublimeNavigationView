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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

/**
 * View implementation for Switch menu item.
 *
 * Created by Vikram.
 */
public class SublimeSwitchItemView extends SublimeBaseItemView {

    private SwitchCompat mSwitch;

    public SublimeSwitchItemView(Context context) {
        this(context, null);
    }

    public SublimeSwitchItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeSwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sublime_menu_switch_item_content, this, true);
        initializeViews();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        mSwitch = (SwitchCompat) findViewById(R.id.switch_ctrl);
    }

    @Override
    public void initialize(SublimeBaseMenuItem itemData, SublimeThemer themer) {
        setCheckableItemTintList(themer.getCheckableItemTintList());
        super.initialize(itemData, themer);
    }

    @Override
    public void setItemTextColor(ColorStateList textColor) {
        super.setItemTextColor(textColor);
        mSwitch.setTextColor(textColor);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
    }

    @Override
    public void setItemChecked(boolean checked) {
        super.setItemChecked(checked);
        mSwitch.setChecked(checked);
    }

    public void setCheckableItemTintList(ColorStateList checkableItemTintList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Covers android M (23)
            Drawable dTrack = getResources().getDrawable(R.drawable.snv_switch_track_material, getContext().getTheme());
            Drawable dThumb = getResources().getDrawable(R.drawable.snv_switch_thumb_material_anim, getContext().getTheme());

            if (dTrack != null && dThumb != null) {
                DrawableCompat.setTintList(dTrack, checkableItemTintList);
                DrawableCompat.setTintList(dThumb, checkableItemTintList);
                mSwitch.setTrackDrawable(dTrack);
                mSwitch.setThumbDrawable(dThumb);
            }
        } else {
            Drawable dTrack = ContextCompat.getDrawable(getContext(), R.drawable.snv_switch_track);
            Drawable dThumb = ContextCompat.getDrawable(getContext(), R.drawable.switch_thumb_pre_lollipop);

            if (dTrack != null && dThumb != null) {
                dTrack = DrawableCompat.wrap(dTrack);
                DrawableCompat.setTintList(dTrack, checkableItemTintList);
                dTrack.setAlpha(85 /* 0.3f */);

                dThumb = DrawableCompat.wrap(dThumb);
                DrawableCompat.setTintList(dThumb, checkableItemTintList);

                mSwitch.setTrackDrawable(dTrack);
                mSwitch.setThumbDrawable(dThumb);
            }
        }
    }
}