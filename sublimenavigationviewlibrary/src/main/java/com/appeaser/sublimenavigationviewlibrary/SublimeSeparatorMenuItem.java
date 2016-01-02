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
 * Separator menu item implementation.
 *
 * Created by Vikram.
 */
public class SublimeSeparatorMenuItem extends SublimeBaseMenuItem {
    private static final String EMPTY_STRING = "";

    public SublimeSeparatorMenuItem(SublimeMenu menu, int group, int id) {
        super(menu, group, id, EMPTY_STRING, EMPTY_STRING, ItemType.SEPARATOR, false, false);
    }

    // Restores state //
    public SublimeSeparatorMenuItem(int group, int id) {
        super(group, id, EMPTY_STRING, EMPTY_STRING, NO_ICON,
                ItemType.SEPARATOR, false, false, ENABLED);
    }

    @Override
    public boolean invoke() {
        return false;
    }
}
