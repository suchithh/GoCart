package com.Go.GoCart;

import androidx.appcompat.app.AppCompatActivity;
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

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView noOfResults;
    private Toolbar toolbar;
    public List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    public List<Float> ratings = new ArrayList<>();
    public ItemAdapter itemAdapter;
    private DisplayActivity activity;
    public String s, category;
    private BottomAppBar bar;
    private ImageButton scanAgain, filter, sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        activity = this;
        String[] mNames = getIntent().getStringArrayExtra("name");
        String[] mCosts = getIntent().getStringArrayExtra("cost");
        s = getIntent().getStringExtra("query");
        category = getIntent().getStringExtra("cat");
        float[] mRatings = getIntent().getFloatArrayExtra("ratings");
        if (mCosts != null) {
            for (int i = 0; i < mCosts.length; i++) {
                if (mCosts[i] != null) {
                    if (!mCosts[i].contains("₹")) {
                        mCosts[i] = "\u20b9" + mCosts[i];
                    }
                }
            }
        }
        scanAgain = findViewById(R.id.scan_again);
        filter = findViewById(R.id.filter);
        sort = findViewById(R.id.sort);
        String[] mSites = getIntent().getStringArrayExtra("site");
        String[] mLinks = getIntent().getStringArrayExtra("link");
        String[] mImages = getIntent().getStringArrayExtra("image");
        if (mCosts != null) {
            names.addAll(Arrays.asList(mNames));
            costs.addAll(Arrays.asList(mCosts));
            sites.addAll(Arrays.asList(mSites));
            links.addAll(Arrays.asList(mLinks));
            images.addAll(Arrays.asList(mImages));
            if (mRatings != null) {
                for (float m : mRatings) {
                    ratings.add(m);
                }
            }
        }
        noOfResults = findViewById(R.id.no_of_results);
        bar = findViewById(R.id.bottom);

        recyclerView = findViewById(R.id.recycler_view);
        if (mCosts != null) {
            itemAdapter = new ItemAdapter(names, costs, sites, links, images, ratings);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(DisplayActivity.this));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(DisplayActivity.this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            resetCount(itemAdapter.getItemCount() + " results");
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noOfResults.setText("0 Results");
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Typeface tf = ResourcesCompat.getFont(this, R.font.lato);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarThemeC);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarThemeE);

        toolbar = findViewById(R.id.toolbar_purchases);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24, null));
        toolbar.setNavigationOnClickListener(view -> startActivity(new Intent(DisplayActivity.this, MainActivity.class)));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_again_item) {
                Intent intent = new Intent(DisplayActivity.this, MainActivity.class);
                intent.putExtra("doggo", "catto");
                startActivityForResult(intent, 4);
                finish();
                return true;
            }
            return false;
        });
        scanAgain.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayActivity.this, ScanActivity.class);
            DisplayActivity.this.startActivity(intent);
            finish();
        });
        filter.setOnClickListener(view -> {
            FilterAlertDialog dialog = new FilterAlertDialog(DisplayActivity.this);
            dialog.show();
        });
        if (mCosts != null && mCosts.length > 0) {
            sort.setOnClickListener(view -> {
                SortAlertDialog dialog = new SortAlertDialog(DisplayActivity.this);
                dialog.show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DisplayActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_menu, menu);
        return true;
    }

    public void resetCount(String resultCount) {
        noOfResults.setText(resultCount);
    }
}