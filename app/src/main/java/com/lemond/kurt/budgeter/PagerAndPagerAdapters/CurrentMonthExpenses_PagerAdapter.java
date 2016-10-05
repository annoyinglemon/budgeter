package com.lemond.kurt.budgeter.PagerAndPagerAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses;
import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses_Daily;

/**
 * Created by kurt_capatan on 2/2/2016.
 */
public class CurrentMonthExpenses_PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    FragmentManager mFragmentManager;
    ViewPager mViewPager;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public CurrentMonthExpenses_PagerAdapter(FragmentManager fm,  ViewPager mViewPager, int NumOfTabs) {
        super(fm);
        this.mFragmentManager = fm;
        this.mViewPager = mViewPager;
        this.mNumOfTabs = NumOfTabs;
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

        switch (position) {
            case 0:
                CurrentMonthExpenses_Daily tab1 = new CurrentMonthExpenses_Daily();
                return tab1;
            case 1:
                CurrentMonthExpenses tab2 = new CurrentMonthExpenses();
                return tab2;
            default:
                return null;
        }
    }

}
