package com.Go.GoCart;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.bendik.simplerangeview.SimpleRangeView;

public class FilterAlertDialog {

    private DisplayActivity activity;
    private AlertDialog dialog;
    private Button negative, positive;
    private SimpleRangeView bar;
    private EditText rangeMax, rangeMin;
    double max = Double.POSITIVE_INFINITY, min = 0;
    private Spinner spinner;
    private String category = "Non-Groceries";
    public List<Float> newRatings = new ArrayList<>();
    public List<String> newNames = new ArrayList<>(), newCosts = new ArrayList<>(), newSites = new ArrayList<>(), newLinks = new ArrayList<>(), newImages = new ArrayList<>();

    public FilterAlertDialog(DisplayActivity mActivity) {
        activity = mActivity;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_alert_filter, null);
        negative = view.findViewById(R.id.negative);
        positive = view.findViewById(R.id.positive);
        spinner = view.findViewById(R.id.spinner);
        rangeMax = view.findViewById(R.id.rangeMax);
        rangeMin = view.findViewById(R.id.rangeMin);
        bar = view.findViewById(R.id.range_seekbar);
        bar.setCount(101);
        bar.setOnChangeRangeListener((simpleRangeView, i, i1) -> {
            if (i1 >= 100) {
                rangeMax.setText("100000+");
                max = Double.POSITIVE_INFINITY;
            } else {
                rangeMax.setText(1000 * i1 + "");
                max = 1000 * i1;
            }
            rangeMin.setText(1000 * i + "");
            min = 1000 * i;
        });
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
                    bar.setStart((Integer.valueOf(rangeMin.getText().toString())) / 1000);
                    min = Integer.valueOf(rangeMin.getText().toString());
                } else {
                    bar.setStart(0);
                    min = 0;
                }
            }
        });
        rangeMin.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                rangeMax.requestFocus();
            }
            return false;
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
                if (rangeMax.getText() != null && !rangeMax.getText().toString().equals("") && !rangeMax.getText().toString().contains("+")) {
                    bar.setEnd((Integer.parseInt(String.valueOf(rangeMax.getText()).replace("+", "")) / 1000));
                    max = Integer.parseInt(rangeMax.getText().toString());
                } else {
                    bar.setEnd(100);
                    max = Double.POSITIVE_INFINITY;
                }
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(activity, R.array.Categories, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        negative.setOnClickListener(view1 -> dismiss());
        positive.setOnClickListener(view12 -> {
            if (activity.category.equals("catto")) {
                for (int i = 0; i < activity.costs.size(); i++) {
                    if (!activity.costs.get(i).replace("\u20b9", "").replace(",", "").equals("Out of stock")) {
                        if (Double.parseDouble(activity.costs.get(i).replace("\u20b9", "").replace(",", "")) <= max && Double.parseDouble(activity.costs.get(i).replace("\u20b9", "").replace(",", "")) >= min) {
                            newRatings.add(activity.ratings.get(i));
                            newCosts.add(activity.costs.get(i).replace("\u20b9", "").replace(",", ""));
                            newNames.add(activity.names.get(i));
                            newSites.add(activity.sites.get(i));
                            newLinks.add(activity.links.get(i));
                            newImages.add(activity.images.get(i));
                        }
                    }
                }
                activity.itemAdapter.updateList(newNames, newCosts, newSites, newLinks, newImages, newRatings);
                dismiss();
            } else if (!category.equals(activity.category)) {
                //place search request
                Intent intent = new Intent(activity, SearchActivity.class);
                intent.putExtra("query", activity.s);
                intent.putExtra("cat", category);
                intent.putExtra("max", (int) max);
                intent.putExtra("min", (int) min);
                dismiss();
                activity.startActivity(intent);
            } else {
                //filter within results
                for (int i = 0; i < activity.costs.size(); i++) {
                    if (!activity.costs.get(i).replace("\u20b9", "").replace(",", "").equals("Out of stock")) {
                        if (Double.parseDouble(activity.costs.get(i).replace("\u20b9", "").replace(",", "")) <= max && Double.parseDouble(activity.costs.get(i).replace("\u20b9", "").replace(",", "")) >= min) {
                            newRatings.add(activity.ratings.get(i));
                            newCosts.add(activity.costs.get(i).replace("\u20b9", "").replace(",", ""));
                            newNames.add(activity.names.get(i));
                            newSites.add(activity.sites.get(i));
                            newLinks.add(activity.links.get(i));
                            newImages.add(activity.images.get(i));
                        }
                    }
                }
                if (activity.costs.size() > 0 && activity.costs != null) {
                    activity.itemAdapter.updateList(newNames, newCosts, newSites, newLinks, newImages, newRatings);
                    if (activity.itemAdapter.getItemCount() == 0) {
                        activity.recyclerView.setVisibility(View.INVISIBLE);
                    } else {
                        activity.recyclerView.setVisibility(View.VISIBLE);
                    }
                    activity.resetCount(activity.itemAdapter.getItemCount() + " results");
                }
                dismiss();
            }
        });
        activity.runOnUiThread(() -> {
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(true);
            dialog.show();
        });

    }

    public void dismiss() {
        dialog.dismiss();
    }
}
