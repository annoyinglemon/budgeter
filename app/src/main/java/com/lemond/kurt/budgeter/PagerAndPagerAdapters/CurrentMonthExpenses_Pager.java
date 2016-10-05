package com.lemond.kurt.budgeter.PagerAndPagerAdapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses;
import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses_Daily;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kurt_capatan on 2/2/2016.
 */
public class CurrentMonthExpenses_Pager extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager viewPager;
    private CurrentMonthExpenses_PagerAdapter mAdapter;
    private SearchView mSearchView;
    private ListView lvActualExpenseSearch;
    private ProgressBar pbSearchingActualExpense;
    private TextView tvSearchNoMatched;
    private DrawerLayout dlCurrentMonth;
    private boolean shouldUpdate = true;


    private static boolean isContextActivated = false;

    private OnFragmentInteractionListener mListener;

    public CurrentMonthExpenses_Pager() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrentMonthExpenses.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentMonthExpenses_Pager newInstance(String param1, String param2) {
        CurrentMonthExpenses_Pager fragment = new CurrentMonthExpenses_Pager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_current_month_expenses_tablayout, container, false);
        setHasOptionsMenu(true);
        dlCurrentMonth = (DrawerLayout) rootView.findViewById(R.id.dlCurrentMonth);
        dlCurrentMonth.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        dlCurrentMonth.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                lvActualExpenseSearch.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                dlCurrentMonth.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                pbSearchingActualExpense.setVisibility(View.GONE);
                lvActualExpenseSearch.setVisibility(View.GONE);
                tvSearchNoMatched.setText("Type your keyword above.");
                tvSearchNoMatched.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        TabLayout expensesTab = (TabLayout) rootView.findViewById(R.id.currentMonthExpensesTabLayout);
        expensesTab.addTab(expensesTab.newTab());
        expensesTab.addTab(expensesTab.newTab());
        expensesTab.getTabAt(0).setCustomView(R.layout.tab_this_day);
        expensesTab.getTabAt(1).setCustomView(R.layout.tab_this_month);
        viewPager = (ViewPager) rootView.findViewById(R.id.currentMonthExpensesPager);
        mAdapter = new CurrentMonthExpenses_PagerAdapter(getActivity().getSupportFragmentManager(), viewPager, expensesTab.getTabCount());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(expensesTab));
        LinearLayout tabStrip = ((LinearLayout) expensesTab.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isContextActivated) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        expensesTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        if (shouldUpdate) {
                            CurrentMonthExpenses_Daily daily = (CurrentMonthExpenses_Daily) mAdapter.getRegisteredFragment(0);
                            if (daily != null)
                                daily.updateData_noSavingsUpdate();
                        }
                        break;
                    case 1:
                        if (shouldUpdate) {
                            CurrentMonthExpenses monthly = (CurrentMonthExpenses) mAdapter.getRegisteredFragment(1);
                            if (monthly != null)
                                monthly.updateData_noSavingsUpdate();
                        }
                        break;
                }
                shouldUpdate = true;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        lvActualExpenseSearch = (ListView) rootView.findViewById(R.id.lvActualExpenseSearch);
        pbSearchingActualExpense = (ProgressBar) rootView.findViewById(R.id.pbSearchingActualExpense);
        tvSearchNoMatched = (TextView) rootView.findViewById(R.id.tvSearchNoMatched);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        shouldUpdate = false;
        dlCurrentMonth.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.menu_actual_expenses, menu);
            final MenuItem item = menu.findItem(R.id.menuCurrentDate);
            final TextView tvMenuCurrentYear = (TextView) item.getActionView().findViewById(R.id.tvMenuCurrentYear);
            final TextView tvMenuCurrentMonth = (TextView) item.getActionView().findViewById(R.id.tvMenuCurrentMonth);
            final TextView tvMenuCurrentDay = (TextView) item.getActionView().findViewById(R.id.tvMenuCurrentDay);
            final SimpleDateFormat readable_format = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
            final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateToday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(new SettingsManager(getContext()).getCurrentDate());
            tvMenuCurrentYear.setText(readable_format.format(dateToday).substring(7));
            tvMenuCurrentMonth.setText(readable_format.format(dateToday).substring(0, 3).toUpperCase());
            tvMenuCurrentDay.setText(readable_format.format(dateToday).substring(4, 6));
            LinearLayout llMenuCurrentDate = (LinearLayout) item.getActionView();
            llMenuCurrentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        final Calendar myCalendar = Calendar.getInstance();
                        myCalendar.setTime(df.parse(new SettingsManager(getContext()).getCurrentDate()));
                        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                new SettingsManager(getContext()).setCurrentDate(dbFormat.format(myCalendar.getTime()));
                                ((CurrentMonthExpenses_Daily) mAdapter.getRegisteredFragment(0)).updateData_noSavingsUpdate();
                                ((CurrentMonthExpenses) mAdapter.getRegisteredFragment(1)).updateData_noSavingsUpdate();
                                tvMenuCurrentYear.setText(readable_format.format(myCalendar.getTime()).substring(7));
                                tvMenuCurrentMonth.setText(readable_format.format(myCalendar.getTime()).substring(0, 3).toUpperCase());
                                tvMenuCurrentDay.setText(readable_format.format(myCalendar.getTime()).substring(4, 6));
                            }
                        };
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Today", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SettingsManager(getContext()).setCurrentDate(dbFormat.format(Calendar.getInstance().getTime()));
                                ((CurrentMonthExpenses_Daily) mAdapter.getRegisteredFragment(0)).updateData_noSavingsUpdate();
                                ((CurrentMonthExpenses) mAdapter.getRegisteredFragment(1)).updateData_noSavingsUpdate();
                                tvMenuCurrentYear.setText(readable_format.format(Calendar.getInstance().getTime()).substring(7));
                                tvMenuCurrentMonth.setText(readable_format.format(Calendar.getInstance().getTime()).substring(0, 3).toUpperCase());
                                tvMenuCurrentDay.setText(readable_format.format(Calendar.getInstance().getTime()).substring(4, 6));
                            }
                        });
                        datePickerDialog.show();
                    } catch (ParseException e) {
                    }
                }
            });
            MenuItem searchMenu = menu.findItem(R.id.menuSearch);
            final ExecuteSearch[] executeSearch = new ExecuteSearch[1];
            mSearchView = (SearchView) searchMenu.getActionView();
            // Assumes current activity is the searchable activity
            mSearchView.setIconifiedByDefault(true); //  iconify the widget; expand it when clicked
            mSearchView.setQueryHint("Search Expense");
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.isEmpty() || !newText.equalsIgnoreCase("")) {
                        executeSearch[0] = new ExecuteSearch(newText);
                        executeSearch[0].execute(newText);
                    } else {
                        pbSearchingActualExpense.setVisibility(View.GONE);
                        tvSearchNoMatched.setText("Cannot search with an empty keyword.");
                        tvSearchNoMatched.setVisibility(View.VISIBLE);
                        lvActualExpenseSearch.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
            MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (executeSearch[0] != null && executeSearch[0].getStatus().equals(AsyncTask.Status.RUNNING))
                        executeSearch[0].cancel(true);
                    dlCurrentMonth.closeDrawer(GravityCompat.END);
                    new G_Functions().unlockOrientation(getActivity());
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    dlCurrentMonth.openDrawer(GravityCompat.END);
                    new G_Functions().lockOrientation(getActivity());
                    return true;
                }
            });
            mSearchView.setMaxWidth(Integer.MAX_VALUE);
        } catch (ParseException e) {
        }
    }

    public SearchView getSearchView() {
        return mSearchView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSort:
                final String[] chosenSort = {""};
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.sort_dialog, null);

                final LinearLayout llNameAsc = (LinearLayout) dialoglayout.findViewById(R.id.llNameAsc);
                final LinearLayout llNameDesc = (LinearLayout) dialoglayout.findViewById(R.id.llNameDesc);
                final LinearLayout llPriceAsc = (LinearLayout) dialoglayout.findViewById(R.id.llPriceAsc);
                final LinearLayout llPriceDesc = (LinearLayout) dialoglayout.findViewById(R.id.llPriceDesc);
                final LinearLayout llDateAsc = (LinearLayout) dialoglayout.findViewById(R.id.llDateAsc);
                final LinearLayout llDateDesc = (LinearLayout) dialoglayout.findViewById(R.id.llDateDesc);

                switch (new SettingsManager(getContext()).getSortOption()) {
                    case SettingsManager.NAME_ASC:
                        llNameAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.NAME_ASC;
                        break;
                    case SettingsManager.NAME_DESC:
                        llNameDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.NAME_DESC;
                        break;
                    case SettingsManager.PRICE_ASC:
                        llPriceAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.PRICE_ASC;
                        break;
                    case SettingsManager.PRICE_DESC:
                        llPriceDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.PRICE_DESC;
                        break;
                    case SettingsManager.DATE_ASC:
                        llDateAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.DATE_ASC;
                        break;
                    case SettingsManager.DATE_DESC:
                        llDateDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.DATE_DESC;
                        break;
                }
                llNameAsc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.NAME_ASC;
                    }
                });
                llNameDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.NAME_DESC;
                    }
                });
                llPriceAsc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.PRICE_ASC;
                    }
                });
                llPriceDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.PRICE_DESC;
                    }
                });
                llDateAsc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.DATE_ASC;
                    }
                });
                llDateDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNameAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llNameDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llPriceDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateAsc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_white_1000));
                        llDateDesc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selected_sort));
                        chosenSort[0] = SettingsManager.DATE_DESC;
                    }
                });
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(getActivity());
                mainbuilder.setView(dialoglayout);
                mainbuilder.setTitle("Sort");
                mainbuilder.setCancelable(true);
                mainbuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SettingsManager(getContext()).setSortOption(chosenSort[0]);
                        ((CurrentMonthExpenses_Daily) mAdapter.getRegisteredFragment(0)).updateData_noSavingsUpdate();
                        ((CurrentMonthExpenses) mAdapter.getRegisteredFragment(1)).updateData_noSavingsUpdate();
                    }
                });
                mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog mainAlert = mainbuilder.create();
                mainAlert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setContextActivated(boolean arg) {
        isContextActivated = arg;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }


    /***********************************
     * FOR SEARCH
     ****************************************/

    public class ExecuteSearch extends AsyncTask<String, Void, ArrayList<ActualExpensesClass>> {
        private DatabaseAdapter dbAdapter;
        private DateUtilities mDateUtil;

        private String mQuery;

        public ExecuteSearch(String query) {
            this.mQuery = query;
        }

        @Override
        protected void onPreExecute() {
            dbAdapter = new DatabaseAdapter(getContext());
            mDateUtil = new DateUtilities(new SettingsManager(getContext()).getCurrentDate());
            pbSearchingActualExpense.setVisibility(View.VISIBLE);
            lvActualExpenseSearch.setVisibility(View.GONE);
            tvSearchNoMatched.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ActualExpensesClass> doInBackground(String... params) {
            return dbAdapter.searchActualExpensesBetweenDates(mDateUtil.getMonthDateBeginning(), mDateUtil.getMonthDateEnd(), params[0]);
        }

        @Override
        protected void onCancelled() {
            pbSearchingActualExpense.setVisibility(View.GONE);
            lvActualExpenseSearch.setVisibility(View.GONE);
            tvSearchNoMatched.setVisibility(View.GONE);
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(ArrayList<ActualExpensesClass> actualExpensesClasses) {
            pbSearchingActualExpense.setVisibility(View.GONE);
            if (actualExpensesClasses.size() > 0) {
                SearchAdapter searchAdapter = new SearchAdapter(actualExpensesClasses, mQuery);
                lvActualExpenseSearch.setAdapter(searchAdapter);
                lvActualExpenseSearch.setVisibility(View.VISIBLE);
                tvSearchNoMatched.setVisibility(View.GONE);
            } else {
                lvActualExpenseSearch.setVisibility(View.GONE);
                tvSearchNoMatched.setText("No items matched your keyword.");
                tvSearchNoMatched.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(actualExpensesClasses);
        }
    }

    class SearchAdapter extends BaseAdapter {
        CurrentMonthExpenses_Daily currentMonthDaily_Fragment = (CurrentMonthExpenses_Daily) mAdapter.getRegisteredFragment(0);
        CurrentMonthExpenses currentMonth_Fragment = (CurrentMonthExpenses) mAdapter.getRegisteredFragment(1);
        SettingsManager mSettings = new SettingsManager(getContext());

        ArrayList<ActualExpensesClass> mSearchList;

        String mSearchQuery;

        SearchAdapter(ArrayList<ActualExpensesClass> searchResultList, String searhQuery) {
            this.mSearchList = searchResultList;
            this.mSearchQuery = searhQuery;
            lvActualExpenseSearch.setOnItemClickListener(new CustomOnItemClickListener());
        }

        @Override
        public int getCount() {
            return mSearchList.size();
        }

        @Override
        public ActualExpensesClass getItem(int position) {
            return mSearchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSearchList.get(position).getActualExpenseId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            G_ViewHolders.CurrentMonthExpensesSearch holder = new G_ViewHolders.CurrentMonthExpensesSearch();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.actual_expense_search_item_layout, parent, false);
            holder.expenseDuration = (TextView) convertView.findViewById(R.id.tvExpenseDuration);
            holder.circleBG = (RelativeLayout) convertView.findViewById(R.id.llCircleBG);
            holder.expenseInitial = (TextView) convertView.findViewById(R.id.tvFirstLetter);
            holder.expenseName = (TextView) convertView.findViewById(R.id.actualExpense_ExpenseName);
            holder.expensePrice = (TextView) convertView.findViewById(R.id.actualExpense_ExpensePrice);
            holder.expenseQuantity = (TextView) convertView.findViewById(R.id.actualExpense_ExpenseQuantity);
            holder.expenseDate = (TextView) convertView.findViewById(R.id.actualExpense_ExpenseDate);
//            holder.actualExpense_edit = (ImageView) convertView.findViewById(R.id.actualExpense_edit);
            holder.actualExpense_delete = (ImageView) convertView.findViewById(R.id.actualExpense_delete);
            if (mSearchList.get(position).getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.DAILY)) {
                holder.expenseDuration.setText("daily");
                holder.expenseDuration.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.caldroid_items_bg));
            } else {
                holder.expenseDuration.setText("monthly");
                holder.expenseDuration.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.caldroid_total_bg));
            }
            GradientDrawable bgShape = (GradientDrawable) holder.circleBG.getBackground();
//            bgShape.setColor(G_Functions.RandomColor(getContext()));
            bgShape.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            String firstLetter = mSearchList.get(position).getActualExpenseName().substring(0, 1);
            if (mSearchList.get(position).getActualExpenseQuantity() > 99) {
                int px = G_Functions.dpToPx_xdpi(getContext(), 2);
                holder.expenseQuantity.setPadding(px, 0, px, 0);
            } else if (mSearchList.get(position).getActualExpenseQuantity() > 9) {
                int px = G_Functions.dpToPx_xdpi(getContext(), 3);
                holder.expenseQuantity.setPadding(px, 0, px, 0);
            } else {
                int px = G_Functions.dpToPx_xdpi(getContext(), 5);
                holder.expenseQuantity.setPadding(px, 0, px, 0);
            }
            holder.expenseQuantity.setText(Integer.toString(mSearchList.get(position).getActualExpenseQuantity()));
            holder.expenseInitial.setText(firstLetter.toUpperCase());
            holder.expenseName.setText(mSearchList.get(position).getActualExpenseName());
            holder.expensePrice.setText(mSettings.getCurrency() + " " + G_Functions.formatNumber(mSearchList.get(position).getActualExpensePrice()));
            holder.expenseDate.setText(DateUtilities.makeReadableFormat(mSearchList.get(position).getAcutalExpenseDate()));
            holder.actualExpense_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mainbuilder = new AlertDialog.Builder(getContext());
                    mainbuilder.setMessage("Are you sure you want to delete " + mSearchList.get(position).getActualExpenseName() + "?");
                    mainbuilder.setTitle("Delete Item");
                    mainbuilder.setCancelable(true);
                    mainbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            if (mSearchList.get(position).getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.MONTHLY)) {
                                currentMonth_Fragment.getAdapter().deleteSingleExpense(mSearchList.get(position));
                                if(mSearchList.get(position).getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.DAILY)) currentMonth_Fragment.addToSavingsUpdateDates(mSearchList.get(position).getAcutalExpenseDate());
                                if (currentMonth_Fragment != null) {
                                    currentMonth_Fragment.updateData();
                                    new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                }
                            } else {
                                currentMonthDaily_Fragment.getAdapter().deleteSingleExpense(mSearchList.get(position));
                                if (currentMonthDaily_Fragment != null) {
                                    currentMonthDaily_Fragment.updateData();
                                    new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                }
                            }
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
            return convertView;
        }


        double newItemPrice = 0.00;

        class CustomOnItemClickListener implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final double priceBeforeEdit = mSearchList.get(position).getActualExpensePrice();
                View dialoglayout = LayoutInflater.from(getContext()).inflate(R.layout.add_actual_expense_dialog, null);
                final EditText etExpenseName = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemName);
                final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
                final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
                final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
                final CheckBox cbActualExpense_saveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
                etExpenseName.setText((mSearchList.get(position).getActualExpenseName()));
                etExpensePrice.setText(G_Functions.formatNumber((mSearchList.get(position).getActualExpensePrice())));
                etExpenseQuantity.setText(Integer.toString((mSearchList.get(position).getActualExpenseQuantity())));
                etExpenseDate.setText((mSearchList.get(position).getAcutalExpenseDate()));
                etExpenseName.setSelection(etExpenseName.getText().length());
                etExpensePrice.setSelection(etExpensePrice.getText().length());
                etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
                newItemPrice = Double.parseDouble(etExpensePrice.getText().toString());
                etExpensePrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty())
                            newItemPrice = Double.parseDouble(etExpensePrice.getText().toString());
                    }
                });
                etExpenseQuantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty()) {
                            double price = newItemPrice * Double.parseDouble(etExpenseQuantity.getText().toString());
                            etExpensePrice.setText(G_Functions.formatNumber(price));
                            newItemPrice = Double.parseDouble(etExpensePrice.getText().toString().trim()) / Double.parseDouble(etExpenseQuantity.getText().toString().trim());
                        }
                    }
                });
                etExpenseQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            etExpenseDate.performClick();
                            return true;
                        }
                        return false;
                    }
                });
                etExpenseDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            final Calendar myCalendar = Calendar.getInstance();
                            myCalendar.setTime(df.parse(mSettings.getCurrentDate()));
                            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    etExpenseDate.setText(sdf.format(myCalendar.getTime()));
                                }
                            };
                            new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR),
                                    myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        } catch (ParseException e) {
                        }
                    }
                });
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(getContext());
                mainbuilder.setView(dialoglayout);
                mainbuilder.setTitle("Update Expense");
                mainbuilder.setCancelable(true);
                mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mSearchList.get(position).getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.MONTHLY)) {
                            ActualExpensesClass actualExpenses = new ActualExpensesClass(mSearchList.get(position).getActualExpenseId(), etExpenseName.getText().toString(),
                                    Double.parseDouble(etExpensePrice.getText().toString()), Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), mSearchList.get(position).getActualExpenseDuration());
                            if (currentMonth_Fragment != null && currentMonth_Fragment.isVisible()) {
                                if (priceBeforeEdit < Double.parseDouble(etExpensePrice.getText().toString())) {
                                    String day = mSettings.getCurrentDate().substring(5, 7);
                                    String day2 = etExpenseDate.getText().toString().substring(5, 7);
                                    if (day.equals(day2)) {
                                        currentMonth_Fragment.setTotalSavingsAndExpenses_update(((currentMonth_Fragment.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etExpensePrice.getText().toString())), actualExpenses, cbActualExpense_saveItem.isChecked());
                                        new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                    } else {
                                        currentMonth_Fragment.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                                        if (currentMonth_Fragment != null)
                                            currentMonth_Fragment.updateData();
                                        new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                    }
                                } else {
                                    currentMonth_Fragment.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                                    if (currentMonth_Fragment != null)
                                        currentMonth_Fragment.updateData();
                                    new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                }
                            }
                        } else {
                            ActualExpensesClass actualExpenses = new ActualExpensesClass(mSearchList.get(position).getActualExpenseId(), etExpenseName.getText().toString(),
                                    Double.parseDouble(etExpensePrice.getText().toString()), Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), mSearchList.get(position).getActualExpenseDuration());
                            if (currentMonthDaily_Fragment != null && currentMonthDaily_Fragment.isVisible()) {
                                if (priceBeforeEdit < Double.parseDouble(etExpensePrice.getText().toString())) {
                                    if (mSettings.getCurrentDate().equals(etExpenseDate.getText().toString().trim())) {
                                        currentMonthDaily_Fragment.setTotalSavingsAndExpenses_update(((currentMonthDaily_Fragment.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etExpensePrice.getText().toString())), actualExpenses, cbActualExpense_saveItem.isChecked());
                                        new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                    } else {
                                        currentMonthDaily_Fragment.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                                        if (currentMonthDaily_Fragment != null)
                                            currentMonthDaily_Fragment.updateData();
                                        new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                    }
                                } else {
                                    currentMonthDaily_Fragment.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                                    if (currentMonthDaily_Fragment != null)
                                        currentMonthDaily_Fragment.updateData();
                                    new ExecuteSearch(mSearchQuery).execute(mSearchQuery);
                                }
                            }
                        }
                    }
                });

                mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                final AlertDialog mainAlert = mainbuilder.create();
                etExpenseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                });
                mainAlert.show();
            }
        }
    }
}
