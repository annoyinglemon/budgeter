package com.lemond.kurt.budgeter.Adapters;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryItemClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kurt_capatan on 2/4/2016.
 */

public class Adapter_salaryBudgetDaily extends RecyclerView.Adapter<Adapter_salaryBudgetDaily.CustomViewHolder> implements Serializable{
    private LayoutInflater inflater;
    private ArrayList<SalaryItemClass> mSalaryItemList;
    private boolean isItemLongClicked = false;
    private Context mContext;

    private HashMap<Integer, SalaryItemClass> mSelectedSalaryItem = new HashMap<Integer, SalaryItemClass>();
    private DatabaseAdapter dbAdapter;

    public Adapter_salaryBudgetDaily(Context mContext, ArrayList<SalaryItemClass> salaryItemClasses) {
        this.mContext = mContext;
        this.dbAdapter = new DatabaseAdapter(mContext);
        this.inflater = LayoutInflater.from(mContext);
        this.mSalaryItemList = salaryItemClasses;
    }

    public Adapter_salaryBudgetDaily(Context mContext) {
        this.mContext = mContext;
        this.dbAdapter = new DatabaseAdapter(mContext);
        this.inflater = LayoutInflater.from(mContext);
        updateDataList();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout circleBG;
        public TextView salaryItemInitial;
        public TextView salaryItemQuantityCircle;
        public ImageView ivChecked;
        public TextView itemName;
        public TextView itemPrice;

        public CustomViewHolder(View view) {
            super(view);
            circleBG = (RelativeLayout) view.findViewById(R.id.llCircleBG);
            salaryItemInitial = (TextView) view.findViewById(R.id.tvFirstLetter);
            salaryItemQuantityCircle = (TextView) view.findViewById(R.id.tvItemQuantity);
            ivChecked = (ImageView) view.findViewById(R.id.ivChecked);
            itemName = (TextView) view.findViewById(R.id.salaryItem_ItemName);
            itemPrice = (TextView) view.findViewById(R.id.salaryItem_ItemPrice);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.salary_item_layout, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        SalaryItemClass salaryItemClass = mSalaryItemList.get(position);
        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
        String firstLetter = salaryItemClass.getSalaryItemName().substring(0,1);
        holder.salaryItemInitial.setText(firstLetter.toUpperCase());
        if(salaryItemClass.getSalaryItemQuantity()>99){
            int px = G_Functions.dpToPx_xdpi(mContext, 2);
            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
        }else if(salaryItemClass.getSalaryItemQuantity()>9){
            int px = G_Functions.dpToPx_xdpi(mContext, 3);
            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
        }else{
            int px = G_Functions.dpToPx_xdpi(mContext, 5);
            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
        }
        holder.salaryItemQuantityCircle.setText(Integer.toString(salaryItemClass.getSalaryItemQuantity()));
        holder.itemName.setText(salaryItemClass.getSalaryItemName());
        holder.itemPrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(salaryItemClass.getSalaryItemPrice()));
        if(!isItemLongClicked) {
//            bgShape.setColor(G_Functions.RandomColor(mContext));
            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.salaryItemQuantityCircle.setVisibility(View.VISIBLE);
            holder.salaryItemInitial.setVisibility(View.VISIBLE);
            holder.ivChecked.setVisibility(View.GONE);
        }else{
            holder.salaryItemInitial.setVisibility(View.GONE);
            holder.salaryItemQuantityCircle.setVisibility(View.GONE);
            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            if(mSelectedSalaryItem.containsValue(salaryItemClass))
                holder.ivChecked.setVisibility(View.VISIBLE);
            else
                holder.ivChecked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSalaryItemList.size();
    }

    //    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        G_ViewHolders.SalaryBudget holder = new G_ViewHolders.SalaryBudget();
//        convertView = inflater.inflate(R.layout.salary_item_layout, parent, false);
//        holder.circleBG = (RelativeLayout) convertView.findViewById(R.id.llCircleBG);
//        holder.salaryItemInitial = (TextView) convertView.findViewById(R.id.tvFirstLetter);
//        holder.salaryItemQuantityCircle = (TextView) convertView.findViewById(R.id.tvItemQuantity);
//        holder.ivChecked = (ImageView) convertView.findViewById(R.id.ivChecked);
//        holder. itemName = (TextView) convertView.findViewById(R.id.salaryItem_ItemName);
//        holder. itemPrice = (TextView) convertView.findViewById(R.id.salaryItem_ItemPrice);
//        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
//        String firstLetter = mSalaryItemList.get(position).getSalaryItemName().substring(0,1);
//        holder.salaryItemInitial.setText(firstLetter.toUpperCase());
//        if(mSalaryItemList.get(position).getSalaryItemQuantity()>99){
//            int px = G_Functions.dpToPx_xdpi(mContext, 2);
//            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
//        }else if(mSalaryItemList.get(position).getSalaryItemQuantity()>9){
//            int px = G_Functions.dpToPx_xdpi(mContext, 3);
//            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
//        }else{
//            int px = G_Functions.dpToPx_xdpi(mContext, 5);
//            holder.salaryItemQuantityCircle.setPadding(px,0,px,0);
//        }
//        holder.salaryItemQuantityCircle.setText(Integer.toString(mSalaryItemList.get(position).getSalaryItemQuantity()));
//        holder.itemName.setText(mSalaryItemList.get(position).getSalaryItemName());
//        holder.itemPrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(mSalaryItemList.get(position).getSalaryItemPrice()));
//        if(!isItemLongClicked) {
////            bgShape.setColor(G_Functions.RandomColor(mContext));
//            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
//            holder.salaryItemQuantityCircle.setVisibility(View.VISIBLE);
//            holder.salaryItemInitial.setVisibility(View.VISIBLE);
//            holder.ivChecked.setVisibility(View.GONE);
//        }else{
//            holder.salaryItemInitial.setVisibility(View.GONE);
//            holder.salaryItemQuantityCircle.setVisibility(View.GONE);
//            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
//            if(mSelectedSalaryItem.containsValue(mSalaryItemList.get(position))) holder.ivChecked.setVisibility(View.VISIBLE);
//            else holder.ivChecked.setVisibility(View.GONE);
//        }
//
//        if (mSelectedSalaryItem.containsValue(mSalaryItemList.get(position))) {
//            convertView.setBackgroundResource(R.color.itemHighlight);
//        }
//        convertView.setTag(holder);
//        return convertView;
//    }

    public Double getTotalPrice(){
        double total = 0.00;
        for(int i = 0; i < mSalaryItemList.size(); i++){
            total = total + mSalaryItemList.get(i).getSalaryItemPrice();
        }
        return total;
    }

    public Double getTotalSavings(Double salary){
        double savings = 0.00;
        savings = salary - getTotalPrice();
        return savings;
    }


    /*********************************************** DATABASE FUNCTIONS *************************************************************/
    public void add(SalaryItemClass salaryItem, boolean isCheckBoxChecked, boolean isSaveItemToMonthlyChecked, int multiplier){
        if(dbAdapter.insertIntoSavedItemsSalaryBudget(salaryItem) < 0) {
            Toast.makeText(this.mContext, salaryItem.getSalaryItemName() + " cannot be inserted into database.", Toast.LENGTH_SHORT).show();
        }else {
            updateDataList();
            if(isCheckBoxChecked){
                double price = salaryItem.getSalaryItemPrice() / salaryItem.getSalaryItemQuantity();
                ItemClass newItem = new ItemClass(salaryItem.getSalaryItemName(), price);
                dbAdapter.insertIntoSavedItems(newItem);
            }
            if(isSaveItemToMonthlyChecked){
                double pricePerUnit = salaryItem.getSalaryItemPrice() / (double)salaryItem.getSalaryItemQuantity();
                int newMultiplier = salaryItem.getSalaryItemQuantity() * multiplier;
                salaryItem.setSalaryItemPrice(pricePerUnit * newMultiplier);
                salaryItem.setSalaryItemQuantity(newMultiplier);
                salaryItem.setSalaryItemDuration(SalaryItemClass.MULTIPLIED_DAILY);
                dbAdapter.insertIntoSavedItemsSalaryBudget(salaryItem);
            }
        }
        notifyDataSetChanged();
    }

    public void updateItem(SalaryItemClass salaryItem, boolean isCheckBoxChecked, boolean isSaveToMonthlyChecked, int multiplier){
        if(dbAdapter.updateSavedItemsSalaryBudget(salaryItem)<0){
            Toast.makeText(this.mContext, salaryItem.getSalaryItemName() + " cannot be updated into database.", Toast.LENGTH_SHORT).show();
        }else {
            updateDataList();
            if(isCheckBoxChecked){
                double price = salaryItem.getSalaryItemPrice() / salaryItem.getSalaryItemQuantity();
                ItemClass newItem = new ItemClass(salaryItem.getSalaryItemName(), price);
                dbAdapter.insertIntoSavedItems(newItem);
            }
            if(isSaveToMonthlyChecked){
                double pricePerUnit = salaryItem.getSalaryItemPrice() / (double)salaryItem.getSalaryItemQuantity();
                int newMultiplier = salaryItem.getSalaryItemQuantity() * multiplier;
                salaryItem.setSalaryItemPrice(pricePerUnit * newMultiplier);
                salaryItem.setSalaryItemQuantity(newMultiplier);
                salaryItem.setSalaryItemDuration(SalaryItemClass.MULTIPLIED_DAILY);
                dbAdapter.insertIntoSavedItemsSalaryBudget(salaryItem);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteSelected(){
        ArrayList<SalaryItemClass> newItemList = new ArrayList<SalaryItemClass>();
        for(int i=0; i<mSalaryItemList.size(); i++){
            if(!mSelectedSalaryItem.containsValue(mSalaryItemList.get(i))){
                newItemList.add(mSalaryItemList.get(i));
            }else {
                if(dbAdapter.deleteSpecificItemSalaryBudget(mSalaryItemList.get(i).getSalaryItemId())<0){
                    newItemList.add(mSalaryItemList.get(i));
                    Toast.makeText(this.mContext, mSalaryItemList.get(i).getSalaryItemName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        mSalaryItemList = newItemList;
        mSelectedSalaryItem.clear();
        notifyDataSetChanged();
    }

    public void deleteSingleSalaryItem(SalaryItemClass salaryItem){
        if(dbAdapter.deleteSpecificItemSalaryBudget(salaryItem.getSalaryItemId())<0){
            Toast.makeText(this.mContext, salaryItem.getSalaryItemName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
        }
        else{
            mSalaryItemList.remove(salaryItem);
        }
        notifyDataSetChanged();
    }

    public void updateDataList(){
        this.mSalaryItemList = new ArrayList<SalaryItemClass>();
        this.mSalaryItemList.addAll(dbAdapter.getAllSavedItemsSalaryBudget_Daily());
        notifyDataSetChanged();
    }

    public ArrayList<ItemClass> getAllSavedItems(){
        return dbAdapter.getAllSavedItems();
    }

    public Double getTotalBudget(){
        double totalBudget = 0.00;
        SalaryClass salaryClass = dbAdapter.getLatestDailyMockSalary();
        if(salaryClass!=null){
            totalBudget = totalBudget + salaryClass.getSalaryAmount();
        }
        return totalBudget;
    }

    public boolean insertDailyMockSalary(SalaryClass salary){
        if(dbAdapter.insertDailyMockSalary(salary) > 0){
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<SalaryClass> getDailyMockSalaries(){
        return dbAdapter.getDailyMockSalaries();
    }

    /************************************************** LISTVIEW FUNCTIONS ************************************************************/
    public void addOnItemsToBeRemove(int integer) {
        mSelectedSalaryItem.put(integer, mSalaryItemList.get(integer));
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(Integer integer) {
        return mSelectedSalaryItem.containsKey(integer);
    }

    public void removeSelection(Integer integer) {
        mSelectedSalaryItem.remove(integer);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelectedSalaryItem.clear();
        notifyDataSetChanged();
    }

    public ArrayList<SalaryItemClass> getItems(){
        return mSalaryItemList;
    }

    public SalaryItemClass getItem(int position) {
        return mSalaryItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  mSalaryItemList.get(position).getSalaryItemId();
    }

    public int getSelectedSalaryItemsSize(){
        return mSelectedSalaryItem.size();
    }

    public void setLongItemClicked(boolean clicked){
        this.isItemLongClicked = clicked;
    }

    public boolean isItemLongClicked() {
        return isItemLongClicked;
    }

}
