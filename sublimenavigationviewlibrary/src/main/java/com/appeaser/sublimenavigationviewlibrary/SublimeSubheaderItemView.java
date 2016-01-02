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
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * View implementation for Subheader menu item.
 *
 * Created by Vikram.
 */
public class SublimeSubheaderItemView extends SublimeBaseItemView {

    ImageView mExpandCollapse;
    Drawable mExpandDrawable, mCollapseDrawable;

    public SublimeSubheaderItemView(Context context) {
        this(context, null);
    }

    public SublimeSubheaderItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeSubheaderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sublime_menu_subheader_item_content, this, true);
        initializeViews();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        mExpandCollapse = (ImageView) findViewById(R.id.expand_collapse);
    }

    private void initializeDrawables(SublimeThemer themer) {
        mExpandDrawable = themer.getGroupExpandDrawable();
        mCollapseDrawable = themer.getGroupCollapseDrawable();
    }

    public void initialize(SublimeBaseMenuItem itemData, SublimeGroup groupData,
                           SublimeThemer themer) {
        initializeDrawables(themer);

        // Subheader Item styling
        TextViewStyleProfile subheaderItemStyleProfile = themer.getSubheaderStyleProfile();
        setSubheaderItemTextColor(subheaderItemStyleProfile.getTextColor());
        if (subheaderItemStyleProfile.getTypeface() != null) {
            setSubheaderItemTypeface(subheaderItemStyleProfile.getTypeface(),
                    subheaderItemStyleProfile.getTypefaceStyle());
        } else {
            setSubheaderItemTypefaceStyle(subheaderItemStyleProfile.getTypefaceStyle());
        }

        // Subheader Hint styling
        TextViewStyleProfile subheaderHintStyleProfile = themer.getSubheaderHintStyleProfile();
        setSubheaderHintTextColor(subheaderHintStyleProfile.getTextColor());
        if (subheaderHintStyleProfile.getTypeface() != null) {
            setSubheaderHintTypeface(subheaderHintStyleProfile.getTypeface(),
                    subheaderHintStyleProfile.getTypefaceStyle());
        } else {
            setSubheaderHintTypefaceStyle(subheaderHintStyleProfile.getTypefaceStyle());
        }

        super.initialize(itemData, themer);
        setExpandCollapseIconVisibility(groupData.isCollapsible());
        setExpandCollapseIconState(groupData.isCollapsed());
    }

    @Override
    public void setIconTintList(ColorStateList tintList) {
        mExpandDrawable = DrawableCompat.wrap(mExpandDrawable
                .getConstantState().newDrawable()).mutate();
        DrawableCompat.setTintList(mExpandDrawable, tintList);

        mCollapseDrawable = DrawableCompat.wrap(mCollapseDrawable
                .getConstantState().newDrawable()).mutate();
        DrawableCompat.setTintList(mCollapseDrawable, tintList);

        super.setIconTintList(tintList);
    }

    @Override
    public void setItemTextColor(ColorStateList textColor) {
        // Block this call
    }

    @Override
    public void setHintTextColor(ColorStateList hintTextColor) {
        // Block this call
    }

    @Override
    public void setItemTypeface(Typeface itemTypeface, int itemTypefaceStyle) {
        // Block this call
    }

    @Override
    public void setHintTypeface(Typeface hintTypeface, int hintTypefaceStyle) {
        // Block this call
    }

    @Override
    public void setItemTypefaceStyle(int itemTypefaceStyle) {
        // Block this call
    }

    @Override
    public void setHintTypefaceStyle(int hintTypefaceStyle) {
        // Block this call
    }

    public void setSubheaderItemTextColor(ColorStateList subheaderTextColor) {
        mText.setTextColor(subheaderTextColor);
    }

    public void setSubheaderHintTextColor(ColorStateList subheaderHintTextColor) {
        mHint.setTextColor(subheaderHintTextColor);
    }

    public void setSubheaderItemTypeface(Typeface itemTypeface, int itemTypefaceStyle) {
        mText.setTypeface(itemTypeface, itemTypefaceStyle);
    }

    public void setSubheaderHintTypeface(Typeface hintTypeface, int hintTypefaceStyle) {
        mHint.setTypeface(hintTypeface, hintTypefaceStyle);
    }

    public void setSubheaderItemTypefaceStyle(int itemTypefaceStyle) {
        mText.setTypeface(mText.getTypeface(), itemTypefaceStyle);
    }

    public void setSubheaderHintTypefaceStyle(int hintTypefaceStyle) {
        mHint.setTypeface(mHint.getTypeface(), hintTypefaceStyle);
    }

    private void setExpandCollapseIconVisibility(boolean visible) {
        mExpandCollapse.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setExpandCollapseIconState(boolean collapsed) {
        mExpandCollapse.setImageDrawable(
                collapsed ? mExpandDrawable : mCollapseDrawable
        );
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mExpandCollapse.setEnabled(enabled);
    }
}
