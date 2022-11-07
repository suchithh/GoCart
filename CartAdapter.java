package com.Go.GoCart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ItemViewHolder> implements View.OnClickListener {

    List<String> names = new ArrayList<>(), costs = new ArrayList<>(), sites = new ArrayList<>(), links = new ArrayList<>(), images = new ArrayList<>();
    List<Float> ratings = new ArrayList<>();
    Context context;
    int position;

    public CartAdapter(List<String> mNames, List<String> mCosts, List<String> mSites, List<String> mLinks, List<String> mImages) {
        names.addAll(mNames);
        costs.addAll(mCosts);
        sites.addAll(mSites);
        links.addAll(mLinks);
        images.addAll(mImages);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        this.position = i;
        if (images.get(i) == null || images.get(i).equals("") || images.get(i).equals("")) {
            holder.imgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
        } else {
            Glide.with(context).load(images.get(i)).into(holder.imgV);
        }
        holder.btnAdd.setText("Remove");
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View x = LayoutInflater.from(context).inflate(R.layout.confirm_remove_dialog, null);
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
                        updateList(names, costs, sites, links, images);
                        Toast.makeText(context, "Removed From Wishlist!", Toast.LENGTH_SHORT).show();
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
                Button xAdd = x.findViewById(R.id.btnAdd);
                ImageView ximgV = x.findViewById(R.id.image);
                ImageButton xClose = x.findViewById(R.id.close);
                ImageButton xShare = x.findViewById(R.id.btnShare);
                xAdd.setText("Remove");
                Button xVisit = x.findViewById(R.id.btnVisit);
                x1.setText(names.get(i));
                x2.setText(costs.get(i));
                x3.setText(sites.get(i));
                if (ratings != null && ratings.size() > 0 && ratings.get(i) > 0) {
                    x4.setText(((Math.floor(ratings.get(i) * 10)) / 10)+"/5");
                } else {
                    x4.setText("-");
                }
                if (images.get(i) != null && !images.get(i).equals("null")) {
                    Glide.with(context).load(images.get(i)).into(ximgV);
                } else {
                   ximgV.setImageResource(R.drawable.ic_baseline_broken_image_24);
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
