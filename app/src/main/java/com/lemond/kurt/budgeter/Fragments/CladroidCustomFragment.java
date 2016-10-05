package com.lemond.kurt.budgeter.Fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kurt_capatan on 1/27/2016.
 */
public class CladroidCustomFragment extends CaldroidFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setArguments(Bundle args) {
        Bundle arg = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        super.setArguments(arg);
    }

    @Override
    public void setEnableSwipe(boolean enableSwipe) {
        super.setEnableSwipe(true);
    }

    @Override
    public void setShowNavigationArrows(boolean showNavigationArrows) {
        super.setShowNavigationArrows(true);
    }

    @Override
    public void setDisableDates(ArrayList<Date> disableDateList) {
        super.setDisableDates(disableDateList);
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CaldroidCustomAdapter(getActivity(), month, year, getCaldroidData(), extraData);
    }

    @Override
    public void setMonthTitleTextView(TextView monthTitleTextView) {
        super.setMonthTitleTextView(monthTitleTextView);
    }




//    @Override
//    public void onStart() {
//        MainActivity.dismissDialog();
//        super.onStart();
//    }

}
