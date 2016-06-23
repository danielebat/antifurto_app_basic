package com.example.antifurtoappbasic;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fragment.antifurtoappbasic.AppSettings;
import com.fragment.antifurtoappbasic.SecSettings;

/*Classe per la gestione del settaggio dei parametri*/

public class SettingsChooser extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chooser);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {

                return new SecSettings();
            } else if (position == 1) {

                return new AppSettings();
            } else {

                return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Secutity".toUpperCase(l);
                case 1:
                    return "Options".toUpperCase(l);

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_settings_chooser, container, false);
            return rootView;
        }
    }

    public void startPhoneSettings(View v) {

        Intent phoneSettings = new Intent(this, SettingsActivity.class);
        phoneSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(phoneSettings);
    }

    public void startEmailSettings(View v) {

        Intent emailSettings = new Intent(this, SettingsActivity2.class);
        emailSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(emailSettings);
    }

    public void startCodeSettings(View v) {

        if (MainActivity.sharedPreferences.getBoolean("permanentCodeSet", false)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsChooser.this);

            builder.setMessage("Enter your Permament code");

            final EditText editCode = new EditText(getApplicationContext());

            int width = 160;// margin in dips
            int height = 40;
            float d = this.getResources().getDisplayMetrics().density;

            editCode.setWidth((int) (width * d));
            editCode.setHeight((int) (height * d));
            editCode.setBackground(getResources().getDrawable(R.drawable.back));
            editCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editCode.setTextColor(Color.BLACK);
            editCode.setGravity(Gravity.CENTER);
            editCode.setTransformationMethod(new PasswordTransformationMethod());

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 0, 30, 0);
            params.gravity = Gravity.CENTER;

            layout.addView(editCode, params);

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(4);
            editCode.setFilters(filters);

            builder.setView(layout);

            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    String code = editCode.getText().toString().trim();
                    String permanentCode = MainActivity.sharedPreferences.getString("PERMANENT CODE", "");
                    Log.i("PROVA STRINGA 2: ", code);

                    if (code.equals("")) {
                        Toast.makeText(getApplicationContext(), "No Code Typed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!code.equals(permanentCode)) {
                        Toast.makeText(getApplicationContext(), "Code different from previous", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent settingActivityCode = new Intent(getApplicationContext(), SettingsActivityCode.class);
                    settingActivityCode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(settingActivityCode);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });

            builder.create();
            AlertDialog alert = builder.show();

            TextView message = (TextView) alert.findViewById(android.R.id.message);
            message.setGravity(Gravity.CENTER);

        } else {

            Intent settingActivityCode = new Intent(getApplicationContext(), SettingsActivityCode.class);
            settingActivityCode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingActivityCode);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}