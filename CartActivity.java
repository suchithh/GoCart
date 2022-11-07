package com.Go.GoCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CartActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CartAdapter cartAdapter;
    String[] json;
    JSONObject obj;
    List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    int total;
    int classTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        classTo = getIntent().getIntExtra("Class", 0);

        toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        Typeface tf = ResourcesCompat.getFont(this, R.font.lato);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarThemeC);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarThemeE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (classTo) {
                    case 1:
                        CartActivity.super.onBackPressed();
                    default :
                        startActivity(new Intent(CartActivity.this, MainActivity.class));
                }
            }
        });
        checkfile();

    }

    private void checkfile() {
        int check = 0;
        while (true) {
            File filecheck = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + String.valueOf(check) + ".json");
            if (filecheck.exists()) {
                check++;
            } else {
                break;
            }
        }
        int i = 0;
        json = new String[check];
        while (true) {
            File file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + String.valueOf(i) + ".json");
            if (file.exists()) {
                int length = (int) file.length();

                byte[] bytes = new byte[length];

                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    in.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                json[i] = new String(bytes);
                try {
                    obj = new JSONObject(json[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    costs.add(obj.getString("cost"));
                    total += Integer.valueOf(costs.get(i).replace(",", "").replace(".", "").replace("₹", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    links.add(obj.getString("link"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    names.add(obj.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sites.add(obj.getString("site"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    images.add(obj.getString("image"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i++;
            } else {
                break;
            }
        }
        UI();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CartActivity.this, MainActivity.class));
    }

    private void UI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i) == null || names.get(i).contains("null")) {
                        Collections.replaceAll(names, names.get(i), "No Result");
                    }
                    if ((costs.get(i) == null) || (costs.get(i).contains("rinf"))) {
                        Collections.replaceAll(costs, costs.get(i), "Out of Stock");
                    }
                    if (sites.get(i) == null || sites.get(i).contains("null")) {
                        Collections.replaceAll(sites, sites.get(i), "-");
                    }
                    if (!costs.get(i).contains("\u20b9")) {
                        Collections.replaceAll(costs, costs.get(i), "\u20b9"+costs.get(i));
                    }
                }
                cartAdapter = new CartAdapter(names, costs, sites, links, images);
                recyclerView.setAdapter(cartAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(CartActivity.this, DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(itemDecoration);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
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
    public boolean onQueryTextSubmit(String query) {
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
        cartAdapter.updateList(newNames, newCosts, newSites, newLinks, newImages);
        return true;
    }
}