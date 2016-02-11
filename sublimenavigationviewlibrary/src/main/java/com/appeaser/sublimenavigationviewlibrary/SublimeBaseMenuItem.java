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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Base menu item implementation for all menu item types.
 */
public abstract class SublimeBaseMenuItem implements Parcelable {
    private static final String TAG = SublimeBaseMenuItem.class.getSimpleName();

    private static final Drawable TRANSPARENT_ICON = new ColorDrawable(Color.TRANSPARENT);

    public enum ItemType {SEPARATOR, TEXT, CHECKBOX, SWITCH, BADGE, GROUP_HEADER, HEADER}

    private final int mId;
    private final int mGroup;
    private CharSequence mTitle, mHint;
    private Intent mIntent;
    private boolean mShowsIconSpace;

    /**
     * The icon's drawable which is only created as needed
     */
    private Drawable mIconDrawable;

    /**
     * The icon's resource ID which is used to get the Drawable when it is
     * needed (if the Drawable isn't already obtained--only one of the two is
     * needed).
     */
    private int mIconResId = NO_ICON;

    /**
     * The menu to which this item belongs
     */
    private SublimeMenu mMenu;

    private int mFlags = ENABLED | CHECKABLE;
    private static final int CHECKABLE = 0x00000001;
    private static final int CHECKED = 0x00000002;
    private static final int HIDDEN = 0x00000004;
    static final int ENABLED = 0x00000008;

    /**
     * Used for the icon resource ID if this item does not have an icon
     */
    static final int NO_ICON = 0;

    private ItemType mItemType;
    private boolean mValueProvidedAsync, mInvalidateEntireMenu;

    private boolean mBlockUpdates;

    /**
     * Instantiates this menu item.
     *
     * @param menu parent menu.
     * @param group ID of the Group that this item belongs to.
     * @param id    Unique item ID.
     * @param title The text to display for the item.
     * @param hint The text to display as hint for this item.
     * @param itemType One of {@link SublimeBaseMenuItem.ItemType} enums.
     * @param valueProvidedAsync Indicates that the badge text will be provided later on.
     *                           Used with TextWithBadge ItemType.
     * @param showsIconSpace Whether to indent the item or not.
     */
    SublimeBaseMenuItem(SublimeMenu menu, int group, int id,
                        CharSequence title, CharSequence hint,
                        ItemType itemType, boolean valueProvidedAsync,
                        boolean showsIconSpace) {
        mMenu = menu;
        mId = id;
        mGroup = group;
        mTitle = title;
        mHint = hint;

        if (itemType == null)
            itemType = ItemType.TEXT;

        mItemType = itemType;
        mValueProvidedAsync = valueProvidedAsync;
        mShowsIconSpace = showsIconSpace;
    }

    /**
     * Restores this menu item. {@link SublimeBaseMenuItem#setParentMenu(SublimeMenu)} must
     * be called after this.
     *
     * @param group ID of the Group that this item belongs to.
     * @param id    Unique item ID.
     * @param title The text to display for the item.
     * @param hint The text to display as hint for this item.
     * @param iconResId Restored iconResId.
     * @param itemType One of {@link SublimeBaseMenuItem.ItemType} enums.
     * @param valueProvidedAsync Indicates that the badge text will be provided later on.
     *                           Used with TextWithBadge ItemType.
     * @param showsIconSpace Whether to indent the item or not.
     * @param flags Restored flags.
     */
    SublimeBaseMenuItem(int group, int id,
                        CharSequence title, CharSequence hint,
                        int iconResId,
                        ItemType itemType, boolean valueProvidedAsync,
                        boolean showsIconSpace, int flags) {
        mId = id;
        mGroup = group;
        mTitle = title;
        mHint = hint;
        mIconResId = iconResId;

        if (itemType == null) {
            itemType = ItemType.TEXT;
        }

        mItemType = itemType;
        mValueProvidedAsync = valueProvidedAsync;
        mShowsIconSpace = showsIconSpace;
        mFlags = flags;
    }

    protected void setParentMenu(SublimeMenu menu) {
        mMenu = menu;
    }

    public abstract boolean invoke();

    /**
     * Invokes the item by calling various listeners or callbacks.
     *
     * @return true if the invocation was handled, false otherwise
     */
    protected boolean invoke(OnNavigationMenuEventListener.Event event,
                             SublimeBaseMenuItem item) {
        if (mMenu.dispatchMenuItemSelected(item, event)) {
            return true;
        }

        if (mIntent != null) {
            try {
                if (Config.DEBUG) {
                    Log.d(TAG, "Context requested from parent menu");
                }
                mMenu.getContext().startActivity(mIntent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Can't find activity to handle intent; ignoring", e);
            }
        }

        return false;
    }

    protected void attemptItemUpdate() {
        if (mBlockUpdates) {
            return;
        }

        if (mInvalidateEntireMenu) {
            // invalidate entire menu
            mInvalidateEntireMenu = false;
            mMenu.onItemsChanged();
        } else {
            mMenu.onItemChanged(getItemId());
        }
    }

    /**
     * Indicate that the badge text will be provided later on. Works only with
     * {@link SublimeBaseMenuItem.ItemType#BADGE}.
     *
     * @param valueProvidedAsync 'true' if badge text will be set later on.
     * @return This {@link SublimeBaseMenuItem} for chaining.
     */
    public SublimeBaseMenuItem setValueProvidedAsync(boolean valueProvidedAsync) {
        mValueProvidedAsync = valueProvidedAsync;
        attemptItemUpdate();
        return this;
    }

    /**
     * Whether the badge text is ready to be displayed or not.
     *
     * @return 'true' if the badge text will be set later on, 'false' otherwise.
     */
    public boolean providesValueAsync() {
        return mValueProvidedAsync;
    }

    public boolean isEnabled() {
        return (mFlags & ENABLED) != 0;
    }

    public SublimeBaseMenuItem setEnabled(boolean enabled) {
        if (enabled) {
            mFlags |= ENABLED;
        } else {
            mFlags &= ~ENABLED;
        }

        attemptItemUpdate();
        return this;
    }

    public int getGroupId() {
        return mGroup;
    }

    public int getItemId() {
        return mId;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public SublimeBaseMenuItem setIntent(Intent intent) {
        mIntent = intent;
        return this;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public SublimeBaseMenuItem setTitle(CharSequence title) {
        mTitle = title;
        attemptItemUpdate();
        return this;
    }

    public SublimeBaseMenuItem setTitle(int title) {
        if (Config.DEBUG) {
            Log.d(TAG, "Context requested from parent menu");
        }
        return setTitle(mMenu.getContext().getString(title));
    }

    public CharSequence getHint() {
        return mHint;
    }

    public SublimeBaseMenuItem setHint(CharSequence hint) {
        mHint = hint;
        attemptItemUpdate();
        return this;
    }

    public Drawable getIcon() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }

        if (mIconResId != NO_ICON) {
            if (Config.DEBUG) {
                Log.d(TAG, "Context requested from parent menu");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mIconDrawable = mMenu.getContext().getResources().getDrawable(mIconResId, mMenu.getContext().getTheme());
            } else {
                mIconDrawable = mMenu.getContext().getResources().getDrawable(mIconResId);
            }
            return mIconDrawable;
        }

        if (mShowsIconSpace) {
            return TRANSPARENT_ICON;
        }

        return null;
    }

    public boolean showsIconSpace() {
        return mShowsIconSpace;
    }

    public SublimeBaseMenuItem setShowsIconSpace(boolean showsIconSpace) {
        mShowsIconSpace = showsIconSpace;
        attemptItemUpdate();
        return this;
    }

    public SublimeBaseMenuItem blockUpdates() {
        mBlockUpdates = true;
        return this;
    }

    public SublimeBaseMenuItem allowUpdates() {
        mBlockUpdates = false;
        return this;
    }

    public SublimeBaseMenuItem setIcon(int iconResId) {
        mIconDrawable = null;
        mIconResId = iconResId;

        // If we have a view, we need to push the Drawable to them
        mInvalidateEntireMenu = true;

        attemptItemUpdate();
        return this;
    }

    public boolean isCheckable() {
        return (mFlags & CHECKABLE) == CHECKABLE;
    }

    public SublimeBaseMenuItem setCheckable(boolean checkable) {
        final int oldFlags = mFlags;
        mFlags = (mFlags & ~CHECKABLE) | (checkable ? CHECKABLE : 0);
        if (oldFlags != mFlags) {
            attemptItemUpdate();
        }

        return this;
    }

    public boolean isChecked() {
        return (mFlags & CHECKED) == CHECKED;
    }

    public SublimeBaseMenuItem setChecked(boolean checked) {
        if (!isCheckable()) {
            return this;
        }

        if (!checked) {
            setCheckedInt(false);
        } else {
            mMenu.setItemChecked(this);
            setCheckedInt(true);
        }

        return this;
    }

    public SublimeBaseMenuItem setCheckedInt(boolean checkedInt) {
        final int oldFlags = mFlags;
        mFlags = (mFlags & ~CHECKED) | (checkedInt ? CHECKED : 0);
        if (oldFlags != mFlags) {
            attemptItemUpdate();
        }

        return this;
    }

    public boolean isVisible() {
        return (mFlags & HIDDEN) == 0;
    }

    /**
     * Changes the visibility of the item. This method DOES NOT notify the parent menu of a change
     * in this item, so this should only be called from methods that will eventually trigger this
     * change.  If unsure, use {@link #setVisible(boolean)} instead.
     *
     * @param shown Whether to show (true) or hide (false).
     * @return Whether the item's shown state was changed
     */
    boolean setVisibleInt(boolean shown) {
        final int oldFlags = mFlags;
        mFlags = (mFlags & ~HIDDEN) | (shown ? 0 : HIDDEN);
        return oldFlags != mFlags;
    }

    public SublimeBaseMenuItem setVisible(boolean shown) {
        // Try to set the shown state to the given state. If the shown state was changed
        // (i.e. the previous state isn't the same as given state), notify the parent menu that
        // the shown state has changed for this item
        if (setVisibleInt(shown)) {
            //mInvalidateEntireMenu = true;
            attemptItemUpdate();
        }

        return this;
    }

    public String toString() {
        return mTitle.toString();
    }

    public SublimeMenu getMenu() {
        return mMenu;
    }

    protected int getFlags() {
        return mFlags;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SublimeBaseMenuItem)) {
            return false;
        }

        SublimeBaseMenuItem other = (SublimeBaseMenuItem) o;

        if (getItemId() != other.getItemId()
                || getItemType() != other.getItemType()
                || getFlags() != other.getFlags()
                || providesValueAsync() != other.providesValueAsync()) {
            return false;
        }

        if (getTitle() == null) {
            if (other.getTitle() != null) {
                return false;
            }
        } else if (!getTitle().equals(other.getTitle())) {
            return false;
        }

        if (getHint() == null) {
            if (other.getHint() != null) {
                return false;
            }
        } else if (!getHint().equals(other.getHint())) {
            return false;
        }

        if (getIntent() == null) {
            if (other.getIntent() != null) {
                return false;
            }
        } else if (!getIntent().equals(other.getIntent())) {
            return false;
        }

        if (getIcon() == null) {
            if (other.getIcon() != null) {
                return false;
            }
        } else if (!getIcon().equals(other.getIcon())) {
            return false;
        }

        return super.equals(o);
    }

    //----------------------------------------------------------------//
    //---------------------------Parcelable---------------------------//
    //----------------------------------------------------------------//

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(saveState());
    }

    private static final String SS_ID = "ss.id";
    private static final String SS_GROUP_ID = "ss.group.id";
    private static final String SS_TITLE = "ss.title";
    private static final String SS_HINT = "ss.hint";
    private static final String SS_INTENT = "ss.intent";
    private static final String SS_SHOWS_ICON_SPACE = "ss.shows.icon.space";
    private static final String SS_ITEM_TYPE = "ss.item.type";
    private static final String SS_VALUE_PROVIDED_ASYNC = "ss.value.provided.async";
    private static final String SS_ICON_RES_ID = "ss.icon.res.id";
    private static final String SS_FLAGS = "ss.flags";

    private Bundle saveState() {
        Bundle bundle = new Bundle();

        bundle.putInt(SS_ID, mId);
        bundle.putInt(SS_GROUP_ID, mGroup);
        bundle.putCharSequence(SS_TITLE, mTitle);
        bundle.putCharSequence(SS_HINT, mHint);
        bundle.putParcelable(SS_INTENT, mIntent);
        bundle.putBoolean(SS_SHOWS_ICON_SPACE, mShowsIconSpace);
        bundle.putString(SS_ITEM_TYPE, mItemType.name());
        bundle.putBoolean(SS_VALUE_PROVIDED_ASYNC, mValueProvidedAsync);
        bundle.putInt(SS_ICON_RES_ID, mIconResId);
        bundle.putInt(SS_FLAGS, mFlags);

        return bundle;
    }

    public static final Creator<SublimeBaseMenuItem> CREATOR
            = new Creator<SublimeBaseMenuItem>() {
        public SublimeBaseMenuItem createFromParcel(Parcel in) {
            return createItemFromParcel(in);
        }

        public SublimeBaseMenuItem[] newArray(int size) {
            return new SublimeBaseMenuItem[size];
        }
    };

    private static SublimeBaseMenuItem createItemFromParcel(Parcel in) {
        Bundle bundle = in.readBundle();

        ItemType itemType = ItemType.valueOf(bundle.getString(SS_ITEM_TYPE));

        int groupId = bundle.getInt(SS_GROUP_ID);
        int id = bundle.getInt(SS_ID);
        String title = bundle.getString(SS_TITLE);
        String hint = bundle.getString(SS_HINT);
        int iconResId = bundle.getInt(SS_ICON_RES_ID);
        boolean valueProvidedAsync = bundle.getBoolean(SS_VALUE_PROVIDED_ASYNC);
        boolean showsIconSpace = bundle.getBoolean(SS_SHOWS_ICON_SPACE);
        int flags = bundle.getInt(SS_FLAGS);

        switch (itemType) {
            case SWITCH:
                return new SublimeSwitchMenuItem(groupId, id, title,
                        hint, iconResId, valueProvidedAsync, showsIconSpace, flags);
            case CHECKBOX:
                return new SublimeCheckboxMenuItem(groupId, id, title,
                        hint, iconResId, valueProvidedAsync, showsIconSpace, flags);
            case BADGE:
                return SublimeTextWithBadgeMenuItem.createFromBundle(bundle, groupId, id, title,
                        hint, iconResId, valueProvidedAsync, showsIconSpace, flags);
            case SEPARATOR:
                return new SublimeSeparatorMenuItem(groupId, id);
            case HEADER:
                return SublimeMenu.HEADER_STUB;
            case GROUP_HEADER:
                return new SublimeGroupHeaderMenuItem(groupId, id, title,
                        hint, iconResId, valueProvidedAsync, showsIconSpace, flags);
            default:
                /* TEXT */
                // TODO: This may be a problem. Will the default always be TEXT?
                return new SublimeTextMenuItem(groupId, id, title,
                        hint, iconResId, valueProvidedAsync, showsIconSpace, flags);
        }
    }
}
