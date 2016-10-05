package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by lemond on 1/30/16.
 */
public class Adapter_importSavedItems extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ItemClass> mItemList;
    private Context mContext;

    public Adapter_importSavedItems(Context mContext, ArrayList<ItemClass> items) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.mItemList = items;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public ItemClass getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getItemId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        G_ViewHolders.ImportSavedItems holder = new G_ViewHolders.ImportSavedItems();
        convertView = inflater.inflate(R.layout.import_saved_item_layout, parent, false);
        holder.itemName = (TextView) convertView.findViewById(R.id.tvImportSavedItemName);
        holder.itemPrice = (TextView) convertView.findViewById(R.id.tvImportSavedPrice);
        holder.itemName.setText(mItemList.get(position).getItemName());
        holder.itemPrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(mItemList.get(position).getItemPrice()));
        convertView.setTag(holder);
        return convertView;
    }

    public Double getTotalPrice(){
        double total = 0.00;
        for(int i = 0; i < mItemList.size(); i++){
            total = total + mItemList.get(i).getItemPrice();
        }
        return total;
    }
}
