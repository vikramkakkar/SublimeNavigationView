/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appeaser.sublimenavigationviewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Borrowed from Support Design library
 */
public class ScrimInsetsFrameLayout extends FrameLayout {
    private Drawable mInsetForeground;
    private Rect mInsets;
    private Rect mTempRect;

    public ScrimInsetsFrameLayout(Context context) {
        this(context, null);
    }

    public ScrimInsetsFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SnvScrimInsetsFrameLayout,
                defStyleAttr,
                R.style.SnvScrimInsetsFrameLayout);
        this.mInsetForeground = a.getDrawable(R.styleable.SnvScrimInsetsFrameLayout_snvInsetForeground);
        a.recycle();
        this.setWillNotDraw(true);
        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        if (null == ScrimInsetsFrameLayout.this.mInsets) {
                            ScrimInsetsFrameLayout.this.mInsets = new Rect();
                        }

                        ScrimInsetsFrameLayout.this.mInsets.set(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                        ScrimInsetsFrameLayout.this.setWillNotDraw(ScrimInsetsFrameLayout.this.mInsets.isEmpty() || ScrimInsetsFrameLayout.this.mInsetForeground == null);
                        ViewCompat.postInvalidateOnAnimation(ScrimInsetsFrameLayout.this);
                        return insets.consumeSystemWindowInsets();
                    }
                });
    }

    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        if (mInsets != null && mInsetForeground != null) {
            int sc = canvas.save();
            canvas.translate(getScrollX(), getScrollY());

            this.mTempRect.set(0, 0, width, this.mInsets.top);
            this.mInsetForeground.setBounds(this.mTempRect);
            this.mInsetForeground.draw(canvas);

            this.mTempRect.set(0, height - this.mInsets.bottom, width, height);
            this.mInsetForeground.setBounds(this.mTempRect);
            this.mInsetForeground.draw(canvas);

            this.mTempRect.set(0, this.mInsets.top, this.mInsets.left, height - this.mInsets.bottom);
            this.mInsetForeground.setBounds(this.mTempRect);
            this.mInsetForeground.draw(canvas);

            this.mTempRect.set(width - this.mInsets.right, this.mInsets.top, width, height - this.mInsets.bottom);
            this.mInsetForeground.setBounds(this.mTempRect);
            this.mInsetForeground.draw(canvas);

            canvas.restoreToCount(sc);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mInsetForeground != null) {
            this.mInsetForeground.setCallback(this);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mInsetForeground != null) {
            this.mInsetForeground.setCallback(null);
        }
    }
}
