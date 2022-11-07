package com.Go.GoCart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.bendik.simplerangeview.SimpleRangeView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private BottomAppBar bottomAppBar;
    int PERMISSION_REQUEST_CODE;
    Toast toast;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private Spinner spinner;
    private SearchView searchView;
    private SimpleRangeView bar;
    private Button btnSettings;
    private TextView textView;
    private TextView rangeMark;
    private EditText rangeMin, rangeMax;
    private ImageView imageView;
    private EditText mEditText;
    private double maxPrice = Double.POSITIVE_INFINITY, minPrice = 0;
    Activity activity;
    String category = "Non-Groceries";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        website();
        toast = Toast.makeText(MainActivity.this, "Servers Pinged!", Toast.LENGTH_SHORT);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        spinner = findViewById(R.id.spinner);
        bar = findViewById(R.id.range_seekbar);
        rangeMark = findViewById(R.id.range);
        searchView = findViewById(R.id.searchView);
        btnSettings = findViewById(R.id.toSettings);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        rangeMax = findViewById(R.id.rangeMax); rangeMin = findViewById(R.id.rangeMin);
        activity = this;
        rangeMax.setText("100000+");
        rangeMin.setText("0");
        bar.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        rangeMark.setVisibility(View.INVISIBLE);
        rangeMax.setVisibility(View.INVISIBLE);
        rangeMin.setVisibility(View.INVISIBLE);

        Typeface tf = ResourcesCompat.getFont(this, R.font.lato);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = Integer.valueOf(sharedPreferences.getString("theme", "0"));
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                bottomAppBar.setTitleTextAppearance(this, R.style.BottomAppBarThemeLight);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                bottomAppBar.setTitleTextAppearance(this, R.style.BottomAppBarThemeDark);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.mycart) {
                    intent = new Intent(MainActivity.this, CartActivity.class);
                    intent.putExtra("Class", 0);
                } else if (item.getItemId() == R.id.settings) {
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                } else if (item.getItemId() == R.id.about) {
                    intent = new Intent(MainActivity.this, AboutActivity.class);
                    intent.putExtra("Class", "Main");
                } else if(item.getItemId()==R.id.help){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("CheckStated", false).commit();
                    intent = new Intent(MainActivity.this, AppIntro.class);
                }
                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            }
        });

        final FloatingActionButton fab = findViewById(R.id.floating_action_button);
        final int REQUEST_IMAGE_CAPTURE = 1;
        fab.setOnClickListener(new View.OnClickListener() {
            private File createImageFile() throws IOException {
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = new File(storageDir, "temp" + ".jpg");
                if (!image.exists()) {
                    Log.d("path", image.toString());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(image);
                        fos.flush();
                        fos.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
                return image;
            }

            private boolean checkPermission() {
                int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (result == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    return false;
                }
            }

            private void requestPermission() {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return;
            }

            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                }
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                            "com.Go.GoCart.fileprovider",
                                            photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        } else {
                            requestPermission();
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                }
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                            "com.Go.GoCart.fileprovider",
                                            photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        }
                    }
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.performClick();
            }
        });

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.Categories, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(MainActivity.this);

        rangeMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rangeMin.getText() != null && !rangeMin.getText().toString().equals("")) {
                    bar.setStart((Integer.valueOf(rangeMin.getText().toString()))/1000);
                    minPrice = Integer.valueOf(rangeMin.getText().toString());;
                } else {
                    bar.setStart(0);
                    minPrice = 0;
                }
            }
        });
        rangeMin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    rangeMax.requestFocus();
                }
                return false;
            }
        });

        rangeMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rangeMax.getText() != null && !rangeMax.getText().toString().equals("")) {
                    bar.setEnd((Integer.valueOf(String.valueOf(rangeMax.getText()).replace("+", ""))/1000));
                    maxPrice = Integer.valueOf(rangeMax.getText().toString().replace("+", ""));
                } else {
                    bar.setEnd(100);
                    maxPrice = 100000;
                }
            }
        });
        rangeMax.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchView.requestFocus();
                }
                return false;
            }
        });

        bar.setCount(101);
        bar.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
            @Override
            public void onRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i, int i1) {
                if (i1 >= 100) {
                    rangeMax.setText("100000+");
                    rangeMin.setText(String.valueOf(1000*i));
                } else {
                    rangeMax.setText(String.valueOf(1000*i1));
                    rangeMin.setText(String.valueOf(1000*i));
                }
                maxPrice = (double) 1000*i1;
                minPrice = (double) 1000*i;
            }
        });

        mEditText = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        mEditText.setTypeface(tf);
        ImageView closeBtn = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null));
        ImageView searchIcon = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null));
        searchIcon.setImageResource(R.drawable.ic_search2);
        closeBtn.setImageResource(R.drawable.ic_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setText("");
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                btnSettings.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                bar.setVisibility(View.INVISIBLE);
                rangeMark.setVisibility(View.INVISIBLE);
                rangeMax.setVisibility(View.INVISIBLE);
                rangeMin.setVisibility(View.INVISIBLE);
            }
        });
        mEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mEditText.setHintTextColor(ContextCompat.getColor(this, R.color.Silver));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(MainActivity.this, search.class);
                intent.putExtra("query", s);
                intent.putExtra("cat", category);
                intent.putExtra("max", (int) maxPrice);
                intent.putExtra("min", (int) minPrice);
                startActivity(intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != "") {
                    textView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    btnSettings.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.VISIBLE);
                    rangeMark.setVisibility(View.VISIBLE);
                    rangeMax.setVisibility(View.VISIBLE);
                    rangeMin.setVisibility(View.VISIBLE);
                } else if (s != null) {
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    btnSettings.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                    bar.setVisibility(View.INVISIBLE);
                    rangeMark.setVisibility(View.INVISIBLE);
                    rangeMax.setVisibility(View.INVISIBLE);
                    rangeMin.setVisibility(View.INVISIBLE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    btnSettings.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                    bar.setVisibility(View.INVISIBLE);
                    rangeMark.setVisibility(View.INVISIBLE);
                    rangeMax.setVisibility(View.INVISIBLE);
                    rangeMin.setVisibility(View.INVISIBLE);
                    hideKeyboard(activity);
                }
                return false;
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent intent = new Intent(MainActivity.this, Barcode_Scanner.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    public void onBackPressed() {
        if (imageView.getVisibility() == View.INVISIBLE) {
            hideKeyboard(activity);
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            btnSettings.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.INVISIBLE);
        } else {
            if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            } else {
                Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }


    private void website() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet("https://shopscrape.herokuapp.com/");
                try {
                    HttpResponse response = client.execute(request);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        mEditText.setText("");
        bar.setVisibility(View.INVISIBLE);
        rangeMark.setVisibility(View.INVISIBLE);
        rangeMin.setVisibility(View.INVISIBLE);
        rangeMax.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}