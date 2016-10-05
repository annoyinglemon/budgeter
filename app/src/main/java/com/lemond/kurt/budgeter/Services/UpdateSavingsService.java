package com.lemond.kurt.budgeter.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.MainActivity;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.ObjectClasses.SavingsClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by kurt_capatan on 7/28/2016.
 */
public class UpdateSavingsService extends IntentService {

    private NotificationManager mNotificationManager;
    private DatabaseAdapter mDbAdapter;
    private SettingsManager mSettings;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private SimpleDateFormat whole = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance(Locale.getDefault());


    public UpdateSavingsService() {
        super("UpdateSavingsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDbAdapter = new DatabaseAdapter(this);
        mSettings = new SettingsManager(this);

        updateSavings();
        Log.d("UpdateSavingsService", "Savings Updated");

        this.updateDailyBudget();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0 || hour == 1) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            SavingsClass save = mDbAdapter.getCurrentSavingsDaily(sdf.format(calendar.getTime()));

            SimpleDateFormat whole = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
            showNotification(whole.format(calendar.getTime()) + " savings: " + mSettings.getCurrency() + " " + String.format("%1$.2f", save.getSavingsAmount()), "Tap to view");
        } else if (hour == 11 || hour == 12 || hour == 13) {
            ArrayList<ActualExpensesClass> actualExpensesClasses = mDbAdapter.getAllActualExpensesByDate(sdf.format(calendar.getTime()));
            if (actualExpensesClasses.size() == 0) {
                showNotification("No expenses added yet as of this day.", "Tap to add item/s");
            }
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void updateSavings() {
        //get date for today
        //if hour is 12 am (midnight) deduct date by one, which means yesterday
//        if(calendar1.get(Calendar.HOUR_OF_DAY)==0||calendar.get(Calendar.HOUR_OF_DAY)==1) calendar.add(Calendar.DATE, -1);
        // get all update dates saved on settings
        HashSet<String> dailyUpdates = mSettings.getSavingsUpdateDates();
        //check if update dates have values
        if (dailyUpdates.size() > 0) {
            //for comparing dates to today's date
            boolean hasToday = false;
            for (String date : dailyUpdates) {
                //if date in hashset is equal to today's date
                if (date.equalsIgnoreCase(sdf.format(calendar.getTime()))) hasToday = true;
                double savings = getDailyBudget(date) - mDbAdapter.getTotalExpensesByDate(date);
                SavingsClass savingsClass = mDbAdapter.getCurrentSavingsDaily(date);
                if (savingsClass != null) {
                    savingsClass.setSavingsAmount(savings);
                    mDbAdapter.updateCurrentDailySavings(savingsClass);
                } else {
                    savingsClass = new SavingsClass(savings, date);
                    mDbAdapter.insertCurrentDailySavings(savingsClass);
                }
                //add yearmonth of each date to update the savings of its month also
                HashSet<String> monthlyUpdates = mSettings.getSavingsUpdateYearMonth();
                monthlyUpdates.add(date.substring(0, 7));
                mSettings.setSavingsUpdateYearMonth(monthlyUpdates);
            }
            //if not a single date is equal to today's date
            if (!hasToday) {
                //insert daily budget as savings
                SavingsClass savingsClass = new SavingsClass(getDailyBudget(sdf.format(calendar.getTime())), sdf.format(calendar.getTime()));
                mDbAdapter.insertCurrentDailySavings(savingsClass);
            }
            //empty the update date hashset in settings
            mSettings.setSavingsUpdateDates(new HashSet<String>());
            // if hash set has no values, it means user does not add, update or delete an item
        } else {
            //insert daily budget as savings
            SavingsClass savingsClass = new SavingsClass(getDailyBudget(sdf.format(calendar.getTime())), sdf.format(calendar.getTime()));
            mDbAdapter.insertCurrentDailySavings(savingsClass);
        }

        DateUtilities dateUtilities = new DateUtilities(sdf.format(calendar.getTime()));
        HashSet<String> monthlyUpdates = mSettings.getSavingsUpdateYearMonth();
        if (monthlyUpdates.size() > 0) {
            boolean hasToday = false;
            for (String monthYear : monthlyUpdates) {
                if (monthYear.equalsIgnoreCase(sdf.format(calendar.getTime()).substring(0, 7)))
                    hasToday = true;
                double savings = getMonthlyBudget(monthYear) - mDbAdapter.getTotalExpensesByMonth(monthYear);
                SavingsClass savingsClass = mDbAdapter.getCurrentSavingsMonthly(monthYear);
                if (savingsClass != null) {
                    savingsClass.setSavingsAmount(savings);
                    mDbAdapter.updateCurrentSavings(savingsClass);
                } else {
                    savingsClass = new SavingsClass(savings, monthYear);
                    mDbAdapter.insertCurrentSavingsMonthly(savingsClass);
                }
            }
            //if not a single date is equal to today's date
            if (!hasToday && sdf.format(calendar.getTime()).equalsIgnoreCase(dateUtilities.getMonthDateEnd())) {
                //insert daily budget as savings
                SavingsClass savingsClass = new SavingsClass(getMonthlyBudget(sdf.format(calendar.getTime()).substring(0, 7)), sdf.format(calendar.getTime()).substring(0, 7));
                mDbAdapter.insertCurrentSavingsMonthly(savingsClass);
            }
        } else {
            //insert daily budget as savings
            SavingsClass savingsClass = new SavingsClass(getMonthlyBudget(sdf.format(calendar.getTime()).substring(0, 7)), sdf.format(calendar.getTime()).substring(0, 7));
            mDbAdapter.insertCurrentSavingsMonthly(savingsClass);
        }
        mSettings.setSavingsUpdateYearMonth(new HashSet<String>());
    }

    public double getDailyBudget(String date) {
        double salary = 0.00;
        if ((salary = mDbAdapter.getCustomDateDailyBudget(date)) == 0) {
            salary = mDbAdapter.getBeforeCurrentMonthDailyBudget(date);
        }
        return salary;
    }

    public double getMonthlyBudget(String monthYear) {
        double salary = 0.00;
        //yyyy-MM
        String month = monthYear.substring(5, 7);
        switch (month) {
            case "01":
                monthYear = monthYear.concat("-" + DateUtilities.JANUARY);
                break;
            case "02":
                if ((Integer.parseInt(monthYear.substring(0, 4)) % 4) == 0)
                    monthYear = monthYear.concat("-" + DateUtilities.FEBRUARY_LEAP_YEAR);
                else
                    monthYear = monthYear.concat("-" + DateUtilities.FEBRUARY);
                break;
            case "03":
                monthYear = monthYear.concat("-" + DateUtilities.MARCH);
                break;
            case "04":
                monthYear = monthYear.concat("-" + DateUtilities.APRIL);
                break;
            case "05":
                monthYear = monthYear.concat("-" + DateUtilities.MAY);
                break;
            case "06":
                monthYear = monthYear.concat("-" + DateUtilities.JUNE);
                break;
            case "07":
                monthYear = monthYear.concat("-" + DateUtilities.JULY);
                break;
            case "08":
                monthYear = monthYear.concat("-" + DateUtilities.AUGUST);
                break;
            case "09":
                monthYear = monthYear.concat("-" + DateUtilities.SEPTEMBER);
                break;
            case "10":
                monthYear = monthYear.concat("-" + DateUtilities.OCTOBER);
                break;
            case "11":
                monthYear = monthYear.concat("-" + DateUtilities.NOVEMBER);
                break;
            case "12":
                monthYear = monthYear.concat("-" + DateUtilities.DECEMBER);
                break;
            default:
                monthYear = monthYear.concat("-28");
        }
        if ((salary = mDbAdapter.getCustomDateMonthlyBudget(monthYear)) == 0) {
            salary = mDbAdapter.getBeforeCurrentMonthMonthlyBudget(monthYear);
        }
        return salary;
    }


    private void showNotification(String contentTitle, String contentText) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        main.putExtra("current_date", sdf.format(calendar.getTime()));
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 1, main, 0);

        PendingIntent contentIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, main, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_month_view)
                .setContentTitle(contentTitle)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentText(contentText);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }


    public void updateDailyBudget() {
        DateUtilities mDateGetter = new DateUtilities(sdf.format(calendar.getTime()));
        String beginDate = mDateGetter.getMonthDateBeginning();
        String endDate = mDateGetter.getMonthDateEnd();
        //get total numbers of all 'monthly' expense;
        double totalMonthlyExpense = mDbAdapter.getTotalExpensesBetweenDates(beginDate, endDate, ActualExpensesClass.MONTHLY);
        //deduct totalMonthlyExpense  to monthlybudget
        double dividend = getTotalBudget() - totalMonthlyExpense;
        // divide the difference by the number of days in settings
        double dailyBudget = dividend / (double) mDateGetter.getCurrentMonthNumberOfDays();
        // set the new and daily
        if (dailyBudget < 0) mSettings.setDailyBudget(0.00);
        else mSettings.setDailyBudget(dailyBudget);
    }

    public Double getTotalBudget() {
        DateUtilities mDateGetter = new DateUtilities(sdf.format(calendar.getTime()));
        String beginDate = mDateGetter.getMonthDateBeginning();
        String endDate = mDateGetter.getMonthDateEnd();
        double totalBudget = 0.00;
        ArrayList<LoanClass> loans = mDbAdapter.getAllLoansBetweenDates(beginDate, endDate);
        if (loans.size() > 0) {
            for (LoanClass loan : loans) {
                totalBudget = totalBudget + loan.getLoanAmount();
            }
        }
        totalBudget = totalBudget + mSettings.getMonthlyBudget();
        return totalBudget;
    }
}
