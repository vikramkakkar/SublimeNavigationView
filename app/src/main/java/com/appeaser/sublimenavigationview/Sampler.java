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

package com.appeaser.sublimenavigationview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimenavigationviewlibrary.OnNavigationMenuEventListener;
import com.appeaser.sublimenavigationviewlibrary.SublimeBaseMenuItem;
import com.appeaser.sublimenavigationviewlibrary.SublimeMenu;
import com.appeaser.sublimenavigationviewlibrary.SublimeNavigationView;

/**
 * Sample usage
 */
public class Sampler extends AppCompatActivity {

    public static final String TAG = Sampler.class.getSimpleName();

    // Keys used when saving menu state to Bundle
    final String SS_KEY_MENU_1 = "ss.key.menu.1";
    final String SS_KEY_MENU_2 = "ss.key.menu.2";

    // For maintaining menu state in case of multiple menus
    SublimeMenu firstMenu, secondMenu;

    // Navigation menu
    SublimeNavigationView snv;

    TextView tvFirstMenuLabel, tvSecondMenuLabel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sampler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState != null) {
            // retrieve saved instances of the two menus
            if (savedInstanceState.containsKey(SS_KEY_MENU_1)) {
                firstMenu = savedInstanceState.getParcelable(SS_KEY_MENU_1);
            }

            if (savedInstanceState.containsKey(SS_KEY_MENU_2)) {
                secondMenu = savedInstanceState.getParcelable(SS_KEY_MENU_2);
            }
        }

        snv = (SublimeNavigationView) findViewById(R.id.navigation_view);

        tvFirstMenuLabel = (TextView) snv.getHeaderView().findViewById(R.id.tvFirstMenu);
        tvSecondMenuLabel = (TextView) snv.getHeaderView().findViewById(R.id.tvSecondMenu);

        // set listener to get notified of menu events
        snv.setNavigationMenuEventListener(new OnNavigationMenuEventListener() {
            @Override
            public boolean onNavigationMenuEvent(OnNavigationMenuEventListener.Event event,
                                                 SublimeBaseMenuItem menuItem) {
                switch (event) {
                    case CHECKED:
                        Log.i(TAG, "Item checked");
                        break;
                    case UNCHECKED:
                        Log.i(TAG, "Item unchecked");
                        break;
                    case GROUP_EXPANDED:
                        Log.i(TAG, "Group expanded");
                        break;
                    case GROUP_COLLAPSED:
                        Log.i(TAG, "Group collapsed");
                        break;
                    default:
                        //CLICK
                        // Something like handleClick(menuItem);
                        // Here, we toggle the 'checked' state
                        Log.i(TAG, "Item clicked");
                        menuItem.setChecked(!menuItem.isChecked());
                        break;
                }
                return true;
            }
        });

        // set up mechanism to switch between the 2 menus
        snv.getHeaderView().findViewById(R.id.tvFirstMenu)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SublimeMenu menu = snv.getMenu();

                        if (menu.getMenuResourceID() != R.menu.test_nav_menu_1) {
                            secondMenu = menu;

                            if (firstMenu == null) {
                                snv.switchMenuTo(R.menu.test_nav_menu_1);
                            } else {
                                snv.switchMenuTo(firstMenu);
                            }

                            updateMenuLabel();
                        }
                    }
                });

        // set up mechanism to switch between the 2 menus
        snv.getHeaderView().findViewById(R.id.tvSecondMenu)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SublimeMenu menu = snv.getMenu();

                        if (menu.getMenuResourceID() != R.menu.test_nav_menu_2) {
                            firstMenu = menu;

                            if (secondMenu == null) {
                                snv.switchMenuTo(R.menu.test_nav_menu_2);
                            } else {
                                snv.switchMenuTo(secondMenu);
                            }

                            updateMenuLabel();
                        }
                    }
                });

        initSamplerContent();
    }

    private void updateMenuLabel() {
        if (snv.getMenu().getMenuResourceID() == R.menu.test_nav_menu_1) {
            tvFirstMenuLabel.setTypeface(null, Typeface.BOLD);
            tvSecondMenuLabel.setTypeface(null, Typeface.NORMAL);
        } else if (snv.getMenu().getMenuResourceID() == R.menu.test_nav_menu_2) {
            tvSecondMenuLabel.setTypeface(null, Typeface.BOLD);
            tvFirstMenuLabel.setTypeface(null, Typeface.NORMAL);
        }
    }

    /**
     * Information about the library
     */
    private void initSamplerContent() {
        TextView tvSummary = (TextView) findViewById(R.id.tv_summary_heading);
        tvSummary.setText(Html.fromHtml("... is a complete rewrite of <b>NavigationView</b> from <b>Design Support library</b>. What it allows:"));

        TextView tvSummaryBullet1 = (TextView) findViewById(R.id.tv_summary_bullet_1);
        tvSummaryBullet1.setText(Html.fromHtml("Menu definition in <b>XML</b>"));

        TextView tvSummaryBullet2 = (TextView) findViewById(R.id.tv_summary_bullet_2);
        tvSummaryBullet2.setText(Html.fromHtml("Menu implements <b>Parcelable</b> for retention of menu state"));

        TextView tvSummaryBullet3 = (TextView) findViewById(R.id.tv_summary_bullet_3);
        tvSummaryBullet3.setText(Html.fromHtml("Choose from <b>Text, Checkbox, Switch, Group, TextWithBadge</b> menu item types"));

        TextView tvSummaryBullet4 = (TextView) findViewById(R.id.tv_summary_bullet_4);
        tvSummaryBullet4.setText(Html.fromHtml("Custom <b>MenuInflater</b> allows defining menu items such as <b>&lt;Checkbox ... /&gt;</b> directly in <b>XML</b>"));

        TextView tvSummaryBullet5 = (TextView) findViewById(R.id.tv_summary_bullet_5);
        tvSummaryBullet5.setText(Html.fromHtml("Switch between any number of Menus <i>without losing state</i>"));

        TextView tvSummaryBullet6 = (TextView) findViewById(R.id.tv_summary_bullet_6);
        tvSummaryBullet6.setText(Html.fromHtml("Collapsible/expandable Groups"));

        TextView tvSummaryBullet7 = (TextView) findViewById(R.id.tv_summary_bullet_7);
        tvSummaryBullet7.setText(Html.fromHtml("On-demand indentation (show icon space <i>without assigning an icon</i>)"));

        TextView tvSummaryBullet8 = (TextView) findViewById(R.id.tv_summary_bullet_8);
        tvSummaryBullet8.setText(Html.fromHtml("<b>&lt;TextWithBadge ... /&gt;</b> item displays a (flushed-right) <b><i>badge</i></b> that can be set <b>asynchronously</b> - a <b>ProgressBar</b> is displayed while the value is being retrieved"));

        TextView tvSummaryBullet9 = (TextView) findViewById(R.id.tv_summary_bullet_9);
        tvSummaryBullet9.setText(Html.fromHtml("Provide custom <b>Typefaces</b> and <b>text-styles</b> (normal, <b>bold</b>, <i>italic</i> &amp; <b><i>bold_italic</i></b>) for item, hint, group-header etc."));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sampler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_rate_this_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.appeaser.sublimenavigationview")));
            } catch (ActivityNotFoundException anfe1) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://market.android.com/details?id=com.appeaser.sublimenavigationview")));
                } catch (ActivityNotFoundException anfe2) {
                    Toast.makeText(this, "You need a browser app to view this link",
                            Toast.LENGTH_LONG).show();
                }
            }
            return true;
        } else if (id == R.id.action_github_link) {
            String data = "https://github.com/vikramkakkar/SublimeNavigationView";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data)));
            } catch (ActivityNotFoundException anfe) {
                Toast.makeText(this, "You need a browser app to view this link",
                        Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuLabel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save state for the two menus
        outState.putParcelable(SS_KEY_MENU_1, firstMenu);
        outState.putParcelable(SS_KEY_MENU_2, secondMenu);
    }
}
