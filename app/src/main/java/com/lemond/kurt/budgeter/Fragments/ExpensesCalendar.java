package com.lemond.kurt.budgeter.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Adapters.Adapter_actualExpensesByDate;
import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpensesCalendar.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpensesCalendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpensesCalendar extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentManager mFragManager;
    private DrawerLayout mDrawer;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView tvEmpty, tvSelectedDate, tvMonthSavings, tvMonthExpenses, tvDaySavings, tvDayExpenses;
    private FloatingActionButton fabPickDate;
    private CladroidCustomFragment caldroidFragment;

    private Date mSelectedDate;
    private boolean newAsync = true;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public ExpensesCalendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpensesCalendar.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpensesCalendar newInstance(String param1, String param2) {
        ExpensesCalendar fragment = new ExpensesCalendar();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mFragManager = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expenses_calendar, container, false);
        new GetExpensesDates().execute();
        setHasOptionsMenu(true);
        mDrawer = (DrawerLayout) rootView.findViewById(R.id.dlExpensesCalendar);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            PrepareExpensesByDateList prepareExpensesByDateList;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (newAsync) {
                    prepareExpensesByDateList = new PrepareExpensesByDateList();
                    prepareExpensesByDateList.execute(DateUtilities.makeSQLiteFormat(mSelectedDate));
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (prepareExpensesByDateList != null)
                    prepareExpensesByDateList.cancel(true);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvExpensesByDate);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pbSearchExpenseByDate);
        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);
        tvSelectedDate = (TextView) rootView.findViewById(R.id.tvSelectedDate);
        tvMonthExpenses = (TextView) rootView.findViewById(R.id.tvMonthExpenses);
        tvMonthSavings = (TextView) rootView.findViewById(R.id.tvMonthSavings);
        tvDayExpenses = (TextView) rootView.findViewById(R.id.tvDayExpenses);
        tvDaySavings = (TextView) rootView.findViewById(R.id.tvDaySavings);
        fabPickDate = (FloatingActionButton) rootView.findViewById(R.id.fabPickDate);
        fabPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalendar = Calendar.getInstance();
                if (mSelectedDate != null) myCalendar.setTime(mSelectedDate);
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mSelectedDate = myCalendar.getTime();
                        tvSelectedDate.setText(DateUtilities.makeWholeDateFormat(myCalendar.getTime()));
                        new PrepareExpensesByDateList().execute(DateUtilities.makeSQLiteFormat(myCalendar.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Today", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDate = Calendar.getInstance().getTime();
                        tvSelectedDate.setText(DateUtilities.makeWholeDateFormat(Calendar.getInstance().getTime()));
                        new PrepareExpensesByDateList().execute(DateUtilities.makeSQLiteFormat(Calendar.getInstance().getTime()));
                    }
                });
                datePickerDialog.show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_expense_calendar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuChooseDate:
                final Calendar myCalendar = Calendar.getInstance();
                if (mSelectedDate != null) myCalendar.setTime(mSelectedDate);
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mSelectedDate = myCalendar.getTime();
                        caldroidFragment.moveToDate(mSelectedDate);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Today", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedDate = Calendar.getInstance().getTime();
                        caldroidFragment.moveToDate(mSelectedDate);
                    }
                });
                datePickerDialog.show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public DrawerLayout getDrawer() {
        return mDrawer;
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


    private class GetExpensesDates extends AsyncTask<Void, Void, ArrayList<String>> {

        ProgressDialog mDialog;
        private DatabaseAdapter dbAdapter;
        HashMap<String, Object> extraData;
        HashMap<String, Integer> allItemsPerDay;
        HashMap<String, Double> allTotalsPerDay;

        public GetExpensesDates() {
            dbAdapter = new DatabaseAdapter(getContext());
            mDialog = new ProgressDialog(getContext());
        }


        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Preparing calendar...");
            mDialog.setCancelable(false);
            mDialog.show();
            caldroidFragment = new CladroidCustomFragment();
            extraData = caldroidFragment.getExtraData();
            allItemsPerDay = new HashMap<String, Integer>();
            allTotalsPerDay = new HashMap<String, Double>();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> allDates = dbAdapter.getAllActualExpensesDates();
            if (allDates.size() > 0) {
                for (String date : allDates) {
                    ArrayList<ActualExpensesClass> expensesByDate = dbAdapter.getAllActualExpensesByDate(date);
                    int itemsPerDay = 0;
                    double totalPricePerDay = 0.00;
                    for (ActualExpensesClass xpensesPerDay : expensesByDate) {
                        itemsPerDay = itemsPerDay + xpensesPerDay.getActualExpenseQuantity();
                        totalPricePerDay = totalPricePerDay + xpensesPerDay.getActualExpensePrice();
                        allItemsPerDay.put(date, itemsPerDay);
                        allTotalsPerDay.put(date, totalPricePerDay);
                    }
                }
            }
            extraData.put("ITEMS_PER_DAY", allItemsPerDay);
            extraData.put("TOTALS_PER_DAY", allTotalsPerDay);
            return allDates;
        }

        @Override
        protected void onPostExecute(ArrayList<String> dates) {
            mDialog.dismiss();
            final CaldroidListener listener = new CaldroidListener() {

                @Override
                public void onSelectDate(Date date, View view) {
                    if (mSelectedDate != date)
                        newAsync = true;
                    else
                        newAsync = false;
                    mSelectedDate = date;
                    mDrawer.openDrawer(GravityCompat.END);
                    tvSelectedDate.setText(DateUtilities.makeWholeDateFormat(date));
                }

                @Override
                public void onChangeMonth(int month, int year) {
                }

                @Override
                public void onLongClickDate(Date date, View view) {
                }

                @Override
                public void onCaldroidViewCreated() {
                    mDialog.dismiss();
                }

            };
            caldroidFragment.setCaldroidListener(listener);
//            Bundle args = new Bundle();
//            args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
//            caldroidFragment.setArguments(args);
            caldroidFragment.refreshView();
            extraData.put("EXPENSES_DATES", dates);

            mFragManager.beginTransaction().replace(R.id.flCalendarContent, caldroidFragment, "CaldroidCustomFragment").commit();
        }
    }

    private class PrepareExpensesByDateList extends AsyncTask<String, Void, Adapter_actualExpensesByDate> {

        private DatabaseAdapter dbAdapter;
        private double monthSavings = 0, monthExpenses = 0, daySavings = 0, dayExpenses = 0;
        private boolean hasLoans = false;
        private SettingsManager mSettings;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            tvMonthSavings.setText("N/A");
            tvMonthExpenses.setText("N/A");
            tvDaySavings.setText("N/A");
            tvDayExpenses.setText("N/A");
            dbAdapter = new DatabaseAdapter(getContext());
            mSettings = new SettingsManager(getContext());
        }

        @Override
        protected Adapter_actualExpensesByDate doInBackground(String... dateString) {
            monthSavings = dbAdapter.getLoansTotalBetweenDates(DateUtilities.makeSQLiteFormat(mSelectedDate));
            if (monthSavings > 0) hasLoans = true;
//            double salary = 0.00;
//            if ((salary = dbAdapter.getCustomDateMonthlyBudget(dateString[0])) == 0) {
//                salary = dbAdapter.getBeforeCurrentMonthMonthlyBudget(dateString[0]);
//            }
//            monthSavings += salary;
//            if ((daySavings = dbAdapter.getCustomDateDailyBudget(dateString[0])) == 0) {
//                daySavings = dbAdapter.getBeforeCurrentMonthDailyBudget(dateString[0]);
//            }
            monthExpenses = dbAdapter.monthlyTotalExpensesAsOfThisDate(dateString[0]);
            dayExpenses = dbAdapter.dailyTotalExpensesOfThisDate(dateString[0]);
//            monthSavings -= monthExpenses;
//            daySavings -= dayExpenses;
            monthSavings = dbAdapter.getTotalExpensesByMonth(dateString[0].substring(0,7));
            daySavings = dbAdapter.getTotalSavingsByDate(dateString[0]);

            return new Adapter_actualExpensesByDate(getContext(), dbAdapter.getAllActualExpensesByDate(dateString[0]));
        }

        @Override
        protected void onPostExecute(Adapter_actualExpensesByDate adapter) {
            super.onPostExecute(adapter);
            tvMonthExpenses.setText(mSettings.getCurrency() + " " + G_Functions.formatNumber(monthExpenses));
            tvDayExpenses.setText(mSettings.getCurrency() + " " + G_Functions.formatNumber(dayExpenses));
            tvMonthSavings.setText(mSettings.getCurrency() + " " + G_Functions.formatNumber(monthSavings));
            if (hasLoans)
                tvMonthSavings.setTextColor(ContextCompat.getColor(getContext(), R.color.md_blue_300));
            else if (monthSavings < 0)
                tvMonthSavings.setTextColor(ContextCompat.getColor(getContext(), R.color.md_red_200));
            else
                tvMonthSavings.setTextColor(ContextCompat.getColor(getContext(), R.color.md_teal_50));
            tvDaySavings.setText(mSettings.getCurrency() + " " + G_Functions.formatNumber(daySavings));
            if (daySavings < 0)
                tvDaySavings.setTextColor(ContextCompat.getColor(getContext(), R.color.md_red_200));
            else
                tvDaySavings.setTextColor(ContextCompat.getColor(getContext(), R.color.md_teal_50));
            if (adapter.getItemCount() > 0) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        }

    }
}
