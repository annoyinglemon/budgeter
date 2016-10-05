package com.lemond.kurt.budgeter.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import hirondelle.date4j.DateTime;

/**
 * Created by kurt_capatan on 1/27/2016.
 */
public class CaldroidCustomAdapter extends CaldroidGridAdapter {

    ArrayList<String> mExpenseDates;
    HashMap<String, Integer> allItemsPerDay;
    HashMap<String, Double> allTotalsPerDay;
    //HashMap<String, ArrayList<ActualExpensesClass>> allActualExpensesPerDay;
    Context mCOntext;


    public CaldroidCustomAdapter(Context context, int month, int year, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
        super(context,month,year,caldroidData,extraData);
        mCOntext = context;
        mExpenseDates = (ArrayList<String>) extraData.get("EXPENSES_DATES");
        allItemsPerDay = (HashMap<String, Integer>) extraData.get("ITEMS_PER_DAY");
        allTotalsPerDay = (HashMap<String, Double>) extraData.get("TOTALS_PER_DAY");
        //allActualExpensesPerDay = (HashMap<String, ArrayList<ActualExpensesClass>>) extraData.get("ACTUAL_EXPENSES_PER_DAY");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        G_ViewHolders.Caldroid holder = new G_ViewHolders.Caldroid();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;
        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.caldroid_custom_cell, null);
        }
        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();
        holder. tv1 = (TextView) cellView.findViewById(R.id.tv1);
        holder. linearLayout = (LinearLayout)cellView.findViewById(R.id.custom_cell);
        holder. mTotal = (TextView)cellView.findViewById(R.id.tvCaldroidExpenseTotal);
        holder. mItems = (TextView)cellView.findViewById(R.id.tvCaldroidItemCount);

        holder.tv1.setTextColor(ContextCompat.getColor(context, R.color.md_grey_800));
        holder. mTotal.setVisibility(View.INVISIBLE);
        holder. mItems.setVisibility(View.INVISIBLE);

        // TODO: Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();


        boolean shouldResetDisabledView = false;
        boolean shouldResetSelectedView = false;

        // TODO: Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime)) || (maxDateTime != null && dateTime.gt(maxDateTime)) || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            holder.tv1.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }

        } else {
            shouldResetDisabledView = true;
        }

//        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources.getColor(com.caldroid.R.color.caldroid_sky_blue));
            holder.tv1.setTextColor(Color.BLACK);
        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDisabledView && shouldResetSelectedView) {
            //check if day is not within current month
            if (dateTime.getMonth() != month) {
                holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_300));
                holder.tv1.setTextColor(ContextCompat.getColor(context, R.color.md_grey_600));
                if(mExpenseDates.contains(dateTime.format("YYYY-MM-DD"))){
                    //get number of items and total per day
                    holder. mTotal.setVisibility(View.VISIBLE);
                    holder. mItems.setVisibility(View.VISIBLE);
                    int numberOfItems = allItemsPerDay.get(dateTime.format("YYYY-MM-DD"));
                    double totalPerDay = allTotalsPerDay.get(dateTime.format("YYYY-MM-DD"));
                    //do cell design
                    if(numberOfItems==1)
                        holder.mItems.setText(""+numberOfItems+ " item");
                    else
                        holder.mItems.setText(""+numberOfItems+ " items");
                    holder.mTotal.setText("" + totalPerDay);
                }
            }
            // date is within current month
            else
            {
                holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.md_white_1000));
                holder.tv1.setTextColor(ContextCompat.getColor(context, R.color.md_teal_900));
                if (dateTime.equals(getToday())) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
                }
                if(mExpenseDates.contains(dateTime.format("YYYY-MM-DD"))){
                    //get number of items and total per day
                    holder. mTotal.setVisibility(View.VISIBLE);
                    holder. mItems.setVisibility(View.VISIBLE);
                    int numberOfItems = allItemsPerDay.get(dateTime.format("YYYY-MM-DD"));
                    double totalPerDay = allTotalsPerDay.get(dateTime.format("YYYY-MM-DD"));
                    //do cell design
                    if(numberOfItems==1)
                        holder.mItems.setText(""+numberOfItems+ " item");
                    else
                        holder.mItems.setText(""+numberOfItems+ " items");
                    holder.mTotal.setText("" + totalPerDay);
                }
            }
        }

        holder.tv1.setText("" + dateTime.getDay());

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, holder.tv1);

        return cellView;
    }
}
