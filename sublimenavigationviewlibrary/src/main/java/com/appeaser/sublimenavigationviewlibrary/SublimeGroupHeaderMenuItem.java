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
 * Group header menu item implementation.
 *
 * Created by Vikram.
 */
public class SublimeGroupHeaderMenuItem extends SublimeBaseMenuItem {

    public SublimeGroupHeaderMenuItem(SublimeMenu menu, int group,
                                      int id, CharSequence title,
                                      CharSequence hint,
                                      boolean valueProvidedAsync, boolean showsIconSpace) {
        super(menu, group, id, title, hint, ItemType.GROUP_HEADER, valueProvidedAsync, showsIconSpace);
    }

    public SublimeGroupHeaderMenuItem(int group,
                                      int id, CharSequence title,
                                      CharSequence hint,
                                      int iconResId,
                                      boolean valueProvidedAsync, boolean showsIconSpace,
                                      int flags) {
        super(group, id, title, hint, iconResId, ItemType.GROUP_HEADER,
                valueProvidedAsync, showsIconSpace, flags);
    }

    @Override
    public boolean invoke() {
        SublimeGroup group = getMenu().getGroup(getGroupId());

        if (group == null) return false;

        if (group.isCollapsible()) {
            group.setStateCollapsed(!group.isCollapsed());
        }

        return invoke(group.isCollapsed() ?
                OnNavigationMenuEventListener.Event.GROUP_COLLAPSED
                : OnNavigationMenuEventListener.Event.GROUP_EXPANDED, this);
    }
}
