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

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Fragments.SalaryBudget;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.SalaryBudget_Pager;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.SalaryBudget_PagerAdapter;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by lemond on 2/15/16.
 */
public class Adapter_mockLoans extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<LoanClass> mLoanList;
    private Context mContext;
    private DatabaseAdapter dbAdapter;
    private SalaryBudget salaryBudget;
    private SettingsManager mSettingsManager;


    public Adapter_mockLoans(Context mContext, ArrayList<LoanClass> loans) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.mLoanList = loans;
        this.dbAdapter = new DatabaseAdapter(mContext);
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        SalaryBudget_Pager myFragment = (SalaryBudget_Pager) fragmentManager.findFragmentByTag("SalaryBudget");
        ViewPager vpPager = myFragment.getViewPager();
        int index = vpPager.getCurrentItem();
        SalaryBudget_PagerAdapter adapter = ((SalaryBudget_PagerAdapter)vpPager.getAdapter());
        this.salaryBudget = (SalaryBudget)adapter.getRegisteredFragment(index);
        this.mSettingsManager = new SettingsManager(mContext);
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
        convertView = inflater.inflate(R.layout.loans_item_layout, parent, false);
        holder.lenderName = (TextView) convertView.findViewById(R.id.loan_lenderName);
        holder. loanAmount = (TextView) convertView.findViewById(R.id.loan_loanAmount);
        holder. loanByMonth_delete = (ImageView) convertView.findViewById(R.id.loan_delete);
        holder.lenderName.setText(mLoanList.get(position).getLenderName());
        holder.loanAmount.setText(G_Functions.formatNumber(mLoanList.get(position).getLoanAmount()));
        holder.loanByMonth_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(mContext);
                mainbuilder.setMessage("Are you sure you want to delete " + mLoanList.get(position).getLenderName() + "?");
                mainbuilder.setTitle("Delete Lender");
                mainbuilder.setCancelable(true);
                mainbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSingleLoan(mLoanList.get(position));
                        salaryBudget.updateData();
                        if(mSettingsManager.isDistributeMockMonthlyBudget()) salaryBudget.getAdapter().updateDailyBudget();
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
        ArrayList<LoanClass> loans = dbAdapter.getAllMockLoans();
        if(loans.size()>0) {
            for (LoanClass loan : loans) {
                totalBudget = totalBudget + loan.getLoanAmount();
            }
        }
        SalaryClass salaryClass = dbAdapter.getLatestMockSalary();
        if(salaryClass!=null){
            totalBudget = totalBudget + salaryClass.getSalaryAmount();
        }
        return totalBudget;
    }

    public Double getTotalLoan(){
        double total = 0.00;
        for(int i = 0; i < mLoanList.size(); i++){
            total = total + mLoanList.get(i).getLoanAmount();
        }
        return total;
    }

    /*********************************************** DATABASE FUNCTIONS *************************************************************/

    public void updateLoan(LoanClass loanClass){
        if(dbAdapter.updateMockLoan(loanClass)<0){
            Toast.makeText(this.mContext, loanClass.getLenderName() + " cannot be updated into database.", Toast.LENGTH_SHORT).show();
        }else {
            this.mLoanList = dbAdapter.getAllMockLoans();
        }
        notifyDataSetChanged();
    }

    public void deleteSingleLoan(LoanClass loanClass){
        if(dbAdapter.deleteSpecificMockLoan(loanClass.getId())<0){
            Toast.makeText(this.mContext, loanClass.getLenderName() + " cannot be deleted in database.", Toast.LENGTH_SHORT).show();
        }
        else{
            this.mLoanList.remove(loanClass);
        }
        notifyDataSetChanged();
    }
}
