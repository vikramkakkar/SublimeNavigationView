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
import android.widget.ImageView;

/**
 * Sub-classed ImageView that responds to custom states.
 */
public class StateAwareImageView extends ImageView {

    // Drawable state set - checked
    private static final int[] CHECKED_STATE_SET = {
            R.attr.state_item_checked
    };

    // Keeps track of checked state
    private boolean mChecked;

    public StateAwareImageView(Context context) {
        this(context, null);
    }

    public StateAwareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateAwareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the checked state for this view.
     *
     * @param checked current state
     */
    void setItemChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;
    }
}
