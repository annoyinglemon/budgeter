package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by lemond on 1/30/16.
 */
public class Adapter_salaryHistory extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<SalaryClass> mSalaries;
    private Context mContext;

    public Adapter_salaryHistory(Context mContext, ArrayList<SalaryClass> salaries) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        this.mSalaries = salaries;
    }

    @Override
    public int getCount() {
        return mSalaries.size();
    }

    @Override
    public SalaryClass getItem(int position) {
        return mSalaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSalaries.get(position).getSalaryId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        G_ViewHolders.SalaryHistory holder = new G_ViewHolders.SalaryHistory();
        convertView = inflater.inflate(R.layout.edit_salary_history_item, parent, false);
        holder.salaryAmount = (TextView) convertView.findViewById(R.id.tvSalaryHistoryAmount);
        holder.salaryDateChanged = (TextView) convertView.findViewById(R.id.tvSalaryHistoryDateChanged);
        holder.salaryAmount.setText(new SettingsManager(mContext).getCurrency() + " " + G_Functions.formatNumber(mSalaries.get(position).getSalaryAmount()));
        holder.salaryDateChanged.setText(DateUtilities.makeReadableFormat(mSalaries.get(position).getSalaryDateChanged()));
        convertView.setTag(holder);
        return convertView;
    }
}
