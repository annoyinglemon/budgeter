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

import com.lemond.kurt.budgeter.ObjectClasses.SavingsClass;
import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kurt_capatan on 1/11/2016.
 */
public class Adapter_currentMonthExpensesDaily extends RecyclerView.Adapter<Adapter_currentMonthExpensesDaily.CustomViewHolder> implements Serializable{
    private ArrayList<ActualExpensesClass> mActualExpenseList;
    private boolean isItemLongClicked = false;
    private Context mContext;
    private String dateToday;

    private HashMap<Integer, ActualExpensesClass> mSelectedActualExpense = new HashMap<Integer, ActualExpensesClass>();
    private DatabaseAdapter dbAdapter;
    private SettingsManager mSettingsManager;

    public Adapter_currentMonthExpensesDaily(Context mContext, ArrayList<ActualExpensesClass> mActualExpenseList) {
        this.mContext = mContext;
        this.dbAdapter = new DatabaseAdapter(mContext);
        mSettingsManager = new SettingsManager(mContext);
        dateToday= mSettingsManager.getCurrentDate();
        this.mActualExpenseList = mActualExpenseList;
    }

    public Adapter_currentMonthExpensesDaily(Context mContext) {
        this.mContext = mContext;
        this.dbAdapter = new DatabaseAdapter(mContext);
        mSettingsManager = new SettingsManager(mContext);
        dateToday= mSettingsManager.getCurrentDate();
        this.mActualExpenseList = dbAdapter.getAllActualExpensesBetweenDates_daily(dateToday);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout circleBG;
        public TextView expenseInitial;
        public TextView expenseQuantityCircle;
        public ImageView ivChecked;
        public TextView expenseName;
        public TextView expensePrice;
        public TextView expenseDate;

        public CustomViewHolder(View view) {
            super(view);
            circleBG = (RelativeLayout) view.findViewById(R.id.llCircleBG);
            expenseInitial = (TextView) view.findViewById(R.id.tvFirstLetter);
            expenseQuantityCircle = (TextView) view.findViewById(R.id.tvExpenseQuantity);
            ivChecked = (ImageView) view.findViewById(R.id.ivChecked);
            expenseName = (TextView) view.findViewById(R.id.actualExpense_ExpenseName);
            expensePrice = (TextView) view.findViewById(R.id.actualExpense_ExpensePrice);
            expenseDate = (TextView) view.findViewById(R.id.actualExpense_ExpenseDate);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.actual_expense_item_layout, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        ActualExpensesClass actualExpensesClass = mActualExpenseList.get(position);
        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
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
        holder.expensePrice.setText(mSettingsManager.getCurrency() + " " + G_Functions.formatNumber(actualExpensesClass.getActualExpensePrice()));
        holder.expenseDate.setText(DateUtilities.makeReadableFormat(actualExpensesClass.getAcutalExpenseDate()));
        if(!isItemLongClicked) {
//            bgShape.setColor(G_Functions.RandomColor(mContext));
            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.expenseQuantityCircle.setVisibility(View.VISIBLE);
            holder.expenseInitial.setVisibility(View.VISIBLE);
            holder.ivChecked.setVisibility(View.GONE);
        }else {
            holder.expenseInitial.setVisibility(View.GONE);
            holder.expenseQuantityCircle.setVisibility(View.GONE);
            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            if(mSelectedActualExpense.containsValue(actualExpensesClass))
                holder.ivChecked.setVisibility(View.VISIBLE);
            else
                holder.ivChecked.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mActualExpenseList.size();
    }

    //    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        G_ViewHolders.CurrentMonthExpenses holder = new G_ViewHolders.CurrentMonthExpenses();
//        convertView = inflater.inflate(R.layout.actual_expense_item_layout, parent, false);
//        holder.circleBG = (RelativeLayout) convertView.findViewById(R.id.llCircleBG);
//        holder.expenseInitial = (TextView) convertView.findViewById(R.id.tvFirstLetter);
//        holder.expenseQuantityCircle = (TextView) convertView.findViewById(R.id.tvExpenseQuantity);
//        holder.ivChecked = (ImageView) convertView.findViewById(R.id.ivChecked);
//        holder.expenseName = (TextView) convertView.findViewById(R.id.actualExpense_ExpenseName);
//        holder.expensePrice = (TextView) convertView.findViewById(R.id.actualExpense_ExpensePrice);
//        holder.expenseDate = (TextView) convertView.findViewById(R.id.actualExpense_ExpenseDate);
//        GradientDrawable bgShape = (GradientDrawable)holder.circleBG.getBackground();
//        String firstLetter = mActualExpenseList.get(position).getActualExpenseName().substring(0,1);
//        holder.expenseInitial.setText(firstLetter.toUpperCase());
//        if(mActualExpenseList.get(position).getActualExpenseQuantity()>99){
//            int px = G_Functions.dpToPx_xdpi(mContext, 2);
//            holder.expenseQuantityCircle.setPadding(px,0,px,0);
//        }else if(mActualExpenseList.get(position).getActualExpenseQuantity()>9){
//            int px = G_Functions.dpToPx_xdpi(mContext, 3);
//            holder.expenseQuantityCircle.setPadding(px,0,px,0);
//        }else{
//            int px = G_Functions.dpToPx_xdpi(mContext, 5);
//            holder.expenseQuantityCircle.setPadding(px,0,px,0);
//        }
//        holder.expenseQuantityCircle.setText(Integer.toString(mActualExpenseList.get(position).getActualExpenseQuantity()));
//        holder.expenseName.setText(mActualExpenseList.get(position).getActualExpenseName());
//        holder.expensePrice.setText(mSettingsManager.getCurrency() + " " + G_Functions.formatNumber(mActualExpenseList.get(position).getActualExpensePrice()));
//        holder.expenseDate.setText(DateUtilities.makeReadableFormat(mActualExpenseList.get(position).getAcutalExpenseDate()));
//        if(!isItemLongClicked) {
////            bgShape.setColor(G_Functions.RandomColor(mContext));
//            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
//            holder.expenseQuantityCircle.setVisibility(View.VISIBLE);
//            holder.expenseInitial.setVisibility(View.VISIBLE);
//            holder.ivChecked.setVisibility(View.GONE);
//        }else {
//            holder.expenseInitial.setVisibility(View.GONE);
//            holder.expenseQuantityCircle.setVisibility(View.GONE);
//            bgShape.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
//            if(mSelectedActualExpense.containsValue(mActualExpenseList.get(position))) holder.ivChecked.setVisibility(View.VISIBLE);
//            else holder.ivChecked.setVisibility(View.GONE);
//        }
//
//        if (mSelectedActualExpense.containsValue(mActualExpenseList.get(position))) {
//            convertView.setBackgroundResource(R.color.itemHighlight);
//        }
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

    public Double getTotalSavings(Double salary){
        double savings = 0.00;
        savings = salary - getTotalPrice();
        return savings;
    }

    /*********************************************** DATABASE FUNCTIONS *************************************************************/
    public void add(ActualExpensesClass actualExpense, boolean isCheckBoxChecked) {
        if(dbAdapter.insertIntoActualExpenses(actualExpense)<0){
            Toast.makeText(this.mContext, actualExpense.getActualExpenseName() + " cannot be inserted into database.", Toast.LENGTH_SHORT).show();
        }else {
            updateDataList();
            if(isCheckBoxChecked){
                double price = actualExpense.getActualExpensePrice() / actualExpense.getActualExpenseQuantity();
                ItemClass newItem = new ItemClass(actualExpense.getActualExpenseName(), price);
                dbAdapter.insertIntoSavedItems(newItem);
            }
        }
    }

    public void updateItem(ActualExpensesClass actualExpense, boolean isCheckBoxChecked){
        if(dbAdapter.updateActualExpense(actualExpense)<0){
            Toast.makeText(this.mContext, actualExpense.getActualExpenseName() + " cannot be updated into database.", Toast.LENGTH_SHORT).show();
        }else {
            updateDataList();
            if(isCheckBoxChecked){
                double price = actualExpense.getActualExpensePrice() / actualExpense.getActualExpenseQuantity();
                ItemClass newItem = new ItemClass(actualExpense.getActualExpenseName(), price);
                dbAdapter.insertIntoSavedItems(newItem);
            }
        }
    }

    public void deleteSelected(){
        ArrayList<ActualExpensesClass> newItemList = new ArrayList<ActualExpensesClass>();
        for(int i=0; i<mActualExpenseList.size(); i++){
            if(!mSelectedActualExpense.containsValue(mActualExpenseList.get(i))){
                newItemList.add(mActualExpenseList.get(i));
            }else {
                if(dbAdapter.deleteSpecificActualExpense(mActualExpenseList.get(i).getActualExpenseId())<0){
                    newItemList.add(mActualExpenseList.get(i));
                    Toast.makeText(this.mContext, mActualExpenseList.get(i).getActualExpenseName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        mActualExpenseList = newItemList;
        mSelectedActualExpense.clear();
        notifyDataSetChanged();
    }

    public void deleteSingleExpense(ActualExpensesClass expense){
        if(dbAdapter.deleteSpecificActualExpense(expense.getActualExpenseId())<0){
            Toast.makeText(this.mContext, expense.getActualExpenseName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
        }
        else{
            mActualExpenseList.remove(expense);
        }
        notifyDataSetChanged();
    }

    public void updateDataList(){
        dateToday = mSettingsManager.getCurrentDate();
        this.mActualExpenseList = dbAdapter.getAllActualExpensesBetweenDates_daily(dateToday);
        notifyDataSetChanged();
    }

    public ArrayList<ItemClass> getAllSavedItems(){
        return dbAdapter.getAllSavedItems();
    }

    public Double getTotalBudget(){
        double totalBudget = 0.00;
        totalBudget = totalBudget + mSettingsManager.getDailyBudget();

        return totalBudget;
    }

    public ArrayList<SalaryClass> getSalaries(){
       return dbAdapter.getDailySalaries();
    }

    public boolean updateCurrentDailySavings(SavingsClass savingsClass){
        boolean result = false;
        long resultUpdate = 0, resultInsert = -1;
        SavingsClass savingsClass1 = dbAdapter.getCurrentSavingsDaily(savingsClass.getSavingsDateOrDuration());
        if(savingsClass1!=null){
            savingsClass1.setSavingsAmount(savingsClass.getSavingsAmount());
            resultUpdate = dbAdapter.updateCurrentDailySavings(savingsClass1);
        }else {
            resultInsert = dbAdapter.insertCurrentDailySavings(savingsClass);
        }
        if(resultUpdate > 0 || resultInsert > -1){
            result = true;
        }
        return result;
    }

//    public void updateMonthlySavingsAndBudget(double currentDaySavings){
//        //get remaining days
//        Adapter_currentMonthExpenses adapter = new Adapter_currentMonthExpenses(mContext);
//        double sumCurrentMonthExpenses = adapter.getTotalPrice();
//        double totalBudget = adapter.getTotalBudget() + currentDaySavings;
//        DateUtilities dateUtilities = new DateUtilities(dateToday);
//        double newDailyBudget = (totalBudget - sumCurrentMonthExpenses) / dateUtilities.remainingCurrentMonthDays();
//        mSettingsManager.setDailyBudget(newDailyBudget);
//    }

    public SavingsClass getCurrentSavingsDaily(String date){
        return dbAdapter.getCurrentSavingsDaily(date);
    }

    /************************************************** LISTVIEW FUNCTIONS ************************************************************/
    public void addOnItemsToBeRemove(int integer) {
        mSelectedActualExpense.put(integer, mActualExpenseList.get(integer));
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(Integer integer) {
        return mSelectedActualExpense.containsKey(integer);
    }

    public void removeSelection(Integer integer) {
        mSelectedActualExpense.remove(integer);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelectedActualExpense.clear();
        notifyDataSetChanged();
    }

    public ArrayList<ActualExpensesClass> getItems(){
        return mActualExpenseList;
    }

    public ActualExpensesClass getItem(int position) {
        return mActualExpenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mActualExpenseList.get(position).getActualExpenseId();
    }

    public int getSelectedSalaryItemsSize(){
        return mSelectedActualExpense.size();
    }

    public void setLongItemClicked(boolean clicked){
        this.isItemLongClicked = clicked;
    }

    public boolean isItemLongClicked() {
        return isItemLongClicked;
    }

}
