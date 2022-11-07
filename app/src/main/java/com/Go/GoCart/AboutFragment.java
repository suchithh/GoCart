package com.Go.GoCart;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AboutFragment extends PreferenceFragment {

    private ViewGroup parent;
    View currView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
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
        parent.getContext().setTheme(R.style.PreferenceTheme);
        currView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.cpWhite));
    }
}
