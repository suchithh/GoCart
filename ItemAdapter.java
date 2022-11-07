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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements View.OnClickListener {

    Context context;
    ViewGroup parentThis;
    int PERMISSION_REQUEST_CODE;
    private List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    private List<Float> ratings = new ArrayList<>();

    public ItemAdapter(List<String> mNames, List<String> mCosts, List<String> mSites, List<String> mLinks, List<String> mImages, List<Float> mRatings) {
        names.addAll(mNames);
        costs.addAll(mCosts);
        sites.addAll(mSites);
        links.addAll(mLinks);
        images.addAll(mImages);
        ratings.addAll(mRatings);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentThis = parent;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_code_recycled, parent, false);
        itemView.setOnClickListener(this);
        ItemViewHolder viewHolder = new ItemViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int i) {
        String name = names.get(i);
        if (names.get(i).length() > 25) {
            name = names.get(i).substring(0, 22)+"...";
        }
        holder.text1.setText(name);
        holder.text2.setText(costs.get(i));
        holder.text3.setText(sites.get(i));
        if (ratings != null && ratings.size() > 0 && ratings.get(i) > 0) {
            holder.text4.setText(((Math.floor(ratings.get(i) * 10)) / 10)+"/5");
        } else {
            holder.text4.setText("-");
        }
        if (images.get(i) == null || images.get(i).equals("null") || images.get(i).equals("")) {
            holder.imgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
        } else {
            Glide.with(context).load(images.get(i)).into(holder.imgV);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            int iteration = 0;
                            while (true) {
                                File file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + String.valueOf(iteration) + ".json");
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                        FileOutputStream stream = new FileOutputStream(file);
                                        try {
                                            String json = "{\"cost\":" + "\"" + costs.get(i) + "\"" + ",\"name\":" + "\"" + names.get(i) + "\"," + "\"image\":" + "\"" + images.get(i) + "\"," + "\"rating\":\"" + ratings.get(i) + "\"," + "\"link\":" + "\"" + links.get(i) + "\"," + "\"site\":" + "\"" + sites.get(i) + "\"}";
                                            stream.write(json.getBytes());
                                        } finally {
                                            stream.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(parentThis, "Added to Wishlist", Snackbar.LENGTH_LONG)
                                            .setAction("View Wishlist", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(context, CartActivity.class);
                                                    intent.putExtra("Class", 1);
                                                    context.startActivity(intent);
                                                }
                                            }).setActionTextColor(Color.WHITE).show();
                                    break;
                                } else {
                                    iteration++;
                                }
                            }

                        } else {
                            requestPermission();
                            int iteration = 0;
                            while (true) {
                                File file = new File("/storage/emulated/0/android/data/com.Go.GoCart/files/Cart" + String.valueOf(iteration) + ".json");
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                        FileOutputStream stream = new FileOutputStream(file);
                                        try {
                                            String json = "{\"cost\":" + "\"" + costs.get(i) + "\"" + ",\"name\":" + "\"" + names.get(i) + "\"," + "\"image\":" + "\"" + images.get(i) + "\"," + "\"link\":" + "\"" + links.get(i) + "\"," + "\"site\":" + "\"" + sites.get(i) + "\"}";
                                            stream.write(json.getBytes());
                                        } finally {
                                            stream.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(parentThis, "Added to Wishlist", Snackbar.LENGTH_LONG)
                                            .setAction("View Wishlist", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(context, CartActivity.class);
                                                    intent.putExtra("Class", 1);
                                                    context.startActivity(intent);
                                                }
                                            }).setActionTextColor(Color.WHITE).show();
                                    break;
                                } else {
                                    iteration++;
                                }
                            }
                        }
                    }
                }
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
        holder.btnVisit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent().setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, links.get(i));
                intent.setType("text/plain");
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "You don't have any app to share!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        holder.imgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View x = LayoutInflater.from(context).inflate(R.layout.item_code_dialog, null);
                TextView x1 = x.findViewById(R.id.text1);
                TextView x2 = x.findViewById(R.id.text2);
                TextView x3 = x.findViewById(R.id.text3);
                TextView x4 = x.findViewById(R.id.text4);
                ImageView ximgV = x.findViewById(R.id.image);
                Button xAdd = x.findViewById(R.id.btnAdd);
                Button xVisit = x.findViewById(R.id.btnVisit);
                ImageButton xClose = x.findViewById(R.id.close);
                ImageButton xShare = x.findViewById(R.id.btnShare);
                x1.setText(names.get(i));
                x2.setText(costs.get(i));
                x3.setText(sites.get(i));
                if (ratings != null && ratings.size() > 0 && ratings.get(i) > 0) {
                    x4.setText(((Math.floor(ratings.get(i) * 10)) / 10)+"/5");
                } else {
                    x4.setText("-");
                }

                if (images.get(i) == null || images.get(i).equals("null") || images.get(i).equals("")) {
                    ximgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
                } else {
                    Glide.with(context).load(images.get(i)).into(ximgV);
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
                        holder.btnVisit.performLongClick();
                    }
                });
                dialog.setCancelable(true);
                dialog.show();
            }
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
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public void updateList(List<String> newNames, List<String> newCosts, List<String> newSites, List<String> newLinks, List<String> newImages) {
        names = new ArrayList<>();
        names.addAll(newNames);
        costs = new ArrayList<>();
        costs.addAll(newCosts);
        sites = new ArrayList<>();
        sites.addAll(newSites);
        links = new ArrayList<>();
        links.addAll(newLinks);
        images = new ArrayList<>();
        images.addAll(newImages);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, text3, text4;
        ImageView imgV;
        Button btnVisit;
        Button btnAdd;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            text3 = itemView.findViewById(R.id.text3);
            text4 = itemView.findViewById(R.id.text4);
            imgV = itemView.findViewById(R.id.image);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            btnVisit = itemView.findViewById(R.id.btnVisit);
        }
    }


}