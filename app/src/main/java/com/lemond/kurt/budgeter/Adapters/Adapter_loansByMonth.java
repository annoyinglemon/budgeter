package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.CurrentMonthExpenses_Pager;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.CurrentMonthExpenses_PagerAdapter;
import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by lemond on 2/15/16.
 */
public class Adapter_loansByMonth extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<LoanClass> mLoanList;
    private Context mContext;
    private DatabaseAdapter dbAdapter;
    private CurrentMonthExpenses currentMonth_Fragment;
    private String startOfCurrentMonth;
    private String endofCurrentMonth;
    private SettingsManager mSettingsManager;

    public Adapter_loansByMonth(Context mContext, ArrayList<LoanClass> loans) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.mLoanList = loans;
        this.dbAdapter = new DatabaseAdapter(mContext);
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        CurrentMonthExpenses_Pager myFragment = (CurrentMonthExpenses_Pager) fragmentManager.findFragmentByTag("CurrentMonthExpenses");
        ViewPager vpPager = myFragment.getViewPager();
        int index = vpPager.getCurrentItem();
        CurrentMonthExpenses_PagerAdapter adapter = ((CurrentMonthExpenses_PagerAdapter)vpPager.getAdapter());
        this.currentMonth_Fragment = (CurrentMonthExpenses)adapter.getRegisteredFragment(index);
        mSettingsManager = new SettingsManager(mContext);
        DateUtilities dateGetter = new DateUtilities(mSettingsManager.getCurrentDate());
        startOfCurrentMonth = dateGetter.getMonthDateBeginning();
        endofCurrentMonth = dateGetter.getMonthDateEnd();
    }

    @Override
    public int getCount() {
        return mLoanList.size();
    }

    @Override
    public LoanClass getItem(int position) {
        return mLoanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mLoanList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        G_ViewHolders.LoansByMonth holder = new G_ViewHolders.LoansByMonth();
        convertView = inflater.inflate(R.layout.loans_by_month_item_layout, parent, false);
        holder.lenderName = (TextView) convertView.findViewById(R.id.loanByMonth_lenderName);
        holder. loanAmount = (TextView) convertView.findViewById(R.id.loanByMonth_loanAmount);
        holder. loanDate = (TextView) convertView.findViewById(R.id.loanByMonth_loanDate);
        holder. loanByMonth_delete = (ImageView) convertView.findViewById(R.id.loanByMonth_delete);
        holder.lenderName.setText(mLoanList.get(position).getLenderName());
        holder.loanAmount.setText(mSettingsManager.getCurrency() + " " + G_Functions.formatNumber(mLoanList.get(position).getLoanAmount()));
        holder.loanDate.setText(DateUtilities.makeReadableFormat(mLoanList.get(position).getLoanDate()));
        holder.loanByMonth_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(mContext);
                mainbuilder.setMessage("Are you sure you want to delete " + mLoanList.get(position).getLenderName() + "?");
                mainbuilder.setTitle("Delete Lender");
                mainbuilder.setCancelable(true);
                mainbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        deleteSingleExpense(mLoanList.get(position));
                        currentMonth_Fragment.updateData();
                        if(mSettingsManager.isDistributeMonthlySavings())currentMonth_Fragment.getAdapter().updateDailyBudget();
                    }
                });
                mainbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog mainAlert = mainbuilder.create();
                mainAlert.show();
            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    public Double getTotalBudget(){
        double totalBudget = 0.00;
        ArrayList<LoanClass> loans = dbAdapter.getAllLoansBetweenDates(startOfCurrentMonth, endofCurrentMonth);
        if(loans.size()>0) {
            for (LoanClass loan : loans) {
                totalBudget = totalBudget + loan.getLoanAmount();
            }
        }
        totalBudget = totalBudget + mSettingsManager.getMonthlyBudget();

        return totalBudget;
    }

    /*********************************************** DATABASE FUNCTIONS *************************************************************/

    public void updateItem(LoanClass loanClass){
        if(dbAdapter.updateLoan(loanClass)<0){
            Toast.makeText(this.mContext, loanClass.getLenderName() + " cannot be updated into database.", Toast.LENGTH_SHORT).show();
        }else {
            this.mLoanList = dbAdapter.getAllLoansBetweenDates(startOfCurrentMonth, endofCurrentMonth);
        }
        notifyDataSetChanged();
    }

    public void deleteSingleExpense(LoanClass loanClass){
        if(dbAdapter.deleteSpecificLoan(loanClass.getId())<0){
            Toast.makeText(this.mContext, loanClass.getLenderName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
        }
        else{
            this.mLoanList.remove(loanClass);
        }
        notifyDataSetChanged();
    }
}
