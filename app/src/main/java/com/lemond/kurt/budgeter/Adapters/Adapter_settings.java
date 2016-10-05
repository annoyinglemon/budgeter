package com.lemond.kurt.budgeter.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.ObjectClasses.ActualExpensesClass;
import com.lemond.kurt.budgeter.ObjectClasses.LoanClass;
import com.lemond.kurt.budgeter.R;
import com.lemond.kurt.budgeter.Fragments.Settings;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.G_Functions;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.util.ArrayList;

/**
 * Created by kurt_capatan on 2/12/2016.
 */
public class Adapter_settings extends BaseAdapter {

    private Settings mSettings;
    private ListView mSettingsList;
    private LayoutInflater mInflater;
    private Context mContext;
    private SettingsManager settingsManager;
    private Spinner currency, exceed;
    private TextView dailySavings, monthlySavings;
    private Switch settings_lenders_switch, settings_distribute_monthly_savings_switch;

    //planned budget
    private TextView mockDailySavings, mockMonthlySavings;
    private Switch settings_distribute_mock_monthly_savings_switch;

    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mAlertBuilder;

    private double vMonthly, vDaily, vMonthlyMock, vDailyMock;

    public Adapter_settings(Context context){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.settingsManager = new SettingsManager(mContext);
        vMonthly = settingsManager.getMonthlyBudget();
        vDaily = settingsManager.getDailyBudget();
        vMonthlyMock = settingsManager.getMockMonthlyBudget();
        vDailyMock = settingsManager.getMockDailyBudget();
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        mSettings = (Settings) fragmentManager.findFragmentByTag("Settings");
        mSettingsList = mSettings.getmListView();
        mSettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if(currency!= null)
                            currency.performClick();
                        break;
                    case 1:
                        if(exceed!= null)
                            exceed.performClick();
                        break;
                    case 2:
                        if(settings_lenders_switch!= null)
                            settings_lenders_switch.performClick();
                        break;
                    /**************ACTUAL EXPENSES*****************/
                    case 4:
                        if(monthlySavings!= null)
                            monthlySavings.performClick();
                        break;
                    case 5:
                        if(settings_distribute_monthly_savings_switch!= null)
                            settings_distribute_monthly_savings_switch.performClick();
                        break;
                    case 6:
                        if(dailySavings!= null)
                            if(!settings_distribute_monthly_savings_switch.isChecked())
                                dailySavings.performClick();
                        break;
                    /**************PLANNED BUDGET*****************/
                    case 8:
                        if(mockMonthlySavings!= null)
                            mockMonthlySavings.performClick();
                        break;
                    case 9:
                        if(settings_distribute_mock_monthly_savings_switch!= null)
                            settings_distribute_mock_monthly_savings_switch.performClick();
                        break;
                    case 10:
                        if(mockDailySavings!= null)
                            if(!settings_distribute_mock_monthly_savings_switch.isChecked())
                                mockDailySavings.performClick();
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position){
            case 0:
                convertView = mInflater.inflate(R.layout.settings_item_currency, parent,false);
                currency = (Spinner) convertView.findViewById(R.id.spinner_currency);
                String[] itemsCurrency = new String[]{"$ Dollar", "€ Euro", "£ Pound", "¥ Yen/Yuan", "₹ Rupee", "₨ Rupee",
                        "₩ Won", "₱ Peso", "₴ Hryvna", "₡ Colon", "₮ Tughrik", "\u20BA Lira", "฿ Baht", "₫ Dong", "₭ Kip",
                        "﷼ Rial/Riyal", "₪ Shekel"};
                ArrayAdapter<String> adapterCurrency = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, itemsCurrency);
                currency.setAdapter(adapterCurrency);
                String c = settingsManager.getCurrency();
                if(c.equals("$")){
                    currency.setSelection(0);
                }else if(c.equals("€")){
                    currency.setSelection(1);
                }else if(c.equals("£")){
                    currency.setSelection(2);
                }else if(c.equals("¥")){
                    currency.setSelection(3);
                }else if(c.equals("₹")){
                    currency.setSelection(4);
                }else if(c.equals("₨")){
                    currency.setSelection(5);
                }else if(c.equals("₩")){
                    currency.setSelection(6);
                }else if(c.equals("₱")){
                    currency.setSelection(7);
                }else if(c.equals("₴")){
                    currency.setSelection(8);
                }else if(c.equals("₡")){
                    currency.setSelection(9);
                }else if(c.equals("₮")){
                    currency.setSelection(10);
                }else if(c.equals("\u20BA")){
                    currency.setSelection(11);
                }else if(c.equals("฿")){
                    currency.setSelection(12);
                } else if(c.equals("₫")){
                    currency.setSelection(13);
                }else if(c.equals("₭")){
                    currency.setSelection(14);
                }else if(c.equals("﷼")){
                    currency.setSelection(15);
                }else if(c.equals("₪")){
                    currency.setSelection(16);
                }
                currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                            settingsManager.setCurrency("$");
                        else if(position==1)
                            settingsManager.setCurrency("€");
                        else if(position==2)
                            settingsManager.setCurrency("£");
                        else if(position==3)
                            settingsManager.setCurrency("¥");
                        else if(position==4)
                            settingsManager.setCurrency("₹");
                        else if(position==5)
                            settingsManager.setCurrency("₨");
                        else if(position==6)
                            settingsManager.setCurrency("₩");
                        else if(position==7)
                            settingsManager.setCurrency("₱");
                        else if(position==8)
                            settingsManager.setCurrency("₴");
                        else if(position==9)
                            settingsManager.setCurrency("₡");
                        else if(position==10)
                            settingsManager.setCurrency("₮");
                        else if(position==11)
                            settingsManager.setCurrency("\u20BA");
                        else if(position==12)
                            settingsManager.setCurrency("฿");
                        else if(position==13)
                            settingsManager.setCurrency("₫");
                        else if(position==14)
                            settingsManager.setCurrency("₭");
                        else if(position==15)
                            settingsManager.setCurrency("﷼");
                        else if(position==16)
                            settingsManager.setCurrency("₪");
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
            case 1:
                convertView = mInflater.inflate(R.layout.settings_item_exceed_expenses, parent,false);
                exceed = (Spinner) convertView.findViewById(R.id.spinner_exceed);
                String[] items = new String[]{"Ask First", "Always"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, items);
                exceed.setAdapter(adapter);
                String s = settingsManager.getExceedExpenses();
                if(s.equals("Ask First")){
                    exceed.setSelection(0);
                }else if(s.equals("Always")){
                    exceed.setSelection(1);
                }
                exceed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                            settingsManager.setExceedExpenses("Ask First");
                        else if(position==1)
                            settingsManager.setExceedExpenses("Always");
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
            case 2:
                convertView = mInflater.inflate(R.layout.settings_item_lenders, parent,false);
                settings_lenders_switch = (Switch) convertView.findViewById(R.id.settings_lenders_switch);
                if(settingsManager.isLenders()){
                    settings_lenders_switch.setChecked(true);
                }else {
                    settings_lenders_switch.setChecked(false);
                }
                settings_lenders_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settingsManager.setLenders(isChecked);
                    }
                });
                break;

            /**********************************ACTUAL EXPENSES********************************************/
            case 3:
                convertView = mInflater.inflate(R.layout.settings_item_header_actual_expenses, parent, false);
                break;
            case 4:
                convertView = mInflater.inflate(R.layout.settings_item_monthly_savings, parent,false);
                monthlySavings = (TextView) convertView.findViewById(R.id.settings_monthly_savings_amount);
                monthlySavings.setText(G_Functions.formatNumber(vMonthly));
                monthlySavings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View dialoglayout = mInflater.inflate(R.layout.settings_update_savings_dialog, null);
                        final EditText etSavings = (EditText) dialoglayout.findViewById(R.id.settings_dialog_update_savings);
                        etSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus)
                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        });
                        etSavings.setHint("Enter Monthly Budget");
                        etSavings.setText(String.format("%1$.2f", settingsManager.getMonthlyBudget()));
                        etSavings.setSelection(etSavings.getText().length());
                        mAlertBuilder = new AlertDialog.Builder(mContext);
                        mAlertBuilder.setView(dialoglayout);
                        mAlertBuilder.setTitle("Update Monthly Budget");
                        mAlertBuilder.setCancelable(true);
                        mAlertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                settingsManager.setMonthlyBudget(Double.parseDouble(etSavings.getText().toString().trim()));
                                vMonthly = settingsManager.getMonthlyBudget();
                                monthlySavings.setText(G_Functions.formatNumber(vMonthly));
                            }
                        });
                        mAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        mAlertDialog = mAlertBuilder.create();
                        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                etSavings.requestFocus();
                            }
                        });
                        mAlertDialog.show();

                    }
                });
                break;
            case 5:
                convertView = mInflater.inflate(R.layout.settings_item_monthly_distribute, parent, false);
                settings_distribute_monthly_savings_switch = (Switch) convertView.findViewById(R.id.settings_distribute_monthly_savings_switch);
                if(settingsManager.isDistributeMonthlySavings()){
                    settings_distribute_monthly_savings_switch.setChecked(true);
                }else {
                    settings_distribute_monthly_savings_switch.setChecked(false);
                }
                settings_distribute_monthly_savings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settingsManager.setDistributeMonthlySavings(isChecked);
                        if(dailySavings!=null) dailySavings.setEnabled(!isChecked);
                        if(isChecked){
                            Double monthly = G_Functions.parseNumber(monthlySavings.getText().toString().trim());
                            if(monthly!=0.00||monthly!=0.0) {
                                double total = 0.00;
                                total = total + vMonthly;
                                ArrayList<LoanClass> loans = new DatabaseAdapter(mContext).getAllLoans();
                                if(loans.size()>0) {
                                    for (LoanClass loan : loans) {total = total + loan.getLoanAmount();}
                                }
                                double totalMonthlyExpense = new DatabaseAdapter(mContext).getTotalExpensesBetweenDates(new DateUtilities(settingsManager.getCurrentDate()).getMonthDateBeginning(),
                                        new DateUtilities(settingsManager.getCurrentDate()).getMonthDateEnd(), ActualExpensesClass.MONTHLY);
                                total = total - totalMonthlyExpense;
                                vDaily = total / (double) settingsManager.getNumberOfDays();
                                if(dailySavings!=null) dailySavings.setText(G_Functions.formatNumber(vDaily));
                            }else {
                                if(dailySavings!=null) dailySavings.setText("0.00");
                            }
                            if(dailySavings!=null) dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                        }else{
                            if(dailySavings!=null) {
                                dailySavings.setText("0.0");
                                dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                            }
                        }
                    }
                });
                break;
            case 6:
                convertView = mInflater.inflate(R.layout.settings_item_daily_savings, parent,false);
                dailySavings = (TextView) convertView.findViewById(R.id.settings_daily_savings_amount);
                dailySavings.setText(G_Functions.formatNumber(vDaily));
                if(settingsManager.isDistributeMonthlySavings()) {
                    dailySavings.setEnabled(false);
                    dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                    dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                }else {
                    dailySavings.setEnabled(true);
                    dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                    dailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                }
                dailySavings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View dialoglayout = mInflater.inflate(R.layout.settings_update_savings_dialog, null);
                        final EditText etSavings = (EditText) dialoglayout.findViewById(R.id.settings_dialog_update_savings);
                        etSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus)
                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        });
                        etSavings.setHint("Enter Daily Budget");
                        etSavings.setText(String.format("%1$.2f", settingsManager.getDailyBudget()));
                        etSavings.setSelection(etSavings.getText().length());
                        mAlertBuilder = new AlertDialog.Builder(mContext);
                        mAlertBuilder.setView(dialoglayout);
                        mAlertBuilder.setTitle("Update Daily Budget");
                        mAlertBuilder.setCancelable(true);
                        mAlertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                settingsManager.setDailyBudget(Double.parseDouble(etSavings.getText().toString().trim()));
                                vDaily = settingsManager.getDailyBudget();
                                dailySavings.setText(G_Functions.formatNumber(vDaily));
                            }
                        });
                        mAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        mAlertDialog = mAlertBuilder.create();
                        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                etSavings.requestFocus();
                            }
                        });
                        mAlertDialog.show();

                    }
                });
                break;
            /******************************* PLANNED BUDGET *********************************/
            case 7:
                convertView = mInflater.inflate(R.layout.settings_item_header_planned_budget, parent, false);
                break;
            /*
            monthly budget
            distribute monthly budget
            number of days of distribution
            daily budget amount
             */
            case 8:
                convertView = mInflater.inflate(R.layout.settings_item_monthly_savings_mock, parent,false);
                mockMonthlySavings = (TextView) convertView.findViewById(R.id.settings_mock_monthly_savings_amount);
                mockMonthlySavings.setText(G_Functions.formatNumber(vMonthlyMock));
                mockMonthlySavings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View dialoglayout = mInflater.inflate(R.layout.settings_update_savings_dialog, null);
                        final EditText etSavings = (EditText) dialoglayout.findViewById(R.id.settings_dialog_update_savings);
                        etSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus)
                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        });
                        etSavings.setHint("Enter Monthly Budget");
                        etSavings.setText(String.format("%1$.2f", settingsManager.getMockMonthlyBudget()));
                        etSavings.setSelection(etSavings.getText().length());
                        mAlertBuilder = new AlertDialog.Builder(mContext);
                        mAlertBuilder.setView(dialoglayout);
                        mAlertBuilder.setTitle("Update Monthly Budget");
                        mAlertBuilder.setCancelable(true);
                        mAlertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                settingsManager.setMockMonthlyBudget(Double.parseDouble(etSavings.getText().toString().trim()));
                                vMonthlyMock = settingsManager.getMockMonthlyBudget();
                                mockMonthlySavings.setText(G_Functions.formatNumber(vMonthlyMock));
                            }
                        });
                        mAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        mAlertDialog = mAlertBuilder.create();
                        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                etSavings.requestFocus();
                            }
                        });
                        mAlertDialog.show();
                    }
                });
                break;
            case 9:
                convertView = mInflater.inflate(R.layout.settings_item_monthly_distribute_mock, parent, false);
                settings_distribute_mock_monthly_savings_switch = (Switch) convertView.findViewById(R.id.settings_distribute_mock_monthly_savings_switch);
                if(settingsManager.isDistributeMockMonthlyBudget()){
                    settings_distribute_mock_monthly_savings_switch.setChecked(true);
                }else {
                    settings_distribute_mock_monthly_savings_switch.setChecked(false);
                }
                settings_distribute_mock_monthly_savings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settingsManager.setDistributeMockMonthlyBudget(isChecked);
                        if (isChecked) {
                            Double monthly = G_Functions.parseNumber(mockMonthlySavings.getText().toString().trim());
                            if (monthly != 0.00 || monthly != 0.0) {
                                double total = 0.00;
                                total = total + vMonthlyMock;
                                ArrayList<LoanClass> loans = new DatabaseAdapter(mContext).getAllMockLoans();
                                if(loans.size()>0) {
                                    for (LoanClass loan : loans) {
                                        total = total + loan.getLoanAmount();
                                    }
                                }
                                double totalMonthlyExpense = new DatabaseAdapter(mContext).getTotalSavedItemsExpenses();
                                total = total - totalMonthlyExpense;
                                vDailyMock = total / (double) settingsManager.getMockNumberOfDays();
                                if (mockDailySavings!=null)
                                    mockDailySavings.setText(G_Functions.formatNumber(vDailyMock));
                            } else {
                                if (mockDailySavings!=null)
                                    mockDailySavings.setText("0.00");
                            }
                            if (mockDailySavings!=null)
                                mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                        } else {
                            if (mockDailySavings!=null)
                                mockDailySavings.setText("0.0");
                            if (mockDailySavings!=null)
                                mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));

                        }
                    }
                });
                break;
            case 10:
                convertView = mInflater.inflate(R.layout.settings_item_mock_daily_savings, parent,false);
                mockDailySavings = (TextView) convertView.findViewById(R.id.settings_mock_daily_savings_amount);
                mockDailySavings.setText(G_Functions.formatNumber(vDailyMock));
                if(settingsManager.isDistributeMockMonthlyBudget()) {
                    mockDailySavings.setEnabled(false);
                    mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                    mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.md_grey_500));
                }else {
                    mockDailySavings.setEnabled(true);
                    mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                    mockDailySavings.setTextColor(ContextCompat.getColor(mContext, R.color.textPrimary));
                }
                mockDailySavings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View dialoglayout = mInflater.inflate(R.layout.settings_update_savings_dialog, null);
                        final EditText etSavings = (EditText) dialoglayout.findViewById(R.id.settings_dialog_update_savings);
                        etSavings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if(hasFocus)
                                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        });
                        etSavings.setHint("Enter Daily Budget");
                        etSavings.setText(String.format("%1$.2f", settingsManager.getMockDailyBudget()));
                        etSavings.setSelection(etSavings.getText().length());
                        mAlertBuilder = new AlertDialog.Builder(mContext);
                        mAlertBuilder.setView(dialoglayout);
                        mAlertBuilder.setTitle("Update Daily Budget");
                        mAlertBuilder.setCancelable(true);
                        mAlertBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                settingsManager.setMockDailyBudget(Double.parseDouble(etSavings.getText().toString().trim()));
                                vDailyMock = settingsManager.getMockDailyBudget();
                                mockDailySavings.setText(G_Functions.formatNumber(vDailyMock));
                            }
                        });
                        mAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        mAlertDialog = mAlertBuilder.create();
                        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                etSavings.requestFocus();
                            }
                        });
                        mAlertDialog.show();

                    }
                });
                break;
        }
        return convertView;
    }

    public void saveSavingsSettings(){
        /******************************ACTUAL EXPENSES******************************/
        if(settingsManager.getMonthlyBudget()!=vMonthly)
            settingsManager.setMonthlyBudget(vMonthly);

        if(settingsManager.getDailyBudget()!=vDaily){
            settingsManager.setDailyBudget(vDaily);
        }


        /******************************PLANNED BUDGET********************************/
        if(settingsManager.getMockMonthlyBudget()!=vMonthlyMock)
            settingsManager.setMockMonthlyBudget(vMonthlyMock);

        if(settingsManager.getMockDailyBudget()!=vDailyMock) {
            settingsManager.setMockDailyBudget(vDailyMock);
        }
    }



}
