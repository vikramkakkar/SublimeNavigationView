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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter used to display a SublimeMenu.
 *
 * Created by Vikram.
 */
public class SublimeMenuPresenter {
    private static final String TAG = "Presenter";
    private SublimeNavMenuView mMenuView;
    private LinearLayout mHeader;
    private SublimeMenu mMenu;
    private MenuRecyclerAdapter mAdapter;
    private LayoutInflater mLayoutInflater;
    private int mPaddingTopDefault;
    private SublimeThemer mThemer;

    private Context mContext;

    private boolean mInitializing;

    public SublimeMenuPresenter() {
        mInitializing = true;
    }

    /**
     * Upon creation, and until initializations are done,
     * {@link SublimeMenuPresenter} blocks all calls for invalidation.
     * We can now finalize the initialization phase to allow
     * invalidation of the menu when required.
     */
    protected void setInitializationDone() {
        mInitializing = false;
        invalidateEntireMenu();
    }

    public void initForMenu(Context context, SublimeMenu menu) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMenu = menu;
        mPaddingTopDefault = context.getResources()
                .getDimensionPixelOffset(R.dimen.snv_navigation_padding_top_default);

        if (mThemer == null) {
            mThemer = new SublimeThemer(context);
        }
    }

    void setThemer(SublimeThemer sublimeThemer) {
        mThemer = sublimeThemer;
        invalidateEntireMenu();
    }

    public SublimeNavMenuView getMenuView(ViewGroup root) {
        if (mMenuView == null) {
            mMenuView = (SublimeNavMenuView) mLayoutInflater
                    .inflate(R.layout.sublime_navigation_menu_view, root, false);
            mMenuView.setLayoutManager(new LinearLayoutManager(root.getContext(),
                    LinearLayoutManager.VERTICAL, false));
            if (mAdapter == null) {
                mAdapter = new MenuRecyclerAdapter();
                mAdapter.setHasStableIds(false);
            }

            mHeader = (LinearLayout) mLayoutInflater
                    .inflate(R.layout.sublime_menu_header_item, mMenuView, false);
            mMenuView.setAdapter(mAdapter);
        }

        return mMenuView;
    }

    protected void reportChange(SublimeMenu.Change change,
                                List<SublimeBaseMenuItem> freshData) {
        if (mAdapter == null) return;

        mAdapter.refreshData(freshData);

        switch (change.getChangeType()) {
            case ITEM_INSERTED:
                mAdapter.notifyItemInserted(change.getAffectedPosition());
                break;
            case ITEM_REMOVED:
                mAdapter.notifyItemRemoved(change.getAffectedPosition());
                break;
            case ITEM_CHANGED:
                mAdapter.notifyItemChanged(change.getAffectedPosition());
                break;
            case ITEM_MOVED:
                mAdapter.notifyItemMoved(change.getMovedFromPosition(),
                        change.getMovedToPosition());
                break;
            case RANGE_INSERTED:
                mAdapter.notifyItemRangeInserted(change.getAffectedPosition(),
                        change.getNumberOfAffectedItems());
                break;
            case RANGE_REMOVED:
                mAdapter.notifyItemRangeRemoved(change.getAffectedPosition(),
                        change.getNumberOfAffectedItems());
                break;
            case RANGE_CHANGED:
                mAdapter.notifyItemRangeChanged(change.getAffectedPosition(),
                        change.getNumberOfAffectedItems());
                break;
            default:
                /* INVALIDATE_ENTIRE_MENU */
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    public void invalidateEntireMenu() {
        if (mInitializing) return;

        if (Config.DEBUG) {
            Log.i(TAG, "invalidateEntireMenu()");
        }

        reportChange(
                new SublimeMenu.Change(
                        SublimeMenu.Change.ChangeType.INVALIDATE_ENTIRE_MENU,
                        -1, -1, -1, -1),
                mMenu.getAdapterData());
    }

    public View getHeaderView() {
        return mHeader;
    }

    public View inflateHeaderView(@LayoutRes int res) {
        View view = mLayoutInflater.inflate(res, mHeader, false);
        addHeaderView(view);
        return view;
    }

    public void addHeaderView(@NonNull View view) {
        mHeader.addView(view);
        mMenuView.setPadding(0, 0, 0, mMenuView.getPaddingBottom());
        invalidateEntireMenu();
    }

    public void removeHeaderView(@NonNull View view) {
        mHeader.removeView(view);
        if (mHeader.getChildCount() == 0) {
            mMenuView.setPadding(0, mPaddingTopDefault, 0, mMenuView.getPaddingBottom());
        }

        invalidateEntireMenu();
    }

    protected boolean hasHeader() {
        return mHeader != null && mHeader.getChildCount() > 0;
    }

    ////////////////////// RV

    public interface Holders {
        void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition);
    }

    public abstract class BaseHolder extends RecyclerView.ViewHolder
            implements Holders, View.OnClickListener {

        protected int mPosition;

        public BaseHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mMenu.performItemAction(mAdapter.getItem(getAdapterPosition()));
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            mPosition = boundPosition;
        }

        public SublimeBaseMenuItem getBoundData() {
            return mAdapter != null ? mAdapter.getItem(mPosition) : null;
        }
    }

    private class MenuViewNavigationHeaderHolder extends BaseHolder {
        public MenuViewNavigationHeaderHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef,
                               int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);
        }

        @Override
        public void onClick(View v) {
            // No-op
        }
    }

    private class MenuViewSeparatorHolder extends BaseHolder {
        public MenuViewSeparatorHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);
        }

        @Override
        public void onClick(View v) {
            // No-op
        }
    }

    private class MenuViewSubHeaderHolder extends BaseHolder {
        public MenuViewSubHeaderHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);
            SublimeSubheaderItemView itemSubHeader
                    = (SublimeSubheaderItemView) itemView;
            SublimeGroup group
                    = SublimeMenuPresenter.this
                    .mMenu.getGroup(sublimeMenuItemDef.getGroupId());
            itemSubHeader.initialize(sublimeMenuItemDef, group,
                    mThemer);
        }
    }

    private class MenuViewTextHolder extends BaseHolder {
        public MenuViewTextHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);
            SublimeTextItemView itemTextView
                    = (SublimeTextItemView) itemView;
            itemTextView.initialize(sublimeMenuItemDef, mThemer);
        }
    }

    private class MenuViewCheckboxHolder extends BaseHolder {
        public MenuViewCheckboxHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);

            SublimeCheckboxItemView itemCheckbox
                    = (SublimeCheckboxItemView) itemView;
            itemCheckbox.initialize(sublimeMenuItemDef, mThemer);
        }
    }

    private class MenuViewSwitchHolder extends BaseHolder {
        public MenuViewSwitchHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);

            SublimeSwitchItemView itemSwitch
                    = (SublimeSwitchItemView) itemView;
            itemSwitch.initialize(sublimeMenuItemDef, mThemer);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
        }
    }

    private class MenuViewTextWithBadgeHolder extends BaseHolder {
        public MenuViewTextWithBadgeHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initialize(SublimeBaseMenuItem sublimeMenuItemDef, int boundPosition) {
            super.initialize(sublimeMenuItemDef, boundPosition);
            SublimeTextWithBadgeItemView itemBadge
                    = (SublimeTextWithBadgeItemView) itemView;
            itemBadge.initialize(sublimeMenuItemDef, mThemer);
        }
    }

    private class MenuRecyclerAdapter extends RecyclerView.Adapter<BaseHolder> {
        private static final int VIEW_TYPE_NAVIGATION_HEADER = 0;
        private static final int VIEW_TYPE_SEPARATOR = VIEW_TYPE_NAVIGATION_HEADER + 1;
        private static final int VIEW_TYPE_SUBHEADER = VIEW_TYPE_SEPARATOR + 1;
        private static final int VIEW_TYPE_TEXT = VIEW_TYPE_SUBHEADER + 1;
        private static final int VIEW_TYPE_CHECKBOX = VIEW_TYPE_TEXT + 1;
        private static final int VIEW_TYPE_SWITCH = VIEW_TYPE_CHECKBOX + 1;
        private static final int VIEW_TYPE_BADGE = VIEW_TYPE_SWITCH + 1;
        private final ArrayList<SublimeBaseMenuItem> mItems = new ArrayList<>();

        MenuRecyclerAdapter() {
            this.mItems.addAll(SublimeMenuPresenter.this.mMenu.getAdapterData());
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public BaseHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_NAVIGATION_HEADER:
                    return new MenuViewNavigationHeaderHolder(mHeader);
                case VIEW_TYPE_SEPARATOR:
                    return new MenuViewSeparatorHolder(SublimeMenuPresenter.this
                            .mLayoutInflater.inflate(
                                    R.layout.sublime_separator_item_view, viewGroup, false));
                case VIEW_TYPE_SUBHEADER:
                    return new MenuViewSubHeaderHolder(SublimeMenuPresenter.this
                            .mLayoutInflater.inflate(
                                    R.layout.sublime_subheader_item_view, viewGroup, false));
                case VIEW_TYPE_CHECKBOX:
                    return new MenuViewCheckboxHolder(SublimeMenuPresenter.this
                            .mLayoutInflater.inflate(
                                    R.layout.sublime_checkbox_item_view, viewGroup, false));
                case VIEW_TYPE_SWITCH:
                    return new MenuViewSwitchHolder(SublimeMenuPresenter.this
                            .mLayoutInflater.inflate(
                                    R.layout.sublime_switch_item_view, viewGroup, false));
                case VIEW_TYPE_BADGE:
                    return new MenuViewTextWithBadgeHolder(SublimeMenuPresenter
                            .this.mLayoutInflater.inflate(
                            R.layout.sublime_text_with_badge_item_view, viewGroup, false));
                default:
                    /* VIEW_TYPE_TEXT */
                    return new MenuViewTextHolder(SublimeMenuPresenter.this
                            .mLayoutInflater.inflate(
                                    R.layout.sublime_text_item_view, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(BaseHolder menuViewHolder, int position) {
            menuViewHolder.initialize(getItem(position), position);
        }

        public SublimeBaseMenuItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return resolveItemViewType(getItem(position));
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        private int resolveItemViewType(SublimeBaseMenuItem item) {
            switch (item.getItemType()) {
                case HEADER:
                    return VIEW_TYPE_NAVIGATION_HEADER;
                case SEPARATOR:
                    return VIEW_TYPE_SEPARATOR;
                case GROUP_HEADER:
                    return VIEW_TYPE_SUBHEADER;
                case CHECKBOX:
                    return VIEW_TYPE_CHECKBOX;
                case SWITCH:
                    return VIEW_TYPE_SWITCH;
                case BADGE:
                    return VIEW_TYPE_BADGE;
                default:
                    return VIEW_TYPE_TEXT;
            }
        }

        public void refreshData(List<SublimeBaseMenuItem> freshData) {
            mItems.clear();
            mItems.addAll(freshData);
        }
    }
}