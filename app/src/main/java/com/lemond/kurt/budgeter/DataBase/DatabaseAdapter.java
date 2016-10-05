package com.lemond.kurt.budgeter.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.ObjectClasses.BudgetPlanClass;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryItemClass;
import com.lemond.kurt.budgeter.ObjectClasses.SavingsClass;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by kurt_capatan on 1/14/2016.
 */
public class DatabaseAdapter {

    DBHelper helper;
    private Context mContext;

    public DatabaseAdapter(Context context){
        this.mContext = context;
        helper = new DBHelper(context);
    }



    public ArrayList<String> getAllActualExpensesDates(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] column = {DBHelper.ACTUAL_EXPENSE_DATE};
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, column, null,null, null, null, DBHelper.ACTUAL_EXPENSE_DATE);
        ArrayList<String> actualExpensesDate = new ArrayList<String>();
        while(cursor.moveToNext()){
            String date = cursor.getString(0);
            actualExpensesDate.add(date);
        }
        cursor.close();
        helper.close();
        return actualExpensesDate;
    }

    public ArrayList<ActualExpensesClass> getAllActualExpensesByDate(String purchaseDate){
        String[] selectionArgs = {purchaseDate};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE, DBHelper.ACTUAL_EXPENSE_DURATION};
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, DBHelper.ACTUAL_EXPENSE_DATE + "=?",selectionArgs, null, null, DBHelper.ACTUAL_EXPENSE_ID + " DESC");
        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDate = cursor.getString(4);
            String duration = cursor.getString(5);
            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate, duration);
            actualExpenses.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return actualExpenses;
    }

    /*****************************************************SAVINGS AND EXPENSE SUM********************************************************/
//    select sum(price) from actual_expenses_table where expense_date='2016-08-13';
//    select distinct expense_date from actual_expenses_table where expense_date like '2016-07-%';   <--select distinct dates by month
//    select sum(price) from actual_expenses_table where expense_date like '2016-08%'; <-- get total expense by month
//    select distinct substr(expense_date, 1, 7) as expense_month from actual_expenses_table where expense_date like '2016%';  <-- select distinct year month by year
    //TODO CREATE GRAPH FUNCTION!!
    /**
     * Returns all the expense year and month within a year
     * @param year year, format must be 'yyyy'
     * @return all expense year and month of the year, in yyyy-MM format
     */
    public ArrayList<String> getAllExpenseDateByYear(String year){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] a = new String[1];
        a[0] =  year + '%';
        Cursor cursor = sqlDB.rawQuery("SELECT DISTINCT SUBSTR(actual_expense_date, 1, 7) FROM saved_items_actual_expenses WHERE actual_expense_date LIKE ?", a);
        ArrayList<String> arrayList = new ArrayList<>();
        while(cursor.moveToNext()){
            String dateString = cursor.getString(0);
            arrayList.add(dateString);
        }
        cursor.close();
        helper.close();
        return arrayList;
    }

    /**
     * Returns the total expenses by month
     * @param yearMonth year and month, format must be 'yyyy-MM'
     * @return total price of expenses of each month
     */
    public double getTotalExpensesByMonth(String yearMonth){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] a = new String[1];
        a[0] =  yearMonth + '%';
        Cursor cursor = sqlDB.rawQuery("SELECT SUM(actual_expense_price) FROM saved_items_actual_expenses WHERE actual_expense_date LIKE ?", a);
        double totalExpense = 0.00;
        if(cursor.moveToFirst()&&cursor!=null){
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    /**
     * Returns all the expense date within a month
     * @param yearMonth year and month, format must be 'yyyy-MM'
     * @return all expense dates of the month in yyyy-MM-dd format
     */
    public ArrayList<String> getAllExpenseDateByMonth(String yearMonth){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] a = new String[1];
        a[0] =  yearMonth + '%';
        Cursor cursor = sqlDB.rawQuery("SELECT DISTINCT actual_expense_date FROM saved_items_actual_expenses WHERE actual_expense_date LIKE ?", a);
        ArrayList<String> arrayList = new ArrayList<>();
        while(cursor.moveToNext()){
            String dateString = cursor.getString(0);
            arrayList.add(dateString);
        }
        cursor.close();
        helper.close();
        return arrayList;
    }

    /**
     * Returns the total expenses by date
     * @param date must be in yyyy-MM-dd format
     * @return total price of expenses for each date
     */
    public double getTotalExpensesByDate(String date){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {"SUM("+DBHelper.ACTUAL_EXPENSE_PRICE+") AS total_price"};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " = ?";
        String[] args = {date};
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates, args, null, null, null);
        double totalExpense = 0.00;
        if(cursor.moveToFirst()&&cursor!=null){
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    /**
     * Returns the total savings by month
     * @param yearMonth year and month, format must be 'yyyy-MM'
     * @return total amount of savings of each month
     */
    public double getTotalSavingsByMonth(String yearMonth){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] a = new String[1];
        a[0] =  yearMonth + '%';
        Cursor cursor = sqlDB.rawQuery("SELECT SUM(savings_amount) FROM monthly_savings WHERE savings_month LIKE ?", a);
        double totalSavings = 0.00;
        if(cursor.moveToFirst()&&cursor!=null){
            totalSavings = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalSavings;
    }

    /**
     * Returns the total savings by date
     * @param date must be in yyyy-MM-dd format
     * @return total amount of savings for each date
     */
    public double getTotalSavingsByDate(String date){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {"SUM("+DBHelper.DAILY_SAVINGS_AMOUNT+") AS total_price"};
        String selectedDates = DBHelper.DAILY_SAVINGS_DATE + " = '" + date + "' ";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SAVINGS_DAILY, columns, selectedDates, null, null, null, null);
        double totalExpense = 0.00;
        if(cursor.moveToFirst()&&cursor!=null){
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    /***************************************************ACTUAL EXPENSE CLASS ******************************************************/

    public double currentMonthTotalExpenses(){
        SettingsManager settingsManager = new SettingsManager(mContext);
        DateUtilities dateUtilities = new DateUtilities(settingsManager.getCurrentDate());
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_PRICE};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " >= '" + dateUtilities.getMonthDateBeginning() + "' AND " + DBHelper.ACTUAL_EXPENSE_DATE+" <= '"+ settingsManager.getCurrentDate()+"' ";
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates,null, null, null, DBHelper.ACTUAL_EXPENSE_DATE + " DESC");
        double totalExpense = 0.00;
        while(cursor.moveToNext()){
            totalExpense = totalExpense + cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    public double monthlyTotalExpensesAsOfThisDate(String endDate){
        DateUtilities dateUtilities = new DateUtilities(endDate);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {"SUM("+DBHelper.ACTUAL_EXPENSE_PRICE+") AS total_price"};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " >= '" + dateUtilities.getMonthDateBeginning() + "' AND " + DBHelper.ACTUAL_EXPENSE_DATE+" <= '"+ endDate+"' ";
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates, null, null, null, DBHelper.ACTUAL_EXPENSE_DATE + " DESC");
        double totalExpense = 0.00;
        if(cursor.moveToFirst()){
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    public double dailyTotalExpensesOfThisDate(String selectedDate){
        SettingsManager settingsManager = new SettingsManager(mContext);
        DateUtilities dateUtilities = new DateUtilities(settingsManager.getCurrentDate());
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {"SUM("+DBHelper.ACTUAL_EXPENSE_PRICE+") AS total_price"};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " = '" + selectedDate + "' ";
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates, null, null, null, DBHelper.ACTUAL_EXPENSE_DATE + " DESC");
        double totalExpense = 0.00;
        if(cursor.moveToFirst()){
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalExpense;
    }

    public ArrayList<ActualExpensesClass> searchActualExpensesBetweenDates(String beginDate, String endDate, String searchQuery){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE, DBHelper.ACTUAL_EXPENSE_DURATION};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " >= ? AND " + DBHelper.ACTUAL_EXPENSE_DATE+" <= ? AND " + DBHelper.ACTUAL_EXPENSE_NAME + " LIKE ?";
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOption()){
            case SettingsManager.NAME_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " DESC";
                break;
            case SettingsManager.DATE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " ASC";
                break;
            case SettingsManager.DATE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " DESC";
                break;
            default:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
        }
        String[] selArgs = {beginDate, endDate, "%"+searchQuery+"%"};
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates, selArgs, null, null, orderBy);
        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDate = cursor.getString(4);
            String duration = cursor.getString(5);
            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate, duration);
            actualExpenses.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return actualExpenses;
    }

    //in monthly, all kinds of expenses duration (monthly and daily) are caught
    public ArrayList<ActualExpensesClass> getAllActualExpensesBetweenDates(String beginDate, String endDate){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE, DBHelper.ACTUAL_EXPENSE_DURATION};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + " >= '" + beginDate + "' AND " + DBHelper.ACTUAL_EXPENSE_DATE+" <= '"+ endDate+"' ";
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOption()){
            case SettingsManager.NAME_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " DESC";
                break;
            case SettingsManager.DATE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " ASC";
                break;
            case SettingsManager.DATE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " DESC";
                break;
            default:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
        }
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates,null, null, null, orderBy);
        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDate = cursor.getString(4);
            String duration = cursor.getString(5);
            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate, duration);
            actualExpenses.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return actualExpenses;
    }

    //in daily, only "daily" expenses are caught
    public ArrayList<ActualExpensesClass> getAllActualExpensesBetweenDates_daily(String purchaseDate){
        String[] selectionArgs = {purchaseDate, ActualExpensesClass.DAILY};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE, DBHelper.ACTUAL_EXPENSE_DURATION};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOption()){
            case SettingsManager.NAME_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_PRICE + " DESC";
                break;
            case SettingsManager.DATE_ASC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " ASC";
                break;
            case SettingsManager.DATE_DESC:
                orderBy = DBHelper.ACTUAL_EXPENSE_DATE + " DESC";
                break;
            default:
                orderBy = DBHelper.ACTUAL_EXPENSE_NAME + " ASC";
        }
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, DBHelper.ACTUAL_EXPENSE_DATE + "=? AND " + DBHelper.ACTUAL_EXPENSE_DURATION + "=?", selectionArgs, null, null, orderBy);
        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDate = cursor.getString(4);
            String duration = cursor.getString(5);
            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate, duration);
            actualExpenses.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return actualExpenses;
    }

    public double getTotalExpensesBetweenDates(String beginDate, String endDate, String duration){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE, DBHelper.ACTUAL_EXPENSE_DURATION};
        String selectedDates = DBHelper.ACTUAL_EXPENSE_DATE + ">='" + beginDate + "' AND " + DBHelper.ACTUAL_EXPENSE_DATE + "<='" + endDate + "' AND " + DBHelper.ACTUAL_EXPENSE_DURATION + "='" + duration+"'";
        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, selectedDates, null, null, null, DBHelper.ACTUAL_EXPENSE_ID + " DESC");
        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDate = cursor.getString(4);
            String durations = cursor.getString(5);
            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate, durations);
            actualExpenses.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        double totalExpense = 0.00;
        if(actualExpenses.size()!=0){
            for (ActualExpensesClass aec: actualExpenses) {
                totalExpense = totalExpense + aec.getActualExpensePrice();
            }
        }
        return totalExpense;
    }

//    public ArrayList<ActualExpensesClass> getAllActualExpenses(){
//        SQLiteDatabase sqlDB = helper.getWritableDatabase();
//        String[] columns = {DBHelper.ACTUAL_EXPENSE_ID, DBHelper.ACTUAL_EXPENSE_NAME, DBHelper.ACTUAL_EXPENSE_PRICE, DBHelper.ACTUAL_EXPENSE_QUANTITY, DBHelper.ACTUAL_EXPENSE_DATE};
//        Cursor cursor = sqlDB.query(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, columns, null,null, null, null, DBHelper.ACTUAL_EXPENSE_ID + " DESC");
//        ArrayList<ActualExpensesClass> actualExpenses = new ArrayList<ActualExpensesClass>();
//        while(cursor.moveToNext()){
//            int itemId = cursor.getInt(0);
//            String itemName = cursor.getString(1);
//            String itemPrice = cursor.getString(2);
//            int itemQuantity = cursor.getInt(3);
//            String itemDate = cursor.getString(4);
//            ActualExpensesClass salaryItemEntry = new ActualExpensesClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDate);
//            actualExpenses.add(salaryItemEntry);
//        }
//        cursor.close();
//        helper.close();
//        return actualExpenses;
//    }

    public long insertIntoActualExpenses(ActualExpensesClass actualExpense){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.ACTUAL_EXPENSE_NAME, actualExpense.getActualExpenseName());
        cValues.put(DBHelper.ACTUAL_EXPENSE_PRICE, actualExpense.getActualExpensePrice());
        cValues.put(DBHelper.ACTUAL_EXPENSE_QUANTITY, actualExpense.getActualExpenseQuantity());
        cValues.put(DBHelper.ACTUAL_EXPENSE_DATE, actualExpense.getAcutalExpenseDate());
        cValues.put(DBHelper.ACTUAL_EXPENSE_DURATION, actualExpense.getActualExpenseDuration());
        long id = sqlDB.insert(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, null, cValues);
        helper.close();
        return id;
    }

    public long updateActualExpense(ActualExpensesClass actualExpense){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.ACTUAL_EXPENSE_NAME, actualExpense.getActualExpenseName());
        cValues.put(DBHelper.ACTUAL_EXPENSE_PRICE, actualExpense.getActualExpensePrice());
        cValues.put(DBHelper.ACTUAL_EXPENSE_QUANTITY, actualExpense.getActualExpenseQuantity());
        cValues.put(DBHelper.ACTUAL_EXPENSE_DATE, actualExpense.getAcutalExpenseDate());
        cValues.put(DBHelper.ACTUAL_EXPENSE_DURATION, actualExpense.getActualExpenseDuration());
        long id = sqlDB.update(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, cValues, DBHelper.ACTUAL_EXPENSE_ID + "=" + actualExpense.getActualExpenseId(), null);
        helper.close();
        return id;
    }

    public int deleteSpecificActualExpense(int itemId){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(itemId)};
        int result = sqlDB.delete(DBHelper.ACTUAL_EXPENSES_ITEMS_TABLE, DBHelper.ACTUAL_EXPENSE_ID + "=?", whereArgs);
        helper.close();
        return result;
    }

    /**************************************************** SALARYITEMCLASS *********************************************************/
    public double getTotalSavedItemsExpenses(){
        ArrayList<SalaryItemClass> monthlyItems = getAllSavedItemsSalaryBudget();
        double total = 0.00;
        if(monthlyItems.size()>0){
            for (SalaryItemClass salaryItem : monthlyItems) {
                total += salaryItem.getSalaryItemPrice();
            }
        }
        return total;
    }

    public double getTotalSavedItemsExpenses_daily(){
        ArrayList<SalaryItemClass> dailyItems = getAllSavedItemsSalaryBudget_Daily();
        double total = 0.00;
        if(dailyItems.size()>0){
            for (SalaryItemClass salaryItem : dailyItems) {
                total += salaryItem.getSalaryItemPrice();
            }
        }
        return total;
    }

    public ArrayList<SalaryItemClass> searchAllSavedItemsSalaryBudget(String query){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ITEM_ID, DBHelper.SALARY_ITEM_NAME, DBHelper.SALARY_ITEM_PRICE, DBHelper.SALARY_ITEM_QUANTITY, DBHelper.SALARY_ITEM_DURATION};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOptionMock()){
            case SettingsManager.NAME_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " DESC";
                break;
            default:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
        }
        String[] selArgs = {"%"+query+"%"};
        Cursor cursor = sqlDB.query(DBHelper.SALARY_ITEMS_TABLE, columns, DBHelper.SALARY_ITEM_NAME + " LIKE ?", selArgs, null, null, orderBy);
        ArrayList<SalaryItemClass> saved_items = new ArrayList<SalaryItemClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDuration = cursor.getString(4);
            SalaryItemClass salaryItemEntry = new SalaryItemClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDuration);
            saved_items.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public ArrayList<SalaryItemClass> getAllSavedItemsSalaryBudget(){
        String[] selArgs = {SalaryItemClass.DAILY};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ITEM_ID, DBHelper.SALARY_ITEM_NAME, DBHelper.SALARY_ITEM_PRICE, DBHelper.SALARY_ITEM_QUANTITY, DBHelper.SALARY_ITEM_DURATION};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOptionMock()){
            case SettingsManager.NAME_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " DESC";
                break;
            default:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
        }
        Cursor cursor = sqlDB.query(DBHelper.SALARY_ITEMS_TABLE, columns, DBHelper.SALARY_ITEM_DURATION + "!=?", selArgs, null, null, orderBy);
        ArrayList<SalaryItemClass> saved_items = new ArrayList<SalaryItemClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDuration = cursor.getString(4);
            SalaryItemClass salaryItemEntry = new SalaryItemClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDuration);
            saved_items.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public ArrayList<SalaryItemClass> getAllSavedItemsSalaryBudget_Daily() {
        String[] selArgs = {SalaryItemClass.DAILY};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ITEM_ID, DBHelper.SALARY_ITEM_NAME, DBHelper.SALARY_ITEM_PRICE, DBHelper.SALARY_ITEM_QUANTITY, DBHelper.SALARY_ITEM_DURATION};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getSortOptionMock()){
            case SettingsManager.NAME_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC_MOCK:
                orderBy = DBHelper.SALARY_ITEM_PRICE + " DESC";
                break;
            default:
                orderBy = DBHelper.SALARY_ITEM_NAME + " ASC";
        }
        Cursor cursor = sqlDB.query(DBHelper.SALARY_ITEMS_TABLE, columns, DBHelper.SALARY_ITEM_DURATION + "=?", selArgs, null, null, orderBy);
        ArrayList<SalaryItemClass> saved_items = new ArrayList<SalaryItemClass>();
        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            int itemQuantity = cursor.getInt(3);
            String itemDuration = cursor.getString(4);
            SalaryItemClass salaryItemEntry = new SalaryItemClass(itemId, itemName, Double.parseDouble(itemPrice), itemQuantity, itemDuration);
            saved_items.add(salaryItemEntry);
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public long insertIntoSavedItemsSalaryBudget(SalaryItemClass salaryItem){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SALARY_ITEM_NAME, salaryItem.getSalaryItemName());
        cValues.put(DBHelper.SALARY_ITEM_PRICE, salaryItem.getSalaryItemPrice());
        cValues.put(DBHelper.SALARY_ITEM_QUANTITY, salaryItem.getSalaryItemQuantity());
        cValues.put(DBHelper.SALARY_ITEM_DURATION, salaryItem.getSalaryItemDuration());
        long id = sqlDB.insert(DBHelper.SALARY_ITEMS_TABLE, null, cValues);
        helper.close();
        return id;
    }

    public long updateSavedItemsSalaryBudget(SalaryItemClass salaryItem){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SALARY_ITEM_NAME, salaryItem.getSalaryItemName());
        cValues.put(DBHelper.SALARY_ITEM_PRICE, salaryItem.getSalaryItemPrice());
        cValues.put(DBHelper.SALARY_ITEM_QUANTITY, salaryItem.getSalaryItemQuantity());
        cValues.put(DBHelper.SALARY_ITEM_DURATION, salaryItem.getSalaryItemDuration());
        long id = sqlDB.update(DBHelper.SALARY_ITEMS_TABLE, cValues, DBHelper.SALARY_ITEM_ID + "=" + salaryItem.getSalaryItemId(), null);
        helper.close();
        return id;
    }

    public int deleteSpecificItemSalaryBudget(int itemId){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(itemId)};
        int result = sqlDB.delete(DBHelper.SALARY_ITEMS_TABLE, DBHelper.SALARY_ITEM_ID + "=?", whereArgs);
        helper.close();
        return result;
    }

    /**************************************************** ITEMCLASS *********************************************************/
    public ArrayList<String> searchItems2(String query){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ITEM_NAME};
        String[] selArgs = {"%"+query+"%"};
        Cursor cursor = sqlDB.query(DBHelper.ITEMS_TABLE, columns, DBHelper.ITEM_NAME + " LIKE ?", selArgs, null, null, null);
        ArrayList<String> saved_items = new ArrayList<String>();
        while(cursor.moveToNext()){
            saved_items.add(cursor.getString(0));
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public ArrayList<ItemClass> searchItems(String query){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ITEM_ID, DBHelper.ITEM_NAME, DBHelper.ITEM_PRICE};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getItemSortOption()){
            case SettingsManager.NAME_ASC:
                orderBy = DBHelper.ITEM_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC:
                orderBy = DBHelper.ITEM_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC:
                orderBy = DBHelper.ITEM_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC:
                orderBy = DBHelper.ITEM_PRICE + " DESC";
                break;
            default:
                orderBy = DBHelper.ITEM_NAME + " ASC";
        }
        String[] selArgs = {"%"+query+"%"};
        Cursor cursor = sqlDB.query(DBHelper.ITEMS_TABLE, columns, DBHelper.ITEM_NAME + " LIKE ?", selArgs, null, null, orderBy);
        ArrayList<ItemClass> saved_items = new ArrayList<ItemClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            ItemClass itemEntry = new ItemClass(itemId, itemName, Double.parseDouble(itemPrice));
            saved_items.add(itemEntry);
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public ArrayList<ItemClass> getAllSavedItems(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.ITEM_ID, DBHelper.ITEM_NAME, DBHelper.ITEM_PRICE};
        SettingsManager settingsManager = new SettingsManager(mContext);
        String orderBy;
        switch (settingsManager.getItemSortOption()){
            case SettingsManager.NAME_ASC:
                orderBy = DBHelper.ITEM_NAME + " ASC";
                break;
            case SettingsManager.NAME_DESC:
                orderBy = DBHelper.ITEM_NAME + " DESC";
                break;
            case SettingsManager.PRICE_ASC:
                orderBy = DBHelper.ITEM_PRICE + " ASC";
                break;
            case SettingsManager.PRICE_DESC:
                orderBy = DBHelper.ITEM_PRICE + " DESC";
                break;
            default:
                orderBy = DBHelper.ITEM_NAME + " ASC";
        }
        Cursor cursor = sqlDB.query(DBHelper.ITEMS_TABLE, columns, null, null, null, null, orderBy);
        ArrayList<ItemClass> saved_items = new ArrayList<ItemClass>();
        while(cursor.moveToNext()){
            int itemId = cursor.getInt(0);
            String itemName = cursor.getString(1);
            String itemPrice = cursor.getString(2);
            ItemClass itemEntry = new ItemClass(itemId, itemName, Double.parseDouble(itemPrice));
            saved_items.add(itemEntry);
        }
        cursor.close();
        helper.close();
        return saved_items;
    }

    public long insertIntoSavedItems(ItemClass item){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.ITEM_NAME, item.getItemName());
        cValues.put(DBHelper.ITEM_PRICE, item.getItemPrice());
        long id = sqlDB.insert(DBHelper.ITEMS_TABLE, null, cValues);
        helper.close();
        return id;
    }

    public long updateSavedItem(ItemClass item){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.ITEM_NAME, item.getItemName());
        cValues.put(DBHelper.ITEM_PRICE, item.getItemPrice());
        long id = sqlDB.update(DBHelper.ITEMS_TABLE, cValues, DBHelper.ITEM_ID + "=" + item.getItemId(), null);
        helper.close();
        return id;
    }

    public int deleteSpecificItem(int itemId){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(itemId)};
        int result = sqlDB.delete(DBHelper.ITEMS_TABLE, DBHelper.ITEM_ID + "=?", whereArgs);
        String res = "Delete result: " + result;
        helper.close();
        return result;
    }

    /**************************************************BUDGET CLASS*****************************************************************/

    public double getCustomDateMonthlyBudget(String customDate){
        DateUtilities dateUtilities = new DateUtilities(customDate);
        String firstDateOfMonth = dateUtilities.getMonthDateBeginning();
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_AMOUNT};
        String selectedDates = DBHelper.SALARY_DATE_CHANGED + " >= '" + firstDateOfMonth + "' AND " + DBHelper.SALARY_DATE_CHANGED+" <= '"+ customDate+"' ";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SALARY, columns, selectedDates, null, null, null, DBHelper.SALARY_DATE_CHANGED + " DESC LIMIT 1");
        double budget = 0.00;
        if(cursor.moveToFirst()){
            String budgetStr = cursor.getString(0);
            budget = Double.parseDouble(budgetStr);
        }
        cursor.close();
        helper.close();
        return budget;
    }

    public double getBeforeCurrentMonthMonthlyBudget(String customDate){
        DateUtilities dateUtilities = new DateUtilities(customDate);
        String firstDateOfMonth = dateUtilities.getMonthDateBeginning();
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_AMOUNT};
        String selectedDates = DBHelper.SALARY_DATE_CHANGED + " < '" + firstDateOfMonth + "'";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SALARY, columns, selectedDates, null, null, null, DBHelper.SALARY_DATE_CHANGED + " DESC LIMIT 1");
        double budget = 0.00;
        if(cursor.moveToFirst()){
            String budgetStr = cursor.getString(0);
            budget = Double.parseDouble(budgetStr);
        }
        cursor.close();
        helper.close();
        return budget;
    }

    public long insertSalary(SalaryClass salary){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SALARY_AMOUNT, salary.getSalaryAmount());
        cValues.put(DBHelper.SALARY_DATE_CHANGED, salary.getSalaryDateChanged());
        long id = sqlDB.insert(DBHelper.TABLE_SALARY, null, cValues);
        helper.close();
        return id;
    }

    public SalaryClass getLatestSalary(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ID, DBHelper.SALARY_AMOUNT, DBHelper.SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SALARY, columns, null, null, null, null, DBHelper.SALARY_DATE_CHANGED + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public SalaryClass getCurrentDateSalary(){
        String[] currentDate = {new SettingsManager(mContext).getCurrentDate()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ID, DBHelper.SALARY_AMOUNT, DBHelper.SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SALARY, columns, DBHelper.SALARY_DATE_CHANGED+"=?", currentDate, null, null, DBHelper.SALARY_ID + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public long updateSalary(SalaryClass salary){
        String[] whereArgs = {Integer.toString(salary.getSalaryId()), salary.getSalaryDateChanged()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SALARY_AMOUNT, salary.getSalaryAmount());
        long id = sqlDB.update(DBHelper.TABLE_SALARY, cValues, DBHelper.SALARY_ID + "=? AND " + DBHelper.SALARY_DATE_CHANGED + "=?", whereArgs);
        helper.close();
        return id;
    }

    public ArrayList<SalaryClass> getSalaries(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SALARY_ID, DBHelper.SALARY_AMOUNT, DBHelper.SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SALARY, columns, null, null, null, null, DBHelper.SALARY_ID + " DESC LIMIT 50");
        ArrayList<SalaryClass> salaries = new ArrayList<SalaryClass>();
        while(cursor.moveToNext()){
            int salaryId = cursor.getInt(0);
            double salaryAmount = cursor.getDouble(1);
            String salaryDateChanged = cursor.getString(2);
            SalaryClass salary = new SalaryClass(salaryId, salaryAmount, salaryDateChanged);
            salaries.add(salary);
        }
        cursor.close();
        helper.close();
        return salaries;
    }

    public double getCustomDateDailyBudget(String customDate){
        DateUtilities dateUtilities = new DateUtilities(customDate);
        String firstDateOfMonth = dateUtilities.getMonthDateBeginning();
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SALARY_AMOUNT};
        String selectedDates = DBHelper.DAILY_SALARY_DATE_CHANGED + " >= '" + firstDateOfMonth + "' AND " + DBHelper.DAILY_SALARY_DATE_CHANGED+" <= '"+ customDate+"' ";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_DAILY_SALARY, columns, selectedDates, null, null, null, DBHelper.DAILY_SALARY_DATE_CHANGED + " DESC LIMIT 1");
        double budget = 0.00;
        if(cursor.moveToFirst()){
            String budgetStr = cursor.getString(0);
            budget = Double.parseDouble(budgetStr);
        }
        cursor.close();
        helper.close();
        return budget;
    }

    public double getBeforeCurrentMonthDailyBudget(String customDate){
        DateUtilities dateUtilities = new DateUtilities(customDate);
        String firstDateOfMonth = dateUtilities.getMonthDateBeginning();
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SALARY_AMOUNT};
        String selectedDates = DBHelper.DAILY_SALARY_DATE_CHANGED + " < '" + firstDateOfMonth + "'";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_DAILY_SALARY, columns, selectedDates, null, null, null, DBHelper.DAILY_SALARY_DATE_CHANGED + " DESC LIMIT 1");
        double budget = 0.00;
        if(cursor.moveToFirst()){
            String budgetStr = cursor.getString(0);
            budget = Double.parseDouble(budgetStr);
        }
        cursor.close();
        helper.close();
        return budget;
    }

    public long insertDailySalary(SalaryClass salary){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.DAILY_SALARY_AMOUNT, salary.getSalaryAmount());
        cValues.put(DBHelper.DAILY_SALARY_DATE_CHANGED, salary.getSalaryDateChanged());
        long id = sqlDB.insert(DBHelper.TABLE_DAILY_SALARY, null, cValues);
        helper.close();
        return id;
    }

    public SalaryClass getLatestDailySalary(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SALARY_ID, DBHelper.DAILY_SALARY_AMOUNT, DBHelper.DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_DAILY_SALARY, columns, null, null, null, null, DBHelper.DAILY_SALARY_DATE_CHANGED + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public SalaryClass getCurrentDateDailySalary(){
        String[] currentDate = {new SettingsManager(mContext).getCurrentDate()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SALARY_ID, DBHelper.DAILY_SALARY_AMOUNT, DBHelper.DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_DAILY_SALARY, columns, DBHelper.DAILY_SALARY_DATE_CHANGED+"=?", currentDate, null, null, DBHelper.DAILY_SALARY_ID + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public long updateDailySalary(SalaryClass salary){
        String[] whereArgs = {Integer.toString(salary.getSalaryId()), salary.getSalaryDateChanged()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.DAILY_SALARY_AMOUNT, salary.getSalaryAmount());
        long id = sqlDB.update(DBHelper.TABLE_DAILY_SALARY, cValues, DBHelper.DAILY_SALARY_ID + "=? AND " + DBHelper.DAILY_SALARY_DATE_CHANGED + "=?", whereArgs);
        helper.close();
        return id;
    }

    public ArrayList<SalaryClass> getDailySalaries(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SALARY_ID, DBHelper.DAILY_SALARY_AMOUNT, DBHelper.DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_DAILY_SALARY, columns, null, null, null, null, DBHelper.DAILY_SALARY_ID + " DESC LIMIT 50");
        ArrayList<SalaryClass> salaries = new ArrayList<SalaryClass>();
        while(cursor.moveToNext()){
            int salaryId = cursor.getInt(0);
            double salaryAmount = cursor.getDouble(1);
            String salaryDateChanged = cursor.getString(2);
            SalaryClass salary = new SalaryClass(salaryId, salaryAmount, salaryDateChanged);
            salaries.add(salary);
        }
        cursor.close();
        helper.close();
        return salaries;
    }

    /**************************************************MOCK SALARY*****************************************************************/

    public long insertMockSalary(SalaryClass salary){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_SALARY_AMOUNT, salary.getSalaryAmount());
        cValues.put(DBHelper.MOCK_SALARY_DATE_CHANGED, salary.getSalaryDateChanged());
        long id = sqlDB.insert(DBHelper.TABLE_MOCK_SALARY, null, cValues);
        helper.close();
        return id;
    }

    public SalaryClass getLatestMockSalary(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_SALARY_ID, DBHelper.MOCK_SALARY_AMOUNT, DBHelper.MOCK_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_SALARY, columns, null, null, null, null, DBHelper.MOCK_SALARY_DATE_CHANGED + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public SalaryClass getCurrentDateMockSalary(){
        String[] currentDate = {new SettingsManager(mContext).getCurrentDate()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_SALARY_ID, DBHelper.MOCK_SALARY_AMOUNT, DBHelper.MOCK_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_SALARY, columns, DBHelper.MOCK_SALARY_DATE_CHANGED+"=?", currentDate, null, null, DBHelper.MOCK_SALARY_ID + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public long updateMockSalary(SalaryClass salary){
        String[] whereArgs = {Integer.toString(salary.getSalaryId()), salary.getSalaryDateChanged()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_SALARY_AMOUNT, salary.getSalaryAmount());
        long id = sqlDB.update(DBHelper.TABLE_MOCK_SALARY, cValues, DBHelper.MOCK_SALARY_ID + "=? AND " + DBHelper.MOCK_SALARY_DATE_CHANGED + "=?", whereArgs);
        helper.close();
        return id;
    }

    public ArrayList<SalaryClass> getMockSalaries(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_SALARY_ID, DBHelper.MOCK_SALARY_AMOUNT, DBHelper.MOCK_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_SALARY, columns, null, null, null, null, DBHelper.MOCK_SALARY_ID + " DESC LIMIT 50");
        ArrayList<SalaryClass> salaries = new ArrayList<SalaryClass>();
        while(cursor.moveToNext()){
            int salaryId = cursor.getInt(0);
            double salaryAmount = cursor.getDouble(1);
            String salaryDateChanged = cursor.getString(2);
            SalaryClass salary = new SalaryClass(salaryId, salaryAmount, salaryDateChanged);
            salaries.add(salary);
        }
        cursor.close();
        helper.close();
        return salaries;
    }

    public long insertDailyMockSalary(SalaryClass salary){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_DAILY_SALARY_AMOUNT, salary.getSalaryAmount());
        cValues.put(DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED, salary.getSalaryDateChanged());
        long id = sqlDB.insert(DBHelper.TABLE_MOCK_DAILY_SALARY, null, cValues);
        helper.close();
        return id;
    }

    public SalaryClass getLatestDailyMockSalary(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_DAILY_SALARY_ID, DBHelper.MOCK_DAILY_SALARY_AMOUNT, DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_DAILY_SALARY, columns, null, null, null, null, DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public SalaryClass getCurrentDateMockDailySalary(){
        String[] currentDate = {new SettingsManager(mContext).getCurrentDate()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_DAILY_SALARY_ID, DBHelper.MOCK_DAILY_SALARY_AMOUNT, DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_DAILY_SALARY, columns, DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED+"=?", currentDate, null, null, DBHelper.MOCK_DAILY_SALARY_ID + " DESC LIMIT 1");
        if(cursor.moveToFirst()) {
            return new SalaryClass(cursor.getInt(0), cursor.getDouble(1), cursor.getString(2));
        }
        cursor.close();
        helper.close();
        return null;
    }

    public long updateDailyMockSalary(SalaryClass salary){
        String[] whereArgs = {Integer.toString(salary.getSalaryId()), salary.getSalaryDateChanged()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_DAILY_SALARY_AMOUNT, salary.getSalaryAmount());
        long id = sqlDB.update(DBHelper.TABLE_MOCK_DAILY_SALARY, cValues, DBHelper.MOCK_DAILY_SALARY_ID + "=? AND " + DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED + "=?", whereArgs);
        helper.close();
        return id;
    }

    public ArrayList<SalaryClass> getDailyMockSalaries(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_DAILY_SALARY_ID, DBHelper.MOCK_DAILY_SALARY_AMOUNT, DBHelper.MOCK_DAILY_SALARY_DATE_CHANGED};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_MOCK_DAILY_SALARY, columns, null, null, null, null, DBHelper.MOCK_DAILY_SALARY_ID + " DESC LIMIT 50");
        ArrayList<SalaryClass> salaries = new ArrayList<SalaryClass>();
        while(cursor.moveToNext()){
            int salaryId = cursor.getInt(0);
            double salaryAmount = cursor.getDouble(1);
            String salaryDateChanged = cursor.getString(2);
            SalaryClass salary = new SalaryClass(salaryId, salaryAmount, salaryDateChanged);
            salaries.add(salary);
        }
        cursor.close();
        helper.close();
        return salaries;
    }

    /**************************************************** SAVINGS CLASS *******************************************************/
    //getCurrentSavings      format: yyyy-MM
    public SavingsClass getCurrentSavingsMonthly(String currentDate){
        String[] selArgs = {currentDate};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.SAVINGS_ID, DBHelper.SAVINGS_AMOUNT, DBHelper.SAVINGS_MONTH};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SAVINGS, columns, DBHelper.SAVINGS_MONTH + "=?", selArgs, null, null, null);
        SavingsClass monthly = null;
        if(cursor.moveToFirst()){
            int savingsId = cursor.getInt(0);
            double savingsAmount = cursor.getDouble(1);
            String savingsMonth = cursor.getString(2);
            monthly = new SavingsClass(savingsId, savingsAmount, savingsMonth);
        }
        cursor.close();
        helper.close();
        return monthly;
    }
    //getCurrentSavingsDaily        format: yyyy-MM-dd
    public SavingsClass getCurrentSavingsDaily(String currentDate){
        String[] selArgs = {currentDate};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.DAILY_SAVINGS_ID, DBHelper.DAILY_SAVINGS_AMOUNT, DBHelper.DAILY_SAVINGS_DATE};
//        String yrAndMonth = new SimpleDateFormat("yyyy-MMM", Locale.getDefault()).format(Calendar.getInstance().getTime());
        Cursor cursor = sqlDB.query(DBHelper.TABLE_SAVINGS_DAILY, columns, DBHelper.DAILY_SAVINGS_DATE + "=?", selArgs, null, null, null);
        SavingsClass monthly = null;
        if(cursor.moveToFirst()){
            int savingsId = cursor.getInt(0);
            double savingsAmount = cursor.getDouble(1);
            String savingsMonth = cursor.getString(2);
            monthly = new SavingsClass(savingsId, savingsAmount, savingsMonth);
        }
        cursor.close();
        helper.close();
        return monthly;
    }
    //insertCurrentSavings
    public long insertCurrentSavingsMonthly(SavingsClass savingsClass){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SAVINGS_AMOUNT, savingsClass.getSavingsAmount());
        cValues.put(DBHelper.SAVINGS_MONTH, savingsClass.getSavingsDateOrDuration());
        long id = sqlDB.insert(DBHelper.TABLE_SAVINGS, null, cValues);
        helper.close();
        return id;
    }
    //insertCurrentDailySavings
    public long insertCurrentDailySavings(SavingsClass savingsClass){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.DAILY_SAVINGS_AMOUNT, savingsClass.getSavingsAmount());
        cValues.put(DBHelper.DAILY_SAVINGS_DATE, savingsClass.getSavingsDateOrDuration());
        long id = sqlDB.insert(DBHelper.TABLE_SAVINGS_DAILY, null, cValues);
        helper.close();
        return id;
    }
    //updateCurrentSavings
    public long updateCurrentSavings(SavingsClass savingsClass){
        String[] whereArgs = {Integer.toString(savingsClass.getSavingsId()), savingsClass.getSavingsDateOrDuration()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.SAVINGS_AMOUNT, savingsClass.getSavingsAmount());
        //savings_id=getSavingsId() AND savings_month=getSavingsDateOrDuration
        long id = sqlDB.update(DBHelper.TABLE_SAVINGS, cValues, DBHelper.SAVINGS_ID + "=? AND " + DBHelper.SAVINGS_MONTH + "=?", whereArgs);
        helper.close();
        return id;
    }
    //updateCurrentDailySavings
    public long updateCurrentDailySavings(SavingsClass savingsClass){
        String[] whereArgs = {Integer.toString(savingsClass.getSavingsId()), savingsClass.getSavingsDateOrDuration()};
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.DAILY_SAVINGS_AMOUNT, savingsClass.getSavingsAmount());
        long id = sqlDB.update(DBHelper.TABLE_SAVINGS_DAILY, cValues, DBHelper.DAILY_SAVINGS_ID + "=? AND " + DBHelper.DAILY_SAVINGS_DATE + "=?", whereArgs);
        helper.close();
        return id;
    }

    /************************************************** LOANS TABLE *****************************************************************/
    public ArrayList<LoanClass> getAllLoans(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.LOAN_ID, DBHelper.LOAN_LENDER_NAME, DBHelper.LOAN_AMOUNT, DBHelper.LOAN_DATE};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_LOANS, columns, null, null, null, null, DBHelper.LOAN_ID + " DESC");
        ArrayList<LoanClass> loans = new ArrayList<LoanClass>();
        while(cursor.moveToNext()){
            int loanId = cursor.getInt(0);
            String lenderName = cursor.getString(1);
            String loanAmount = cursor.getString(2);
            String loanDate = cursor.getString(3);
            LoanClass loanClass = new LoanClass(loanId, lenderName, Double.parseDouble(loanAmount), loanDate);
            loans.add(loanClass);
        }
        cursor.close();
        helper.close();
        return loans;
    }

    public ArrayList<LoanClass> getAllLoansBetweenDates(String beginDate, String endDate){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.LOAN_ID, DBHelper.LOAN_LENDER_NAME, DBHelper.LOAN_AMOUNT, DBHelper.LOAN_DATE};
        String selectedDates = DBHelper.LOAN_DATE + " >= '" + beginDate + "' AND " + DBHelper.LOAN_DATE+" <= '"+ endDate+"' ";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_LOANS, columns, selectedDates,null, null, null, DBHelper.LOAN_ID + " DESC");
        ArrayList<LoanClass> loans = new ArrayList<LoanClass>();
        while(cursor.moveToNext()){
            int loanId = cursor.getInt(0);
            String lenderName = cursor.getString(1);
            String loanAmount = cursor.getString(2);
            String loanDate = cursor.getString(3);
            LoanClass loanClass = new LoanClass(loanId, lenderName, Double.parseDouble(loanAmount), loanDate);
            loans.add(loanClass);
        }
        cursor.close();
        helper.close();
        return loans;
    }

    public double getLoansTotalBetweenDates(String endDate){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        DateUtilities dateUtilities = new DateUtilities(endDate);
//        String[] columns = {DBHelper.LOAN_ID, DBHelper.LOAN_LENDER_NAME, DBHelper.LOAN_AMOUNT, DBHelper.LOAN_DATE};
        String[] columns = {"SUM("+DBHelper.LOAN_AMOUNT+") AS total_loans"};
        String selectedDates = DBHelper.LOAN_DATE + " >= '" + dateUtilities.getMonthDateBeginning() + "' AND " + DBHelper.LOAN_DATE+" <= '"+ endDate+"' ";
        Cursor cursor = sqlDB.query(DBHelper.TABLE_LOANS, columns, selectedDates,null, null, null, DBHelper.LOAN_ID + " DESC");
        double totalLoans = 0.00;
        if(cursor.moveToFirst()){
            totalLoans = cursor.getDouble(0);
        }
        cursor.close();
        helper.close();
        return totalLoans;
    }

    public long insertLoan(LoanClass loan){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.LOAN_LENDER_NAME, loan.getLenderName());
        cValues.put(DBHelper.LOAN_AMOUNT, loan.getLoanAmount());
        cValues.put(DBHelper.LOAN_DATE, loan.getLoanDate());
        long id = sqlDB.insert(DBHelper.TABLE_LOANS, null, cValues);
        helper.close();
        return id;
    }

    public long updateLoan(LoanClass loan){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.LOAN_LENDER_NAME, loan.getLenderName());
        cValues.put(DBHelper.LOAN_AMOUNT, loan.getLoanAmount());
        cValues.put(DBHelper.LOAN_DATE, loan.getLoanDate());
        long id = sqlDB.update(DBHelper.TABLE_LOANS, cValues, DBHelper.LOAN_ID + "=" + loan.getId(), null);
        helper.close();
        return id;
    }

    public int deleteSpecificLoan(int itemId){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(itemId)};
        int result = sqlDB.delete(DBHelper.TABLE_LOANS, DBHelper.LOAN_ID + "=?", whereArgs);
        helper.close();
        return result;
    }

    public ArrayList<LoanClass> getAllMockLoans(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.MOCK_LOAN_ID, DBHelper.MOCK_LOAN_LENDER_NAME, DBHelper.MOCK_LOAN_AMOUNT};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_LOANS_MOCK, columns, null, null, null, null, DBHelper.MOCK_LOAN_ID + " DESC");
        ArrayList<LoanClass> loans = new ArrayList<LoanClass>();
        while(cursor.moveToNext()){
            int loanId = cursor.getInt(0);
            String lenderName = cursor.getString(1);
            String loanAmount = cursor.getString(2);
            LoanClass loanClass = new LoanClass(loanId, lenderName, Double.parseDouble(loanAmount));
            loans.add(loanClass);
        }
        cursor.close();
        helper.close();
        return loans;
    }

    public long insertMockLoan(LoanClass loan){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_LOAN_LENDER_NAME, loan.getLenderName());
        cValues.put(DBHelper.MOCK_LOAN_AMOUNT, loan.getLoanAmount());
        long id = sqlDB.insert(DBHelper.TABLE_LOANS_MOCK, null, cValues);
        helper.close();
        return id;
    }

    public long updateMockLoan(LoanClass loan){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.MOCK_LOAN_LENDER_NAME, loan.getLenderName());
        cValues.put(DBHelper.MOCK_LOAN_AMOUNT, loan.getLoanAmount());
        long id = sqlDB.update(DBHelper.TABLE_LOANS_MOCK, cValues, DBHelper.MOCK_LOAN_ID + "=" + loan.getId(), null);
        helper.close();
        return id;
    }

    public int deleteSpecificMockLoan(int itemId){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {Integer.toString(itemId)};
        int result = sqlDB.delete(DBHelper.TABLE_LOANS_MOCK, DBHelper.MOCK_LOAN_ID + "=?", whereArgs);
        helper.close();
        return result;
    }

    /**************** FOR BUDGET LISTS ****************************/
    public ArrayList<BudgetPlanClass> GetAllBudgetLists(){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.LIST_ID, DBHelper.LIST_NAME, DBHelper.LIST_BUDGET};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_PLANNED_BUDGET_LIST, columns, null, null, null, null, null);
        ArrayList<BudgetPlanClass> plans =  new ArrayList<BudgetPlanClass>();
        while(cursor.moveToNext()){
            int list_id = cursor.getInt(0);
            String list_name = cursor.getString(1);
            double budget = cursor.getDouble(2);
            BudgetPlanClass budgetPlanClass = new BudgetPlanClass(list_id, list_name, budget);
            plans.add(budgetPlanClass);
        }
        cursor.close();
        helper.close();
        return plans;
    }

    public BudgetPlanClass GetBudgetPlan(String planName){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] columns = {DBHelper.LIST_ID, DBHelper.LIST_NAME, DBHelper.LIST_BUDGET};
        String[] selArgs = {planName};
        Cursor cursor = sqlDB.query(DBHelper.TABLE_PLANNED_BUDGET_LIST, columns, DBHelper.LIST_NAME + "=?", selArgs, null, null, null);
        BudgetPlanClass budgetPlanClass = null;
        if(cursor.moveToFirst()){
            int list_id = cursor.getInt(0);
            String list_name = cursor.getString(1);
            double budget = cursor.getDouble(2);
            budgetPlanClass = new BudgetPlanClass(list_id, list_name, budget);
        }
        cursor.close();
        helper.close();
        return budgetPlanClass;
    }

    public long insertIntoBudgetLists(BudgetPlanClass budgetPlan){
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(DBHelper.LIST_NAME, budgetPlan.getPlan_name());
        cValues.put(DBHelper.LIST_BUDGET, budgetPlan.getBudget_ammount());
        long id = sqlDB.insert(DBHelper.TABLE_PLANNED_BUDGET_LIST, null, cValues);
        helper.close();
        return id;
    }

    public int deleteSpecificBudgetPlan(String planName){
        //delete * from testtable where name = 'name'
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        String[] whereArgs = {planName};
        int result = sqlDB.delete(DBHelper.TABLE_PLANNED_BUDGET_LIST, DBHelper.LIST_NAME + "=?", whereArgs);
        helper.close();
        return result;
    }

    public static class DBHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "budgeter";
        private static final int DATABASE_VERSION = 40;

        private static final String ITEMS_TABLE = "saved_items";
        private static final String SALARY_ITEMS_TABLE = "saved_items_salary_budget";
        private static final String ACTUAL_EXPENSES_ITEMS_TABLE = "saved_items_actual_expenses";
        private static final String TABLE_SALARY = "saved_salary";
        private static final String TABLE_DAILY_SALARY = "saved_daily_salary";
        private static final String TABLE_MOCK_SALARY = "mock_saved_salary";
        private static final String TABLE_MOCK_DAILY_SALARY = "mock_saved_daily_salary";
        private static final String TABLE_SAVINGS = "monthly_savings";
        private static final String TABLE_SAVINGS_DAILY = "daily_savings";
        private static final String TABLE_LOANS = "saved_loans";
        private static final String TABLE_LOANS_MOCK = "saved_mock_loans";
        private static final String TABLE_PLANNED_BUDGET_LIST = "planned_budget_list";

        //COLUMNS FOR SAVED ITEMS TABLE
        private static final String ITEM_ID = "item_id";
        private static final String ITEM_NAME = "item_name";
        private static final String ITEM_PRICE = "item_price";
        //COLUMNS FOR SALARY SAVED ITEMS TABLE
        private static final String SALARY_ITEM_ID = "salary_item_id";
        private static final String SALARY_ITEM_NAME = "salary_item_name";
        private static final String SALARY_ITEM_PRICE = "salary_item_price";
        private static final String SALARY_ITEM_QUANTITY = "salary_item_quantity";
        private static final String SALARY_ITEM_DURATION = "salary_item_duration";

        private static final String ACTUAL_EXPENSE_ID = "actual_expense_id";
        private static final String ACTUAL_EXPENSE_NAME = "actual_expense_name";
        private static final String ACTUAL_EXPENSE_PRICE = "actual_expense_price";
        private static final String ACTUAL_EXPENSE_QUANTITY = "actual_expense_quantity";
        private static final String ACTUAL_EXPENSE_DATE = "actual_expense_date";
        private static final String ACTUAL_EXPENSE_DURATION = "actual_expense_duration";
        //COLUMNS FOR SALARY TABLE
        private static final String SALARY_ID = "salary_id";
        private static final String SALARY_AMOUNT = "salary_amount";
        private static final String SALARY_DATE_CHANGED = "salary_date_changed";
        //COLUMNS FOR SALARY DAILY TABLE
        private static final String DAILY_SALARY_ID = "salary_id";
        private static final String DAILY_SALARY_AMOUNT = "salary_amount";
        private static final String DAILY_SALARY_DATE_CHANGED = "salary_date_changed";
        //COLUMNS FOR MOCK SALARY TABLE
        private static final String MOCK_SALARY_ID = "mock_salary_id";
        private static final String MOCK_SALARY_AMOUNT = "mock_salary_amount";
        private static final String MOCK_SALARY_DATE_CHANGED = "mock_salary_date_changed";
        //COLUMNS FOR MOCK SALARY DAILY TABLE
        private static final String MOCK_DAILY_SALARY_ID = "mock_salary_id";
        private static final String MOCK_DAILY_SALARY_AMOUNT = "mock_salary_amount";
        private static final String MOCK_DAILY_SALARY_DATE_CHANGED = "mock_salary_date_changed";
        //COLUMNS FOR SAVINGS TABLE
        private static final String SAVINGS_ID = "savings_id";
        private static final String SAVINGS_AMOUNT = "savings_amount";
        private static final String SAVINGS_MONTH = "savings_month";
        //COLUMNS FOR SAVINGS DAILY TABLE
        private static final String DAILY_SAVINGS_ID = "daily_savings_id";
        private static final String DAILY_SAVINGS_AMOUNT = "daily_savings_amount";
        private static final String DAILY_SAVINGS_DATE = "daily_savings_date";
        //COLUMNS FOR LOANS TABLE
        private static final String LOAN_ID = "loan_id";
        private static final String LOAN_LENDER_NAME = "lender";
        private static final String LOAN_AMOUNT = "loan_amount";
        private static final String LOAN_DATE = "loan_date";
        //COLUMNS FOR MOCK LOANS
        private static final String MOCK_LOAN_ID = "loan_id";
        private static final String MOCK_LOAN_LENDER_NAME = "lender";
        private static final String MOCK_LOAN_AMOUNT = "loan_amount";
        //COLUMNS FOR TABLE_PLANNED_BUDGET_LIST
        private static final String LIST_ID = "list_id";
        private static final String LIST_NAME = "list_name";
        private static final String LIST_BUDGET = "list_budget";

        private static final String CREATE_TABLE1 = "CREATE TABLE " + ITEMS_TABLE +
                " (" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT COLLATE NOCASE, " +
                ITEM_PRICE + " DOUBLE)";

        private static final String CREATE_TABLE2 = "CREATE TABLE " + SALARY_ITEMS_TABLE +
                " (" + SALARY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SALARY_ITEM_NAME + " TEXT COLLATE NOCASE, " +
                SALARY_ITEM_PRICE + " DOUBLE, " +
                SALARY_ITEM_QUANTITY + " INTEGER, " +
                SALARY_ITEM_DURATION + " TEXT)";

        private static final String CREATE_TABLE5 = "CREATE TABLE " + ACTUAL_EXPENSES_ITEMS_TABLE +
                " (" + ACTUAL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACTUAL_EXPENSE_NAME + " TEXT COLLATE NOCASE, " +
                ACTUAL_EXPENSE_PRICE + " DOUBLE, " +
                ACTUAL_EXPENSE_QUANTITY + " INTEGER, " +
                ACTUAL_EXPENSE_DATE + " DATE, " +
                ACTUAL_EXPENSE_DURATION + " TEXT)";

        private static final String CREATE_SALARY_TABLE = "CREATE TABLE " + TABLE_SALARY +
                " (" + SALARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SALARY_AMOUNT + " DOUBLE, " +
                SALARY_DATE_CHANGED + " DATE)";

        private static final String CREATE_DAILY_SALARY_TABLE = "CREATE TABLE " + TABLE_DAILY_SALARY +
                " (" + DAILY_SALARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DAILY_SALARY_AMOUNT + " DOUBLE, " +
                DAILY_SALARY_DATE_CHANGED + " DATE)";

        private static final String CREATE_MOCK_SALARY_TABLE = "CREATE TABLE " + TABLE_MOCK_SALARY +
                " (" + MOCK_SALARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOCK_SALARY_AMOUNT + " DOUBLE, " +
                MOCK_SALARY_DATE_CHANGED + " DATE)";

        private static final String CREATE_MOCK_DAILY_SALARY_TABLE = "CREATE TABLE " + TABLE_MOCK_DAILY_SALARY +
                " (" + MOCK_DAILY_SALARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOCK_DAILY_SALARY_AMOUNT + " DOUBLE, " +
                MOCK_DAILY_SALARY_DATE_CHANGED + " DATE)";

        private static final String CREATE_SAVINGS_MONTHLY = "CREATE TABLE " + TABLE_SAVINGS +
                " (" + SAVINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SAVINGS_AMOUNT + " DOUBLE, " +
                SAVINGS_MONTH + " TEXT)";

        private static final String CREATE_SAVINGS_DAILY= "CREATE TABLE " + TABLE_SAVINGS_DAILY +
                " (" + DAILY_SAVINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DAILY_SAVINGS_AMOUNT + " DOUBLE, " +
                DAILY_SAVINGS_DATE + " TEXT)";

        private static final String CREATE_LOANS_TABLE = "CREATE TABLE " + TABLE_LOANS +
                " (" + LOAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LOAN_LENDER_NAME + " TEXT COLLATE NOCASE, " +
                LOAN_AMOUNT + " DOUBLE, " +
                LOAN_DATE + " DATE)";

        private static final String CREATE_MOCK_LOANS_TABLE = "CREATE TABLE " + TABLE_LOANS_MOCK +
                " (" + MOCK_LOAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MOCK_LOAN_LENDER_NAME + " TEXT COLLATE NOCASE, " +
                MOCK_LOAN_AMOUNT + " DOUBLE) ";

        private static final String CREATE_PLANNED_BUDGET_LIST_TABLE = "CREATE TABLE " + TABLE_PLANNED_BUDGET_LIST +
                " (" + LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LIST_NAME + " TEXT, " +
                LIST_BUDGET + " DOUBLE) ";

        private static final String DROP_TABLE1 = "DROP TABLE IF EXISTS " + ITEMS_TABLE;
        private static final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + SALARY_ITEMS_TABLE;
        private static final String DROP_TABLE5 = "DROP TABLE IF EXISTS " + ACTUAL_EXPENSES_ITEMS_TABLE;
        private static final String DROP_SALARY = "DROP TABLE IF EXISTS " + TABLE_SALARY;
        private static final String DROP_DAILY_SALARY = "DROP TABLE IF EXISTS " + TABLE_DAILY_SALARY;
        private static final String DROP_MOCK_SALARY = "DROP TABLE IF EXISTS " + TABLE_MOCK_SALARY;
        private static final String DROP_MOCK_DAILY_SALARY = "DROP TABLE IF EXISTS " + TABLE_MOCK_DAILY_SALARY;
        private static final String DROP_SAVINGS = "DROP TABLE IF EXISTS " + TABLE_SAVINGS;
        private static final String DROP_SAVINGS_DAILY = "DROP TABLE IF EXISTS " + TABLE_SAVINGS_DAILY;
        private static final String DROP_LOANS_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOANS;
        private static final String DROP_LOANS_TABLE_MOCK = "DROP TABLE IF EXISTS " + TABLE_LOANS_MOCK;
        private static final String DROP_LOANS_TABLE_PLANNED_BUDGET_LIST = "DROP TABLE IF EXISTS " + TABLE_PLANNED_BUDGET_LIST;

        private Context context;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE1);
                db.execSQL(CREATE_TABLE2);
                db.execSQL(CREATE_TABLE5);
                db.execSQL(CREATE_SALARY_TABLE);
                db.execSQL(CREATE_DAILY_SALARY_TABLE);
                db.execSQL(CREATE_MOCK_SALARY_TABLE);
                db.execSQL(CREATE_MOCK_DAILY_SALARY_TABLE);
                db.execSQL(CREATE_SAVINGS_MONTHLY);
                db.execSQL(CREATE_SAVINGS_DAILY);
                db.execSQL(CREATE_LOANS_TABLE);
                db.execSQL(CREATE_MOCK_LOANS_TABLE);
                db.execSQL(CREATE_PLANNED_BUDGET_LIST_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE1);
                db.execSQL(DROP_TABLE2);
                db.execSQL(DROP_TABLE5);
                db.execSQL(DROP_SALARY);
                db.execSQL(DROP_DAILY_SALARY);
                db.execSQL(DROP_MOCK_SALARY);
                db.execSQL(DROP_MOCK_DAILY_SALARY);
                db.execSQL(DROP_SAVINGS);
                db.execSQL(DROP_SAVINGS_DAILY);
                db.execSQL(DROP_LOANS_TABLE);
                db.execSQL(DROP_LOANS_TABLE_MOCK);
                db.execSQL(DROP_LOANS_TABLE_PLANNED_BUDGET_LIST);
                onCreate(db);
                } catch (SQLException e) {

            }
        }

    }

}
