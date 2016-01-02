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

/**
 * Switch menu item implementation.
 */
public class SublimeSwitchMenuItem extends SublimeBaseMenuItem {

    public SublimeSwitchMenuItem(SublimeMenu menu, int group, int id,
                                 CharSequence title, CharSequence hint,
                                 boolean valueProvidedAsync, boolean showsIconSpace) {
        super(menu, group, id, title, hint, ItemType.SWITCH, valueProvidedAsync, showsIconSpace);
    }

    // Restores state
    public SublimeSwitchMenuItem(int group, int id,
                                 CharSequence title, CharSequence hint,
                                 int iconResId,
                                 boolean valueProvidedAsync, boolean showsIconSpace,
                                 int flags) {
        super(group, id, title, hint, iconResId, ItemType.SWITCH, valueProvidedAsync, showsIconSpace, flags);
    }

    @Override
    public boolean invoke() {
        if (isCheckable()) {
            setChecked(!isChecked());
            return invoke(isChecked() ?
                    OnNavigationMenuEventListener.Event.CHECKED
                    : OnNavigationMenuEventListener.Event.UNCHECKED, this);
        } else {
            return invoke(OnNavigationMenuEventListener.Event.CLICKED, this);
        }
    }
}
