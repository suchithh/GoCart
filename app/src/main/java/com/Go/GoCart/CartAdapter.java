package com.Go.GoCart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ItemViewHolder> implements View.OnClickListener {

    List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    List<Float> ratings = new ArrayList<>();
    List<Integer> quantities = new ArrayList<>();
    Context context;
    CartActivity activity;
    int position, PERMISSION_REQUEST_CODE;

    public CartAdapter(CartActivity a, List<String> mNames, List<String> mCosts, List<String> mSites, List<String> mLinks, List<String> mImages, List<Integer> mQuantities) {
        names.addAll(mNames);
        costs.addAll(mCosts);
        sites.addAll(mSites);
        links.addAll(mLinks);
        images.addAll(mImages);
        quantities.addAll(mQuantities);
        activity = a;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View cartView = inflater.inflate(R.layout.cart_code_recycled, parent, false);
        cartView.setOnClickListener(this);
        ItemViewHolder viewHolder = new ItemViewHolder(cartView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int i) {
        String name = names.get(i);
        if (names.get(i).length() > 25) {
            name = names.get(i).substring(0, 22) + "...";
        }
        holder.text1.setText(name);
        holder.text2.setText(costs.get(i));
        holder.text3.setText(sites.get(i));
        if (ratings != null && ratings.size() > 0 && ratings.get(i) > 0) {
            holder.text4.setText(((Math.floor(ratings.get(i) * 10)) / 10) + "/5");
        } else {
            holder.text4.setText("-");
        }
        if (!costs.get(i).contains("Out of Stock")) {
            holder.textQuan.setText(quantities.get(i) + "");

        }
        this.position = i;
        if (images.get(i) == null || images.get(i).equals("")) {
            holder.imgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
        } else {
            Glide.with(context).load(images.get(i)).into(holder.imgV);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View x = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_remove, null);
                Button xPos = x.findViewById(R.id.positive);
                Button xNeg = x.findViewById(R.id.negative);
                builder.setView(x);
                final AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                xPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + (i) + ".json");
                        boolean deleted = file.delete();
                        for (int j = i + 1; j < names.size(); j++) {
                            file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + j + ".json");
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
                                try {
                                    File file1 = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + (j - 1) + ".json");
                                    file1.createNewFile();
                                    FileOutputStream stream = new FileOutputStream(file1);
                                    try {
                                        stream.write(bytes);
                                    } finally {
                                        stream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (names.size() - 1 > 0) {
                            file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + (names.size() - 1) + ".json");
                            deleted = file.delete();
                        }
                        names.remove(position);
                        costs.remove(position);
                        sites.remove(position);
                        links.remove(position);
                        images.remove(position);
                        dialog.dismiss();
                        Toast.makeText(context, "Removed From Wishlist!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, CartActivity.class);
                        context.startActivity(intent);
                    }
                });
                xNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        holder.btnVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(links.get(i)));
                context.startActivity(intent);
            }
        });
        holder.btnShare.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, links.get(i));
            context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });

        holder.btnQuanN.setOnClickListener(view -> {
            if (!costs.get(i).contains("Out of Stock")) {
                if (quantities.get(i) > 1) {
                    quantities.set(i, quantities.get(i) - 1);
                    write(holder, i);
                    updateList(names, costs, sites, links, images, ratings, quantities);
                    activity.totalsetter(quantities);
                } else {
                    Toast.makeText(context, "Cannot have less than one item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnQuanP.setOnClickListener(view -> {
            if (!costs.get(i).contains("Out of Stock")) {
                quantities.set(i, quantities.get(i) + 1);
                write(holder, i);
                updateList(names, costs, sites, links, images, ratings, quantities);
                activity.totalsetter(quantities);
            }
        });

        holder.imgV.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View x = LayoutInflater.from(context).inflate(R.layout.cart_code_dialog, null);
            TextView x1 = x.findViewById(R.id.text1);
            TextView x2 = x.findViewById(R.id.text2);
            TextView x3 = x.findViewById(R.id.text3);
            TextView x4 = x.findViewById(R.id.text4);
            Button xAdd = x.findViewById(R.id.btnAdd);
            ImageView ximgV = x.findViewById(R.id.image);
            ImageButton xClose = x.findViewById(R.id.close);
            ImageButton xShare = x.findViewById(R.id.btnShare);
            ImageButton xExpand = x.findViewById(R.id.expand);
            ImageButton xQuanP = x.findViewById(R.id.quan_add);
            ImageButton xQuanN = x.findViewById(R.id.quan_rem);
            TextView xTextQuan = x.findViewById(R.id.text_quan0);
            xAdd.setText("Remove");
            Button xVisit = x.findViewById(R.id.btnVisit);
            x1.setText(names.get(i));
            x2.setText(costs.get(i));
            x3.setText(sites.get(i));
            if (ratings != null && ratings.size() > 0 && ratings.get(i) > 0) {
                x4.setText(((Math.floor(ratings.get(i) * 10)) / 10) + "/5");
            } else {
                x4.setText("-");
            }
            if (images.get(i) != null && !images.get(i).equals("null")) {
                Glide.with(context).load(images.get(i)).into(ximgV);
            } else {
                ximgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
            }
            if (!costs.get(i).contains("Out of Stock")) {
                xTextQuan.setText(quantities.get(i) + "");
            }

            xVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.btnVisit.performClick();
                }
            });
            xAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.btnAdd.performClick();
                }
            });
            builder.setView(x);
            final AlertDialog dialog = builder.create();
            xClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            xShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.btnShare.performClick();
                }
            });
            xExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(images.get(i)), "image/*");
                    context.startActivity(intent);
                }
            });
            dialog.setCancelable(true);
            dialog.show();
            xQuanN.setOnClickListener(view1 -> {
                if (!costs.get(i).contains("Out of Stock")) {
                    quantities.set(i, quantities.get(i) + 1);
                    write(holder, i);
                    updateList(names, costs, sites, links, images, ratings, quantities);
                    xTextQuan.setText(quantities.get(i) + "");
                    activity.totalsetter(quantities);
                }
            });
            xQuanP.setOnClickListener(view12 -> {
                if (!costs.get(i).contains("Out of Stock")) {
                    if (quantities.get(i) > 1) {
                        quantities.set(i, quantities.get(i) - 1);
                        write(holder, i);
                        updateList(names, costs, sites, links, images, ratings, quantities);
                        xTextQuan.setText(quantities.get(i) + "");
                        activity.totalsetter(quantities);
                    } else {
                        Toast.makeText(context, "Oops! You can't want less than one of something.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgV.performClick();
            }
        };
        holder.text1.setOnClickListener(listener);
        holder.text2.setOnClickListener(listener);
        holder.text3.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    @Override
    public void onClick(View view) {

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public void write(ItemViewHolder holder, int i) {

        while (true) {
            File file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + String.valueOf(i) + ".json");
            try {
                file.createNewFile();
                FileOutputStream stream = new FileOutputStream(file);
                try {
                    String json = "{\"cost\":" + "\"" + costs.get(i) + "\"" + ",\"name\":" + "\"" + names.get(i) + "\"," + "\"image\":" + "\"" + images.get(i) + "\"," + "\"link\":" + "\"" + links.get(i) + "\"," + "\"site\":" + "\"" + sites.get(i) + "\"" + "," + "\"quantity\":" + "\"" + (quantities.get(i)) + "\"}";
                    stream.write(json.getBytes());
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public void updateList(List<String> newNames, List<String> newCosts, List<String> newSites, List<String> newLinks, List<String> newImages, List<Float> newRatings, List<Integer> newQuantities) {
        names = new ArrayList<>(newNames);
        costs = new ArrayList<>(newCosts);
        sites = new ArrayList<>(newSites);
        links = new ArrayList<>(newLinks);
        images = new ArrayList<>(newImages);
        ratings = new ArrayList<>(newRatings);
        quantities = new ArrayList<>(newQuantities);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, text3, text4, textQuan;
        ImageView imgV;
        Button btnVisit;
        ImageButton btnShare, btnQuanP, btnQuanN;
        Button btnAdd;

        public ItemViewHolder(@NonNull View cartView) {
            super(cartView);
            text1 = cartView.findViewById(R.id.text1);
            text2 = cartView.findViewById(R.id.text2);
            text3 = cartView.findViewById(R.id.text3);
            text4 = cartView.findViewById(R.id.text4);
            imgV = cartView.findViewById(R.id.image);
            btnAdd = cartView.findViewById(R.id.btnAdd);
            btnVisit = cartView.findViewById(R.id.btnVisit);
            btnShare = cartView.findViewById(R.id.share);
            btnQuanP = cartView.findViewById(R.id.quan_add);
            btnQuanN = cartView.findViewById(R.id.quan_rem);
            textQuan = cartView.findViewById(R.id.text_quan2);
        }
    }
}
