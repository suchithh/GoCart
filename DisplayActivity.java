package com.Go.GoCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView noOfResults;
    private Toolbar toolbar;
    private int i;
    private List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    private List<Float> ratings = new ArrayList<>();
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        String[] mNames = getIntent().getStringArrayExtra("name");
        String[] mCosts = getIntent().getStringArrayExtra("cost");
        float[] mRatings = getIntent().getFloatArrayExtra("ratings");
        for (i = 0; i < mCosts.length; i++) {
            if (mCosts[i] != null) {
                if (!mCosts[i].contains("₹")) {
                    mCosts[i] = "\u20b9" + mCosts[i];
                }
            }
        }
        String[] mSites = getIntent().getStringArrayExtra("site");
        String[] mLinks = getIntent().getStringArrayExtra("link");
        String[] mImages = getIntent().getStringArrayExtra("image");
        names.addAll(Arrays.asList(mNames));
        costs.addAll(Arrays.asList(mCosts));
        sites.addAll(Arrays.asList(mSites));
        links.addAll(Arrays.asList(mLinks));
        images.addAll(Arrays.asList(mImages));
        if(mRatings!=null){
        for (float m : mRatings) {
            ratings.add(m);
        }}
        noOfResults = findViewById(R.id.no_of_results);
        noOfResults.setText("" + mNames.length + " results");

        recyclerView = findViewById(R.id.recycler_view);
        itemAdapter = new ItemAdapter(names, costs, sites, links, images, ratings);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayActivity.this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(DisplayActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Typeface tf = ResourcesCompat.getFont(this, R.font.lato);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarThemeC);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarThemeE);

        toolbar = findViewById(R.id.toolbar_purchases);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayActivity.this, MainActivity.class));
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DisplayActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_menu, menu);
        MenuItem item = menu.findItem(R.id.search_view_item);
        SearchView searchView = (SearchView) item.getActionView();
        EditText mEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface tf = ResourcesCompat.getFont(this, R.font.lato);
        mEditText.setTypeface(tf);
        mEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mEditText.setHint("Search for products...");
        mEditText.setHintTextColor(ContextCompat.getColor(this, R.color.Silver));
        ImageView closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_search2);
        closeBtn.setImageResource(R.drawable.ic_close);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        List<String> newNames = new ArrayList<>();
        List<String> newCosts = new ArrayList<>();
        List<String> newSites = new ArrayList<>();
        List<String> newLinks = new ArrayList<>();
        List<String> newImages = new ArrayList<>();
        int i = 0;
        for (; i < names.size(); i++) {
            if (names.get(i).toLowerCase().contains(s.toLowerCase()) || sites.get(i).toLowerCase().contains(s.toLowerCase())) {
                newNames.add(names.get(i));
                newCosts.add(costs.get(i));
                newSites.add(sites.get(i));
                newLinks.add(links.get(i));
                newImages.add(images.get(i));
            }
        }
        itemAdapter.updateList(newNames, newCosts, newSites, newLinks, newImages);
        return true;
    }
}