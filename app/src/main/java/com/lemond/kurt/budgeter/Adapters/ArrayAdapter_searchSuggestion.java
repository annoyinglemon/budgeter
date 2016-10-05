package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.List;

/**
 * Created by kurt_capatan on 5/30/2016.
 */
public class ArrayAdapter_searchSuggestion extends ArrayAdapter<ItemClass>  {


    public ArrayAdapter_searchSuggestion(final Context context, final int resource, final List<ItemClass> itemClasses) {
        super(context, resource, itemClasses);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_suggestions, parent, false);
        G_ViewHolders.SearchSuggestions holder = new G_ViewHolders.SearchSuggestions();
        holder.firstLetter = (TextView) rootView.findViewById(R.id.tvFirstLetter);
        holder.ItemName = (TextView) rootView.findViewById(R.id.tvItemName);
        holder.ItemPrice = (TextView) rootView.findViewById(R.id.tvItemPrice);
        holder.firstLetter.setText(getItem(position).getItemName().substring(0,1).toUpperCase());
        holder.ItemName.setText(getItem(position).getItemName());
        holder.ItemPrice.setText(new SettingsManager(getContext()).getCurrency() + " " + G_Functions.formatNumber(getItem(position).getItemPrice()));
        return rootView;
    }

    @Override
    public ItemClass getItem(int position) {
        return super.getItem(position);
    }
}
