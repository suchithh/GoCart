package com.Go.GoCart;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortAlertDialog {

    private DisplayActivity activity;
    private AlertDialog dialog;
    private Button negative, positive;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    String sortMethod;

    public SortAlertDialog(DisplayActivity mActivity) {
        activity = mActivity;
    }

    public void show() {

        activity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
            final LayoutInflater inflater = LayoutInflater.from(activity);
            View dView = inflater.inflate(R.layout.dialog_sort, null);
            negative = dView.findViewById(R.id.negative);
            positive = dView.findViewById(R.id.positive);
            radioGroup = dView.findViewById(R.id.radioGroup);
            negative.setOnClickListener(view -> dismiss());
            positive.setOnClickListener(view -> {
                int checkedId = radioGroup.getCheckedRadioButtonId();
                radioButton = dView.findViewById(checkedId);
                if (radioButton != null) {
                    sortMethod = radioButton.getText().toString();
                } else {
                    sortMethod = "catto";
                }
                switch (sortMethod) {
                    case "Price (Low to High)":
                        sort("PLH");
                        break;
                    case "Price (High to Low)":
                        sort("PHL");
                        break;
                    case "Seller":
                        sort("S");
                        break;
                    default:
                        dismiss();
                        break;
                }
                dismiss();
            });
            builder.setView(dView);
            dialog = builder.create();
            dialog.setCancelable(true);
            dialog.show();
        });
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void sort(String sortBy) {
        List<PItem> items = new ArrayList<>();
        for (int i = 0; i < activity.names.size(); i++) {
            PItem item = new PItem(activity.names.get(i), activity.costs.get(i), activity.sites.get(i), activity.links.get(i), activity.images.get(i), activity.ratings.get(i));
            items.add(item);
        }
        Collections.sort(items, (item0, item1) -> {
            if (sortBy.equals("PLH")) {
                return Double.compare(Double.parseDouble(item0.getCost().replace("\u20b9", "").replace(",", "").replace("Out of stock", String.valueOf(Double.POSITIVE_INFINITY))), Double.parseDouble(item1.getCost().replace("\u20b9", "").replace(",", "").replace("Out of stock", String.valueOf(Double.POSITIVE_INFINITY))));
            } else if (sortBy.equals("PHL")) {
                return Double.compare(Double.parseDouble(item1.getCost().replace("\u20b9", "").replace(",", "").replace("Out of stock", String.valueOf(0))), Double.parseDouble(item0.getCost().replace("\u20b9", "").replace(",", "").replace("Out of stock", String.valueOf(0))));
            } else {
                return item0.getSite().compareTo(item1.getSite());
            }
        });
        List<String> newNames = new ArrayList<>(PItem.getAllNames(items)), newCosts = new ArrayList<>(PItem.getAllCosts(items)), newSites = new ArrayList<>(PItem.getAllSites(items)), newLinks = new ArrayList<>(PItem.getAllLinks(items)), newImages = new ArrayList<>(PItem.getAllImages(items));
        List<Float> newRatings = new ArrayList<>(PItem.getAllRatings(items));
        activity.itemAdapter.updateList(newNames, newCosts, newSites, newLinks, newImages, newRatings);
    }

}
