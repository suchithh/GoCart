package com.Go.GoCart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


public class SettingsFragment extends PreferenceFragment {

    public int scrape_setting;
    public int resultCount;
    public int pincode;
    public int currTheme;
    public static final String ocrenable = "ocr_enable";
    public static final String scrapeUsing = "scrape_using";
    public static final String resultnumber = "no_of_results";
    public static final String pincoderefer = "pincode";
    public static final String theme = "theme";
    public static final String goToAbout = "about_here";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ViewGroup parent;
    private View currView;

    @Override
    public void onCreate(@Nullable Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Preference preference = findPreference(s);
                if (s.equals(scrapeUsing)) {
                    preference.setTitle("Find Using");
                    scrape_setting = Integer.valueOf(sharedPreferences.getString(s, "0"));
                    switch (scrape_setting) {
                        case 1:
                            preference.setSummary("Server");
                            break;
                        case 2:
                            preference.setSummary("Phone");
                            break;
                        default:
                            preference.setSummary("Default");
                    }
                    preference.setTitle("Find Using");
                } else if (s.equals(pincoderefer)) {
                    pincode = Integer.valueOf(sharedPreferences.getString(s, "000000"));
                    preference.setSummary(pincode + "");
                    preference.setTitle("Pincode");
                } else if (s.equals(resultnumber)) {
                    preference.setTitle("No. of Results");
                    resultCount = Integer.valueOf(sharedPreferences.getString(s, "40"));
                    preference.setSummary(resultCount + "");
                    preference.setTitle("No. of Results");
                } else if (s.equals(theme)) {
                    preference.setTitle("Theme");
                    currTheme = Integer.valueOf(sharedPreferences.getString(s, "0"));
                    Intent intent = null;
                    switch (currTheme) {
                        case 2:
                            preference.setSummary("Dark");
                            intent = new Intent(parent.getContext(), SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            parent.getContext().startActivity(intent);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case 1:
                            preference.setSummary("Light");
                            intent = new Intent(parent.getContext(), SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            parent.getContext().startActivity(intent);

                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        default:
                            preference.setSummary("System Settings");
                            intent = new Intent(parent.getContext(), SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            parent.getContext().startActivity(intent);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    preference.setTitle("Theme");
                } else if (s.equals(ocrenable)) {
                    preference = findPreference(ocrenable);
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ListView listView = view.findViewById(android.R.id.list);
            if (listView != null) {
                listView.setCacheColorHint(Color.TRANSPARENT);
                listView.setFocusableInTouchMode(true);
                listView.setSelector(getResources().getDrawable(R.drawable.transparent_drawable));
            }
        }
        parent = container;
        currView = view;
        view.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        EditTextPreference preferencePincode = (EditTextPreference) findPreference(pincoderefer);
        Typeface tf = ResourcesCompat.getFont(parent.getContext(), R.font.lato);
        preferencePincode.getEditText().setTypeface(tf);
        preferencePincode.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        preferencePincode.setTitle("Pincode");
        preferencePincode.setSummary(getPreferenceScreen().getSharedPreferences().getString(pincoderefer, "None set"));
        preferencePincode.getView(null, parent);
        Preference preferenceScrape = findPreference(scrapeUsing);
        preferenceScrape.setSummary(getPreferenceScreen().getSharedPreferences().getString(scrapeUsing, "Default"));
        preferenceScrape.setTitle("Find Using");
        int scrapeSet = Integer.valueOf(getPreferenceScreen().getSharedPreferences().getString(scrapeUsing, "0"));
        switch (scrapeSet) {
            case 1:
                preferenceScrape.setSummary("Server");
                break;
            case 2:
                preferenceScrape.setSummary("Phone");
                break;
            default:
                preferenceScrape.setSummary("Default");
                break;
        }
        Preference preferenceResultCount = findPreference(resultnumber);
        preferenceResultCount.setTitle("No. of Results");
        preferenceResultCount.setSummary(getPreferenceScreen().getSharedPreferences().getString(resultnumber, "20"));
        Preference preferenceTheme = findPreference(theme);
        preferenceTheme.setTitle("Theme");
        int themeSet = Integer.valueOf(getPreferenceScreen().getSharedPreferences().getString(theme, "0"));
        switch (themeSet) {
            case 2:
                preferenceTheme.setSummary("Dark");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 1:
                preferenceTheme.setSummary("Light");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                preferenceTheme.setSummary("System Default");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        currView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.cpWhite));
        parent.getContext().setTheme(R.style.PreferenceTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
