package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Fragments.SavedCommodities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kurt_capatan on 1/11/2016.
 */
public class Adapter_savedCommodities extends RecyclerView.Adapter<Adapter_savedCommodities.CustomViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<ItemClass> mItemList;
    private boolean isItemLongClicked = false;
    private Context mContext;

    private HashMap<Integer, ItemClass> mSelectedItem = new HashMap<Integer, ItemClass>();
    private DatabaseAdapter dbAdapter;
    private SavedCommodities savedCommodities;

    public Adapter_savedCommodities(Context mContext, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.dbAdapter = new DatabaseAdapter(mContext);
        this.inflater = LayoutInflater.from(mContext);
        this.mItemList = dbAdapter.getAllSavedItems();
        this.savedCommodities = (SavedCommodities) fragmentManager.findFragmentByTag("SavedCommodities");
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout circleBG;
        public TextView itemInitial;
        public ImageView ivChecked;
        public TextView itemName;
        public TextView itemPrice;

        public CustomViewHolder(View view) {
            super(view);
            circleBG = (LinearLayout) view.findViewById(R.id.llCircleBG);
            itemInitial = (TextView) view.findViewById(R.id.tvFirstLetter);
            ivChecked = (ImageView) view.findViewById(R.id.ivChecked);
            itemName = (TextView) view.findViewById(R.id.item_ItemName);
            itemPrice = (TextView) view.findViewById(R.id.item_ItemPrice);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        ItemClass itemClass = mItemList.get(position);
        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
//        bgShape.setColor(G_Functions.RandomColor(mContext));
        bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        String firstLetter = itemClass.getItemName().substring(0,1);
        holder.itemInitial.setText(firstLetter.toUpperCase());
        holder.itemName.setText(itemClass.getItemName());
        holder.itemPrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(itemClass.getItemPrice()));
        if(!isItemLongClicked) {
            holder.itemInitial.setVisibility(View.VISIBLE);
            holder.ivChecked.setVisibility(View.GONE);
        } else{
            holder.itemInitial.setVisibility(View.GONE);
            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            if(mSelectedItem.containsValue(itemClass))
                holder.ivChecked.setVisibility(View.VISIBLE);
            else
                holder.ivChecked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    //    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        G_ViewHolders.SavedCommodities holder = new G_ViewHolders.SavedCommodities();
//        convertView = inflater.inflate(R.layout.item_layout, parent, false);
//        holder.circleBG = (LinearLayout) convertView.findViewById(R.id.llCircleBG);
//        holder.itemInitial = (TextView) convertView.findViewById(R.id.tvFirstLetter);
//        holder.ivChecked = (ImageView) convertView.findViewById(R.id.ivChecked);
//        holder.itemName = (TextView) convertView.findViewById(R.id.item_ItemName);
//        holder.itemPrice = (TextView) convertView.findViewById(R.id.item_ItemPrice);
//        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
////        bgShape.setColor(G_Functions.RandomColor(mContext));
//        bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
//        String firstLetter = mItemList.get(position).getItemName().substring(0,1);
//        holder.itemInitial.setText(firstLetter.toUpperCase());
//        holder.itemName.setText(mItemList.get(position).getItemName());
//        holder.itemPrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(mItemList.get(position).getItemPrice()));
//        if(!isItemLongClicked) {
//            holder.itemInitial.setVisibility(View.VISIBLE);
//            holder.ivChecked.setVisibility(View.GONE);
//        } else{
//            holder.itemInitial.setVisibility(View.GONE);
//            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
//            if(mSelectedItem.containsValue(mItemList.get(position))) holder.ivChecked.setVisibility(View.VISIBLE);
//            else holder.ivChecked.setVisibility(View.GONE);
//        }
//
//        if (mSelectedItem.containsValue(mItemList.get(position))) {
//           convertView.setBackgroundResource(R.color.itemHighlight);
//        }
//        return convertView;
//    }

    /*********************************************** DATABASE FUNCTIONS *************************************************************/

    public void add(ItemClass item){
        if(dbAdapter.insertIntoSavedItems(item)<0){
            Toast.makeText(this.mContext, item.getItemName() + " cannot be inserted into database.", Toast.LENGTH_SHORT).show();
        }else {
            this.mItemList = dbAdapter.getAllSavedItems();
        }
        updateData();
    }

    public void updateItem(ItemClass item){
        if(dbAdapter.updateSavedItem(item)<0){
            Toast.makeText(this.mContext, item.getItemName() + " cannot be updated into database.", Toast.LENGTH_SHORT).show();
        }else {
            this.mItemList = dbAdapter.getAllSavedItems();
        }
        updateData();
    }

    public void deleteSelected(){
        ArrayList<ItemClass> newItemList = new ArrayList<ItemClass>();
        for(int i=0; i<mItemList.size(); i++){
            if(!mSelectedItem.containsValue(mItemList.get(i))){
                newItemList.add(mItemList.get(i));
            }else {
                if(dbAdapter.deleteSpecificItem(mItemList.get(i).getItemId())<0){
                    newItemList.add(mItemList.get(i));
                    Toast.makeText(this.mContext, mItemList.get(i).getItemName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        mItemList = newItemList;
        mSelectedItem.clear();
        updateData();
    }

    public void deleteSingleItem(ItemClass item){
        if(dbAdapter.deleteSpecificItem(item.getItemId())<0){
            Toast.makeText(this.mContext, item.getItemName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
        }
        else{
            mItemList.remove(item);
        }
        updateData();
    }


    public void updateData(){
        this.mItemList = dbAdapter.getAllSavedItems();
        notifyDataSetChanged();
        this.savedCommodities.checkListIfEmpty();
    }

    /************************************************** LISTVIEW FUNCTIONS ************************************************************/

    public void addOnItemsToBeRemove(int integer) {
        mSelectedItem.put(integer, mItemList.get(integer));
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(Integer integer) {
        return mSelectedItem.containsKey(integer);
    }

    public void removeSelection(Integer integer) {
        mSelectedItem.remove(integer);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelectedItem.clear();
        notifyDataSetChanged();
    }
    public List<ItemClass> getItems(){
        return mItemList;
    }

    public ItemClass getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getItemId();
    }

    public void setLongItemClicked(boolean clicked){
        this.isItemLongClicked = clicked;
    }

    public boolean isItemLongClicked() {
        return isItemLongClicked;
    }

    /********************************** SEARCH FUNCTION *******************************************/

    SearchItem searchItem;

    public void executeSearch(String query){
        searchItem = new SearchItem();
        searchItem.execute(query);
    }

    public void cancelSearch(){
        if(searchItem!=null)searchItem.cancel(true);
    }


    class SearchItem extends AsyncTask<String, Void, Void>{

        ArrayList<ItemClass> searchResults;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchResults = new ArrayList<ItemClass>();
        }

        @Override
        protected Void doInBackground(String... params) {
            searchResults = dbAdapter.searchItems(params[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            mItemList = dbAdapter.getAllSavedItems();
            notifyDataSetChanged();
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(Void params) {
            mItemList = searchResults;
            notifyDataSetChanged();
            super.onPostExecute(params);
        }
    }
}
