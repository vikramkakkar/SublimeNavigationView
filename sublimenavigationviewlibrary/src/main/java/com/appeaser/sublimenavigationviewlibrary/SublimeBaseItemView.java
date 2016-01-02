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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Base ViewGroup for all menu item types.
 */
public class SublimeBaseItemView extends LinearLayout {
    private static final String TAG = SublimeBaseItemView.class.getSimpleName();

    // Drawable state set - checked
    private static final int[] CHECKED_STATE_SET = {
            R.attr.state_item_checked
    };

    private static final boolean isJBorHigher
            = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    protected int mIconSize;
    protected StateAwareTextView mText, mHint;
    protected SublimeBaseMenuItem mItemData;
    protected StateAwareImageView mIconHolder;
    protected ColorStateList mIconTintList;

    public SublimeBaseItemView(Context context) {
        this(context, null);
    }

    public SublimeBaseItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeBaseItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.snv_navigation_icon_size);
    }

    protected void initializeViews() {
        mText = (StateAwareTextView) findViewById(R.id.text);
        mHint = (StateAwareTextView) findViewById(R.id.hint);
        mIconHolder = (StateAwareImageView) findViewById(R.id.iconHolder);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initialize(SublimeBaseMenuItem itemData, SublimeThemer themer) {
        mItemData = itemData;
        setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
        setEnabled(itemData.isEnabled());

        // Item Title styling
        TextViewStyleProfile itemStyleProfile = themer.getItemStyleProfile();
        setItemTextColor(itemStyleProfile.getTextColor());
        if (itemStyleProfile.getTypeface() != null) {
            setItemTypeface(itemStyleProfile.getTypeface(),
                    itemStyleProfile.getTypefaceStyle());
        } else {
            setItemTypefaceStyle(itemStyleProfile.getTypefaceStyle());
        }
        setTitle(itemData.getTitle());

        setIconTintList(themer.getIconTintList());
        setIcon(itemData.getIcon());

        boolean showHint = !TextUtils.isEmpty(itemData.getHint());
        mHint.setVisibility(showHint ? View.VISIBLE : View.GONE);
        if (showHint) {
            // Hint styling
            TextViewStyleProfile hintStyleProfile = themer.getItemHintStyleProfile();
            setHintTextColor(hintStyleProfile.getTextColor());
            if (hintStyleProfile.getTypeface() != null) {
                setHintTypeface(hintStyleProfile.getTypeface(),
                        hintStyleProfile.getTypefaceStyle());
            } else {
                setHintTypefaceStyle(hintStyleProfile.getTypefaceStyle());
            }
            mHint.setText(itemData.getHint());
        }

        setItemChecked(itemData.isChecked());
        setItemBackground(themer.getItemBackground());
    }

    public SublimeBaseMenuItem getItemData() {
        return mItemData;
    }

    public void setTitle(CharSequence title) {
        mText.setText(title);
    }

    private Drawable prepareIcon(Drawable icon) {
        icon = DrawableCompat.wrap(icon.getConstantState().newDrawable()).mutate();
        icon.setBounds(0, 0, mIconSize, mIconSize);
        DrawableCompat.setTintList(icon, mIconTintList);
        return icon;
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            mIconHolder.setVisibility(View.VISIBLE);
            mIconHolder.setImageDrawable(prepareIcon(icon));
        } else {
            mIconHolder.setVisibility(View.GONE);
        }
    }

    public void setIconTintList(ColorStateList tintList) {
        mIconTintList = tintList;
    }

    public void setItemTextColor(ColorStateList textColor) {
        mText.setTextColor(textColor);
    }

    public void setHintTextColor(ColorStateList hintTextColor) {
        mHint.setTextColor(hintTextColor);
    }

    public void setItemTypeface(Typeface itemTypeface, int itemTypefaceStyle) {
        mText.setTypeface(itemTypeface, itemTypefaceStyle);
    }

    public void setItemTypefaceStyle(int itemTypefaceStyle) {
        mText.setTypeface(mText.getTypeface(), itemTypefaceStyle);
    }

    public void setHintTypeface(Typeface hintTypeface, int hintTypefaceStyle) {
        mHint.setTypeface(hintTypeface, hintTypefaceStyle);
    }

    public void setHintTypefaceStyle(int hintTypefaceStyle) {
        mHint.setTypeface(mHint.getTypeface(), hintTypefaceStyle);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mText.setEnabled(enabled);
        mHint.setEnabled(enabled);
        mIconHolder.setEnabled(enabled);
    }

    public void setItemChecked(boolean checked) {
        mText.setItemChecked(mItemData.isChecked());
        mHint.setItemChecked(mItemData.isChecked());
        mIconHolder.setItemChecked(mItemData.isChecked());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setItemBackground(Drawable itemBackground) {
        if (isJBorHigher) {
            setBackground(itemBackground);
        } else {
            setBackgroundDrawable(itemBackground);
        }

        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mItemData != null && mItemData.isCheckable() && mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;
    }
}
