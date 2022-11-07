package com.Go.GoCart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScannedItemAdapter extends RecyclerView.Adapter<ScannedItemAdapter.ItemViewHolder> {

    String[] itemNames;
    String[] siteNames;
    String[] links;
    String[] costs;

    public ScannedItemAdapter (String[] mItemNames, String[] mSiteNames, String[] mLinks, String[] mCosts) {
        this.itemNames = mItemNames;
        this.costs = mCosts;
        this.siteNames = mSiteNames;
        this.links = mLinks;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.scanned_item_code, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        if(i==0){
        holder.itemName1.setText(itemNames[i]);
        holder.siteName1.setText(siteNames[i]);
        holder.linkDisplay1.setText(links[i]);
        holder.costDisplay1.setText(costs[i]);}
        else{holder.itemName.setText(itemNames[i]);
            holder.siteName.setText(siteNames[i]);
            holder.linkDisplay.setText(links[i]);
            holder.costDisplay.setText(costs[i]);}
    }

    @Override
    public int getItemCount() {
        return itemNames.length;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName1;
        TextView siteName1;
        TextView costDisplay1;
        TextView linkDisplay1;
        TextView itemName;
        TextView siteName;
        TextView costDisplay;
        TextView linkDisplay;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName1 = itemView.findViewById(R.id.txt_itemName1);
            siteName1 = itemView.findViewById(R.id.txt_siteName1);
            costDisplay1 = itemView.findViewById(R.id.txt_costDisplay1);
            linkDisplay1 = itemView.findViewById(R.id.txt_linkDisplay1);
            itemName = itemView.findViewById(R.id.txt_itemName);
            siteName = itemView.findViewById(R.id.txt_siteName);
            costDisplay = itemView.findViewById(R.id.txt_costDisplay);
            linkDisplay = itemView.findViewById(R.id.txt_linkDisplay);

        }
    }

}
