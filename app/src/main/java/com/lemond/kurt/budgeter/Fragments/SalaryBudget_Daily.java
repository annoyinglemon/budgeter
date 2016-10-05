package com.lemond.kurt.budgeter.Fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Adapters.Adapter_salaryBudgetDaily;

import com.lemond.kurt.budgeter.ObjectClasses.SalaryItemClass;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.SalaryBudget_Pager;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Utilities.G_AlertDialogs;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.G_ViewHolders;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SalaryBudget#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalaryBudget_Daily extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private TextView mTotalSavingsLabel;
    private TextView mTotalSavings;
    private TextView mBudget;
    private TextView mTotal;
    private CardView cvSalary_salary_budget_daily;
    private FloatingActionButton mFab;
    private FloatingActionButton mFab_cancel;
    private Adapter_salaryBudgetDaily mAdapter;
    private LinearLayout llEmptyExpenses;
    private ActionMode mActionMode;
    private ActionModeCallback mActionModeCallback;

    private SettingsManager mSettingsManager;
    private int nr = 0;

    /*************************************************
     * AUTO GENERATED BY ANDROID STUDIO
     **************************************************/

    public SalaryBudget_Daily() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SalaryBudget.
     */
    // TODO: Rename and change types and number of parameters
    public static SalaryBudget_Daily newInstance(String param1, String param2) {
        SalaryBudget_Daily fragment = new SalaryBudget_Daily();
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

    /***********************************************************************************************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_salary_budget_daily, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lvItems_salary_budget_daily);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new G_ViewHolders().new DividerItemDecoration(getContext()));

        llEmptyExpenses = (LinearLayout) rootView.findViewById(R.id.llEmptyExpenses);

        mTotalSavingsLabel = (TextView) rootView.findViewById(R.id.tvSavings_salary_budget_label_daily);
        mTotalSavings = (TextView) rootView.findViewById(R.id.tvSavings_salary_budget_daily);
        mTotal = (TextView) rootView.findViewById(R.id.tvTotalExpense_salary_budget_daily);
        mBudget = (TextView) rootView.findViewById(R.id.tvSalary_salary_budget_daily);

        cvSalary_salary_budget_daily = (CardView) rootView.findViewById(R.id.cvSalary_salary_budget_daily);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab_salaryBudget_daily);
        mFab_cancel = (FloatingActionButton) rootView.findViewById(R.id.fab_salaryBudget_daily_cancel);

        if(savedInstanceState==null) {
            mAdapter = new Adapter_salaryBudgetDaily(getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter = new Adapter_salaryBudgetDaily(getContext(), (ArrayList<SalaryItemClass>)savedInstanceState.getSerializable("all_items"));
            mRecyclerView.setAdapter(mAdapter);
            if(mAdapter.getItemCount()>0){
                mRecyclerView.setVisibility(View.VISIBLE);
                llEmptyExpenses.setVisibility(View.GONE);
            }else{
                mRecyclerView.setVisibility(View.GONE);
                llEmptyExpenses.setVisibility(View.VISIBLE);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mTotalSavingsLabel.setText(savedInstanceState.getString("savings_label_text"));
//            mTotalSavingsLabel.setTextColor(ContextCompat.getColor(getContext(), savedInstanceState.getInt("savings_label_text_color")));
            mTotalSavings.setText(savedInstanceState.getString("savings_text"));
//            mTotalSavings.setTextColor(ContextCompat.getColor(getContext(), savedInstanceState.getInt("savings_text_color")));
            mTotal.setText(savedInstanceState.getString("expenses_text"));
            mBudget.setText(savedInstanceState.getString("budget_text"));
        }

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView));

        cvSalary_salary_budget_daily.setOnClickListener(new CustomClickListener());
        mFab.setOnClickListener(new CustomClickListener());
        mFab_cancel.setOnClickListener(new CustomClickListener());
        mActionModeCallback = new ActionModeCallback();
        mTotalSavings.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double savings = G_Functions.parseNumber(mTotalSavings.getText().toString().substring(2).trim());
                if (savings < 0) {
                    mTotalSavings.setText("" + G_Functions.formatNumber(savings * -1));
                    mTotalSavingsLabel.setText("Credits:");
                    mTotalSavingsLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_red_100));
                    mTotalSavings.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_red_200));
                } else {
                    mTotalSavingsLabel.setText("Savings:");
                    mTotalSavingsLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_teal_100));
                    mTotalSavings.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_teal_200));
                }
            }
        });

        mSettingsManager = new SettingsManager(getActivity());

        if(savedInstanceState==null) updateData();
        mActionModeCallback = new ActionModeCallback();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("all_items", mAdapter.getItems());
        outState.putString("savings_label_text", mTotalSavingsLabel.getText().toString());
        outState.putInt("savings_label_text_color", mTotalSavingsLabel.getCurrentTextColor());
        outState.putString("savings_text", mTotalSavings.getText().toString());
        outState.putInt("savings_text_color", mTotalSavings.getCurrentTextColor());
        outState.putString("expenses_text", mTotal.getText().toString());
        outState.putString("budget_text", mBudget.getText().toString());
        super.onSaveInstanceState(outState);
    }


    @TargetApi(21)
    public void retainWindowColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
    }

    public void setTotalSavingsAndExpenses(double beforeTotalExpenses, final SalaryItemClass salaryItem, final boolean isChecked, final boolean saveToMonthlyIsChecked, int multiplier) {
        Double salary = G_Functions.parseNumber(mBudget.getText().toString().substring(2).trim());
        if ((salary - beforeTotalExpenses) < 0) {
            if (mSettingsManager.getExceedExpenses().equals("Ask First")) {
                new G_AlertDialogs().setTotalSavingsAndExpenses_Dialog(getContext(), this, salaryItem, isChecked, saveToMonthlyIsChecked, multiplier);
            } else if (mSettingsManager.getExceedExpenses().equals("Always")) {
                mAdapter.add(salaryItem, isChecked, saveToMonthlyIsChecked, multiplier);
                updateData();
            }
        } else {
            mAdapter.add(salaryItem, isChecked, saveToMonthlyIsChecked, multiplier);
            updateData();
        }
    }

    public void setTotalSavingsAndExpenses_update(double beforeTotalExpenses, final SalaryItemClass salaryItem, final boolean isChecked, final boolean isSaveToMonthlyChecked, int multiplier) {
        Double salary = G_Functions.parseNumber(mBudget.getText().toString().substring(2).trim());
        if ((salary - beforeTotalExpenses) < 0) {
            if (mSettingsManager.getExceedExpenses().equals("Ask First")) {
                new G_AlertDialogs().setTotalSavingsAndExpenses_update_Dialog(getContext(), this, salaryItem, isChecked, isSaveToMonthlyChecked, multiplier);
            } else if (mSettingsManager.getExceedExpenses().equals("Always")) {
                mAdapter.updateItem(salaryItem, isChecked, isSaveToMonthlyChecked, multiplier);
                updateData();
            }
        } else {
            mAdapter.updateItem(salaryItem, isChecked, isSaveToMonthlyChecked, multiplier);
            updateData();
        }
    }

    public void updateData() {
        mAdapter.updateDataList();
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            llEmptyExpenses.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            llEmptyExpenses.setVisibility(View.VISIBLE);
        }
        double totalBudget = mAdapter.getTotalBudget();
        double totalExpenses = mAdapter.getTotalPrice();
        double totalSavings = mAdapter.getTotalSavings(totalBudget);
        mBudget.setText(mSettingsManager.getCurrency() + " " + G_Functions.formatNumber(totalBudget));
        mTotal.setText(mSettingsManager.getCurrency()+" "+G_Functions.formatNumber(totalExpenses));
        mTotalSavings.setText(mSettingsManager.getCurrency() + " " + G_Functions.formatNumber(totalSavings));
    }

    public Adapter_salaryBudgetDaily getAdapter() {
        return mAdapter;
    }

    /****************************************************
     * CUSTOM LISTENERS
     *********************************************************/
    private class CustomClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_salaryBudget_daily:
                    new G_AlertDialogs().prepareAddDialog_Dialog(getContext(), mTotal, SalaryBudget_Daily.this);
                    break;
                case R.id.fab_salaryBudget_daily_cancel:
                    nr = 0;
                    mActionMode.finish();
                    break;
                case R.id.cvSalary_salary_budget_daily:
                    if (!mSettingsManager.isDistributeMockMonthlyBudget()) {
                        G_AlertDialogs.PrepareUpdateSalaryDialog_Dialog_SalaryBudget_Daily prepareUpdateSalaryDialog = new G_AlertDialogs().new PrepareUpdateSalaryDialog_Dialog_SalaryBudget_Daily(getContext(), SalaryBudget_Daily.this);
                        prepareUpdateSalaryDialog.execute();
                    }
                    break;
                default:
            }

        }
    }

//    private class CustomMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
//        @Override
//        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//            if (checked) {
//                nr++;
//                mAdapter.addOnItemsToBeRemove(position);
//            } else {
//                nr--;
//                mAdapter.removeSelection(Integer.valueOf(position));
//            }
//            mode.setTitle(nr + " selected");
//        }
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            nr = 0;
//            mAdapter.setLongItemClicked(true);
//            MenuInflater inflater = getActivity().getMenuInflater();
//            inflater.inflate(R.menu.context_menu, menu);
////            mToolbar.setVisibility(View.GONE);
//            SalaryBudget_Pager.setContextActivated(true);
//            retainWindowColor();
//            mFab.setVisibility(View.GONE);
//            mFab_cancel.setVisibility(View.VISIBLE);
//            mActionMode = mode;
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_delete:
//                    String thisString = "these items?";
//                    if (nr == 1) thisString = "this item?";
//                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
//                    alertBuilder.setMessage("Are you sure you want to delete " + thisString);
//                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            nr = 0;
//                            mAdapter.deleteSelected();
//                            updateData();
//                            mode.finish();
//                            mFab.setVisibility(View.VISIBLE);
//                            mFab_cancel.setVisibility(View.GONE);
//                        }
//                    });
//                    alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    alertBuilder.create().show();
//                    break;
//                case R.id.menu_selectAll:
//                    for (int count = 0; count < mAdapter.getCount(); count++) {
//                        mListView.setItemChecked(count, true);
//                        mAdapter.addOnItemsToBeRemove(count);
//                    }
//                    nr = mAdapter.getCount();
//                    mode.setTitle(nr + " selected");
//                    break;
//            }
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            mAdapter.setLongItemClicked(false);
//            SalaryBudget_Pager.setContextActivated(false);
////            mToolbar.setVisibility(View.VISIBLE);
//            mAdapter.clearSelection();
//            mFab.setVisibility(View.VISIBLE);
//            mFab_cancel.setVisibility(View.GONE);
//            mActionMode = null;
//        }
//    }
//
//    private class CustomOnItemClickListener implements AdapterView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            new G_AlertDialogs().CustomOnItemClick_Dialog(getContext(), mAdapter.getItem(position), SalaryBudget_Daily.this);
//        }
//    }
//
//    private class CustomOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            mListView.setItemChecked(position, !mAdapter.isPositionChecked(position));
//            return false;
//        }
//    }
//

    private class ActionModeCallback implements android.support.v7.view.ActionMode.Callback {
        @SuppressWarnings("unused")

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            SalaryBudget_Pager.setContextActivated(true);
            retainWindowColor();
            mFab.setVisibility(View.GONE);
            mFab_cancel.setVisibility(View.VISIBLE);
            mActionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    String thisString = "these items?";
                    if (nr == 1) thisString = "this item?";
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setMessage("Are you sure you want to delete " + thisString);
                    alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nr = 0;
                            mAdapter.deleteSelected();
                            updateData();
                            mode.finish();
                            mFab.setVisibility(View.VISIBLE);
                            mFab_cancel.setVisibility(View.GONE);
                        }
                    });
                    alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create().show();
                    return true;
                case R.id.menu_selectAll:
                    for (int count = 0; count < mAdapter.getItemCount(); count++) {
                        mAdapter.addOnItemsToBeRemove(count);
                    }
                    nr = mAdapter.getItemCount();
                    mode.setTitle(nr + " selected");
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            if(mActionMode!=null){
                mActionMode = null;
            }
            mAdapter.setLongItemClicked(false);
            SalaryBudget_Pager.setContextActivated(false);
            mAdapter.clearSelection();
            mFab.setVisibility(View.VISIBLE);
            mFab_cancel.setVisibility(View.GONE);
        }
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetectorCompat gestureDetectorCompat;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView) {
            gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                private int itemSelected = 0;

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    itemSelected = recyclerView.getChildAdapterPosition(child);

                    if(itemSelected>-1) {
                        nr = 0;
                        if (mActionMode == null) {
                            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                        }
                        if (!mAdapter.isItemLongClicked()) {
                            mAdapter.setLongItemClicked(true);
                            mAdapter.addOnItemsToBeRemove(itemSelected);
                            nr++;
                            mActionMode.setTitle(nr + " selected");
                        }
                    }
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    itemSelected = recyclerView.getChildAdapterPosition(child);

                    if(itemSelected>-1) {
                        if (mAdapter.isItemLongClicked()) {
                            if (!mAdapter.isPositionChecked(itemSelected)) {
                                nr++;
                                mAdapter.addOnItemsToBeRemove(itemSelected);
                                mActionMode.setTitle(nr + " selected");
                            } else {
                                nr--;
                                if(nr==0)mActionMode.finish();
                                else mActionMode.setTitle(nr + " selected");
                                mAdapter.removeSelection(itemSelected);
                            }
                        } else {
                            new G_AlertDialogs().CustomOnItemClick_Dialog(getContext(), mAdapter.getItem(itemSelected), SalaryBudget_Daily.this);
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            gestureDetectorCompat.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
