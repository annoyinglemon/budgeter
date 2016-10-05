package com.lemond.kurt.budgeter;


import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lemond.kurt.budgeter.DataBase.DatabaseAdapter;
import com.lemond.kurt.budgeter.Fragments.ExpensesCalendar;
import com.lemond.kurt.budgeter.Fragments.SavedCommodities;
import com.lemond.kurt.budgeter.Fragments.Settings;
import com.lemond.kurt.budgeter.Fragments.Statistics;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.CurrentMonthExpenses_Pager;
import com.lemond.kurt.budgeter.PagerAndPagerAdapters.SalaryBudget_Pager;
import com.lemond.kurt.budgeter.Services.AlarmReceiver;
import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;
import com.lemond.kurt.budgeter.Utilities.SingleMediaScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SalaryBudget_Pager.OnFragmentInteractionListener,
        CurrentMonthExpenses_Pager.OnFragmentInteractionListener, SavedCommodities.OnFragmentInteractionListener,
        ExpensesCalendar.OnFragmentInteractionListener, Statistics.OnFragmentInteractionListener,
        Settings.OnFragmentInteractionListener {

    private int selectedMenu = 0;
    SettingsManager settingsManager;
    FragmentManager manager;
    Toolbar mToolbar;
    DatabaseAdapter dbAdapter;
    NavigationView navigationView;
    private AppBarLayout appbarlayout;
    AlarmReceiver alarmReceiver;
    private final int FILE_DIRECTORY_PICKER = 1, FILE_DB_PICKER = 2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appbarlayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // check if for phone or tablet landscape
        if (findViewById(R.id.drawer_layout) != null) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    invalidateOptionsMenu();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert getCurrentFocus() != null;
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    super.onDrawerSlide(drawerView, slideOffset);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        ImageView ivNavPattern = (ImageView) header.findViewById(R.id.ivNavPattern);
        Random random = new Random();
        switch (random.nextInt(5)) {
            case 0:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater1));
                break;
            case 1:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater2));
                break;
            case 2:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater3));
                break;
            case 3:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater4));
                break;
            case 4:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater5));
                break;
            default:
                ivNavPattern.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.repeater4));
        }


        navigationView.setNavigationItemSelectedListener(this);
        manager = getSupportFragmentManager();
        dbAdapter = new DatabaseAdapter(this);
        if (savedInstanceState == null) {
            //set number of day on the current month's number of days
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            settingsManager = new SettingsManager(MainActivity.this);

            if (getIntent().getStringExtra("current_date").isEmpty())
                settingsManager.setCurrentDate(df.format(new Date()));
            else
                settingsManager.setCurrentDate(getIntent().getStringExtra("current_date"));
            settingsManager.setNumberOfDays(new DateUtilities(settingsManager.getCurrentDate()).getCurrentMonthNumberOfDays());
            settingsManager.setMockNumberOfDays(new DateUtilities(settingsManager.getCurrentDate()).getCurrentMonthNumberOfDays());
            navigationView.getMenu().getItem(selectedMenu).setTitle("➤   "+navigationView.getMenu().getItem(selectedMenu).getTitle());
            manager.beginTransaction().replace(R.id.mainContent, new CurrentMonthExpenses_Pager(), "CurrentMonthExpenses").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Expenses");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                setAppbarlayoutElevation(0);
        } else {
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(savedInstanceState.getString("Actionbar_title"));
            if(selectedMenu==0&&getSupportActionBar().getTitle().toString().equals("Expenses"))
                navigationView.getMenu().getItem(selectedMenu).setTitle("➤   "+navigationView.getMenu().getItem(selectedMenu).getTitle());
            else {
                navigationView.getMenu().getItem(savedInstanceState.getInt("selectedMenu")).setTitle("➤   " + navigationView.getMenu().getItem(savedInstanceState.getInt("selectedMenu")).getTitle());
                selectedMenu = savedInstanceState.getInt("selectedMenu");
            }
        }

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmUp) {
            alarmReceiver = new AlarmReceiver();
            alarmReceiver.setAlarm(this);
        }


    }

    //interval between two back button press
    private static final int TIME_INTERVAL = 2000;
    private long timeBackPressed;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        SalaryBudget_Pager salaryBudget_pager = (SalaryBudget_Pager) manager.findFragmentByTag("SalaryBudget");
        CurrentMonthExpenses_Pager currentMonthExpenses_pager = (CurrentMonthExpenses_Pager) manager.findFragmentByTag("CurrentMonthExpenses");
        SavedCommodities savedCommodities = (SavedCommodities) manager.findFragmentByTag("SavedCommodities");
        ExpensesCalendar expensesCalendar = (ExpensesCalendar) manager.findFragmentByTag("Calendar");
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentMonthExpenses_pager != null && currentMonthExpenses_pager.getSearchView() != null && !currentMonthExpenses_pager.getSearchView().isIconified()) {
            currentMonthExpenses_pager.getSearchView().setIconified(true);
        } else if (salaryBudget_pager != null && salaryBudget_pager.getSearchView() != null && !salaryBudget_pager.getSearchView().isIconified()) {
            salaryBudget_pager.getSearchView().setIconified(true);
        } else if (savedCommodities != null && savedCommodities.getSearchView() != null && !savedCommodities.getSearchView().isIconified()) {
            savedCommodities.getSearchView().setIconified(true);
        } else if (expensesCalendar != null && expensesCalendar.getDrawer() != null && expensesCalendar.getDrawer().isDrawerOpen(GravityCompat.END)) {
            expensesCalendar.getDrawer().closeDrawer(GravityCompat.END);
        } else {
            if (timeBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            }
            timeBackPressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Actionbar_title", getSupportActionBar().getTitle().toString());
        outState.putInt("selectedMenu", selectedMenu);

        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //replace fragment on which item the user clicks
        if (id == R.id.current_month_expenses) {
            manager.beginTransaction().replace(R.id.mainContent, new CurrentMonthExpenses_Pager(), "CurrentMonthExpenses").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Expenses");
            menuSelect(0);
        } else if (id == R.id.salary_budget) {
            manager.beginTransaction().replace(R.id.mainContent, new SalaryBudget_Pager(), "SalaryBudget").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Budget");
            menuSelect(1);
        } else if (id == R.id.saved_commodities) {
            manager.beginTransaction().replace(R.id.mainContent, new SavedCommodities(), "SavedCommodities").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Saved Items");
            menuSelect(2);
        } else if (id == R.id.nav_calendar) {
            manager.beginTransaction().replace(R.id.mainContent, new ExpensesCalendar(), "Calendar").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Expenses Calendar");
            menuSelect(3);
        } else if (id == R.id.nav_statistics) {
            manager.beginTransaction().replace(R.id.mainContent, new Statistics(), "Statistics").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Budget Statistics");
            menuSelect(4);
        } else if (id == R.id.nav_settings) {
            manager.beginTransaction().replace(R.id.mainContent, new Settings(), "Settings").commit();
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle("Settings");
            menuSelect(5);
        }
//        else if (id == R.id.nav_db) {
//            importExportDatabase();
//        }

        if (findViewById(R.id.drawer_layout) != null) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    void menuSelect(int menuItemPosition){
        navigationView.getMenu().getItem(selectedMenu).setTitle(navigationView.getMenu().getItem(selectedMenu).getTitle().toString().substring(4));
        navigationView.getMenu().getItem(menuItemPosition).setTitle("➤   " + navigationView.getMenu().getItem(menuItemPosition).getTitle());
        selectedMenu = menuItemPosition;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // can be left empty
    }

    protected void importExportDatabase() {
        CharSequence[] actions = new CharSequence[]{"Backup Database", "Import Database"};

        AlertDialog.Builder chooseActionBuilder = new AlertDialog.Builder(this);
        chooseActionBuilder.setTitle("Choose Action");
        chooseActionBuilder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, FileDirectoryPickerActivity.class);
                        intent.putExtra(FileDirectoryPickerActivity.MODE, FileDirectoryPickerActivity.MODE_DIRECTORY);
                        intent.putExtra(FileDirectoryPickerActivity.FILE_TYPE, ".db");
                        startActivityForResult(intent, FILE_DIRECTORY_PICKER);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this, FileDirectoryPickerActivity.class);
                        intent2.putExtra(FileDirectoryPickerActivity.MODE, FileDirectoryPickerActivity.MODE_FILE);
                        intent2.putExtra(FileDirectoryPickerActivity.FILE_TYPE, ".db");
                        startActivityForResult(intent2, FILE_DB_PICKER);
                        break;
                }
            }
        });
        chooseActionBuilder.show();
    }

    AlertDialog dialogBackup;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_DIRECTORY_PICKER:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    final String result = res.getString("path");

                    View edittext = LayoutInflater.from(MainActivity.this).inflate(R.layout.edittext_only, null);
                    final EditText editText2 = (EditText) edittext.findViewById(R.id.edittext);
                    editText2.setText("budgeter_db_" + new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date()));
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("File Name");
                    builder.setView(edittext);
                    builder.setPositiveButton("Create Backup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String myString = editText2.getText().toString().trim().replaceAll("[^a-zA-Z0-9.-]", "_");
                            final File outputFile = new File(result, myString + ".db");
                            final File dbFile = MainActivity.this.getDatabasePath(DatabaseAdapter.DBHelper.DATABASE_NAME);
                            if (outputFile.exists()) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setMessage("File " + myString + ".db already exists, overwrite?");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //todo execute copy asynctask here
                                        new CopyDatabase("backup").execute(outputFile, dbFile);
                                    }
                                });
                                builder1.setNegativeButton("New Filename", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogBackup.show();
                                    }
                                });
                                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder1.setCancelable(false);
                                builder1.show();
                            } else {
                                //todo execute copy asynctask here
                                new CopyDatabase("backup").execute(outputFile, dbFile);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBackup = builder.create();
                    dialogBackup.show();
                    editText2.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (editText2.getText().toString().trim().isEmpty())
                                dialogBackup.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                            else
                                dialogBackup.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        }
                    });
                }
                break;

            case FILE_DB_PICKER:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    final String result = res.getString("file");
                    new CheckDatabaseIfValid(new File(result)).execute();
                    /** INITIALIZE EMPTY DATABASE ***/

                }
                break;
        }
    }


    /***********************************
     * FOR DATABASE BACKUP
     ************************************/

    class CopyDatabase extends AsyncTask<File, Integer, String> {
        ProgressDialog mProgressDialog;
        File output, input;
        String mAction = "";

        /**
         * @param mAction must be equal to "backup" or "import"
         */
        CopyDatabase(String mAction) {
            this.mAction = mAction;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            if (mAction.equalsIgnoreCase("backup")) {
                mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CopyDatabase.this.cancel(true);
                    }
                });
                mProgressDialog.setMessage("Creating Backup");
            } else {
                mProgressDialog.setMessage("Importing Database");

            }
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(File... params) {
            String result = "failed";
            output = params[0];
            input = params[1];
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(params[1]);
                outputStream = new FileOutputStream(params[0]);
                byte[] buffer = new byte[1024];
                long total = 0;
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    total += length;
                    publishProgress((int) (total * 100 / input.length()));
                    outputStream.write(buffer, 0, length);
                }
                result = "success";
            } catch (FileNotFoundException exc) {
                result = "Exception: " + exc.getMessage();
            } catch (IOException exc) {
                result = "Exception: " + exc.getMessage();
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException ignored) {
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            if (s.equalsIgnoreCase("success")) {
                if (mAction.equalsIgnoreCase("backup")) {
                    Toast.makeText(MainActivity.this, "Backup is saved in " + output.getPath(), Toast.LENGTH_LONG).show();
                    if (output.exists())
                        new SingleMediaScanner(MainActivity.this, output);
                } else {
                    Toast.makeText(MainActivity.this, input.getName() + " is successfully imported.", Toast.LENGTH_LONG).show();
                    if (output.exists())
                        new SingleMediaScanner(MainActivity.this, output);
                    MainActivity.this.recreate();
                }
            } else if (s.contains("Exception: ")) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            } else {
                if (mAction.equalsIgnoreCase("backup")) {
                    Toast.makeText(MainActivity.this, "Backup failed.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Import failed.", Toast.LENGTH_LONG).show();
                }
            }
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
            if (output.exists()) {
                output.delete();
            }
            super.onCancelled();
        }
    }

    class CheckDatabaseIfValid extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog mProgressDialog;
        File dbFile;

        CheckDatabaseIfValid(File dbFile) {
            this.dbFile = dbFile;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Validating database file...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean valid = true;
            SQLiteDatabase sqLiteDatabase = null;
            try {
                sqLiteDatabase = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                if (sqLiteDatabase.isDatabaseIntegrityOk()) {
                    String[] column = {"name"};
                    String[] selectionArgs = {"table", "saved_items"};
                    for (int i = 0; i < 11; i++) {
                        switch (i) {
                            case 1:
                                selectionArgs[1] = "saved_items_salary_budget";
                                break;
                            case 2:
                                selectionArgs[1] = "saved_items_actual_expenses";
                                break;
                            case 3:
                                selectionArgs[1] = "saved_salary";
                                break;
                            case 4:
                                selectionArgs[1] = "saved_daily_salary";
                                break;
                            case 5:
                                selectionArgs[1] = "mock_saved_salary";
                                break;
                            case 6:
                                selectionArgs[1] = "mock_saved_daily_salary";
                                break;
                            case 7:
                                selectionArgs[1] = "monthly_savings";
                                break;
                            case 8:
                                selectionArgs[1] = "daily_savings";
                                break;
                            case 9:
                                selectionArgs[1] = "saved_loans";
                                break;
                            case 10:
                                selectionArgs[1] = "saved_mock_loans";
                                break;
                        }
                        Cursor cursor = sqLiteDatabase.query("sqlite_master", column, "type = ? AND name = ?", selectionArgs, null, null, null);
                        if (cursor.moveToFirst()) {
                            if (!cursor.getString(0).equalsIgnoreCase(selectionArgs[1])) {
                                valid = false;
                                break;
                            }
                        } else {
                            valid = false;
                        }
                        cursor.close();
                    }
                }
            } catch (Exception exc) {

            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
            return valid;
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            mProgressDialog.dismiss();
            //TODO: if db file is valid, create empty database if does not exist
            if (valid) {
                SQLiteDatabase checkDB = null;
                File internalDbpath = null;
                try {
                    internalDbpath = MainActivity.this.getDatabasePath(DatabaseAdapter.DBHelper.DATABASE_NAME);
                    checkDB = SQLiteDatabase.openDatabase(internalDbpath.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                } catch (Exception exc) {

                }
                if (checkDB != null) {
                    checkDB.close();
                } else {
                    new DatabaseAdapter.DBHelper(MainActivity.this).getReadableDatabase();
                }
                //TODO: copy db file to empty db
//            File dbFile = new File(result);
                new CopyDatabase("import").execute(internalDbpath, dbFile);
            } else {
                Toast.makeText(MainActivity.this, "Database file is invalid for importing.", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(valid);
        }
    }

}
