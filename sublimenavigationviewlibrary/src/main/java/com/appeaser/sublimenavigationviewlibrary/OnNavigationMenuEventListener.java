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
 * Listener for Menu events
 *
 * GROUP_HEADER_CLICKED added:
 * This is invoked if {@link SublimeMenu#mExpandCollapseGroupOnlyOnChevronClick} is true
 * and the user has clicked the Group Header, but not the expand/collapse chevron.
 */
public interface OnNavigationMenuEventListener {

    // actions

    // GROUP_HEADER_CLICKED added
    // This is invoked if {@link SublimeMenu#mExpandCollapseGroupOnlyOnChevronClick}
    enum Event {
        CLICKED, CHECKED, UNCHECKED, GROUP_EXPANDED, GROUP_COLLAPSED, GROUP_HEADER_CLICKED
    }

    boolean onNavigationMenuEvent(Event event, SublimeBaseMenuItem menuItem);
}
