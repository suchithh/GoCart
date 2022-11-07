package com.Go.GoCart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;

public class AppIntro extends com.github.appintro.AppIntro2 {
    int bgcolor;
    int textcolor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.valueOf(sharedPreferences.getString("theme", "0"));
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bgcolor = Color.rgb(238, 238, 238);
                    textcolor=Color.rgb(34,34,34);
                }
                break;

            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bgcolor = Color.rgb(34, 34, 34);
                    textcolor=Color.rgb(238,238,238);
                }
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        showStatusBar(true);
        setImmersiveMode();
        setIndicatorColor(
                getColor(R.color.green),
                getColor(R.color.colorAccent)
        );

        if (theme == 0) {
            addSlide(AppIntroFragment.newInstance("Welcome to GoCart!", "Comparisons Now Made Easier!",
                    R.drawable.ic_gocart, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Shopping Now Made Easier", "GoCart helps you compare prices of products to online stores, ad-free!",
                    R.drawable.ic_baseline_computer_24, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Barcode Scanner", "Tap on the Camera Icon to Scan for the Barcode",
                    R.drawable.camera_slider, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Search Bar", "Alternatively, You Can Search For Products By Tapping The Search Icon As Well!",
                    R.drawable.ic_search, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Wishlist", "You Can Also Add Products To Your Wishlist By Tapping On the Add to Wishlist Button",
                    R.drawable.cart, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Settings", "You Can Also Customize The App (Themes, View etc) By Tapping On the Settings Icon",
                    R.drawable.ic_settingsforsplash, ContextCompat.getColor(getApplicationContext(), R.color.cpWhite), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), R.font.lato, R.font.lato));
            addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.activity_disclaimer));
        } else {
            addSlide(AppIntroFragment.newInstance("Welcome to GoCart!", "Comparisons Now Made Easier!",
                    R.drawable.ic_gocart, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Shopping Now Made Easier", "GoCart helps you compare prices of products to online stores, ad-free!",
                    R.drawable.ic_baseline_computer_24, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Barcode Scanner", "Tap on the Camera Icon to Scan for the Barcode",
                    R.drawable.camera_slider, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Search Bar", "Alternatively, You Can Search For Products By Tapping The Search Icon As Well!",
                    R.drawable.ic_search, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Wishlist", "You Can Also Add Products To Your Wishlist By Tapping On the Add to Wishlist Button",
                    R.drawable.cart, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroFragment.newInstance("Settings", "You Can Also Customize The App (Themes, View etc) By Tapping On the Settings Icon",
                    R.drawable.ic_settingsforsplash, bgcolor, textcolor, textcolor, R.font.lato, R.font.lato));
            addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.activity_disclaimer));
        }


        sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences != null) {
            boolean checkShared = sharedPreferences.getBoolean("CheckStated", false);
            if (checkShared) {
                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                finish();
            }
        }
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
        editor.putBoolean("CheckStated", true).commit();
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
        editor.putBoolean("CheckStated", true).commit();
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
        startActivity(intent);
        finish();
    }

}