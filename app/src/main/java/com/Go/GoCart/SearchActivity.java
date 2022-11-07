package com.Go.GoCart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    int minimum = 0, maximum = 0, results, scrapeusing;
    String urltext, url = null, url2 = null, url1 = null, url3 = null, category, grocery, s;
    Toast noresult, error;
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.recycler_view);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        results = Integer.parseInt(sharedPreferences.getString("no_of_results", "50"));
        scrapeusing = Integer.parseInt(sharedPreferences.getString("scrape_using", "2"));
        error = Toast.makeText(SearchActivity.this, "No results found!", Toast.LENGTH_SHORT);
        noresult = Toast.makeText(getBaseContext(), "No results found!", Toast.LENGTH_SHORT);
        int theme = Integer.parseInt(sharedPreferences.getString("theme", "0"));
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        s = getIntent().getStringExtra("query");
        category = getIntent().getStringExtra("cat");
        maximum = getIntent().getIntExtra("max", (int) Double.POSITIVE_INFINITY);
        if (maximum >= 100000) {
            maximum = (int) Double.POSITIVE_INFINITY;
        }
        minimum = getIntent().getIntExtra("min", 0);
        if (s != null) {
            String text = s.replaceAll(" ", "+");
            String texttemp = text.replaceAll("\"", "+");
            urltext = texttemp.replaceAll("\n", "+");
            if (category.equals("Groceries")) {
                grocery = "&gr=1";
            } else {
                grocery = "&gr=0";
            }
            url = "https://shopscrape.herokuapp.com/api?id=" + urltext + grocery;
            url1 = "https://shopscrape1.herokuapp.com/api?id=" + urltext +  grocery;
            url2 = "https://shopscrape2.herokuapp.com/api?id=" + urltext +  grocery;
            url3 = "https://shopscrapestage.herokuapp.com/api?id=" + urltext + grocery;

            Intent intent = new Intent(SearchActivity.this, FetchActivity.class);
            intent.putExtra("scrapeusing", scrapeusing);
            intent.putExtra("url", url);
            intent.putExtra("url1", url1);
            intent.putExtra("url2", url2);
            intent.putExtra("url3", url3);
            intent.putExtra("urltext", urltext);
            intent.putExtra("resultcount", results);
            intent.putExtra("min", minimum);
            intent.putExtra("max", maximum);
            intent.putExtra("query", s);
            intent.putExtra("cat", category);
            startActivity(intent);
            finish();
        }
    }

    public void onBackPressed() {
        SearchActivity.this.startActivityForResult(new Intent(SearchActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), 2);
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }


}

