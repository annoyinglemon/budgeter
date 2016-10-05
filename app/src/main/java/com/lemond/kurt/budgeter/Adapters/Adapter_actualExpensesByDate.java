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

import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by lemond on 1/30/16.
 */
public class Adapter_actualExpensesByDate extends RecyclerView.Adapter<Adapter_actualExpensesByDate.CustomViewHolder> {
    private ArrayList<ActualExpensesClass> mActualExpenseList;
    private Context mContext;

    public Adapter_actualExpensesByDate(Context mContext, ArrayList<ActualExpensesClass> items) {
        this.mContext = mContext;
        this.mActualExpenseList = items;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView expenseInitial;
        public TextView expenseQuantityCircle;
        public TextView expenseName;
        public TextView expensePrice;

        public CustomViewHolder(View view) {
            super(view);
            expenseInitial = (TextView) view.findViewById(R.id.actualExpenseByDate_FirstLetter);
            expenseQuantityCircle = (TextView) view.findViewById(R.id.actualExpenseByDate_ExpenseQuantity);
            expenseName = (TextView) view.findViewById(R.id.actualExpenseByDate_ExpenseName);
            expensePrice = (TextView) view.findViewById(R.id.actualExpenseByDate_ExpensePrice);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.actual_expenses_bydate_item_layout, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        ActualExpensesClass actualExpensesClass = mActualExpenseList.get(position);
//        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
        String firstLetter = actualExpensesClass.getActualExpenseName().substring(0,1);
        holder.expenseInitial.setText(firstLetter.toUpperCase());
        if(actualExpensesClass.getActualExpenseQuantity()>99){
            int px = G_Functions.dpToPx_xdpi(mContext, 2);
            holder.expenseQuantityCircle.setPadding(px,0,px,0);
        }else if(actualExpensesClass.getActualExpenseQuantity()>9){
            int px = G_Functions.dpToPx_xdpi(mContext, 3);
            holder.expenseQuantityCircle.setPadding(px,0,px,0);
        }else{
            int px = G_Functions.dpToPx_xdpi(mContext, 5);
            holder.expenseQuantityCircle.setPadding(px,0,px,0);
        }
        holder.expenseQuantityCircle.setText(Integer.toString(actualExpensesClass.getActualExpenseQuantity()));
        holder.expenseName.setText(actualExpensesClass.getActualExpenseName());
        holder.expensePrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(actualExpensesClass.getActualExpensePrice()));
    }

    @Override
    public int getItemCount() {
        return mActualExpenseList.size();
    }

    public ActualExpensesClass getItem(int position) {
        return mActualExpenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mActualExpenseList.get(position).getActualExpenseId();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        G_ViewHolders.ActualExpensesByDate holder = new G_ViewHolders.ActualExpensesByDate();
//        convertView = inflater.inflate(R.layout.actual_expenses_bydate_item_layout, parent, false);
//        holder.expenseName = (TextView) convertView.findViewById(R.id.actualExpenseByDate_ExpenseName);
//        holder.expensePrice = (TextView) convertView.findViewById(R.id.actualExpenseByDate_ExpensePrice);
//        holder.expenseQuantity = (TextView) convertView.findViewById(R.id.actualExpenseByDate_ExpenseQuantity);
//        holder.expenseName.setText(mActualExpenseList.get(position).getActualExpenseName());
//        holder.expensePrice.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(mActualExpenseList.get(position).getActualExpensePrice()));
//        holder.expenseQuantity.setText(Integer.toString(mActualExpenseList.get(position).getActualExpenseQuantity()));
//        convertView.setTag(holder);
//        return convertView;
//    }

    public Double getTotalPrice(){
        double total = 0.00;
        for(int i = 0; i < mActualExpenseList.size(); i++){
            total = total + mActualExpenseList.get(i).getActualExpensePrice();
        }
        return total;
    }
}
