package com.Go.GoCart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;


public class SettingsFragment extends PreferenceFragment {

    public int scrape_setting;
    public int resultCount;
    public int currTheme;
    public static final String scrapeUsing = "scrape_using";
    public static final String resultnumber = "no_of_results";
    public static final String theme = "theme";
    public static final String goToAbout = "about_here";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ViewGroup parent;
    private View currView;

    @Override
    public void onCreate(@Nullable Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        listener = (sharedPreferences, s) -> {
            Preference preference = findPreference(s);
            if (s.equals(scrapeUsing)) {
                preference.setTitle("Priority");
                scrape_setting = Integer.valueOf(sharedPreferences.getString(s, "2"));
                switch (scrape_setting) {
                    case 1:
                        preference.setSummary("Maximize Result Reliability");
                        break;
                    case 0:
                        preference.setSummary("Maximize Number of Results");
                        break;
                    default:
                        preference.setSummary("Minimize Search Time");
                }
            } else if (s.equals(resultnumber)) {
                preference.setTitle("No. of Results");
                resultCount = Integer.valueOf(sharedPreferences.getString(s, "50"));
                preference.setSummary(resultCount + "");
                preference.setTitle("No. of Results");
            } else if (s.equals(theme)) {
                preference.setTitle("Theme");
                currTheme = Integer.valueOf(sharedPreferences.getString(s, "0"));
                Intent intent = new Intent(parent.getContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                switch (currTheme) {
                    case 2:
                        preference.setSummary("Dark");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case 1:
                        preference.setSummary("Light");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    default:
                        preference.setSummary("System Settings");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                parent.getContext().startActivity(intent);
                preference.setTitle("Theme");
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
        Preference preferenceScrape = findPreference(scrapeUsing);
        preferenceScrape.setTitle("Priority");
        int scrapeSet = Integer.valueOf(getPreferenceScreen().getSharedPreferences().getString(scrapeUsing, "2"));
        switch (scrapeSet) {
            case 1:
                preferenceScrape.setSummary("Maximize Result Reliability");
                break;
            case 0:
                preferenceScrape.setSummary("Maximize Number of Results");
                break;
            default:
                preferenceScrape.setSummary("Minimize Search Time");
                break;
        }
        Preference preferenceResultCount = findPreference(resultnumber);
        preferenceResultCount.setTitle("No. of Results");
        preferenceResultCount.setSummary(getPreferenceScreen().getSharedPreferences().getString(resultnumber, "50"));
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
