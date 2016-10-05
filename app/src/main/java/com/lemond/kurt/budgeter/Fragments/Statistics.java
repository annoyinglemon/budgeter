package com.lemond.kurt.budgeter.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.MonthYearPickerDialog;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Statistics.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Statistics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Statistics extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private BarChart barChartMonthly, barChartDaily;
    private LineChart lineChartMonthly, lineChartDaily;

    /**
     * All values of daily chart
     */
    private ArrayList<Float> DailyValues;

    /**
     * All values of monthly chart
     */
    private ArrayList<Float> MonthlyValues;

    private SettingsManager mSettings;

    private DateUtilities mDateUtil;

    public Statistics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Statistics.
     */
    // TODO: Rename and change types and number of parameters
    public static Statistics newInstance(String param1, String param2) {
        Statistics fragment = new Statistics();
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
        mSettings = new SettingsManager(getContext());
        mDateUtil = new DateUtilities(DateUtilities.makeSQLiteFormat(new Date()));

        setHasOptionsMenu(true);

        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        barChartMonthly = (BarChart) rootView.findViewById(R.id.barChartMonthly);
        barChartDaily = (BarChart) rootView.findViewById(R.id.barChartDaily);
        lineChartMonthly = (LineChart) rootView.findViewById(R.id.lineChartMonthly);
        lineChartDaily = (LineChart) rootView.findViewById(R.id.lineChartDaily);

        if (savedInstanceState != null) {
            DailyValues = new ArrayList<>();
            for (float f : savedInstanceState.getFloatArray("DailyValues")) {
                DailyValues.add(f);
            }
            MonthlyValues = new ArrayList<>();
            for (float f : savedInstanceState.getFloatArray("MonthlyValues")) {
                MonthlyValues.add(f);
            }
            if (mSettings.getSelectedChartType().equalsIgnoreCase("bar")) {
                setupBarChartDaily();
                setupBarChartMonthly();
            } else {
                setupLineChartDaily();
                setupLineChartMonthly();
            }

        } else {
            if (mSettings.getSelectedChartType().equalsIgnoreCase("bar"))
                new PrepareBarCharts().execute();
            else
                new PrepareLineCharts().execute();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Expenses");
        else
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Savings");
        super.onResume();
    }

    void setupBarChartDaily() {
        /*************DAILY CHART*****************/
        barChartDaily.setVisibility(View.VISIBLE);
        lineChartDaily.setVisibility(View.GONE);
        ArrayList<BarEntry> chartDailyValues = new ArrayList<>();
        ArrayList<String> chartDailyLabels = new ArrayList<String>();
        for (int i = 0; i < DailyValues.size(); i++) {
            chartDailyValues.add(new BarEntry(DailyValues.get(i), i));
        }
        int dayCounter = new DateUtilities(mDateUtil.getCurrentDate()).getCurrentMonthNumberOfDays();
        for (int i = 1; i <= dayCounter; i++) {
            String stringNumber = Integer.toString(i);
            if (i < 10) stringNumber = '0' + stringNumber;
            chartDailyLabels.add(stringNumber);
        }
        BarDataSet chartDailyDataset;
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            chartDailyDataset = new BarDataSet(chartDailyValues, "Amount of expenses");
        else
            chartDailyDataset = new BarDataSet(chartDailyValues, "Amount of savings");
        chartDailyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent), ContextCompat.getColor(getContext(), R.color.colorPrimary)});
//            chartDailyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_red_300));
//            chartDailyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            chartDailyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//            chartDailyDataset.setDrawFilled(true);
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mDateUtil.getCurrentDate());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                barChartDaily.setDescription("Total expenses on each day of " + simpleDateFormat.format(date));
            else
                barChartDaily.setDescription("Savings on each day of " + simpleDateFormat.format(date));
        } catch (ParseException e) {
            if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                barChartDaily.setDescription("Total expenses on each day");
            else
                barChartDaily.setDescription("Savings on each day");
        }
        barChartDaily.setData(new BarData(chartDailyLabels, chartDailyDataset));
        barChartDaily.animateXY(350, 700);
    }

    void setupBarChartMonthly() {
        /***********MONTHLY CHART******************/
        barChartMonthly.setVisibility(View.VISIBLE);
        lineChartMonthly.setVisibility(View.GONE);
        ArrayList<BarEntry> chartMonthlyValues = new ArrayList<>();
        ArrayList<String> chartMonthlyLabels = new ArrayList<String>();
        for (int i = 0; i < MonthlyValues.size(); i++) {
            chartMonthlyValues.add(new BarEntry(MonthlyValues.get(i), i));
        }
        for (int i = 1; i <= 12; i++) {
            String stringNumber = Integer.toString(i);
            if (i < 10) stringNumber = "0" + stringNumber;
            switch (stringNumber) {
                case "01":
                    chartMonthlyLabels.add("JAN");
                    break;
                case "02":
                    chartMonthlyLabels.add("FEB");
                    break;
                case "03":
                    chartMonthlyLabels.add("MAR");
                    break;
                case "04":
                    chartMonthlyLabels.add("APR");
                    break;
                case "05":
                    chartMonthlyLabels.add("MAY");
                    break;
                case "06":
                    chartMonthlyLabels.add("JUN");
                    break;
                case "07":
                    chartMonthlyLabels.add("JUL");
                    break;
                case "08":
                    chartMonthlyLabels.add("AUG");
                    break;
                case "09":
                    chartMonthlyLabels.add("SEP");
                    break;
                case "10":
                    chartMonthlyLabels.add("OCT");
                    break;
                case "11":
                    chartMonthlyLabels.add("NOV");
                    break;
                case "12":
                    chartMonthlyLabels.add("DEC");
                    break;
            }
        }
        BarDataSet chartMonthlyDataset;
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            chartMonthlyDataset = new BarDataSet(chartMonthlyValues, "Amount of expenses");
        else {
            chartMonthlyDataset = new BarDataSet(chartMonthlyValues, "Amount of savings");
        }
        chartMonthlyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent), ContextCompat.getColor(getContext(), R.color.colorPrimary)});
//            chartMonthlyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_teal_300));
//            chartMonthlyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//            chartMonthlyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorAccent));
//            chartMonthlyDataset.setDrawFilled(true);
        barChartMonthly.setData(new BarData(chartMonthlyLabels, chartMonthlyDataset));
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            barChartMonthly.setDescription("Total expenses on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
        else
            barChartMonthly.setDescription("Savings on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
        barChartMonthly.animateXY(350, 700);
    }

    void setupLineChartDaily() {
        /*************DAILY CHART*****************/
        barChartDaily.setVisibility(View.GONE);
        lineChartDaily.setVisibility(View.VISIBLE);
        ArrayList<Entry> chartDailyValues = new ArrayList<>();
        ArrayList<String> chartDailyLabels = new ArrayList<String>();
        for (int i = 0; i < DailyValues.size(); i++) {
            chartDailyValues.add(new Entry(DailyValues.get(i), i));
        }
        int dayCounter = new DateUtilities(mDateUtil.getCurrentDate()).getCurrentMonthNumberOfDays();
        for (int i = 1; i <= dayCounter; i++) {
            String stringNumber = Integer.toString(i);
            if (i < 10) stringNumber = '0' + stringNumber;
            chartDailyLabels.add(stringNumber);
        }
        LineDataSet chartDailyDataset;
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            chartDailyDataset = new LineDataSet(chartDailyValues, "Amount of expenses");
        else
            chartDailyDataset = new LineDataSet(chartDailyValues, "Amount of savings");
        chartDailyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent)});
        chartDailyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_red_300));
        chartDailyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        chartDailyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        chartDailyDataset.setDrawFilled(true);
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mDateUtil.getCurrentDate());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
            if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                lineChartDaily.setDescription("Total expenses on each day of " + simpleDateFormat.format(date));
            else
                lineChartDaily.setDescription("Savings on each day of " + simpleDateFormat.format(date));
        } catch (ParseException e) {
            if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                lineChartDaily.setDescription("Total expenses on each day");
            else
                lineChartDaily.setDescription("Savings on each day");
        }
        lineChartDaily.setData(new LineData(chartDailyLabels, chartDailyDataset));
        lineChartDaily.animateXY(350, 700);
    }

    void setupLineChartMonthly() {
        /***********MONTHLY CHART******************/
        barChartMonthly.setVisibility(View.GONE);
        lineChartMonthly.setVisibility(View.VISIBLE);
        ArrayList<Entry> chartMonthlyValues = new ArrayList<>();
        ArrayList<String> chartMonthlyLabels = new ArrayList<String>();
        for (int i = 0; i < MonthlyValues.size(); i++) {
            chartMonthlyValues.add(new Entry(MonthlyValues.get(i), i));
        }
        for (int i = 1; i <= 12; i++) {
            String stringNumber = Integer.toString(i);
            if (i < 10) stringNumber = "0" + stringNumber;
            switch (stringNumber) {
                case "01":
                    chartMonthlyLabels.add("JAN");
                    break;
                case "02":
                    chartMonthlyLabels.add("FEB");
                    break;
                case "03":
                    chartMonthlyLabels.add("MAR");
                    break;
                case "04":
                    chartMonthlyLabels.add("APR");
                    break;
                case "05":
                    chartMonthlyLabels.add("MAY");
                    break;
                case "06":
                    chartMonthlyLabels.add("JUN");
                    break;
                case "07":
                    chartMonthlyLabels.add("JUL");
                    break;
                case "08":
                    chartMonthlyLabels.add("AUG");
                    break;
                case "09":
                    chartMonthlyLabels.add("SEP");
                    break;
                case "10":
                    chartMonthlyLabels.add("OCT");
                    break;
                case "11":
                    chartMonthlyLabels.add("NOV");
                    break;
                case "12":
                    chartMonthlyLabels.add("DEC");
                    break;
            }
        }
        LineDataSet chartMonthlyDataset;
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            chartMonthlyDataset = new LineDataSet(chartMonthlyValues, "Amount of expenses");
        else {
            chartMonthlyDataset = new LineDataSet(chartMonthlyValues, "Amount of savings");
        }
        chartMonthlyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorPrimary)});
        chartMonthlyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_teal_300));
        chartMonthlyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        chartMonthlyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorAccent));
        chartMonthlyDataset.setDrawFilled(true);
        lineChartMonthly.setData(new LineData(chartMonthlyLabels, chartMonthlyDataset));
        if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
            lineChartMonthly.setDescription("Total expenses on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
        else
            lineChartMonthly.setDescription("Savings on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
        lineChartMonthly.animateXY(350, 700);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_statistics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private String chosenDate;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuStatSettings:
                chosenDate = mDateUtil.getCurrentDate();
                View dialoglayout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_stat_settings, null);
                final RadioGroup rgData = (RadioGroup) dialoglayout.findViewById(R.id.rgData);
                final RadioGroup rgChartTypes = (RadioGroup) dialoglayout.findViewById(R.id.rgChartTypes);

                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses")) {
                    rgData.check(R.id.rbExpenses);
                } else {
                    rgData.check(R.id.rbSavings);
                }
                if (mSettings.getSelectedChartType().equalsIgnoreCase("bar")) {
                    rgChartTypes.check(R.id.rbBar);
                } else {
                    rgChartTypes.check(R.id.rbLine);
                }
                final Button bnPicker = (Button) dialoglayout.findViewById(R.id.bnPicker);
                bnPicker.setText(DateUtilities.makeWholeDateFormatNoDate(mDateUtil.getCurrentDate().substring(0, 7)));
                bnPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MonthYearPickerDialog monthYearPickerDialog = MonthYearPickerDialog.newInstance(mDateUtil.getCurrentDate());
                        monthYearPickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String monthString = monthOfYear + "";
                                if (monthOfYear < 10) monthString = "0" + monthOfYear;
                                bnPicker.setText(DateUtilities.makeWholeDateFormatNoDate(year + "-" + monthString));
                                chosenDate = year + "-" + monthString + "-" + "01";
                            }
                        });
                        monthYearPickerDialog.show(getActivity().getFragmentManager(), "MonthYearPickerDialog");
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialoglayout);
                builder.setTitle("Graph Settings");
                builder.setCancelable(true);
                builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDateUtil = new DateUtilities(chosenDate);
                        if (rgData.getCheckedRadioButtonId() == R.id.rbExpenses) {
                            mSettings.setSelectedStatisticsData("expenses");
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Expenses");
                        } else {
                            mSettings.setSelectedStatisticsData("savings");
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Savings");
                        }
                        if (rgChartTypes.getCheckedRadioButtonId() == R.id.rbBar) {
                            mSettings.setSelectedChartType("bar");
                            new PrepareBarCharts().execute();
                        } else {
                            mSettings.setSelectedChartType("line");
                            new PrepareLineCharts().execute();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        float[] dailyArray = new float[DailyValues.size()];
        int i = 0;
        for (Float f : DailyValues) {
            dailyArray[i++] = (f != null ? f : 0f); // Or whatever default you want.
        }
        outState.putFloatArray("DailyValues", dailyArray);
        float[] monthlyArray = new float[MonthlyValues.size()];
        i = 0;
        for (Float f : MonthlyValues) {
            monthlyArray[i++] = (f != null ? f : 0f); // Or whatever default you want.
        }
        outState.putFloatArray("MonthlyValues", monthlyArray);
        super.onSaveInstanceState(outState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStop() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
        super.onStop();
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


    /**
     * ASYNCTASKS
     */

    private class PrepareBarCharts extends AsyncTask<Void, Void, ArrayList<BarData>> {
        ProgressDialog mDialog;
        private DatabaseAdapter dbAdapter;
        private SettingsManager mSettings;
        private String ERROR = "";

        public PrepareBarCharts() {
            barChartDaily.setVisibility(View.VISIBLE);
            barChartMonthly.setVisibility(View.VISIBLE);
            lineChartDaily.setVisibility(View.GONE);
            lineChartMonthly.setVisibility(View.GONE);
            dbAdapter = new DatabaseAdapter(getContext());
            mDialog = new ProgressDialog(getContext());
            mSettings = new SettingsManager(getContext());
            DailyValues = new ArrayList<>();
            MonthlyValues = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Preparing Chart...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected ArrayList<BarData> doInBackground(Void... params) {
            ArrayList<BarData> lineDatas = new ArrayList<>();
            try {
                int currentYear = Integer.parseInt(mDateUtil.getCurrentDate().substring(0, 4));
                /**************DAILY CHART****************/
                ArrayList<BarEntry> chartDailyValues = new ArrayList<>();
                ArrayList<String> chartDailyLabels = new ArrayList<String>();
                int dayCounter = new DateUtilities(mDateUtil.getCurrentDate()).getCurrentMonthNumberOfDays();
                String currentYearMonth = mDateUtil.getCurrentDate().substring(0, 7);
                for (int i = 1; i <= dayCounter; i++) {
                    String stringNumber = Integer.toString(i);
                    if (i < 10) stringNumber = '0' + stringNumber;
                    chartDailyLabels.add(stringNumber);
                    double dailySum = 0.0;
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        dailySum = dbAdapter.getTotalExpensesByDate(currentYearMonth + "-" + stringNumber);
                    else
                        dailySum = dbAdapter.getTotalSavingsByDate(currentYearMonth + "-" + stringNumber);
                    DailyValues.add((float) dailySum);
                    chartDailyValues.add(new BarEntry((float) dailySum, i - 1));
                }
                BarDataSet chartDailyDataset;
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    chartDailyDataset = new BarDataSet(chartDailyValues, "Amount of expenses");
                else
                    chartDailyDataset = new BarDataSet(chartDailyValues, "Amount of savings");
                chartDailyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent), ContextCompat.getColor(getContext(), R.color.colorPrimary)});
//                chartDailyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_red_300));
//                chartDailyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                chartDailyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorPrimary));
//                chartDailyDataset.setDrawFilled(true);
                lineDatas.add(0, new BarData(chartDailyLabels, chartDailyDataset));

                /**************MONTHLY CHART****************/
                ArrayList<BarEntry> chartMonthlyValues = new ArrayList<>();
                ArrayList<String> chartMonthlyLabels = new ArrayList<String>();
                for (int i = 1; i <= 12; i++) {
                    String stringNumber = Integer.toString(i);
                    if (i < 10) stringNumber = "0" + stringNumber;
                    switch (stringNumber) {
                        case "01":
                            chartMonthlyLabels.add("JAN");
                            break;
                        case "02":
                            chartMonthlyLabels.add("FEB");
                            break;
                        case "03":
                            chartMonthlyLabels.add("MAR");
                            break;
                        case "04":
                            chartMonthlyLabels.add("APR");
                            break;
                        case "05":
                            chartMonthlyLabels.add("MAY");
                            break;
                        case "06":
                            chartMonthlyLabels.add("JUN");
                            break;
                        case "07":
                            chartMonthlyLabels.add("JUL");
                            break;
                        case "08":
                            chartMonthlyLabels.add("AUG");
                            break;
                        case "09":
                            chartMonthlyLabels.add("SEP");
                            break;
                        case "10":
                            chartMonthlyLabels.add("OCT");
                            break;
                        case "11":
                            chartMonthlyLabels.add("NOV");
                            break;
                        case "12":
                            chartMonthlyLabels.add("DEC");
                            break;
                    }
                    double monthlySum = 0.0;
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        monthlySum = dbAdapter.getTotalExpensesByMonth(currentYear + "-" + stringNumber);
                    else
                        monthlySum = dbAdapter.getTotalSavingsByMonth(currentYear + "-" + stringNumber);
                    MonthlyValues.add((float) monthlySum);
                    chartMonthlyValues.add(new BarEntry((float) monthlySum, i - 1));
                }
                BarDataSet chartMonthlyDataset;
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    chartMonthlyDataset = new BarDataSet(chartMonthlyValues, "Amount of expenses");
                else {
                    chartMonthlyDataset = new BarDataSet(chartMonthlyValues, "Amount of savings");
                }

                chartMonthlyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent)});
//                chartMonthlyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_teal_300));
//                chartMonthlyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//                chartMonthlyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorAccent));
//                chartMonthlyDataset.setDrawFilled(true);
                lineDatas.add(1, new BarData(chartMonthlyLabels, chartMonthlyDataset));
            } catch (Exception e) {
                ERROR = e.getMessage() + "\n" + e.getCause();
            }
            return lineDatas;
        }

        @Override
        protected void onPostExecute(ArrayList<BarData> lineDatas) {
            if (lineDatas.size() > 0) {
                /*********** DAILY CHART **************/
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mDateUtil.getCurrentDate());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        barChartDaily.setDescription("Total expenses on each day of " + simpleDateFormat.format(date));
                    else
                        barChartDaily.setDescription("Total savings on each day of " + simpleDateFormat.format(date));

                } catch (ParseException e) {
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        barChartDaily.setDescription("Total expenses on each day");
                    else
                        barChartDaily.setDescription("Total savings on each day");
                }
                barChartDaily.setData(lineDatas.get(0));
                barChartDaily.animateXY(350, 700);
                /********** MONTHLY CHART ************/
                barChartMonthly.setData(lineDatas.get(1));
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    barChartMonthly.setDescription("Total expenses on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
                else
                    barChartMonthly.setDescription("Total savings on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
                barChartMonthly.animateXY(350, 700);
                mDialog.dismiss();
            } else {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(ERROR);
                builder.show();
            }
            super.onPostExecute(lineDatas);
        }
    }

    private class PrepareLineCharts extends AsyncTask<Void, Void, ArrayList<LineData>> {
        ProgressDialog mDialog;
        private DatabaseAdapter dbAdapter;
        private SettingsManager mSettings;
        private String ERROR = "";

        public PrepareLineCharts() {
            barChartDaily.setVisibility(View.GONE);
            barChartMonthly.setVisibility(View.GONE);
            lineChartDaily.setVisibility(View.VISIBLE);
            lineChartMonthly.setVisibility(View.VISIBLE);
            dbAdapter = new DatabaseAdapter(getContext());
            mDialog = new ProgressDialog(getContext());
            mSettings = new SettingsManager(getContext());
            DailyValues = new ArrayList<>();
            MonthlyValues = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Preparing Chart...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected ArrayList<LineData> doInBackground(Void... params) {
            ArrayList<LineData> lineDatas = new ArrayList<>();
            try {
                int currentYear = Integer.parseInt(mDateUtil.getCurrentDate().substring(0, 4));
                /**************DAILY CHART****************/
                ArrayList<Entry> chartDailyValues = new ArrayList<>();
                ArrayList<String> chartDailyLabels = new ArrayList<String>();
                int dayCounter = new DateUtilities(mDateUtil.getCurrentDate()).getCurrentMonthNumberOfDays();
                String currentYearMonth = mDateUtil.getCurrentDate().substring(0, 7);
                for (int i = 1; i <= dayCounter; i++) {
                    String stringNumber = Integer.toString(i);
                    if (i < 10) stringNumber = '0' + stringNumber;
                    chartDailyLabels.add(stringNumber);
                    double dailySum = 0.0;
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        dailySum = dbAdapter.getTotalExpensesByDate(currentYearMonth + "-" + stringNumber);
                    else
                        dailySum = dbAdapter.getTotalSavingsByDate(currentYearMonth + "-" + stringNumber);
                    DailyValues.add((float) dailySum);
                    chartDailyValues.add(new Entry((float) dailySum, i - 1));
                }
                LineDataSet chartDailyDataset;
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    chartDailyDataset = new LineDataSet(chartDailyValues, "Amount of expenses");
                else
                    chartDailyDataset = new LineDataSet(chartDailyValues, "Amount of savings");
                chartDailyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorAccent)});
                chartDailyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_red_300));
                chartDailyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                chartDailyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                chartDailyDataset.setDrawFilled(true);
                lineDatas.add(0, new LineData(chartDailyLabels, chartDailyDataset));

                /**************MONTHLY CHART****************/
                ArrayList<Entry> chartMonthlyValues = new ArrayList<>();
                ArrayList<String> chartMonthlyLabels = new ArrayList<String>();
                for (int i = 1; i <= 12; i++) {
                    String stringNumber = Integer.toString(i);
                    if (i < 10) stringNumber = "0" + stringNumber;
                    switch (stringNumber) {
                        case "01":
                            chartMonthlyLabels.add("JAN");
                            break;
                        case "02":
                            chartMonthlyLabels.add("FEB");
                            break;
                        case "03":
                            chartMonthlyLabels.add("MAR");
                            break;
                        case "04":
                            chartMonthlyLabels.add("APR");
                            break;
                        case "05":
                            chartMonthlyLabels.add("MAY");
                            break;
                        case "06":
                            chartMonthlyLabels.add("JUN");
                            break;
                        case "07":
                            chartMonthlyLabels.add("JUL");
                            break;
                        case "08":
                            chartMonthlyLabels.add("AUG");
                            break;
                        case "09":
                            chartMonthlyLabels.add("SEP");
                            break;
                        case "10":
                            chartMonthlyLabels.add("OCT");
                            break;
                        case "11":
                            chartMonthlyLabels.add("NOV");
                            break;
                        case "12":
                            chartMonthlyLabels.add("DEC");
                            break;
                    }
                    double monthlySum = 0.0;
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        monthlySum = dbAdapter.getTotalExpensesByMonth(currentYear + "-" + stringNumber);
                    else
                        monthlySum = dbAdapter.getTotalSavingsByMonth(currentYear + "-" + stringNumber);
                    MonthlyValues.add((float) monthlySum);
                    chartMonthlyValues.add(new BarEntry((float) monthlySum, i - 1));
                }
                LineDataSet chartMonthlyDataset;
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    chartMonthlyDataset = new LineDataSet(chartMonthlyValues, "Amount of expenses");
                else {
                    chartMonthlyDataset = new LineDataSet(chartMonthlyValues, "Amount of savings");
                }

                chartMonthlyDataset.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorPrimary)});
                chartMonthlyDataset.setFillColor(ContextCompat.getColor(getContext(), R.color.md_teal_300));
                chartMonthlyDataset.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                chartMonthlyDataset.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorAccent));
                chartMonthlyDataset.setDrawFilled(true);
                lineDatas.add(1, new LineData(chartMonthlyLabels, chartMonthlyDataset));
            } catch (Exception e) {
                ERROR = e.getMessage() + "\n" + e.getCause();
            }
            return lineDatas;
        }

        @Override
        protected void onPostExecute(ArrayList<LineData> lineDatas) {
            if (lineDatas.size() > 0) {
                /*********** DAILY CHART **************/
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mDateUtil.getCurrentDate());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        lineChartDaily.setDescription("Total expenses on each day of " + simpleDateFormat.format(date));
                    else
                        lineChartDaily.setDescription("Total savings on each day of " + simpleDateFormat.format(date));

                } catch (ParseException e) {
                    if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                        lineChartDaily.setDescription("Total expenses on each day");
                    else
                        lineChartDaily.setDescription("Total savings on each day");
                }
                lineChartDaily.setData(lineDatas.get(0));
                lineChartDaily.animateXY(350, 700);
                /********** MONTHLY CHART ************/
                lineChartMonthly.setData(lineDatas.get(1));
                if (mSettings.getSelectedStatisticsData().equalsIgnoreCase("expenses"))
                    lineChartMonthly.setDescription("Total expenses on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
                else
                    lineChartMonthly.setDescription("Total savings on each month of " + mDateUtil.getCurrentDate().substring(0, 4));
                lineChartMonthly.animateXY(350, 700);
                mDialog.dismiss();
            } else {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(ERROR);
                builder.show();
            }
            super.onPostExecute(lineDatas);
        }
    }


}
