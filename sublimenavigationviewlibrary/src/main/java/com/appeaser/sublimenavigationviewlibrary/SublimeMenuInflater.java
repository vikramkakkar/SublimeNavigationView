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
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Custom menu inflater.
 */
public class SublimeMenuInflater {
    private static final String TAG = SublimeMenuInflater.class.getSimpleName();

    /**
     * Menu tag name in XML.
     */
    private static final String XML_MENU = "menu";

    /**
     * Group tag name in XML.
     */
    private static final String XML_GROUP = "Group";

    /**
     * Text item tag name in XML.
     */
    private static final String XML_TEXT = "Text";

    /**
     * Text w/ badge item tag name in XML.
     */
    private static final String XML_TEXT_WITH_BADGE = "TextWithBadge";

    /**
     * Checkbox item tag name in XML.
     */
    private static final String XML_CHECKBOX = "Checkbox";

    /**
     * Switch item tag name in XML.
     */
    private static final String XML_SWITCH = "Switch";

    /**
     * GroupHeader item tag name in XML.
     */
    private static final String XML_GROUP_HEADER = "GroupHeader";

    /**
     * Separator item tag name in XML.
     */
    private static final String XML_SEPARATOR = "Separator";

    private static final int NO_ID = -1;

    private Context mContext;

    /**
     * Constructs a menu inflater.
     *
     * @param context Context of the calling entity.
     */
    public SublimeMenuInflater(Context context) {
        mContext = context;
    }

    /**
     * Inflate a menu hierarchy from the specified XML resource. Throws
     * {@link InflateException} if there is an error.
     *
     * @param menuRes Resource ID for an XML layout resource to load (e.g.,
     *                <code>R.menu.nav_main</code>)
     * @param menu    The Menu to inflate into. The items and groups will be
     *                added to this Menu.
     */
    public void inflate(int menuRes, SublimeMenu menu) {
        XmlResourceParser parser = null;
        try {
            parser = mContext.getResources().getLayout(menuRes);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            parseMenu(parser, attrs, menu);
        } catch (XmlPullParserException e) {
            throw new InflateException("Error inflating menu XML", e);
        } catch (IOException e) {
            throw new InflateException("Error inflating menu XML", e);
        } finally {
            if (parser != null) parser.close();
        }
    }

    /**
     * Called internally to fill the given menu.
     *
     * @param parser an XmlPullParser used to parse XML menu definition
     * @param attrs  attributes
     * @param menu   the menu to inflate into
     * @throws XmlPullParserException error occurred while parsing XML
     * @throws IOException            in case the given xml file cannot be accessed
     *                                because of an I/O related issue.
     */
    private void parseMenu(XmlPullParser parser, AttributeSet attrs, SublimeMenu menu)
            throws XmlPullParserException, IOException {
        MenuState menuState = new MenuState(menu);

        int eventType = parser.getEventType();
        String tagName;
        boolean lookingForEndOfUnknownTag = false;
        String unknownTagName = null;

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
                if (tagName.equals(XML_MENU)) {
                    // Go to next tag
                    eventType = parser.next();
                    break;
                }

                throw new RuntimeException("Expecting menu, got " + tagName);
            }
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);

        boolean reachedEndOfMenu = false;
        while (!reachedEndOfMenu) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (lookingForEndOfUnknownTag) {
                        break;
                    }

                    tagName = parser.getName();
                    if (tagName.equals(XML_GROUP)) {
                        // A Group item cannot have other Group items as children
                        if (menuState.groupId != MenuState.defaultGroupId) {
                            throw new RuntimeException("A 'Group' item cannot have " +
                                    "other 'Group' items as children.");
                        }

                        menuState.readGroup(attrs);
                        menuState.addGroup();
                    } else if (tagName.equals(XML_TEXT)
                            || tagName.equals(XML_TEXT_WITH_BADGE)
                            || tagName.equals(XML_CHECKBOX)
                            || tagName.equals(XML_SWITCH)) {
                        menuState.readMenuItem(attrs, tagName);
                    } else if (tagName.equals(XML_SEPARATOR)) {
                        menuState.readMenuItem(attrs, tagName);
                    } else if (tagName.equals(XML_GROUP_HEADER)) {
                        if (menuState.groupId == MenuState.defaultGroupId) {
                            throw new RuntimeException("'GroupHeader' item should " +
                                    "be placed inside a Group element.");
                        }

                        menuState.readMenuItem(attrs, tagName);
                    } else if (tagName.equals(XML_MENU)) {
                        throw new RuntimeException("Sub-menus are not supported. " +
                                "Similar functionality can be afforded " +
                                "using the 'group' tag.");
                    } else {
                        lookingForEndOfUnknownTag = true;
                        unknownTagName = tagName;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if (lookingForEndOfUnknownTag && tagName.equals(unknownTagName)) {
                        lookingForEndOfUnknownTag = false;
                        unknownTagName = null;
                    } else if (tagName.equals(XML_GROUP)) {
                        if (menuState.isGroupCollapsible()
                                && menuState.groupHeadersAdded != 1) {
                            if (menuState.groupHeadersAdded < 1) {
                                throw new RuntimeException("A 'GroupHeader' is required " +
                                        "to create a 'collapsible' Group.");
                            } else {
                                throw new RuntimeException("A 'collapsible' Group can only " +
                                        "have ONE 'GroupHeader'. You have provided: "
                                        + menuState.groupHeadersAdded + ".");
                            }
                        }

                        menuState.resetGroup();
                    } else if (tagName.equals(XML_TEXT)
                            || tagName.equals(XML_TEXT_WITH_BADGE)
                            || tagName.equals(XML_CHECKBOX)
                            || tagName.equals(XML_SWITCH)
                            || tagName.equals(XML_GROUP_HEADER)
                            || tagName.equals(XML_SEPARATOR)) {
                        // Add the item if it hasn't been added (if the item was
                        // a submenu, it would have been added already)
                        if (!menuState.hasAddedItem()) {
                            menuState.addItem();
                        }
                    } else if (tagName.equals(XML_MENU)) {
                        reachedEndOfMenu = true;
                    }
                    break;

                case XmlPullParser.END_DOCUMENT:
                    throw new RuntimeException("Unexpected end of document");
            }

            eventType = parser.next();
        }
    }

    /**
     * State for the current menu.
     * <p/>
     * Groups can not be nested unless there is another menu (which will have
     * its state class).
     */
    private class MenuState {
        private SublimeMenu menu;

        /*
         * Group state is set on items as they are added, allowing an item to
         * override its group state. (As opposed to set on items at the group end tag.)
         */
        private int groupId;
        private boolean groupVisible;
        private boolean groupEnabled;
        private boolean groupIsCollapsible;
        private boolean groupIsCollapsed;
        private SublimeGroup.CheckableBehavior groupCheckableBehavior;
        private int groupHeadersAdded;

        private boolean itemAdded;
        private int itemId;
        private CharSequence itemTitle;
        private CharSequence itemBadgeText;
        private int itemIconResId;

        private boolean itemCheckable;
        private boolean itemChecked;
        private boolean itemVisible;
        private boolean itemEnabled;
        private boolean itemShowIconSpace;

        private SublimeBaseMenuItem.ItemType itemType;
        private boolean valueProvidedAsync;
        private CharSequence itemHint;

        private static final int defaultGroupId = NO_ID;
        private static final int defaultItemId = NO_ID;

        private static final int defaultItemCheckable = 1;
        private static final boolean defaultItemChecked = false;
        private static final boolean defaultItemVisible = true;
        private static final boolean defaultItemEnabled = true;
        private static final boolean defaultItemShowIconSpace = false;
        private static final boolean defaultGroupCollapsible = false;
        private static final boolean defaultGroupCollapsed = false;
        private static final int defaultGroupHeadersAdded = 0;

        public MenuState(final SublimeMenu menu) {
            this.menu = menu;
            resetGroup();
        }

        public void addGroup() {
            menu.addGroup(groupId, groupIsCollapsible, groupIsCollapsed,
                    groupEnabled, groupVisible, groupCheckableBehavior);
        }

        public boolean isGroupCollapsible() {
            return groupIsCollapsible;
        }

        public void resetGroup() {
            groupId = defaultGroupId;
            groupVisible = defaultItemVisible;
            groupEnabled = defaultItemEnabled;
            groupIsCollapsed = defaultGroupCollapsed;
            groupIsCollapsible = defaultGroupCollapsible;
            groupCheckableBehavior = SublimeGroup.CheckableBehavior.ALL;
            groupHeadersAdded = defaultGroupHeadersAdded;
        }

        /**
         * Called when the parser is pointing to a group tag.
         */
        public void readGroup(AttributeSet attrs) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SublimeMenuGroup);

            groupId = a.getResourceId(R.styleable.SublimeMenuGroup_android_id, defaultGroupId);
            groupVisible = a.getBoolean(R.styleable.SublimeMenuGroup_android_visible, defaultItemVisible);
            groupEnabled = a.getBoolean(R.styleable.SublimeMenuGroup_android_enabled, defaultItemEnabled);
            groupIsCollapsible = a.getBoolean(R.styleable.SublimeMenuGroup_collapsible,
                    defaultGroupCollapsible);
            groupIsCollapsed = a.getBoolean(R.styleable.SublimeMenuGroup_collapsed,
                    defaultGroupCollapsed);

            groupCheckableBehavior = getGroupCheckableBehavior(
                    a.getInt(R.styleable.SublimeMenuGroup_android_checkableBehavior,
                            defaultItemCheckable));

            a.recycle();
        }

        /**
         * Returns {@link SublimeGroup.CheckableBehavior} corresponding to the
         * passed value.
         *
         * @param checkable value defined in XML
         * @return {@link SublimeGroup.CheckableBehavior} that corresponds to
         * {@param checkable}.
         */
        private SublimeGroup.CheckableBehavior getGroupCheckableBehavior(final int checkable) {
            switch (checkable) {
                case 1:
                    return SublimeGroup.CheckableBehavior.ALL;
                case 2:
                    return SublimeGroup.CheckableBehavior.SINGLE;
                default:
                    return SublimeGroup.CheckableBehavior.NONE;
            }
        }

        /**
         * Called when the parser is pointing to a generic item tag.
         */
        public void readMenuItem(AttributeSet attrs, String tagName) {
            itemType = getItemType(tagName);

            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.SublimeMenuGenericItem);

            // Inherit attributes from the group as default value
            itemId = a.getResourceId(R.styleable.SublimeMenuGenericItem_android_id, defaultItemId);
            itemTitle = a.getText(R.styleable.SublimeMenuGenericItem_android_title);
            itemHint = a.getText(R.styleable.SublimeMenuGenericItem_android_hint);
            itemIconResId = a.getResourceId(R.styleable.SublimeMenuGenericItem_android_icon, 0);

            itemCheckable = a.getBoolean(R.styleable.SublimeMenuGenericItem_android_checkable,
                    (groupCheckableBehavior != SublimeGroup.CheckableBehavior.NONE));

            itemChecked = a.getBoolean(R.styleable.SublimeMenuGenericItem_android_checked, defaultItemChecked);
            itemVisible = a.getBoolean(R.styleable.SublimeMenuGenericItem_android_visible, groupVisible);
            itemEnabled = a.getBoolean(R.styleable.SublimeMenuGenericItem_android_enabled, groupEnabled);
            itemShowIconSpace = a.getBoolean(R.styleable.SublimeMenuGenericItem_showIconSpace,
                    itemIconResId != 0);

            valueProvidedAsync
                    = a.getBoolean(R.styleable.SublimeMenuGenericItem_valueProvidedAsync, false);

            itemBadgeText = a.getText(R.styleable.SublimeMenuGenericItem_badgeText);

            a.recycle();

            itemAdded = false;
        }

        private SublimeBaseMenuItem.ItemType getItemType(final String type) {
            switch (type) {
                case XML_CHECKBOX:
                    return SublimeBaseMenuItem.ItemType.CHECKBOX;
                case XML_SWITCH:
                    return SublimeBaseMenuItem.ItemType.SWITCH;
                case XML_TEXT_WITH_BADGE:
                    return SublimeBaseMenuItem.ItemType.BADGE;
                case XML_GROUP_HEADER:
                    return SublimeBaseMenuItem.ItemType.GROUP_HEADER;
                case XML_SEPARATOR:
                    return SublimeBaseMenuItem.ItemType.SEPARATOR;
                default:
                    /* XML_TEXT */
                    return SublimeBaseMenuItem.ItemType.TEXT;
            }
        }

        private void setItem(SublimeBaseMenuItem item) {
            // Ordering: 'setChecked(boolean)' checks if
            // the item 'isCheckable()' before making changes.
            item.setCheckable(itemCheckable)
                    .setChecked(itemChecked)
                    .setVisible(itemVisible)
                    .setEnabled(itemEnabled)
                    .setIcon(itemIconResId)
                    .setHint(itemHint)
                    .setShowsIconSpace(itemShowIconSpace)
                    .setValueProvidedAsync(valueProvidedAsync);
        }

        public void addItem() {
            itemAdded = true;

            switch (itemType) {
                case CHECKBOX:
                    setItem(menu.addCheckboxItem(groupId, itemId,
                            itemTitle, itemHint, itemShowIconSpace));
                    break;
                case SWITCH:
                    setItem(menu.addSwitchItem(groupId, itemId,
                            itemTitle, itemHint, itemShowIconSpace));
                    break;
                case BADGE:
                    setItem(menu.addTextWithBadgeItem(groupId, itemId,
                            itemTitle, itemHint, itemBadgeText, itemShowIconSpace));
                    break;
                case SEPARATOR:
                    setItem(menu.addSeparatorItem(groupId, itemId));
                    break;
                case GROUP_HEADER:
                    setItem(menu.addGroupHeaderItem(groupId, itemId,
                            itemTitle, itemHint, itemShowIconSpace));
                    groupHeadersAdded++;
                    break;
                default:
                    /* TEXT */
                    setItem(menu.addTextItem(groupId, itemId,
                            itemTitle, itemHint, itemShowIconSpace));
                    break;
            }
        }

        public boolean hasAddedItem() {
            return itemAdded;
        }
    }
}
