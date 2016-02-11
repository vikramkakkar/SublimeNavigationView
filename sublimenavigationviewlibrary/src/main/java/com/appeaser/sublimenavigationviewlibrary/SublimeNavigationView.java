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
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Top level view that hosts a SublimeMenu
 */
public class SublimeNavigationView extends ScrimInsetsFrameLayout {

    private static final String TAG = SublimeNavigationView.class.getSimpleName();

    // Key used for saving state
    private static final String SS_MENU = "ss.menu";

    // Menu
    private SublimeMenu mMenu;

    // Presenter
    private final SublimeMenuPresenter mPresenter;

    // Listener
    private OnNavigationMenuEventListener mEventListener;

    // Max width set for this drawer
    private int mMaxWidth;

    // Custom MenuInflater
    private SublimeMenuInflater mMenuInflater;

    // Theme controller
    private SublimeThemer mThemer;

    public SublimeNavigationView(Context context) {
        this(context, null);
    }

    public SublimeNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.SublimeNavigationView, defStyleAttr,
                R.style.SnvSublimeNavigationView);

        try {
            // Used for creating default resources
            SublimeThemer.DefaultTheme defaultTheme = SublimeThemer.DefaultTheme.LIGHT;

            if (a.hasValue(R.styleable.SublimeNavigationView_snvDefaultTheme)) {
                defaultTheme = a.getInt(R.styleable.SublimeNavigationView_snvDefaultTheme,
                        0) == 0 ? SublimeThemer.DefaultTheme.LIGHT
                        : SublimeThemer.DefaultTheme.DARK;
            }

            mThemer = new SublimeThemer(getContext(), defaultTheme);

            mThemer.setDrawerBackground(a.getDrawable(
                    R.styleable.SublimeNavigationView_android_background));

            if (a.hasValue(R.styleable.SublimeNavigationView_elevation)) {
                mThemer.setElevation((float) a.getDimensionPixelSize(
                        R.styleable.SublimeNavigationView_elevation, 0));
            }

            ViewCompat.setFitsSystemWindows(this,
                    a.getBoolean(R.styleable.SublimeNavigationView_android_fitsSystemWindows,
                            false));
            mMaxWidth = a.getDimensionPixelSize(
                    R.styleable.SublimeNavigationView_android_maxWidth, 0);

            if (a.hasValue(R.styleable.SublimeNavigationView_snvItemIconTint)) {
                mThemer.setIconTintList(a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvItemIconTint));
            }

            mThemer.setGroupExpandDrawable(a.getDrawable(
                    R.styleable.SublimeNavigationView_snvGroupExpandDrawable));

            mThemer.setGroupCollapseDrawable(a.getDrawable(
                    R.styleable.SublimeNavigationView_snvGroupCollapseDrawable));

            // Text style profiles for Item, Hint, SubheaderItem & SubheaderHint
            ColorStateList itemTextColor = null, hintTextColor = null,
                    subheaderItemTextColor = null, subheaderHintTextColor = null,
                    badgeTextColor = null;
            Typeface itemTypeface = null, hintTypeface = null,
                    subheaderItemTypeface = null, subheaderHintTypeface = null,
                    badgeTypeface = null;
            int itemTypefaceStyle = 0, hintTypefaceStyle = 0,
                    subheaderItemTypefaceStyle = 0, subheaderHintTypefaceStyle = 0,
                    badgeTypefaceStyle = 0;

            if (a.hasValue(R.styleable.SublimeNavigationView_snvItemTextColor)) {
                itemTextColor = a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvItemTextColor);
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvHintTextColor)) {
                hintTextColor = a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvHintTextColor);
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderItemTextColor)) {
                subheaderItemTextColor = a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvSubheaderItemTextColor);
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderHintTextColor)) {
                subheaderHintTextColor = a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvSubheaderHintTextColor);
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvBadgeTextColor)) {
                badgeTextColor = a.getColorStateList(
                        R.styleable.SublimeNavigationView_snvBadgeTextColor);
            }

            try {   // Catch the RuntimeException thrown if
                    // the Typeface filename is incorrect
                if (a.hasValue(R.styleable.SublimeNavigationView_snvItemTypefaceFilename)) {
                    String itemTypefaceFilename = a.getString(
                            R.styleable.SublimeNavigationView_snvItemTypefaceFilename);
                    if (!TextUtils.isEmpty(itemTypefaceFilename)) {
                        itemTypeface = Typeface.createFromAsset(context.getAssets(),
                                itemTypefaceFilename);
                    }
                }

                if (a.hasValue(R.styleable.SublimeNavigationView_snvHintTypefaceFilename)) {
                    String hintTypefaceFilename = a.getString(
                            R.styleable.SublimeNavigationView_snvHintTypefaceFilename);
                    if (!TextUtils.isEmpty(hintTypefaceFilename)) {
                        hintTypeface = Typeface.createFromAsset(context.getAssets(),
                                hintTypefaceFilename);
                    }
                }

                if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderItemTypefaceFilename)) {
                    String subheaderItemTypefaceFilename = a.getString(
                            R.styleable.SublimeNavigationView_snvSubheaderItemTypefaceFilename);
                    if (!TextUtils.isEmpty(subheaderItemTypefaceFilename)) {
                        subheaderItemTypeface = Typeface.createFromAsset(context.getAssets(),
                                subheaderItemTypefaceFilename);
                    }
                }

                if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderHintTypefaceFilename)) {
                    String subheaderHintTypefaceFilename = a.getString(
                            R.styleable.SublimeNavigationView_snvSubheaderHintTypefaceFilename);
                    if (!TextUtils.isEmpty(subheaderHintTypefaceFilename)) {
                        subheaderHintTypeface = Typeface.createFromAsset(context.getAssets(),
                                subheaderHintTypefaceFilename);
                    }
                }

                if (a.hasValue(R.styleable.SublimeNavigationView_snvBadgeTypefaceFilename)) {
                    String badgeTypefaceFilename = a.getString(
                            R.styleable.SublimeNavigationView_snvBadgeTypefaceFilename);
                    if (!TextUtils.isEmpty(badgeTypefaceFilename)) {
                        badgeTypeface = Typeface.createFromAsset(context.getAssets(),
                                badgeTypefaceFilename);
                    }
                }
            } catch (RuntimeException re) {
                Log.e(TAG, "Error loading Typeface from Assets. " +
                        "Confirm that the Typeface filename is correct:\n" +
                        "    - filename should include the extension\n" +
                        "    - filename is case-sensitive");
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvItemTypefaceStyle)) {
                itemTypefaceStyle = a.getInt(
                        R.styleable.SublimeNavigationView_snvItemTypefaceStyle, Typeface.NORMAL);

                switch (itemTypefaceStyle) {
                    case 1:
                        itemTypefaceStyle = Typeface.BOLD;
                        break;
                    case 2:
                        itemTypefaceStyle = Typeface.ITALIC;
                        break;
                    case 3:
                        itemTypefaceStyle = Typeface.BOLD_ITALIC;
                        break;
                    default:
                        // case 0: NORMAL
                        itemTypefaceStyle = Typeface.NORMAL;
                        break;
                }
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvHintTypefaceStyle)) {
                hintTypefaceStyle = a.getInt(
                        R.styleable.SublimeNavigationView_snvHintTypefaceStyle, Typeface.NORMAL);

                switch (hintTypefaceStyle) {
                    case 1:
                        hintTypefaceStyle = Typeface.BOLD;
                        break;
                    case 2:
                        hintTypefaceStyle = Typeface.ITALIC;
                        break;
                    case 3:
                        hintTypefaceStyle = Typeface.BOLD_ITALIC;
                        break;
                    default:
                        // case 0: NORMAL
                        hintTypefaceStyle = Typeface.NORMAL;
                        break;
                }
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderItemTypefaceStyle)) {
                subheaderItemTypefaceStyle = a.getInt(
                        R.styleable.SublimeNavigationView_snvSubheaderItemTypefaceStyle,
                        Typeface.NORMAL);

                switch (subheaderItemTypefaceStyle) {
                    case 1:
                        subheaderItemTypefaceStyle = Typeface.BOLD;
                        break;
                    case 2:
                        subheaderItemTypefaceStyle = Typeface.ITALIC;
                        break;
                    case 3:
                        subheaderItemTypefaceStyle = Typeface.BOLD_ITALIC;
                        break;
                    default:
                        // case 0: NORMAL
                        subheaderItemTypefaceStyle = Typeface.NORMAL;
                        break;
                }
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvSubheaderHintTypefaceStyle)) {
                subheaderHintTypefaceStyle = a.getInt(
                        R.styleable.SublimeNavigationView_snvSubheaderHintTypefaceStyle,
                        Typeface.NORMAL);

                switch (subheaderHintTypefaceStyle) {
                    case 1:
                        subheaderHintTypefaceStyle = Typeface.BOLD;
                        break;
                    case 2:
                        subheaderHintTypefaceStyle = Typeface.ITALIC;
                        break;
                    case 3:
                        subheaderHintTypefaceStyle = Typeface.BOLD_ITALIC;
                        break;
                    default:
                        // case 0: NORMAL
                        subheaderHintTypefaceStyle = Typeface.NORMAL;
                        break;
                }
            }

            if (a.hasValue(R.styleable.SublimeNavigationView_snvBadgeTypefaceStyle)) {
                badgeTypefaceStyle = a.getInt(
                        R.styleable.SublimeNavigationView_snvBadgeTypefaceStyle,
                        Typeface.NORMAL);

                switch (badgeTypefaceStyle) {
                    case 1:
                        badgeTypefaceStyle = Typeface.BOLD;
                        break;
                    case 2:
                        badgeTypefaceStyle = Typeface.ITALIC;
                        break;
                    case 3:
                        badgeTypefaceStyle = Typeface.BOLD_ITALIC;
                        break;
                    default:
                        // case 0: NORMAL
                        badgeTypefaceStyle = Typeface.NORMAL;
                        break;
                }
            }

            // Item text styling
            TextViewStyleProfile itemStyleProfile
                    = new TextViewStyleProfile(context, defaultTheme);
            itemStyleProfile.setTextColor(itemTextColor)
                    .setTypeface(itemTypeface)
                    .setTypefaceStyle(itemTypefaceStyle);
            mThemer.setItemStyleProfile(itemStyleProfile);

            // Hint text styling
            TextViewStyleProfile hintStyleProfile
                    = new TextViewStyleProfile(context, defaultTheme);
            hintStyleProfile.setTextColor(hintTextColor)
                    .setTypeface(hintTypeface)
                    .setTypefaceStyle(hintTypefaceStyle);
            mThemer.setItemHintStyleProfile(hintStyleProfile);

            // Sub-header item text styling
            TextViewStyleProfile subheaderItemStyleProfile
                    = new TextViewStyleProfile(context, defaultTheme);
            subheaderItemStyleProfile.setTextColor(subheaderItemTextColor)
                    .setTypeface(subheaderItemTypeface)
                    .setTypefaceStyle(subheaderItemTypefaceStyle);
            mThemer.setSubheaderStyleProfile(subheaderItemStyleProfile);

            // Sub-header hint text styling
            TextViewStyleProfile subheaderHintStyleProfile
                    = new TextViewStyleProfile(context, defaultTheme);
            subheaderHintStyleProfile.setTextColor(subheaderHintTextColor)
                    .setTypeface(subheaderHintTypeface)
                    .setTypefaceStyle(subheaderHintTypefaceStyle);
            mThemer.setSubheaderHintStyleProfile(subheaderHintStyleProfile);

            // Badge text styling
            TextViewStyleProfile badgeStyleProfile
                    = new TextViewStyleProfile(context, defaultTheme);
            badgeStyleProfile.setTextColor(badgeTextColor)
                    .setTypeface(badgeTypeface)
                    .setTypefaceStyle(badgeTypefaceStyle);
            mThemer.setBadgeStyleProfile(badgeStyleProfile);

            mThemer.setItemBackground(a.getDrawable(
                    R.styleable.SublimeNavigationView_snvItemBackground));

            if (a.hasValue(R.styleable.SublimeNavigationView_snvMenu)) {
                int menuResId = a.getResourceId(R.styleable.SublimeNavigationView_snvMenu, -1);

                if (menuResId == -1) {
                    throw new RuntimeException("Passed menuResId was not valid");
                }

                mMenu = new SublimeMenu(menuResId);
                inflateMenu(menuResId);
            }

            mMenu.setCallback(new SublimeMenu.Callback() {
                public boolean onMenuItemSelected(SublimeMenu menu, SublimeBaseMenuItem item,
                                                  OnNavigationMenuEventListener.Event event) {
                    return SublimeNavigationView.this.mEventListener != null
                            && SublimeNavigationView.this
                            .mEventListener.onNavigationMenuEvent(event, item);
                }
            });

            mPresenter = new SublimeMenuPresenter();
            applyThemer();

            mMenu.setMenuPresenter(getContext(), mPresenter);
            addView(mPresenter.getMenuView(this));

            if (a.hasValue(R.styleable.SublimeNavigationView_snvHeaderLayout)) {
                inflateHeaderView(a.getResourceId(R.styleable.SublimeNavigationView_snvHeaderLayout, 0));
            }
        } finally {
            a.recycle();
        }

        // Upon creation, and until initializations are done,
        // SublimeMenuPresenter blocks all calls for invalidation.
        // We can now finalize the initialization phase to allow
        // invalidation of the menu when required.
        mPresenter.setInitializationDone();
    }

    /**
     * Provides a mechanism for switching between any number of Menus.
     *
     * @param newMenuResId id of the menu that you wish
     *                     to switch to. Eg: R.menu.new_menu_id
     */
    public void switchMenuTo(@MenuRes int newMenuResId) {
        if (newMenuResId < 1) {
            Log.e(TAG, "Could not switch to new menu: passed menuResourceId was invalid.");
            return;
        }

        mMenu = new SublimeMenu(newMenuResId);
        inflateMenu(newMenuResId);

        mMenu.setCallback(new SublimeMenu.Callback() {
            public boolean onMenuItemSelected(SublimeMenu menu, SublimeBaseMenuItem item,
                                              OnNavigationMenuEventListener.Event event) {
                return SublimeNavigationView.this.mEventListener != null
                        && SublimeNavigationView.this
                        .mEventListener.onNavigationMenuEvent(event, item);
            }
        });

        mMenu.setMenuPresenter(getContext(), mPresenter);
    }

    /**
     * Provides a mechanism for switching between any number of Menus.
     *
     * @param newMenu Typically, this would
     *                have been returned from a former call to
     *                {@link SublimeNavigationView#getMenu()}.
     */
    public void switchMenuTo(@NonNull SublimeMenu newMenu) {
        // Todo: pending removal of this NULL check
        if (newMenu == null) {
            Log.e(TAG, "Could not switch to new menu: passed menu was 'null'.");
            return;
        }

        mMenu = newMenu;

        mMenu.setCallback(new SublimeMenu.Callback() {
            public boolean onMenuItemSelected(SublimeMenu menu, SublimeBaseMenuItem item,
                                              OnNavigationMenuEventListener.Event event) {
                return SublimeNavigationView.this.mEventListener != null
                        && SublimeNavigationView.this
                        .mEventListener.onNavigationMenuEvent(event, item);
            }
        });

        mMenu.setMenuPresenter(getContext(), mPresenter);
    }

    /**
     * Returns the currently set header view.
     *
     * @return Currently set header {@link View}. Null, if the header
     * is not set.
     */
    public View getHeaderView() {
        return mPresenter.getHeaderView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SublimeNavigationView.SavedState state
                = new SublimeNavigationView.SavedState(superState);
        state.getMenuState().putParcelable(SS_MENU, mMenu);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable savedState) {
        SublimeNavigationView.SavedState state
                = (SublimeNavigationView.SavedState) savedState;
        super.onRestoreInstanceState(state.getSuperState());

        Bundle menuState = state.getMenuState();

        if (menuState != null && menuState.containsKey(SS_MENU)) {
            mMenu = menuState.getParcelable(SS_MENU);
        }

        if (mMenu != null) {
            mMenu.setCallback(new SublimeMenu.Callback() {
                public boolean onMenuItemSelected(SublimeMenu menu, SublimeBaseMenuItem item,
                                                  OnNavigationMenuEventListener.Event event) {
                    return SublimeNavigationView.this.mEventListener != null
                            && SublimeNavigationView.this
                            .mEventListener.onNavigationMenuEvent(event, item);
                }
            });
            mMenu.setMenuPresenter(getContext(), mPresenter);
        }
    }

    /**
     * Sets the listener.
     *
     * @param listener Listener
     */
    public void setNavigationMenuEventListener(OnNavigationMenuEventListener listener) {
        mEventListener = listener;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        switch (View.MeasureSpec.getMode(widthSpec)) {
            case View.MeasureSpec.AT_MOST:
                widthSpec = View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(widthSpec), mMaxWidth),
                        View.MeasureSpec.EXACTLY);
                break;
            case View.MeasureSpec.UNSPECIFIED:
                widthSpec = View.MeasureSpec.makeMeasureSpec(mMaxWidth, View.MeasureSpec.EXACTLY);
            case View.MeasureSpec.EXACTLY:
        }

        super.onMeasure(widthSpec, heightSpec);
    }

    /**
     * Inflates menu resource with the given ID into the
     * current {@link SublimeMenu} item.
     *
     * @param menuResId ID of the resource to inflate.
     */
    private void inflateMenu(@MenuRes int menuResId) {
        getMenuInflater().inflate(menuResId, mMenu);
    }

    /**
     * Returns the current {@link SublimeMenu} item.
     *
     * @return current {@link SublimeMenu}.
     */
    @NonNull
    public SublimeMenu getMenu() {
        return mMenu;
    }

    /**
     * Inflates and returns the view resource with the given ID.
     *
     * @param res view resource ID to inflate.
     * @return inflated {@link View}.
     */
    private View inflateHeaderView(@LayoutRes int res) {
        return mPresenter.inflateHeaderView(res);
    }

    /**
     * Sets the given {@link View} as the header this Menu.
     *
     * @param view the {@link View} to add as header.
     */
    public void addHeaderView(@NonNull View view) {
        mPresenter.addHeaderView(view);
    }

    /**
     * Removes the given {@link View} from the header.
     *
     * @param view the {@link View} to remove from header.
     */
    public void removeHeaderView(@NonNull View view) {
        mPresenter.removeHeaderView(view);
    }

    /**
     * Used internally to create a custom inflater for xml
     * definition of a {@link SublimeMenu}.
     *
     * @return inflater that can work with xml definition
     * of a {@link SublimeMenu}.
     */
    @NonNull
    private SublimeMenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new SublimeMenuInflater(getContext());
        }

        return mMenuInflater;
    }

    /**
     * Returns the currently used {@link SublimeThemer}.
     * Note that several method that provide access to
     * theme elements has been removed from {@link SublimeNavigationView}.
     * For example, to gain access to the currently used
     * Icon Tint List (accessible through 'NavigationView#getItemIconTintList()'),
     * call {@link SublimeThemer#getIconTintList()}.
     *
     * @return Currently used {@link SublimeThemer}.
     */
    @NonNull
    public SublimeThemer getCurrentThemer() {
        return mThemer;
    }

    /**
     * Style navigation view components through java (instead of xml).
     * Note that {@link SublimeThemer} is currently not
     * preserved when saving state.
     *
     * @param sublimeThemer A valid {@link SublimeThemer}. Method does nothing
     *                      if this parameter is NULL.
     */
    public void updateThemer(@NonNull SublimeThemer sublimeThemer) {
        // Todo: pending removal of this NULL check
        if (sublimeThemer == null) {
            Log.e(TAG, "'updateThemer(SublimeThemer)' was called with a 'null' value");
            return;
        }

        mThemer = sublimeThemer;
        applyThemer();
    }

    /**
     * Used internally to apply the currently set {@link SublimeThemer}.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void applyThemer() {
        Log.i(TAG, "applyThemer()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(mThemer.getDrawerBackground());
        } else {
            setBackgroundDrawable(mThemer.getDrawerBackground());
        }

        ViewCompat.setElevation(this, mThemer.getElevation());

        // propagate themer to the presenter
        mPresenter.setThemer(mThemer);
    }

    //----------------------------------------------------------------//
    //------------------------Preserving State------------------------//
    //----------------------------------------------------------------//

    public static class SavedState extends View.BaseSavedState {
        public Bundle sMenuState;
        public static final Creator<SublimeNavigationView.SavedState> CREATOR
                = new Creator<SublimeNavigationView.SavedState>() {
            public SublimeNavigationView.SavedState createFromParcel(Parcel parcel) {
                return new SublimeNavigationView.SavedState(parcel);
            }

            public SublimeNavigationView.SavedState[] newArray(int size) {
                return new SublimeNavigationView.SavedState[size];
            }
        };

        public SavedState(Parcel in) {
            super(in);
            sMenuState = in.readBundle();
        }

        public SavedState(Parcelable superState) {
            super(superState);
            sMenuState = new Bundle();
        }

        public Bundle getMenuState() {
            return sMenuState;
        }

        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(sMenuState);
        }
    }
}
