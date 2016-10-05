package com.lemond.kurt.budgeter.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by kurt_capatan on 2/18/2016.
 */
public class SettingsManager {
    private Context mContext;
    private DatabaseAdapter dbAdapter;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    //default settings
    private String currency = "$";
    private String exceedExpenses = "Ask First";
    private boolean lenders = true;
    private boolean distributeMonthlySavings = false;
    private boolean distributeMockMonthlySavings = false;
    private int numberOfDays = 30;
    private int mockNumberOfDays = 30;
    private String selectedStatisticsData = "expenses";
    private String databasePath = "/storage";
    private boolean isFirstTimeAppLaunched = true;
    private String selectedChartType = "bar";

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String currentDate = df.format(new Date());

    public SettingsManager(Context mContext) {
        this.mContext = mContext;
        dbAdapter = new DatabaseAdapter(mContext);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPreferences.edit();
    }

    /******************FIRST TIME EVENT CHECKERS*********************************/

    public boolean isFirstTimeAppLaunched() {
        return  mPreferences.getBoolean("isFirstTimeAppLaunched", isFirstTimeAppLaunched);
    }

    public void setIsFirstTimeAppLaunched(boolean isFirstTimeAppLaunched) {
        mEditor.putBoolean("isFirstTimeAppLaunched", isFirstTimeAppLaunched);
        mEditor.commit();
    }

    /*******************SETTINGS************************/

    public String getCurrentDate() {
        return mPreferences.getString("CurrentDate", currentDate);
    }

    public void setCurrentDate(String currentDate) {
        mEditor.putString("CurrentDate", currentDate);
        mEditor.commit();
    }


    public String getCurrency() {
        return mPreferences.getString("Currency", currency).trim();
    }

    public void setCurrency(String currency) {
        mEditor.putString("Currency", currency);
        mEditor.commit();
    }


    public String getExceedExpenses() {
        return mPreferences.getString("ExceedExpenses", exceedExpenses);
    }

    public void setExceedExpenses(String exceed) {
        mEditor.putString("ExceedExpenses", exceed);
        mEditor.commit();
    }

    public boolean isLenders() {
        return mPreferences.getBoolean("Lenders", lenders);
    }

    public void setLenders(boolean lenders) {
        mEditor.putBoolean("Lenders", lenders);
        mEditor.commit();
    }

    /************************** ACTUAL BUDGET ********************************************/
    public double getMonthlyBudget() {
        double salary = 0.00;
        if((salary = dbAdapter.getCustomDateMonthlyBudget(getCurrentDate()))==0){
            salary = dbAdapter.getBeforeCurrentMonthMonthlyBudget(getCurrentDate());
        }
        return salary;
    }

    public void setMonthlyBudget(double monthlySavings) {
        SalaryClass salary;
        if((salary = dbAdapter.getCurrentDateSalary())!=null) {
            salary.setSalaryAmount(monthlySavings);
            dbAdapter.updateSalary(salary);
        }else {
            salary = new SalaryClass(monthlySavings, getCurrentDate());
            dbAdapter.insertSalary(salary);
        }
    }

    public boolean isDistributeMonthlySavings() {
        return mPreferences.getBoolean("DistributeMonthlySavings", distributeMonthlySavings);
    }

    public void setDistributeMonthlySavings(boolean distributeMonthlySavings) {
        mEditor.putBoolean("DistributeMonthlySavings", distributeMonthlySavings);
        mEditor.commit();
    }

    public int getNumberOfDays() {
        return mPreferences.getInt("NumberOfDays", numberOfDays);
    }

    public void setNumberOfDays(int numberOfDays) {
        mEditor.putInt("NumberOfDays", numberOfDays);
        mEditor.commit();
    }

    public double getDailyBudget() {
        double salary = 0.00;
        if((salary = dbAdapter.getCustomDateDailyBudget(getCurrentDate()))==0) {
            salary = dbAdapter.getBeforeCurrentMonthDailyBudget(getCurrentDate());
        }
        return salary;
    }

    public void setDailyBudget(double dailySavings) {
        SalaryClass salary;
        if((salary = dbAdapter.getCurrentDateDailySalary())!=null) {
            salary.setSalaryAmount(dailySavings);
            dbAdapter.updateDailySalary(salary);
        }else {
            salary = new SalaryClass(dailySavings, getCurrentDate());
            dbAdapter.insertDailySalary(salary);
        }
    }


    /************************************** PLANNED BUDGET SAVINGS ****************************************************/

    public double getMockMonthlyBudget() {
        double salary = 0.00;
        if(dbAdapter.getLatestMockSalary()!=null){
            salary = dbAdapter.getLatestMockSalary().getSalaryAmount();
        }
        return salary;
    }

    public void setMockMonthlyBudget(double mockMonthlySavings) {
        SalaryClass salary;
        if((salary = dbAdapter.getCurrentDateMockSalary())!=null) {
            salary.setSalaryAmount(mockMonthlySavings);
            dbAdapter.updateMockSalary(salary);
        }else {
            salary = new SalaryClass(mockMonthlySavings, getCurrentDate());
            dbAdapter.insertMockSalary(salary);
        }
    }

    //create default values for mock distributeMockMonthlySavings, daily
    public boolean isDistributeMockMonthlyBudget() {
        return mPreferences.getBoolean("DistributeMockMonthlySavings", distributeMockMonthlySavings);
    }

    public void setDistributeMockMonthlyBudget(boolean distributeMockMonthlySavings) {
        mEditor.putBoolean("DistributeMockMonthlySavings", distributeMockMonthlySavings);
        mEditor.commit();
    }

    public int getMockNumberOfDays() {
        return mPreferences.getInt("MockNumberOfDays", mockNumberOfDays);
    }

    public void setMockNumberOfDays(int mockNumberOfDays) {
        mEditor.putInt("MockNumberOfDays", mockNumberOfDays);
        mEditor.commit();
    }

    public double getMockDailyBudget() {
        double salary = 0.00;
        if(dbAdapter.getLatestDailyMockSalary()!=null) {
            salary = dbAdapter.getLatestDailyMockSalary().getSalaryAmount();
        }
        return salary;
    }

    public void setMockDailyBudget(double dailyMockSavings) {
       SalaryClass salary;
        if((salary = dbAdapter.getCurrentDateMockDailySalary())!=null) {
            salary.setSalaryAmount(dailyMockSavings);
            dbAdapter.updateDailyMockSalary(salary);
        }else {
            salary = new SalaryClass(dailyMockSavings, getCurrentDate());
            dbAdapter.insertDailyMockSalary(salary);
        }
    }

    /******************************SORT OPTIONS***************************************/
    public static final String NAME_ASC = "name_asc";
    public static final String NAME_DESC = "name_desc";
    public static final String PRICE_ASC = "price_asc";
    public static final String PRICE_DESC = "price_desc";
    public static final String DATE_ASC = "date_asc";
    public static final String DATE_DESC = "date_desc";

    public String getSortOption() {
        return mPreferences.getString("SortOption", NAME_ASC);
    }

    public void setSortOption(String sortOption) {
        mEditor.putString("SortOption", sortOption);
        mEditor.commit();
    }

    public static final String NAME_ASC_MOCK = "name_asc";
    public static final String NAME_DESC_MOCK = "name_desc";
    public static final String PRICE_ASC_MOCK = "price_asc";
    public static final String PRICE_DESC_MOCK = "price_desc";


    public String getSortOptionMock() {
        return mPreferences.getString("SortOptionMock", NAME_ASC_MOCK);
    }

    public void setSortOptionMock(String sortOption) {
        mEditor.putString("SortOptionMock", sortOption);
        mEditor.commit();
    }

    public String getItemSortOption(){
        return mPreferences.getString("ItemSortOption", NAME_ASC_MOCK);
    }

    public void setItemSortOption(String sortOption){
        mEditor.putString("ItemSortOption", sortOption);
        mEditor.commit();
    }

    /************************ FOR SERVICE *******************************/
    public void setSavingsUpdateDates(HashSet<String> updateDates){
        mEditor.putStringSet("Savings_dates", updateDates);
        mEditor.commit();
    }

    public HashSet<String> getSavingsUpdateDates(){
        return (HashSet<String>) mPreferences.getStringSet("Savings_dates", new HashSet<String>());
    }

    public void setSavingsUpdateYearMonth(HashSet<String> yearMonth){
        mEditor.putStringSet("Savings_yearMonth", yearMonth);
        mEditor.commit();
    }

    public HashSet<String> getSavingsUpdateYearMonth(){
        return (HashSet<String>) mPreferences.getStringSet("Savings_yearMonth", new HashSet<String>());
    }

    /***********************STATISTICS********************************/

    public String getSelectedStatisticsData() {
        return mPreferences.getString("stat_data", selectedStatisticsData);
    }

    /**
     * Show which data to be shown on graph
     * @param selectedStatisticsData must be "expenses" or "savings"
     */
    public void setSelectedStatisticsData(String selectedStatisticsData) {
        mEditor.putString("stat_data", selectedStatisticsData);
        mEditor.commit();
    }

    public String getSelectedChartType() {
        return mPreferences.getString("chart_type", selectedChartType);
    }

    /**
     * sets chart to bar or line
     * @param selectedChartType must be 'bar' or 'line'
     */
    public void setSelectedChartType(String selectedChartType) {
        mEditor.putString("chart_type", selectedChartType);
        mEditor.commit();
    }

    /**********************IMPORT/EXPORT DATABASE********************/


    public String getDatabasePath() {
        return mPreferences.getString("db_path", databasePath);

    }

    public void setDatabasePath(String databasePath) {
        mEditor.putString("db_path", databasePath);
        mEditor.commit();
    }



}
