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

import android.os.Bundle;

/**
 * Text with Badge menu item implementation.
 *
 * Created by Vikram.
 */
public class SublimeTextWithBadgeMenuItem extends SublimeBaseMenuItem {

    private static final String SS_BADGE_TEXT = "ss.badge.text";

    private CharSequence mBadgeText;

    public SublimeTextWithBadgeMenuItem(SublimeMenu menu, int group, int id,
                                        CharSequence title, CharSequence hint,
                                        boolean valueProvidedAsync,
                                        CharSequence badgeText, boolean showsIconSpace) {
        super(menu, group, id, title, hint, ItemType.BADGE, valueProvidedAsync, showsIconSpace);
        mBadgeText = badgeText;
    }

    // Restores state
    public SublimeTextWithBadgeMenuItem(int group, int id,
                                        CharSequence title, CharSequence hint,
                                        int iconResId,
                                        boolean valueProvidedAsync,
                                        CharSequence badgeText, boolean showsIconSpace,
                                        int flags) {
        super(group, id, title, hint, iconResId, ItemType.BADGE, valueProvidedAsync,
                showsIconSpace, flags);
        mBadgeText = badgeText;
    }

    static SublimeTextWithBadgeMenuItem createFromBundle(Bundle bundle, int group, int id,
                                                         CharSequence title, CharSequence hint,
                                                         int iconResId,
                                                         boolean valueProvidedAsync,
                                                         boolean showsIconSpace, int flags) {
        String badgeText = bundle.getString(SS_BADGE_TEXT);
        return new SublimeTextWithBadgeMenuItem(group, id, title, hint, iconResId,
                valueProvidedAsync, badgeText, showsIconSpace, flags);
    }

    @Override
    public boolean invoke() {
        return invoke(OnNavigationMenuEventListener.Event.CLICKED, this);
    }

    /**
     * Set/change badge text.
     *
     * @param badgeText The text that should be displayed as the badge.
     * @return This {@link SublimeTextWithBadgeMenuItem} for chaining.
     */
    public SublimeTextWithBadgeMenuItem setBadgeText(CharSequence badgeText) {
        mBadgeText = badgeText;
        attemptItemUpdate();

        return this;
    }

    /**
     * Returns the text that should be displayed as the badge.
     *
     * @return text to display as the badge.
     */
    public CharSequence getBadgeText() {
        return mBadgeText;
    }
}
