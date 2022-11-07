package com.Go.GoCart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import me.bendik.simplerangeview.SimpleRangeView;

public class FilterAlertDialog {

    private Activity activity;
    private AlertDialog dialog;
    private Button negative;
    private Button positive;
    private TextView rangeMark;
    private SimpleRangeView bar;
    double max = Double.POSITIVE_INFINITY, min = 0;
    private Spinner spinner;
    private String category = "Non-Groceries";

    public FilterAlertDialog (Activity mActivity) {
        activity = mActivity;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_alert_dialog, null);
        negative = view.findViewById(R.id.negative);
        positive = view.findViewById(R.id.positive);
        spinner = view.findViewById(R.id.spinner);
        rangeMark = view.findViewById(R.id.priceMinMax);
        bar = view.findViewById(R.id.range_seekbar);
        bar.setCount(101);
        bar.setOnChangeRangeListener(new SimpleRangeView.OnChangeRangeListener() {
            @Override
            public void onRangeChanged(@NotNull SimpleRangeView simpleRangeView, int i, int i1) {
                if (i1 >= 100) {
                    rangeMark.setText("Price Range: \u20b9"+1000*i+" to \u20b9100000+");
                } else {
                    rangeMark.setText("Price Range: \u20b9"+1000*i+" to \u20b9"+1000*i1);
                }
                max = i1;
                min = i;
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
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, search.class);
                intent.putExtra("cat", category);
                intent.putExtra("max", (int) max);
                intent.putExtra("min", (int) min);
                dismiss();
                activity.startActivity(intent);
            }
        });
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
