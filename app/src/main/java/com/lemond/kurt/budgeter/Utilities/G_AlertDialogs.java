package com.lemond.kurt.budgeter.Utilities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lemond.kurt.budgeter.Adapters.Adapter_importSavedItems;
import com.lemond.kurt.budgeter.Adapters.Adapter_loansByMonth;
import com.lemond.kurt.budgeter.Adapters.Adapter_mockLoans;
import com.lemond.kurt.budgeter.Adapters.Adapter_salaryHistory;
import com.lemond.kurt.budgeter.Adapters.ArrayAdapter_searchSuggestion;
import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses;
import com.lemond.kurt.budgeter.Fragments.CurrentMonthExpenses_Daily;
import com.lemond.kurt.budgeter.Fragments.SalaryBudget;
import com.lemond.kurt.budgeter.Fragments.SalaryBudget_Daily;
import com.lemond.kurt.budgeter.Fragments.SavedCommodities;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.ObjectClasses.ItemClass;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.ObjectClasses.SalaryItemClass;
import com.lemond.kurt.budgeter.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kurt_capatan on 5/24/2016.
 */
public class G_AlertDialogs {

    private AlertDialog mainAlert;

    private boolean isDouble(String str) {
        try {
            Double d = Double.parseDouble(str);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public boolean checkInput(TextView stringText, TextView doubleText){
        if(stringText.getText().toString().trim().equalsIgnoreCase("")||!isDouble(doubleText.getText().toString().trim())){
            return false;
        }
        return true;
    }

    public boolean checkInput(TextView stringText, TextView doubleText, TextView intText){
        if(stringText.getText().toString().trim().equalsIgnoreCase("")||!isDouble(doubleText.getText().toString().trim())||!isInteger(intText.getText().toString().trim())){
            return false;
        }
        return true;
    }

    /********************************************
      CurrentMonthExpenses.java
     ******************************************************/

    public void setTotalSavingsAndExpenses_Dialog(final Context context, final CurrentMonthExpenses currentMonthExpenses, final ActualExpensesClass actualExpense, final boolean isCheckBoxChecked) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMonthExpenses.getAdapter().add(actualExpense, isCheckBoxChecked);
                currentMonthExpenses.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    public void setTotalSavingsAndExpenses_update_Dialog(final Context context, final CurrentMonthExpenses currentMonthExpenses, final ActualExpensesClass actualExpense, final boolean isCheckBoxChecked) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMonthExpenses.getAdapter().updateItem(actualExpense, isCheckBoxChecked);
                currentMonthExpenses.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    double newItemPrice = 0.0;
    public void prepareAddDialog_Dialog(final Context context, final TextView mTotalExpenses, final CurrentMonthExpenses currentMonthExpenses) {
        final SettingsManager mSettings = new SettingsManager(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.add_actual_expense_dialog, null);
        final AutoCompleteTextView etExpenseName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addActualExpense_itemName);
        final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
        final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
        final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
        final CheckBox cbExpenseSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        etExpenseName.setSelection(etExpenseName.getText().length());
        etExpensePrice.setSelection(etExpensePrice.getText().length());
        etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
        etExpenseName.setThreshold(2);
        etExpenseName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etExpensePrice.setText(String.format("%1$.2f", ((ItemClass) etExpenseName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etExpenseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity)&&mainAlert!=null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etExpenseName.isPerformingCompletion()){
                    return;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etExpensePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity)&&mainAlert!=null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etExpensePrice.getText().toString().trim()) && isInteger(etExpenseQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0)
                        newItemPrice = Double.parseDouble(etExpensePrice.getText().toString());
            }
        });
        etExpenseQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity) && mainAlert != null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0) {
                        double price = newItemPrice * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
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
        etExpenseDate.setText(mSettings.getCurrentDate());
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
                    new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (ParseException exc) {
                }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Add Expense");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActualExpensesClass expenseItem = new ActualExpensesClass(etExpenseName.getText().toString(), Double.parseDouble(etExpensePrice.getText().toString()),
                        Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), ActualExpensesClass.MONTHLY);
                String day = mSettings.getCurrentDate().substring(0, 7);
                String day2 = etExpenseDate.getText().toString().substring(0, 7);
                //check if actual expense date is within current month (yyyy-mm-dd)
                if (day.equals(day2)) {
                    Double totalExpenses = G_Functions.parseNumber(mTotalExpenses.getText().toString().substring(2).trim()) + expenseItem.getActualExpensePrice();
                    currentMonthExpenses.setTotalSavingsAndExpenses(totalExpenses, expenseItem, cbExpenseSaveItem.isChecked());
                } else {
                    currentMonthExpenses.getAdapter().add(expenseItem, cbExpenseSaveItem.isChecked());
                    currentMonthExpenses.updateData();
                }
            }
        });
//        mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PrepareSavedItemsDialog_Dialog prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog(context, currentMonthExpenses, mTotalExpenses);
//                prepareSavedItemsDialog.execute();
//            }
//        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        mainAlert = mainbuilder.create();
        etExpenseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                new SearchItem().execute();
            }
            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion>{
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }
                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etExpenseName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    double newItemPrice2 = 0.00;
    public void CustomOnItemClick_Dialog(final Context context, final ActualExpensesClass actualExpensesClass, final CurrentMonthExpenses currentMonthExpenses) {
        final SettingsManager settingsManager = new SettingsManager(context);
        newItemPrice2 = actualExpensesClass.getActualExpensePrice() / (double)actualExpensesClass.getActualExpenseQuantity();
        final double priceBeforeEdit = actualExpensesClass.getActualExpensePrice();
        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.add_actual_expense_dialog, null);
        final AutoCompleteTextView etExpenseName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addActualExpense_itemName);
        final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
        final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
        final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
        final CheckBox cbActualExpense_saveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        etExpenseName.setText((actualExpensesClass.getActualExpenseName()));
        etExpensePrice.setText(String.format("%1$.2f", (actualExpensesClass.getActualExpensePrice())));
        etExpenseQuantity.setText(Integer.toString((actualExpensesClass.getActualExpenseQuantity())));
        etExpenseDate.setText((actualExpensesClass.getAcutalExpenseDate()));
        etExpenseName.setSelection(etExpenseName.getText().length());
        etExpensePrice.setSelection(etExpensePrice.getText().length());
        etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
        etExpenseName.setThreshold(2);
        etExpenseName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etExpensePrice.setText(String.format("%1$.2f", ((ItemClass) etExpenseName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etExpenseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etExpenseName.isPerformingCompletion()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etExpensePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etExpensePrice.getText().toString().trim()) && isInteger(etExpenseQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0)
                        newItemPrice2 = Double.parseDouble(etExpensePrice.getText().toString()) / Double.parseDouble(etExpenseQuantity.getText().toString().trim());
            }
        });
        etExpenseQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty()) {
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0) {
                        double price = newItemPrice2 * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
                        newItemPrice2 = Double.parseDouble(etExpensePrice.getText().toString().trim()) / Double.parseDouble(etExpenseQuantity.getText().toString().trim());
                    }
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
                    myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
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
                    new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (ParseException e) {
                }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Update Expense");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                ActualExpensesClass actualExpenses = new ActualExpensesClass(actualExpensesClass.getActualExpenseId(), etExpenseName.getText().toString(),
                        Double.parseDouble(etExpensePrice.getText().toString()), Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), actualExpensesClass.getActualExpenseDuration());
//                    if (CurrentMonthExpenses.this != null && currentMonth_Fragment.isVisible()) {
                if (priceBeforeEdit < Double.parseDouble(etExpensePrice.getText().toString())) {
                    String day = settingsManager.getCurrentDate().substring(5, 7);
                    String day2 = etExpenseDate.getText().toString().substring(5, 7);
                    if (day.equals(day2)) {
                        currentMonthExpenses.setTotalSavingsAndExpenses_update(((currentMonthExpenses.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etExpensePrice.getText().toString())), actualExpenses, cbActualExpense_saveItem.isChecked());
                    } else {
                        currentMonthExpenses.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                        currentMonthExpenses.updateData();
                        if(actualExpensesClass.getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.DAILY)) currentMonthExpenses.addToSavingsUpdateDates(actualExpensesClass.getAcutalExpenseDate());
                    }
                } else {
                    currentMonthExpenses.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                    currentMonthExpenses.updateData();
                    if(actualExpensesClass.getActualExpenseDuration().equalsIgnoreCase(ActualExpensesClass.DAILY)) currentMonthExpenses.addToSavingsUpdateDates(actualExpensesClass.getAcutalExpenseDate());
                }
//                    }
            }
        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        mainAlert = mainbuilder.create();
        etExpenseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new SearchItem().execute();
            }

            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }

                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etExpenseName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    /***************************** ASYNCTASKS ******************************/

    public class PrepareSavedItemsDialog_Dialog extends AsyncTask<Void, Void, ArrayList<ItemClass>> {

        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private View dialoglayout;
        private LayoutInflater inflater;
        private AlertDialog mainAlert;
        private ListView lvSavedItems;
        private CurrentMonthExpenses currentMonthExpenses;
        private Context context;
        private SettingsManager settingsManager;
        private TextView tvTotalExpenses;

        public PrepareSavedItemsDialog_Dialog(Context context, CurrentMonthExpenses currentMonthExpenses, TextView tvTotalExpenses) {
            this.context = context;
            this.currentMonthExpenses = currentMonthExpenses;
            this.settingsManager = new SettingsManager(context);
            this.tvTotalExpenses = tvTotalExpenses;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering saved items...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<ItemClass> doInBackground(Void... params) {
            return currentMonthExpenses.getAdapter().getAllSavedItems();
        }

        @Override
        protected void onPostExecute(ArrayList<ItemClass> allSavedItems) {
            super.onPostExecute(allSavedItems);
            if (allSavedItems.size() > 0) {
                inflater = LayoutInflater.from(context);
                dialoglayout = inflater.inflate(R.layout.import_saved_item_dialog, null);
                final Adapter_importSavedItems mSavedItemsAdapter = new Adapter_importSavedItems(context, allSavedItems);
                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvImport_saved_items);
                lvSavedItems.setAdapter(mSavedItemsAdapter);
                lvSavedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String strItemName = mSavedItemsAdapter.getItem(position).getItemName();
                        String strItemPrice = String.format("%1$.2f", mSavedItemsAdapter.getItem(position).getItemPrice());
                        mainAlert.dismiss();
                        prepareAddFromListDialog(strItemName, strItemPrice);
                    }
                });
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Saved Items");
                alertBuilder.setView(dialoglayout);
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            } else {
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("You do not have any items saved on Saved Items.");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }
            progressDialog.dismiss();
        }

        double newItemPrice = 0;

        private void prepareAddFromListDialog(final String itemName, final String itemPrice) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_actual_expense_dialog, null);

            final EditText etExpenseName = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemName);
            final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
            final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
            final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
            final CheckBox cbExpenseSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);

            etExpenseName.setText(itemName);
            etExpensePrice.setText(itemPrice);
            etExpenseQuantity.setText("" + 1);
            etExpenseName.setSelection(etExpenseName.getText().length());
            etExpensePrice.setSelection(etExpensePrice.getText().length());
            etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
            etExpenseName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etExpensePrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isDouble(etExpensePrice.getText().toString().trim()) && isInteger(etExpenseQuantity.getText().toString().trim()))
                        newItemPrice = Double.parseDouble(etExpensePrice.getText().toString());
                }
            });
            etExpenseQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty()) {
                        double price = newItemPrice * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
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
            etExpenseDate.setText(settingsManager.getCurrentDate());
            etExpenseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        final Calendar myCalendar = Calendar.getInstance();
                        myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
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
                        new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    } catch (ParseException exc) {
                    }
                }
            });
            cbExpenseSaveItem.setEnabled(false);
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Expense");
            mainbuilder.setCancelable(true);
            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            ActualExpensesClass actualExpenses = new ActualExpensesClass(etExpenseName.getText().toString(), Double.parseDouble(etExpensePrice.getText().toString()),
                                    Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), ActualExpensesClass.MONTHLY);
                            String day = settingsManager.getCurrentDate().substring(0, 7);
                            String day2 = etExpenseDate.getText().toString().substring(0, 7);
                            if (day.equals(day2)) {
                                Double totalExpenses = G_Functions.parseNumber(tvTotalExpenses.getText().toString()) + actualExpenses.getActualExpensePrice();
                                currentMonthExpenses.setTotalSavingsAndExpenses(totalExpenses, actualExpenses, cbExpenseSaveItem.isChecked());
                            } else {
                                currentMonthExpenses.getAdapter().add(actualExpenses, cbExpenseSaveItem.isChecked());
                                currentMonthExpenses.updateData();
                            }
                        }
                    }

            );
            mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener()

                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PrepareSavedItemsDialog_Dialog prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog(context, currentMonthExpenses, tvTotalExpenses);
                            prepareSavedItemsDialog.execute();
                        }
                    }

            );
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                    {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }

            );

            mainAlert = mainbuilder.create();
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

    public class PrepareUpdateSalaryDialog_Dialog extends AsyncTask<Void, Void, Adapter_salaryHistory> {

        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private AlertDialog mainAlert;
        private View dialoglayout;
        private LayoutInflater inflater;
        private Context context;
        private CurrentMonthExpenses currentMonthExpenses;
        private SettingsManager settingsManager;

        public PrepareUpdateSalaryDialog_Dialog(Context context, CurrentMonthExpenses currentMonthExpenses) {
            this.context = context;
            this.currentMonthExpenses = currentMonthExpenses;
            settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering salary history...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Adapter_salaryHistory doInBackground(Void... params) {
            return new Adapter_salaryHistory(context, currentMonthExpenses.getAdapter().getSalaries());
        }

        @Override
        protected void onPostExecute(final Adapter_salaryHistory adapter_salaryHistory) {
            super.onPostExecute(adapter_salaryHistory);
            inflater = LayoutInflater.from(context);
            dialoglayout = inflater.inflate(R.layout.edit_salary_dialog, null);
            final TextView lvSalaryHistoryLabel = (TextView) dialoglayout.findViewById(R.id.lvSalaryHistoryLabel);
            final EditText etSalary = (EditText) dialoglayout.findViewById(R.id.editSalary_salaryAmount);
            final ListView lvSalaryHistory = (ListView) dialoglayout.findViewById(R.id.lvSalaryHistory);
            etSalary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!isDouble(etSalary.getText().toString().trim())) mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    else mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (adapter_salaryHistory.getCount() > 0) {
                lvSalaryHistory.setVisibility(View.VISIBLE);
                lvSalaryHistoryLabel.setVisibility(View.VISIBLE);
                lvSalaryHistory.setAdapter(adapter_salaryHistory);
                lvSalaryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etSalary.setText(String.format("%1$.2f", adapter_salaryHistory.getItem(position).getSalaryAmount()));
                    }
                });
            } else {
                lvSalaryHistory.setVisibility(View.GONE);
                lvSalaryHistoryLabel.setVisibility(View.GONE);
            }
            alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setView(dialoglayout);
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    settingsManager.setMonthlyBudget(Double.parseDouble(etSalary.getText().toString()));
                    currentMonthExpenses.updateData();
                    if (settingsManager.isDistributeMonthlySavings()) {
                        double totalExpenses = new DatabaseAdapter(context).getTotalExpensesBetweenDates(new DateUtilities(settingsManager.getCurrentDate()).getMonthDateBeginning(),
                                new DateUtilities(settingsManager.getCurrentDate()).getMonthDateEnd(), ActualExpensesClass.MONTHLY);
                        // subtract the sum to monthly budget
                        double dividend = currentMonthExpenses.getAdapter().getTotalBudget() - totalExpenses;
                        settingsManager.setDailyBudget(dividend / settingsManager.getNumberOfDays());
                    }
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mainAlert = alertBuilder.create();
            etSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            mainAlert.show();
            progressDialog.dismiss();
        }
    }

    public class PrepareLoanDialog_Dialog extends AsyncTask<Void, Void, ArrayList<LoanClass>> {
        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private View dialoglayout;
        private LayoutInflater inflater;
        private AlertDialog mainAlert;
        private AlertDialog alertDialog = null;
        private ListView lvSavedItems;
        private Context context;
        private CurrentMonthExpenses currentMonthExpenses;
        private SettingsManager settingsManager;
        private Adapter_loansByMonth loansAdapter;

        public PrepareLoanDialog_Dialog(Context context, CurrentMonthExpenses currentMonthExpenses) {
            this.context = context;
            this.currentMonthExpenses = currentMonthExpenses;
            this.settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering loans for this month...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<LoanClass> doInBackground(Void... params) {
            return currentMonthExpenses.getAdapter().getAllLoansBetweenDates();
        }

        @Override
        protected void onPostExecute(ArrayList<LoanClass> allLoans) {
            super.onPostExecute(allLoans);
            if (allLoans.size() > 0) {
                inflater = LayoutInflater.from(context);
                dialoglayout = inflater.inflate(R.layout.loans_dialog, null);
                loansAdapter = new Adapter_loansByMonth(context, allLoans);
                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvLoans_by_month);
                lvSavedItems.setAdapter(loansAdapter);
                lvSavedItems.setOnItemClickListener(new LoanItemClickListener());
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Lenders For This Month");
                alertBuilder.setView(dialoglayout);
                alertBuilder.setPositiveButton("Add Lender", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAddLoanDialog();
                    }
                });
                alertBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            } else {
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("You do not have any lenders this month. Would you like to add one?");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAddLoanDialog();
                    }
                });
                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }
            progressDialog.dismiss();
        }

        class LoanItemClickListener implements AdapterView.OnItemClickListener{
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LoanClass loanClass = loansAdapter.getItem(position);
                View dialoglayout = inflater.inflate(R.layout.add_lender_dialog, null);
                final EditText etLenderName = (EditText) dialoglayout.findViewById(R.id.addLender_lenderName);
                final EditText etLoanAmount = (EditText) dialoglayout.findViewById(R.id.addLender_loanAmount);
                final EditText etLoanDate = (EditText) dialoglayout.findViewById(R.id.addLender_loanDate);
                etLenderName.setText(loanClass.getLenderName());
                etLenderName.setSelection(etLenderName.getText().length());
                etLoanAmount.setText(String.format("%1$.2f", (loanClass.getLoanAmount())));
                etLenderName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (checkInput(etLenderName, etLoanAmount))
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        else
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                etLoanAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(checkInput(etLenderName, etLoanAmount)) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        else alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                etLoanAmount.setSelection(etLoanAmount.getText().length());
                etLoanAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            etLoanDate.performClick();
                            return true;
                        }
                        return false;
                    }
                });
                etLoanDate.setText((loanClass.getLoanDate()));
                etLoanDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            final Calendar myCalendar = Calendar.getInstance();
                            myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
                            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    etLoanDate.setText(sdf.format(myCalendar.getTime()));
                                }
                            };
                            new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                                    myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }catch (ParseException exc){}
                    }
                });
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
                mainbuilder.setView(dialoglayout);
                mainbuilder.setTitle("Update Lender");
                mainbuilder.setCancelable(false);
                mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoanClass loanClass2 = new LoanClass(loanClass.getId(), etLenderName.getText().toString(), Double.parseDouble(etLoanAmount.getText().toString()), etLoanDate.getText().toString());
                                loansAdapter.updateItem(loanClass2);
                                currentMonthExpenses.updateData();
                                if (settingsManager.isDistributeMonthlySavings())
                                    currentMonthExpenses.getAdapter().updateDailyBudget();
                            }
                        }

                );
                mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
                alertDialog = mainbuilder.create();
                etLenderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                });
                alertDialog.show();
            }
        }

        private void prepareAddLoanDialog() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_lender_dialog, null);
            final EditText etLenderName = (EditText) dialoglayout.findViewById(R.id.addLender_lenderName);
            final EditText etLoanAmount = (EditText) dialoglayout.findViewById(R.id.addLender_loanAmount);
            final EditText etLoanDate = (EditText) dialoglayout.findViewById(R.id.addLender_loanDate);
            etLenderName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(checkInput(etLenderName, etLoanAmount))
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etLoanAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etLenderName, etLoanAmount))
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etLoanAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        etLoanDate.performClick();
                        return true;
                    }
                    return false;
                }
            });
            etLoanDate.setText(settingsManager.getCurrentDate());
            etLoanDate.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  try {
                                                      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                      final Calendar myCalendar = Calendar.getInstance();
                                                      myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
                                                      DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                                                          @Override
                                                          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                              myCalendar.set(Calendar.YEAR, year);
                                                              myCalendar.set(Calendar.MONTH, monthOfYear);
                                                              myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                              etLoanDate.setText(sdf.format(myCalendar.getTime()));
                                                          }
                                                      };
                                                      new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                                                              myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                                                  } catch (ParseException exc) {
                                                  }
                                              }
                                          }

            );
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Lender");
            mainbuilder.setCancelable(false);
            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoanClass loanClass = new LoanClass(etLenderName.getText().toString(), Double.parseDouble(etLoanAmount.getText().toString()), etLoanDate.getText().toString());
                            if (currentMonthExpenses.getAdapter().insertLoan(loanClass)) {
                                currentMonthExpenses.updateData();
                                dialog.dismiss();
                                PrepareLoanDialog_Dialog prepareLoanDialog = new PrepareLoanDialog_Dialog(context, currentMonthExpenses);
                                prepareLoanDialog.execute();
                            } else {
                                Toast.makeText(context, "Loan cannot be saved into database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }

            );
            alertDialog = mainbuilder.create();
            etLenderName.setOnFocusChangeListener(new View.OnFocusChangeListener()

                                                  {
                                                      @Override
                                                      public void onFocusChange(View v, boolean hasFocus) {
                                                          if (hasFocus)
                                                              alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                                      }
                                                  }

            );
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            alertDialog.show();
        }
    }



    /****************************************************
      CurrentMonthExpenses_Daily.java
     ****************************************************/

    public void setTotalSavingsAndExpenses_Dialog(final Context context, final CurrentMonthExpenses_Daily currentMonthExpenses_daily, final ActualExpensesClass actualExpenses, final boolean isCheckBoxChecked){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMonthExpenses_daily.getAdapter().add(actualExpenses, isCheckBoxChecked);
                currentMonthExpenses_daily.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    public void setTotalSavingsAndExpenses_update_Dialog(final Context context, final CurrentMonthExpenses_Daily currentMonthExpenses_daily, final ActualExpensesClass actualExpenses, final boolean isCheckBoxChecked){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentMonthExpenses_daily.getAdapter().updateItem(actualExpenses, isCheckBoxChecked);
                currentMonthExpenses_daily.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    double newItemPrice3 = 0;
    public void prepareAddDialog_Dialog(final Context context, final TextView mTotalExpenses, final CurrentMonthExpenses_Daily currentMonthExpenses_daily){
        final SettingsManager settingsManager = new SettingsManager(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.add_actual_expense_dialog, null);
        final AutoCompleteTextView etExpenseName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addActualExpense_itemName);
        final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
        final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
        final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
        final CheckBox cbExpenseSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        etExpenseName.setSelection(etExpenseName.getText().length());
        etExpensePrice.setSelection(etExpensePrice.getText().length());
        etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
        etExpenseName.setThreshold(2);
        etExpenseName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etExpensePrice.setText(String.format("%1$.2f", ((ItemClass) etExpenseName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etExpenseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity) && mainAlert != null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etExpensePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity)&&mainAlert!=null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etExpenseName.isPerformingCompletion()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty() && isDouble(etExpensePrice.getText().toString().trim()) && isInteger(etExpenseQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0)
                        newItemPrice3 = Double.parseDouble(etExpensePrice.getText().toString());
            }
        });
        etExpenseQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity)&&mainAlert!=null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0){
                        double price = newItemPrice3 * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
                        newItemPrice3 = Double.parseDouble(etExpensePrice.getText().toString().trim()) / Double.parseDouble(etExpenseQuantity.getText().toString().trim());
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
        etExpenseDate.setText(settingsManager.getCurrentDate());
        etExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    final Calendar myCalendar = Calendar.getInstance();
                    myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
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
                    new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }catch (ParseException exc){}
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Add Expense");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActualExpensesClass expenseItem = new ActualExpensesClass(etExpenseName.getText().toString(), Double.parseDouble(etExpensePrice.getText().toString()), Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), ActualExpensesClass.DAILY);
                //if current date
                if (settingsManager.getCurrentDate().equalsIgnoreCase(etExpenseDate.getText().toString().trim())) {
                    Double totalExpenses = G_Functions.parseNumber(mTotalExpenses.getText().toString().substring(2).trim()) + expenseItem.getActualExpensePrice();
                    currentMonthExpenses_daily.setTotalSavingsAndExpenses(totalExpenses, expenseItem, cbExpenseSaveItem.isChecked());
                } else {
                    currentMonthExpenses_daily.getAdapter().add(expenseItem, cbExpenseSaveItem.isChecked());
                    currentMonthExpenses_daily.updateData();
                }
            }
        });
//        mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PrepareSavedItemsDialog_Dialog_Daily prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_Daily(currentMonthExpenses_daily, context, mTotalExpenses);
//                prepareSavedItemsDialog.execute();
//            }
//        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        mainAlert = mainbuilder.create();
        etExpenseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                new SearchItem().execute();
            }
            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion>{
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }
                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etExpenseName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    double newItemPrice4 = 0.00;
    public void CustomOnItemClick_Dialog(final Context context, final ActualExpensesClass actualExpensesClass, final CurrentMonthExpenses_Daily currentMonthExpenses_daily){
        final SettingsManager settingsManager = new SettingsManager(context);
        newItemPrice4 = actualExpensesClass.getActualExpensePrice() / (double)actualExpensesClass.getActualExpenseQuantity();
        final double priceBeforeEdit = actualExpensesClass.getActualExpensePrice();
        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.add_actual_expense_dialog, null);
        final AutoCompleteTextView etExpenseName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addActualExpense_itemName);
        final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
        final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
        final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
        final CheckBox cbActualExpense_saveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        etExpenseName.setText((actualExpensesClass.getActualExpenseName()));
        etExpensePrice.setText(String.format("%1$.2f", (actualExpensesClass.getActualExpensePrice())));
        etExpenseQuantity.setText(Integer.toString((actualExpensesClass.getActualExpenseQuantity())));
        etExpenseDate.setText((actualExpensesClass.getAcutalExpenseDate()));
        etExpenseName.setSelection(etExpenseName.getText().length());
        etExpensePrice.setSelection(etExpensePrice.getText().length());
        etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
        etExpenseName.setThreshold(2);
        etExpenseName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etExpensePrice.setText(String.format("%1$.2f", ((ItemClass) etExpenseName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etExpenseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etExpenseName.isPerformingCompletion()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etExpensePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etExpensePrice.getText().toString().trim()) && !etExpenseQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0)
                        newItemPrice4 = Double.parseDouble(etExpensePrice.getText().toString());
            }
        });
        etExpenseQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etExpensePrice.getText().toString().trim())!=0.0&&Integer.parseInt(etExpenseQuantity.getText().toString().trim())!=0){
                        double price = newItemPrice4 * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
                        newItemPrice4 = Double.parseDouble(etExpensePrice.getText().toString().trim()) / Double.parseDouble(etExpenseQuantity.getText().toString().trim());
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
                    myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
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
                    new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (ParseException exc) {
                }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Update Expense");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                ActualExpensesClass actualExpenses = new ActualExpensesClass(actualExpensesClass.getActualExpenseId(), etExpenseName.getText().toString(),
                        Double.parseDouble(etExpensePrice.getText().toString()), Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), actualExpensesClass.getActualExpenseDuration());
                if (priceBeforeEdit < Double.parseDouble(etExpensePrice.getText().toString())) {
                    if (settingsManager.getCurrentDate().equalsIgnoreCase(etExpenseDate.getText().toString().trim())) {
                        currentMonthExpenses_daily.setTotalSavingsAndExpenses_update(((currentMonthExpenses_daily.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etExpensePrice.getText().toString())), actualExpenses, cbActualExpense_saveItem.isChecked());
                    } else {
                        currentMonthExpenses_daily.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                        currentMonthExpenses_daily.updateData();
                    }
                } else {
                    currentMonthExpenses_daily.getAdapter().updateItem(actualExpenses, cbActualExpense_saveItem.isChecked());
                    currentMonthExpenses_daily.updateData();
                }
            }
        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        mainAlert = mainbuilder.create();
        etExpenseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new SearchItem().execute();
            }

            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }

                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etExpenseName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    /***************************** ASYNCTASKS ******************************/

    public class PrepareSavedItemsDialog_Dialog_Daily extends AsyncTask<Void, Void, ArrayList<ItemClass>>{

        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private View dialoglayout;
        private LayoutInflater inflater;
        private AlertDialog mainAlert;
        private ListView lvSavedItems;
        private CurrentMonthExpenses_Daily currentMonthExpenses_daily;
        private Context context;
        private SettingsManager settingsManager;
        private TextView tvTotalExpenses;

        public PrepareSavedItemsDialog_Dialog_Daily(CurrentMonthExpenses_Daily currentMonthExpenses_daily, Context context, TextView tvTotalExpenses) {
            this.currentMonthExpenses_daily = currentMonthExpenses_daily;
            this.context = context;
            this.tvTotalExpenses = tvTotalExpenses;
            this.settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering saved items...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<ItemClass> doInBackground(Void... params) {
            return currentMonthExpenses_daily.getAdapter().getAllSavedItems();
        }

        @Override
        protected void onPostExecute(ArrayList<ItemClass> allSavedItems) {
            super.onPostExecute(allSavedItems);
            if(allSavedItems.size()>0){
                inflater = LayoutInflater.from(context);
                dialoglayout = inflater.inflate(R.layout.import_saved_item_dialog, null);
                final Adapter_importSavedItems mSavedItemsAdapter = new Adapter_importSavedItems(context, allSavedItems);
                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvImport_saved_items);
                lvSavedItems.setAdapter(mSavedItemsAdapter);
                lvSavedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String strItemName = mSavedItemsAdapter.getItem(position).getItemName();
                        String strItemPrice = String.format("%1$.2f", mSavedItemsAdapter.getItem(position).getItemPrice());
                        mainAlert.dismiss();
                        prepareAddFromListDialog(strItemName, strItemPrice);
                    }
                });
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Saved Items");
                alertBuilder.setView(dialoglayout);
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }else {
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("You do not have any items saved on Saved Items.");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }
            progressDialog.dismiss();
        }
        double newItemPrice = 0;
        private void prepareAddFromListDialog(final String itemName, final String itemPrice) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_actual_expense_dialog, null);

            final EditText etExpenseName = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemName);
            final EditText etExpensePrice = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPrice);
            final EditText etExpenseQuantity = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemQuantity);
            final EditText etExpenseDate = (EditText) dialoglayout.findViewById(R.id.addActualExpense_itemPurchaseDate);
            final CheckBox cbExpenseSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);

            etExpenseName.setText(itemName);
            etExpensePrice.setText(itemPrice);
            etExpenseQuantity.setText("" + 1);
            etExpenseName.setSelection(etExpenseName.getText().length());
            etExpensePrice.setSelection(etExpensePrice.getText().length());
            etExpenseQuantity.setSelection(etExpenseQuantity.getText().length());
            etExpenseName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etExpensePrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty() && isDouble(etExpensePrice.getText().toString().trim()) && isInteger(etExpenseQuantity.getText().toString().trim()))
                        newItemPrice = Double.parseDouble(etExpensePrice.getText().toString());
                }
            });
            etExpenseQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etExpenseName, etExpensePrice, etExpenseQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!etExpensePrice.getText().toString().trim().isEmpty() && !etExpenseQuantity.getText().toString().trim().isEmpty()) {
                        double price = newItemPrice * Double.parseDouble(etExpenseQuantity.getText().toString());
                        etExpensePrice.setText(String.format("%1$.2f", price));
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
            etExpenseDate.setText(settingsManager.getCurrentDate());
            etExpenseDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        final Calendar myCalendar = Calendar.getInstance();
                        myCalendar.setTime(df.parse(settingsManager.getCurrentDate()));
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
                        new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }catch (ParseException e){}
                }
            });
            cbExpenseSaveItem.setEnabled(false);
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Expense");
            mainbuilder.setCancelable(true);
            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            ActualExpensesClass actualExpenses = new ActualExpensesClass(etExpenseName.getText().toString(), Double.parseDouble(etExpensePrice.getText().toString()),
                                    Integer.parseInt(etExpenseQuantity.getText().toString()), etExpenseDate.getText().toString(), ActualExpensesClass.DAILY);
                            if (settingsManager.getCurrentDate().equalsIgnoreCase(etExpenseDate.getText().toString().trim())) {
                                Double totalExpenses = G_Functions.parseNumber(tvTotalExpenses.getText().toString()) + actualExpenses.getActualExpensePrice();
                                currentMonthExpenses_daily.setTotalSavingsAndExpenses(totalExpenses, actualExpenses, cbExpenseSaveItem.isChecked());
                            } else {
                                currentMonthExpenses_daily.getAdapter().add(actualExpenses, cbExpenseSaveItem.isChecked());
                                currentMonthExpenses_daily.updateData();
                            }
                        }
                    }

            );
            mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener()

                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PrepareSavedItemsDialog_Dialog_Daily prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_Daily(currentMonthExpenses_daily, context, tvTotalExpenses);
                            prepareSavedItemsDialog.execute();
                        }
                    }

            );
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }

            );

            mainAlert = mainbuilder.create();
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

    public class PrepareUpdateSalaryDialog_Dialog_Daily extends AsyncTask<Void, Void, Adapter_salaryHistory> {

        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private AlertDialog mainAlert;
        private View dialoglayout;
        private LayoutInflater inflater;
        private Context context;
        private CurrentMonthExpenses_Daily currentMonthExpenses_daily;
        private SettingsManager settingsManager;

        public PrepareUpdateSalaryDialog_Dialog_Daily(Context context, CurrentMonthExpenses_Daily currentMonthExpenses_daily) {
            this.context = context;
            this.currentMonthExpenses_daily = currentMonthExpenses_daily;
            settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering salary history...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Adapter_salaryHistory doInBackground(Void... params) {
            return new Adapter_salaryHistory(context, currentMonthExpenses_daily.getAdapter().getSalaries());
        }

        @Override
        protected void onPostExecute(final Adapter_salaryHistory adapter_salaryHistory) {
            super.onPostExecute(adapter_salaryHistory);
            inflater = LayoutInflater.from(context);
            dialoglayout = inflater.inflate(R.layout.edit_salary_dialog, null);
            final TextView lvSalaryHistoryLabel = (TextView) dialoglayout.findViewById(R.id.lvSalaryHistoryLabel);
            final EditText etSalary = (EditText) dialoglayout.findViewById(R.id.editSalary_salaryAmount);
            final ListView lvSalaryHistory = (ListView) dialoglayout.findViewById(R.id.lvSalaryHistory);
            etSalary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!isDouble(etSalary.getText().toString().trim())){
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }else {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (adapter_salaryHistory.getCount()>0) {
                lvSalaryHistory.setVisibility(View.VISIBLE);
                lvSalaryHistoryLabel.setVisibility(View.VISIBLE);
                lvSalaryHistory.setAdapter(adapter_salaryHistory);
                lvSalaryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etSalary.setText(String.format("%1$.2f", adapter_salaryHistory.getItem(position).getSalaryAmount()));
                    }
                });
            } else {
                lvSalaryHistory.setVisibility(View.GONE);
                lvSalaryHistoryLabel.setVisibility(View.GONE);
            }
            alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setView(dialoglayout);
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    settingsManager.setDailyBudget(Double.parseDouble(etSalary.getText().toString()));
                    currentMonthExpenses_daily.updateData();

                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mainAlert = alertBuilder.create();
            etSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            mainAlert.show();
            progressDialog.dismiss();
        }
    }



    /****************************************************
     SalaryBudget.java
     ****************************************************/

    public void setTotalSavingsAndExpenses_Dialog(final Context context, final SalaryBudget salaryBudget, final SalaryItemClass salaryItem, final boolean isCheckBoxChecked){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salaryBudget.getAdapter().add(salaryItem, isCheckBoxChecked);
                salaryBudget.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    public void setTotalSavingsAndExpenses_update_Dialog(final Context context, final SalaryBudget salaryBudget, final SalaryItemClass salaryItem, final boolean isCheckBoxChecked){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salaryBudget.getAdapter().updateItem(salaryItem, isCheckBoxChecked);
                salaryBudget.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    double newItemPrice5 = 0;
    public void prepareAddDialog_Dialog(final Context context, final TextView mTotalExpenses, final SalaryBudget salaryBudget){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.add_salary_item_dialog, null);

        final AutoCompleteTextView etSalaryItemName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
        final EditText etSalaryItemPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
        final EditText etSalaryItemQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
        final CheckBox cbIsSaveItemChecked = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);
        final EditText etSalaryItem_multiplier = (EditText) dialoglayout.findViewById(R.id.etSalaryItem_multiplier);

        etSalaryItemQuantity.setSelection(etSalaryItemQuantity.getText().length());
        etSalaryItemPrice.setSelection(etSalaryItemPrice.getText().length());
        etSalaryItemName.setSelection(etSalaryItemName.getText().length());
        cbActualExpense_saveToMonthly.setChecked(false);
        cbActualExpense_saveToMonthly.setVisibility(View.GONE);
        etSalaryItem_multiplier.setText("");
        etSalaryItem_multiplier.setVisibility(View.GONE);
        etSalaryItemName.setThreshold(2);
        etSalaryItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSalaryItemPrice.setText(String.format("%1$.2f", ((ItemClass) etSalaryItemName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etSalaryItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etSalaryItemName.isPerformingCompletion()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSalaryItemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etSalaryItemPrice.getText().toString().trim()) && isInteger(etSalaryItemQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etSalaryItemPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryItemQuantity.getText().toString().trim())!=0)
                        newItemPrice5 = Double.parseDouble(etSalaryItemPrice.getText().toString());
            }
        });
        etSalaryItemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etSalaryItemPrice.getText().toString().trim().isEmpty() && !etSalaryItemQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etSalaryItemPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryItemQuantity.getText().toString().trim())!=0) {
                        double price = newItemPrice5 * Double.parseDouble(etSalaryItemQuantity.getText().toString());
                        etSalaryItemPrice.setText(String.format("%1$.2f", price));
                        newItemPrice5 = Double.parseDouble(etSalaryItemPrice.getText().toString().trim()) / Double.parseDouble(etSalaryItemQuantity.getText().toString().trim());
                    }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Add Item");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final SalaryItemClass salaryItem = new SalaryItemClass(etSalaryItemName.getText().toString(), Double.parseDouble(etSalaryItemPrice.getText().toString()), Integer.parseInt(etSalaryItemQuantity.getText().toString()), SalaryItemClass.MONTHLY);
                Double totalExpenses = G_Functions.parseNumber(mTotalExpenses.getText().toString().substring(2).trim()) + salaryItem.getSalaryItemPrice();
                salaryBudget.setTotalSavingsAndExpenses(totalExpenses, salaryItem, cbIsSaveItemChecked.isChecked());
            }
        });
//        mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PrepareSavedItemsDialog_Dialog_SalaryBudget prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_SalaryBudget(context, salaryBudget, mTotalExpenses);
//                prepareSavedItemsDialog.execute();
//            }
//        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        mainAlert = mainbuilder.create();
        etSalaryItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                new SearchItem().execute();
            }
            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion>{
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }
                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etSalaryItemName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    double newItemPrice6 = 0.00;
    public void CustomOnItemClick_Dialog(final Context context, final SalaryItemClass salaryItemClass, final SalaryBudget salaryBudget){
        newItemPrice6 = salaryItemClass.getSalaryItemPrice() / (double)salaryItemClass.getSalaryItemQuantity();
        final double priceBeforeEdit = salaryItemClass.getSalaryItemPrice();
        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.add_salary_item_dialog, null);
        final AutoCompleteTextView etSalaryBudgetName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
        final EditText etSalaryBudgetPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
        final EditText etSalaryBudgetQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
        final CheckBox cbSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);
        final EditText etSalaryItem_multiplier = (EditText) dialoglayout.findViewById(R.id.etSalaryItem_multiplier);

        etSalaryBudgetName.setText((salaryItemClass.getSalaryItemName()));
        etSalaryBudgetPrice.setText(String.format("%1$.2f", (salaryItemClass.getSalaryItemPrice())));
        etSalaryBudgetQuantity.setText(Integer.toString(salaryItemClass.getSalaryItemQuantity()));
        etSalaryBudgetName.setSelection(etSalaryBudgetName.getText().length());
        etSalaryBudgetPrice.setSelection(etSalaryBudgetPrice.getText().length());
        etSalaryBudgetQuantity.setSelection(etSalaryBudgetQuantity.getText().length());
        cbActualExpense_saveToMonthly.setChecked(false);
        cbActualExpense_saveToMonthly.setVisibility(View.GONE);
        etSalaryItem_multiplier.setText("");
        etSalaryItem_multiplier.setVisibility(View.GONE);
        etSalaryBudgetName.setThreshold(2);
        etSalaryBudgetName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSalaryBudgetPrice.setText(String.format("%1$.2f", ((ItemClass) etSalaryBudgetName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etSalaryBudgetName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                if(etSalaryBudgetName.isPerformingCompletion()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSalaryBudgetPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etSalaryBudgetPrice.getText().toString().trim()) && isInteger(etSalaryBudgetQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryBudgetQuantity.getText().toString().trim())!=0)
                        newItemPrice6 = Double.parseDouble(etSalaryBudgetPrice.getText().toString());
            }
        });
        etSalaryBudgetQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity))
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                else
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etSalaryBudgetPrice.getText().toString().trim().isEmpty() && !etSalaryBudgetQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryBudgetQuantity.getText().toString().trim())!=0){
                        double price = newItemPrice6 * Double.parseDouble(etSalaryBudgetQuantity.getText().toString());
                        etSalaryBudgetPrice.setText(String.format("%1$.2f", price));
                        newItemPrice6 = Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim()) / Double.parseDouble(etSalaryBudgetQuantity.getText().toString().trim());
                    }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Update Item");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                salaryItemClass.setSalaryItemName(etSalaryBudgetName.getText().toString());
                salaryItemClass.setSalaryItemPrice(Double.parseDouble(etSalaryBudgetPrice.getText().toString()));
                salaryItemClass.setSalaryItemQuantity(Integer.parseInt(etSalaryBudgetQuantity.getText().toString()));
                if (priceBeforeEdit < Double.parseDouble(etSalaryBudgetPrice.getText().toString())) {
                    salaryBudget.setTotalSavingsAndExpenses_update(((salaryBudget.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etSalaryBudgetPrice.getText().toString())), salaryItemClass, cbSaveItem.isChecked());
                } else {
                    salaryBudget.getAdapter().updateItem(salaryItemClass, cbSaveItem.isChecked());
                    salaryBudget.updateData();
                }
            }
        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        mainAlert = mainbuilder.create();
        etSalaryBudgetName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new SearchItem().execute();
            }

            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }

                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etSalaryBudgetName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    /***************************** ASYNCTASKS ******************************/

    public class PrepareSavedItemsDialog_Dialog_SalaryBudget extends AsyncTask<Void, Void, ArrayList<ItemClass>> {

        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private AlertDialog mainAlert;
        private ListView lvSavedItems;
        private View dialoglayout;
        private LayoutInflater inflater;
        private SalaryBudget salaryBudget;
        private Context context;
        private TextView tvTotalExpenses;

        public PrepareSavedItemsDialog_Dialog_SalaryBudget(Context context, SalaryBudget salaryBudget, TextView tvTotalExpenses) {
            this.context = context;
            this.salaryBudget = salaryBudget;
            this.tvTotalExpenses = tvTotalExpenses;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering saved items...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<ItemClass> doInBackground(Void... params) {
            return salaryBudget.getAdapter().getAllSavedItems();
        }

        @Override
        protected void onPostExecute(ArrayList<ItemClass> allSavedItems) {
            super.onPostExecute(allSavedItems);
            if (allSavedItems.size() > 0) {
                inflater = LayoutInflater.from(context);
                dialoglayout = inflater.inflate(R.layout.import_saved_item_dialog, null);
                final Adapter_importSavedItems mSavedItemsAdapter = new Adapter_importSavedItems(context, allSavedItems);
                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvImport_saved_items);
                lvSavedItems.setAdapter(mSavedItemsAdapter);
                lvSavedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String strItemName = mSavedItemsAdapter.getItem(position).getItemName();
                        String strItemPrice = String.format("%1$.2f", mSavedItemsAdapter.getItem(position).getItemPrice());
                        mainAlert.dismiss();
                        prepareAddFromListDialog(strItemName, strItemPrice);
                    }
                });
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Saved Items");
                alertBuilder.setView(dialoglayout);
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            } else {
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("You do not have any items saved on Saved Items.");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }
            progressDialog.dismiss();
        }

        double newItemPrice = 0;

        private void prepareAddFromListDialog(final String itemName, final String itemPrice) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_salary_item_dialog, null);

            final EditText etSalaryItemName = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
            final EditText etSalaryItemPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
            final EditText etSalaryItemQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
            final CheckBox cbIsSaveItemChecked = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
            final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);

            etSalaryItemName.setText(itemName);
            etSalaryItemPrice.setText((itemPrice));
            etSalaryItemQuantity.setText("" + 1);
            etSalaryItemQuantity.setSelection(etSalaryItemQuantity.getText().length());
            etSalaryItemPrice.setSelection(etSalaryItemPrice.getText().length());
            etSalaryItemName.setSelection(etSalaryItemName.getText().length());
            cbActualExpense_saveToMonthly.setChecked(false);
            cbActualExpense_saveToMonthly.setVisibility(View.GONE);
            etSalaryItemName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etSalaryItemPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isDouble(etSalaryItemPrice.getText().toString().trim()) && isInteger(etSalaryItemQuantity.getText().toString().trim()))
                        newItemPrice = Double.parseDouble(etSalaryItemPrice.getText().toString());
                }
            });
            etSalaryItemQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!etSalaryItemPrice.getText().toString().trim().isEmpty() && !etSalaryItemQuantity.getText().toString().trim().isEmpty()) {
                        double price = newItemPrice * Double.parseDouble(etSalaryItemQuantity.getText().toString());
                        etSalaryItemPrice.setText(String.format("%1$.2f", price));
                        newItemPrice = Double.parseDouble(etSalaryItemPrice.getText().toString().trim()) / Double.parseDouble(etSalaryItemQuantity.getText().toString().trim());
                    }
                }
            });
            cbIsSaveItemChecked.setEnabled(false);
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Item");
            mainbuilder.setCancelable(true);
            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    SalaryItemClass newSalaryItem = new SalaryItemClass(etSalaryItemName.getText().toString(), Double.parseDouble(etSalaryItemPrice.getText().toString()), Integer.parseInt(etSalaryItemQuantity.getText().toString()), SalaryItemClass.MONTHLY);
                    Double totalExpenses = G_Functions.parseNumber(tvTotalExpenses.getText().toString()) + Double.parseDouble(etSalaryItemPrice.getText().toString());
                    salaryBudget.setTotalSavingsAndExpenses(totalExpenses, newSalaryItem, cbIsSaveItemChecked.isChecked());
                }
            });
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PrepareSavedItemsDialog_Dialog_SalaryBudget prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_SalaryBudget(context, salaryBudget, tvTotalExpenses);
                    prepareSavedItemsDialog.execute();
                }
            });
            mainAlert = mainbuilder.create();
            etSalaryItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.show();
        }
    }

    public class PrepareUpdateSalaryDialog_Dialog_SalaryBudget extends AsyncTask<Void, Void, Adapter_salaryHistory> {
        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private AlertDialog mainAlert;
        private View dialoglayout;
        private LayoutInflater inflater;
        private Context context;
        private SalaryBudget salaryBudget;
        private SettingsManager settingsManager;

        public PrepareUpdateSalaryDialog_Dialog_SalaryBudget(Context context, SalaryBudget salaryBudget) {
            this.salaryBudget = salaryBudget;
            this.context = context;
            this.settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering salary history...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Adapter_salaryHistory doInBackground(Void... params) {
            return new Adapter_salaryHistory(context, salaryBudget.getAdapter().getMockSalaries());
        }

        @Override
        protected void onPostExecute(final Adapter_salaryHistory adapter_salaryHistory) {
            super.onPostExecute(adapter_salaryHistory);
            inflater = LayoutInflater.from(context);
            dialoglayout = inflater.inflate(R.layout.edit_salary_dialog, null);
            final TextView lvSalaryHistoryLabel = (TextView) dialoglayout.findViewById(R.id.lvSalaryHistoryLabel);
            final EditText etSalary = (EditText) dialoglayout.findViewById(R.id.editSalary_salaryAmount);
            final ListView lvSalaryHistory = (ListView) dialoglayout.findViewById(R.id.lvSalaryHistory);
            etSalary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!isDouble(etSalary.getText().toString().trim())){
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }else {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (adapter_salaryHistory.getCount()>0) {
                lvSalaryHistory.setVisibility(View.VISIBLE);
                lvSalaryHistoryLabel.setVisibility(View.VISIBLE);
                lvSalaryHistory.setAdapter(adapter_salaryHistory);
                lvSalaryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etSalary.setText(String.format("%1$.2f", adapter_salaryHistory.getItem(position).getSalaryAmount()));
                    }
                });
            } else {
                lvSalaryHistory.setVisibility(View.GONE);
                lvSalaryHistoryLabel.setVisibility(View.GONE);
            }
            alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setView(dialoglayout);
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    settingsManager.setMockMonthlyBudget(Double.parseDouble(etSalary.getText().toString()));
                    salaryBudget.updateData();
                    if (settingsManager.isDistributeMockMonthlyBudget()) {
                        double totalExpenses = salaryBudget.getAdapter().getTotalPrice();
                        // subtract the sum to monthly budget
                        double dividend = salaryBudget.getAdapter().getTotalBudget() - totalExpenses;
                        settingsManager.setMockDailyBudget(dividend / settingsManager.getNumberOfDays());
                    }

                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mainAlert = alertBuilder.create();
            etSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            mainAlert.show();
            progressDialog.dismiss();
        }
    }

    public class PrepareLoanDialog_Dialog_SalaryBudget extends AsyncTask<Void, Void, ArrayList<LoanClass>> {
        private ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private View dialoglayout;
        private LayoutInflater inflater;
        private AlertDialog mainAlert;
        private AlertDialog alertDialog = null;
        private ListView lvSavedItems;
        private Context context;
        private SalaryBudget salaryBudget;
        private Adapter_mockLoans loansAdapter;
        private SettingsManager settingsManager;

        public PrepareLoanDialog_Dialog_SalaryBudget(Context context, SalaryBudget salaryBudget) {
            this.context = context;
            this.salaryBudget = salaryBudget;
            this.settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering loans...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<LoanClass> doInBackground(Void... params) {
            return salaryBudget.getAdapter().getAllMockLoans();
        }

        @Override
        protected void onPostExecute(ArrayList<LoanClass> allLoans) {
            super.onPostExecute(allLoans);
            if (allLoans.size() > 0) {
                inflater = LayoutInflater.from(context);
                dialoglayout = inflater.inflate(R.layout.loans_dialog, null);
                loansAdapter = new Adapter_mockLoans(context, allLoans);
                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvLoans_by_month);
                lvSavedItems.setAdapter(loansAdapter);
                lvSavedItems.setOnItemClickListener(new LoanItemClickListener());
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setCancelable(false);
                alertBuilder.setTitle("Lenders");
                alertBuilder.setView(dialoglayout);
                alertBuilder.setPositiveButton("Add Lender", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAddLoanDialog();
                    }
                });
                alertBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            } else {
                alertBuilder = new AlertDialog.Builder(context);
                alertBuilder.setMessage("You do not have any lenders. Would you like to add one?");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAddLoanDialog();
                    }
                });
                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mainAlert = alertBuilder.create();
                mainAlert.show();
            }
            progressDialog.dismiss();
        }

        class LoanItemClickListener implements AdapterView.OnItemClickListener{
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LoanClass loanClass = loansAdapter.getItem(position);
                View dialoglayout = inflater.inflate(R.layout.add_mock_lender_dialog, null);
                final EditText etLenderName = (EditText) dialoglayout.findViewById(R.id.addLender_lenderName);
                final EditText etLoanAmount = (EditText) dialoglayout.findViewById(R.id.addLender_loanAmount);
                etLenderName.setText((loanClass.getLenderName()));
                etLenderName.setSelection(etLenderName.getText().length());
                etLoanAmount.setText(String.format("%1$.2f", (loanClass.getLoanAmount())));
                etLenderName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (checkInput(etLenderName, etLoanAmount))
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        else
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                etLoanAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (checkInput(etLenderName, etLoanAmount))
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        else
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
                mainbuilder.setView(dialoglayout);
                mainbuilder.setTitle("Update Lender");
                mainbuilder.setCancelable(false);
                mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoanClass loanClass2 = new LoanClass(loanClass.getId(), etLenderName.getText().toString(), Double.parseDouble(etLoanAmount.getText().toString()));
                                loansAdapter.updateLoan(loanClass2);
                                salaryBudget.updateData();
                                if (settingsManager.isDistributeMockMonthlyBudget())
                                    salaryBudget.getAdapter().updateDailyBudget();
                            }
                        }
                );
                mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );
                alertDialog = mainbuilder.create();
                etLenderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                });
                alertDialog.show();
            }
        }

        private void prepareAddLoanDialog() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_mock_lender_dialog, null);

            final EditText etLenderName = (EditText) dialoglayout.findViewById(R.id.addLender_lenderName);
            final EditText etLoanAmount = (EditText) dialoglayout.findViewById(R.id.addLender_loanAmount);
            etLenderName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(checkInput(etLenderName, etLoanAmount))
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etLoanAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etLenderName, etLoanAmount))
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Lender");
            mainbuilder.setCancelable(false);
            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoanClass loanClass = new LoanClass(etLenderName.getText().toString(), Double.parseDouble(etLoanAmount.getText().toString()));
                            if (salaryBudget.getAdapter().insertMockLoan(loanClass)) {
                                salaryBudget.updateData();
                                PrepareLoanDialog_Dialog_SalaryBudget prepareLoanDialog = new PrepareLoanDialog_Dialog_SalaryBudget(context, salaryBudget);
                                prepareLoanDialog.execute();
                            } else {
                                Toast.makeText(context, "Loan cannot be saved into database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }
            );
            alertDialog = mainbuilder.create();
            etLenderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            alertDialog.show();
        }
    }



    /****************************************************
     SalaryBudget_Daily.java
     ****************************************************/

    public void setTotalSavingsAndExpenses_Dialog(final Context context, final SalaryBudget_Daily salaryBudget_daily, final SalaryItemClass salaryItem, final boolean isCheckBoxChecked, final boolean saveToMonthlyIsChecked, final int multiplier){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salaryBudget_daily.getAdapter().add(salaryItem, isCheckBoxChecked, saveToMonthlyIsChecked, multiplier);
                salaryBudget_daily.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    public void setTotalSavingsAndExpenses_update_Dialog(final Context context, final SalaryBudget_Daily salaryBudget_daily, final SalaryItemClass salaryItem, final boolean isCheckBoxChecked, final boolean saveToMonthlyIsChecked, final int multiplier){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.expenses_confirm, null);
        final CheckBox cbExpensesConfirm = (CheckBox) dialoglayout.findViewById(R.id.cbExpensesConfirm);
        final AlertDialog.Builder mainbuilder2 = new AlertDialog.Builder(context);
        mainbuilder2.setCancelable(false);
        mainbuilder2.setMessage("Expenses will exceed your budget amount. Would you like to proceed?");
        mainbuilder2.setView(dialoglayout);
        mainbuilder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                salaryBudget_daily.getAdapter().updateItem(salaryItem, isCheckBoxChecked, saveToMonthlyIsChecked, multiplier);
                salaryBudget_daily.updateData();
                if (cbExpensesConfirm.isChecked()) {
                    new SettingsManager(context).setExceedExpenses("Always");
                }
            }
        });
        mainbuilder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog mainAlert = mainbuilder2.create();
        mainAlert.show();
    }

    double newItemPrice7 = 0;
    public void prepareAddDialog_Dialog(final Context context, final TextView mTotalExpenses, final SalaryBudget_Daily salaryBudget_daily){
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialoglayout = inflater.inflate(R.layout.add_salary_item_dialog, null);

        final AutoCompleteTextView etSalaryItemName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
        final EditText etSalaryItemPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
        final EditText etSalaryItemQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
        final CheckBox cbIsSaveItemChecked = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);
        final EditText etSalaryItem_multiplier = (EditText) dialoglayout.findViewById(R.id.etSalaryItem_multiplier);
        etSalaryItemQuantity.setSelection(etSalaryItemQuantity.getText().length());
        etSalaryItemPrice.setSelection(etSalaryItemPrice.getText().length());
        etSalaryItemName.setSelection(etSalaryItemName.getText().length());
        etSalaryItemName.setThreshold(2);
        etSalaryItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSalaryItemPrice.setText(String.format("%1$.2f", ((ItemClass) etSalaryItemName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etSalaryItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cbActualExpense_saveToMonthly.isChecked()){
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                if (etSalaryItemName.isPerformingCompletion()) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSalaryItemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cbActualExpense_saveToMonthly.isChecked()){
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etSalaryItemPrice.getText().toString().trim()) && isInteger(etSalaryItemQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etSalaryItemPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryItemQuantity.getText().toString().trim())!=0)
                        newItemPrice7 = Double.parseDouble(etSalaryItemPrice.getText().toString());
            }
        });
        etSalaryItemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cbActualExpense_saveToMonthly.isChecked()){
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etSalaryItemPrice.getText().toString().trim().isEmpty() && !etSalaryItemQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etSalaryItemPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryItemQuantity.getText().toString().trim())!=0){
                        double price = newItemPrice7 * Double.parseDouble(etSalaryItemQuantity.getText().toString());
                        etSalaryItemPrice.setText(String.format("%1$.2f", price));
                        newItemPrice7 = Double.parseDouble(etSalaryItemPrice.getText().toString().trim()) / Double.parseDouble(etSalaryItemQuantity.getText().toString().trim());
                    }
            }
        });
        cbActualExpense_saveToMonthly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etSalaryItem_multiplier.setEnabled(isChecked);
                if(isChecked){
                    etSalaryItem_multiplier.setText("1");
                }else{
                    etSalaryItem_multiplier.setText("");
                }
                if(isChecked){
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Add Item");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final SalaryItemClass salaryItem = new SalaryItemClass(etSalaryItemName.getText().toString(), Double.parseDouble(etSalaryItemPrice.getText().toString()), Integer.parseInt(etSalaryItemQuantity.getText().toString()), SalaryItemClass.DAILY);
                Double totalExpenses = G_Functions.parseNumber(mTotalExpenses.getText().toString().substring(2).trim()) + salaryItem.getSalaryItemPrice();
                salaryBudget_daily.setTotalSavingsAndExpenses(totalExpenses, salaryItem, cbIsSaveItemChecked.isChecked(), cbActualExpense_saveToMonthly.isChecked(), Integer.parseInt(etSalaryItem_multiplier.getText().toString()));
            }
        });
//        mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily(context, salaryBudget_daily, mTotalExpenses);
//                prepareSavedItemsDialog.execute();
//            }
//        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        mainAlert = mainbuilder.create();
        etSalaryItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                new SearchItem().execute();
            }
            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion>{
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }
                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etSalaryItemName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    double newItemPrice8 = 0.00;
    public void CustomOnItemClick_Dialog(final Context context, final SalaryItemClass salaryItemClass, final SalaryBudget_Daily salaryBudget_daily){
        newItemPrice8 = salaryItemClass.getSalaryItemPrice() / (double)salaryItemClass.getSalaryItemQuantity();
        final double priceBeforeEdit = salaryItemClass.getSalaryItemPrice() ;
        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.add_salary_item_dialog, null);
        final AutoCompleteTextView etSalaryBudgetName = (AutoCompleteTextView) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
        final EditText etSalaryBudgetPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
        final EditText etSalaryBudgetQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
        final CheckBox cbSaveItem = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
        final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);
        final EditText etSalaryItem_multiplier = (EditText) dialoglayout.findViewById(R.id.etSalaryItem_multiplier);
        etSalaryBudgetName.setText((salaryItemClass.getSalaryItemName()));
        etSalaryBudgetPrice.setText(String.format("%1$.2f", (salaryItemClass.getSalaryItemPrice())));
        etSalaryBudgetQuantity.setText(Integer.toString(salaryItemClass.getSalaryItemQuantity()));
        etSalaryBudgetName.setSelection(etSalaryBudgetName.getText().length());
        etSalaryBudgetPrice.setSelection(etSalaryBudgetPrice.getText().length());
        etSalaryBudgetQuantity.setSelection(etSalaryBudgetQuantity.getText().length());
        cbActualExpense_saveToMonthly.setChecked(false);
        etSalaryItem_multiplier.setText("");
        etSalaryBudgetName.setThreshold(2);
        etSalaryBudgetName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSalaryBudgetPrice.setText(String.format("%1$.2f", ((ItemClass) etSalaryBudgetName.getAdapter().getItem(position)).getItemPrice()));
            }
        });
        etSalaryBudgetName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cbActualExpense_saveToMonthly.isChecked()) {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                if (etSalaryBudgetName.isPerformingCompletion()) {
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSalaryBudgetPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cbActualExpense_saveToMonthly.isChecked()){
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isDouble(etSalaryBudgetPrice.getText().toString().trim()) && isInteger(etSalaryBudgetQuantity.getText().toString().trim()))
                    if(Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryBudgetQuantity.getText().toString().trim())!=0)
                        newItemPrice8 = Double.parseDouble(etSalaryBudgetPrice.getText().toString());
            }
        });
        etSalaryBudgetQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(cbActualExpense_saveToMonthly.isChecked()){
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etSalaryBudgetPrice.getText().toString().trim().isEmpty() && !etSalaryBudgetQuantity.getText().toString().trim().isEmpty())
                    if(Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim())!=0.0&&Integer.parseInt(etSalaryBudgetQuantity.getText().toString().trim())!=0){
                        double price = newItemPrice8 * Double.parseDouble(etSalaryBudgetQuantity.getText().toString());
                        etSalaryBudgetPrice.setText(String.format("%1$.2f", price));
                        newItemPrice8 = Double.parseDouble(etSalaryBudgetPrice.getText().toString().trim()) / Double.parseDouble(etSalaryBudgetQuantity.getText().toString().trim());
                    }
            }
        });
        cbActualExpense_saveToMonthly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etSalaryItem_multiplier.setEnabled(isChecked);
                if (isChecked) {
                    etSalaryItem_multiplier.setText("1");
                } else {
                    etSalaryItem_multiplier.setText("");
                }
                if (isChecked) {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null && isInteger(etSalaryItem_multiplier.getText().toString().trim()))
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    if (checkInput(etSalaryBudgetName, etSalaryBudgetPrice, etSalaryBudgetQuantity) && mainAlert != null)
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    else
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Update Item");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                salaryItemClass.setSalaryItemName(etSalaryBudgetName.getText().toString());
                salaryItemClass.setSalaryItemPrice(Double.parseDouble(etSalaryBudgetPrice.getText().toString()));
                salaryItemClass.setSalaryItemQuantity(Integer.parseInt(etSalaryBudgetQuantity.getText().toString()));
                if (priceBeforeEdit < Double.parseDouble(etSalaryBudgetPrice.getText().toString())) {
                    salaryBudget_daily.setTotalSavingsAndExpenses_update(((salaryBudget_daily.getAdapter().getTotalPrice() - priceBeforeEdit) + Double.parseDouble(etSalaryBudgetPrice.getText().toString())), salaryItemClass, cbSaveItem.isChecked(), cbActualExpense_saveToMonthly.isChecked(), Integer.parseInt(etSalaryItem_multiplier.getText().toString().trim()));
                } else {
                    salaryBudget_daily.getAdapter().updateItem(salaryItemClass, cbSaveItem.isChecked(), cbActualExpense_saveToMonthly.isChecked(), Integer.parseInt(etSalaryItem_multiplier.getText().toString().trim()));
                    salaryBudget_daily.updateData();
                }
            }
        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        mainAlert = mainbuilder.create();
        etSalaryBudgetName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new SearchItem().execute();
            }

            class SearchItem extends AsyncTask<Void, Void, ArrayAdapter_searchSuggestion> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayAdapter_searchSuggestion doInBackground(Void... params) {
                    return new ArrayAdapter_searchSuggestion(context, R.layout.item_suggestions, new DatabaseAdapter(context).getAllSavedItems());
                }

                @Override
                protected void onPostExecute(ArrayAdapter_searchSuggestion itemClasses) {
                    etSalaryBudgetName.setAdapter(itemClasses);
                }
            }
        });
        mainAlert.show();
    }

    /*************************************
     * ASYNCTASK
     *******************************************/

//    public class PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily extends AsyncTask<Void, Void, ArrayList<ItemClass>> {
//
//        ProgressDialog progressDialog;
//        AlertDialog.Builder alertBuilder;
//        View dialoglayout;
//        LayoutInflater inflater;
//        AlertDialog mainAlert;
//        ListView lvSavedItems;
//        private SalaryBudget_Daily salaryBudget_daily;
//        private Context context;
//        private SettingsManager settingsManager;
//        private TextView tvTotalExpenses;
//
//        public PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily(Context context, SalaryBudget_Daily salaryBudget_daily, TextView tvTotalExpenses) {
//            this.context = context;
//            this.salaryBudget_daily = salaryBudget_daily;
//            this.settingsManager = new SettingsManager(context);
//            this.tvTotalExpenses = tvTotalExpenses;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage("Gathering saved items...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected ArrayList<ItemClass> doInBackground(Void... params) {
//            return salaryBudget_daily.getAdapter().getAllSavedItems();
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<ItemClass> allSavedItems) {
//            super.onPostExecute(allSavedItems);
//            if (allSavedItems.size() > 0) {
//                inflater = LayoutInflater.from(context);
//                dialoglayout = inflater.inflate(R.layout.import_saved_item_dialog, null);
//                final Adapter_importSavedItems mSavedItemsAdapter = new Adapter_importSavedItems(context, allSavedItems);
//                lvSavedItems = (ListView) dialoglayout.findViewById(R.id.lvImport_saved_items);
//                lvSavedItems.setAdapter(mSavedItemsAdapter);
//                lvSavedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        String strItemName = mSavedItemsAdapter.getItem(position).getItemName();
//                        String strItemPrice = String.format("%1$.2f", mSavedItemsAdapter.getItem(position).getItemPrice());
//                        mainAlert.dismiss();
//                        prepareAddFromListDialog(strItemName, strItemPrice);
//                    }
//                });
//                alertBuilder = new AlertDialog.Builder(context);
//                alertBuilder.setCancelable(false);
//                alertBuilder.setTitle("Saved Items");
//                alertBuilder.setView(dialoglayout);
//                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                mainAlert = alertBuilder.create();
//                mainAlert.show();
//            } else {
//                alertBuilder = new AlertDialog.Builder(context);
//                alertBuilder.setMessage("You do not have any items saved on Saved Items.");
//                alertBuilder.setCancelable(false);
//                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                mainAlert = alertBuilder.create();
//                mainAlert.show();
//            }
//            progressDialog.dismiss();
//        }
//
//        double newItemPrice = 0;
//
//        private void prepareAddFromListDialog(final String itemName, final String itemPrice) {
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View dialoglayout = inflater.inflate(R.layout.add_salary_item_dialog, null);
//
//            final EditText etSalaryItemName = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemName);
//            final EditText etSalaryItemPrice = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemPrice);
//            final EditText etSalaryItemQuantity = (EditText) dialoglayout.findViewById(R.id.addSalaryItem_itemQuantity);
//            final CheckBox cbIsSaveItemChecked = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveItem);
//            final CheckBox cbActualExpense_saveToMonthly = (CheckBox) dialoglayout.findViewById(R.id.cbActualExpense_saveToMonthly);
//
//            etSalaryItemName.setText(itemName);
//            etSalaryItemPrice.setText((itemPrice));
//            etSalaryItemQuantity.setText("" + 1);
//            etSalaryItemQuantity.setSelection(etSalaryItemQuantity.getText().length());
//            etSalaryItemPrice.setSelection(etSalaryItemPrice.getText().length());
//            etSalaryItemName.setSelection(etSalaryItemName.getText().length());
//            etSalaryItemName.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
//                    else
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//            etSalaryItemPrice.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
//                    else
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (isDouble(etSalaryItemPrice.getText().toString().trim()) && isInteger(etSalaryItemQuantity.getText().toString().trim()))
//                        newItemPrice = Double.parseDouble(etSalaryItemPrice.getText().toString());
//                }
//            });
//            etSalaryItemQuantity.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (checkInput(etSalaryItemName, etSalaryItemPrice, etSalaryItemQuantity))
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
//                    else
//                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (!etSalaryItemPrice.getText().toString().trim().isEmpty() && !etSalaryItemQuantity.getText().toString().trim().isEmpty()) {
//                        double price = newItemPrice * Double.parseDouble(etSalaryItemQuantity.getText().toString());
//                        etSalaryItemPrice.setText(String.format("%1$.2f", price));
//                        newItemPrice = Double.parseDouble(etSalaryItemPrice.getText().toString().trim()) / Double.parseDouble(etSalaryItemQuantity.getText().toString().trim());
//                    }
//                }
//            });
//            cbIsSaveItemChecked.setEnabled(false);
//            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
//            mainbuilder.setView(dialoglayout);
//            mainbuilder.setTitle("Add Item");
//            mainbuilder.setCancelable(true);
//            mainbuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int id) {
//                    SalaryItemClass newSalaryItem = new SalaryItemClass(etSalaryItemName.getText().toString(), Double.parseDouble(etSalaryItemPrice.getText().toString()), Integer.parseInt(etSalaryItemQuantity.getText().toString()), SalaryItemClass.DAILY);
//                    Double totalExpenses = G_Functions.parseNumber(tvTotalExpenses.getText().toString()) + Double.parseDouble(etSalaryItemPrice.getText().toString());
//                    salaryBudget_daily.setTotalSavingsAndExpenses(totalExpenses, newSalaryItem, cbIsSaveItemChecked.isChecked(), cbActualExpense_saveToMonthly.isChecked());
//                }
//            });
//            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int id) {
//                    dialog.dismiss();
//                }
//            });
//            mainbuilder.setNeutralButton("Import", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily prepareSavedItemsDialog = new PrepareSavedItemsDialog_Dialog_SalaryBudget_Daily(context, salaryBudget_daily, tvTotalExpenses);
//                    prepareSavedItemsDialog.execute();
//                }
//            });
//            mainAlert = mainbuilder.create();
//            etSalaryItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus)
//                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            });
//            mainAlert.show();
//        }
//
//    }

    public class PrepareUpdateSalaryDialog_Dialog_SalaryBudget_Daily extends AsyncTask<Void, Void, Adapter_salaryHistory> {

        ProgressDialog progressDialog;
        AlertDialog.Builder alertBuilder;
        AlertDialog mainAlert;
        View dialoglayout;
        LayoutInflater inflater;
        private Context context;
        private SalaryBudget_Daily salaryBudget_daily;
        private SettingsManager settingsManager;

        public PrepareUpdateSalaryDialog_Dialog_SalaryBudget_Daily(Context context, SalaryBudget_Daily salaryBudget_daily) {
            this.salaryBudget_daily = salaryBudget_daily;
            this.context = context;
            this.settingsManager = new SettingsManager(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Gathering salary history...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Adapter_salaryHistory doInBackground(Void... params) {
            return new Adapter_salaryHistory(context, salaryBudget_daily.getAdapter().getDailyMockSalaries());
        }

        @Override
        protected void onPostExecute(final Adapter_salaryHistory adapter_salaryHistory) {
            super.onPostExecute(adapter_salaryHistory);
            inflater = LayoutInflater.from(context);
            dialoglayout = inflater.inflate(R.layout.edit_salary_dialog, null);
            final TextView lvSalaryHistoryLabel = (TextView) dialoglayout.findViewById(R.id.lvSalaryHistoryLabel);
            final EditText etSalary = (EditText) dialoglayout.findViewById(R.id.editSalary_salaryAmount);
            final ListView lvSalaryHistory = (ListView) dialoglayout.findViewById(R.id.lvSalaryHistory);
            etSalary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!isDouble(etSalary.getText().toString().trim())) {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (adapter_salaryHistory.getCount()>0) {
                lvSalaryHistory.setVisibility(View.VISIBLE);
                lvSalaryHistoryLabel.setVisibility(View.VISIBLE);
                lvSalaryHistory.setAdapter(adapter_salaryHistory);
                lvSalaryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etSalary.setText(String.format("%1$.2f", adapter_salaryHistory.getItem(position).getSalaryAmount()));
                    }
                });
            } else {
                lvSalaryHistory.setVisibility(View.GONE);
                lvSalaryHistoryLabel.setVisibility(View.GONE);
            }
            alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setView(dialoglayout);
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    settingsManager.setMockDailyBudget(Double.parseDouble(etSalary.getText().toString()));
                    salaryBudget_daily.updateData();
                }
            });
            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mainAlert = alertBuilder.create();
            etSalary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            });
            mainAlert.show();
            progressDialog.dismiss();
        }
    }


    /************************************
      SavedCommodities.java
     ************************************/

    public void PrepareAddDialog_Dialog(final Context context, final SavedCommodities savedCommodities){
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialoglayout = inflater.inflate(R.layout.add_item_dialog, null);

            final EditText etItemName = (EditText) dialoglayout.findViewById(R.id.addItem_itemName);
            final EditText etItemPrice = (EditText) dialoglayout.findViewById(R.id.addItem_itemPrice);
            etItemName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(checkInput(etItemName, etItemPrice)){
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }else{
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            etItemPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (checkInput(etItemName, etItemPrice)) {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    } else {
                        mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
            mainbuilder.setView(dialoglayout);
            mainbuilder.setTitle("Add Item");
            mainbuilder.setCancelable(true);
            mainbuilder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ItemClass item = new ItemClass(etItemName.getText().toString(), Double.parseDouble(etItemPrice.getText().toString()));
                    savedCommodities.getAdapter().add(item);
                }
            });
            mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            mainAlert = mainbuilder.create();
            etItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });
            mainAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }
        });
            mainAlert.show();
    }

    public void CustomOnItemClick_Dialog(final Context context, final ItemClass itemClass, final SavedCommodities savedCommodities){
        View dialoglayout = LayoutInflater.from(context).inflate(R.layout.add_item_dialog, null);
//
        final EditText etItemName = (EditText) dialoglayout.findViewById(R.id.addItem_itemName);
        final EditText etItemPrice = (EditText) dialoglayout.findViewById(R.id.addItem_itemPrice);
        etItemName.setText(itemClass.getItemName());
        etItemPrice.setText(String.format("%1$.2f", (itemClass.getItemPrice())));
        etItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etItemName, etItemPrice) && mainAlert != null) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etItemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput(etItemName, etItemPrice) && mainAlert != null) {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    mainAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etItemName.setSelection(etItemName.getText().length());
        etItemPrice.setSelection(etItemPrice.getText().length());
        AlertDialog.Builder mainbuilder = new AlertDialog.Builder(context);
        mainbuilder.setView(dialoglayout);
        mainbuilder.setTitle("Update Item");
        mainbuilder.setCancelable(true);
        mainbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                ItemClass newItem = new ItemClass(itemClass.getItemId(), etItemName.getText().toString(), Double.parseDouble(etItemPrice.getText().toString()));
                savedCommodities.getAdapter().updateItem(newItem);
            }
        });
        mainbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        mainAlert = mainbuilder.create();
        etItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mainAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        mainAlert.show();
    }

}
