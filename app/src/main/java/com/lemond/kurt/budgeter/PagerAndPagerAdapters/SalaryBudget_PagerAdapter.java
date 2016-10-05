package com.lemond.kurt.budgeter.PagerAndPagerAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Fragments.SalaryBudget;
import com.lemond.kurt.budgeter.Fragments.SalaryBudget_Daily;
import com.lemond.kurt.budgeter.ObjectClasses.BudgetPlanClass;

/**
 * Created by kurt_capatan on 2/2/2016.
 */
public class SalaryBudget_PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentManager mFragmentManager;
    ViewPager mViewPager;
    DatabaseAdapter mDbAdapter;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public SalaryBudget_PagerAdapter(Context context, FragmentManager fm, ViewPager mViewPager, int NumOfTabs) {
        super(fm);
        this.mFragmentManager = fm;
        this.mViewPager = mViewPager;
        this.mNumOfTabs = NumOfTabs;
        mDbAdapter = new DatabaseAdapter(context);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        if(position==0){
            SalaryBudget_Daily tab1 = new SalaryBudget_Daily();
            return tab1;
        }else if(position==(mNumOfTabs-1)){
            SalaryBudget tab2 = new SalaryBudget();
            return tab2;
        }else{
            return new SalaryBudget_Daily();
        }
    }

}
