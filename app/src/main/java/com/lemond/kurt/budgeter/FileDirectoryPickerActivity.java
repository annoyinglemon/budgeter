package com.lemond.kurt.budgeter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FileDirectoryPickerActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private TextView tvTitleSelect, tvFileNamePath, tvFolderEmpty;
    private ImageButton bnFileOK, bnFileNew, bnBack;
    private RecyclerView rvFileDirectory;
    private FileDirectoryAdapter mAdapter;

    private int mMode = 0;
    private String mFiletype = ".db";
    public static final int MODE_DIRECTORY = 0, MODE_FILE = 1;
    public static final String MODE = "mode", FILE_TYPE = "file_type";

    private static final int READ_WRITE_EXTERNAL_STORAGE = 0;

    private SettingsManager mSettings;
    ArrayList<File> mFileList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_directory_picker);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mSettings = new SettingsManager(this);
        //test only
//        mSettings.setDatabasePath("/storage");

        tvTitleSelect = (TextView) findViewById(R.id.tvTitleSelect);
        tvFileNamePath = (TextView) findViewById(R.id.tvFileNamePath);
        tvFolderEmpty = (TextView) findViewById(R.id.tvFolderEmpty);
        bnFileOK = (ImageButton) findViewById(R.id.bnFileOK);
        bnFileNew = (ImageButton) findViewById(R.id.bnFileNew);
        bnBack = (ImageButton) findViewById(R.id.bnBack);
        rvFileDirectory = (RecyclerView) findViewById(R.id.rvFileDirectory);

        mMode = getIntent().getIntExtra(MODE, MODE_DIRECTORY);
        mFiletype = getIntent().getStringExtra(FILE_TYPE);

        //directory or file
        //file extension e.g .db, .txt, .csv
        if (mMode == MODE_DIRECTORY) {
            tvTitleSelect.setText("Select Directory");
        } else {
            tvTitleSelect.setText("Select .db File");
        }

        rvFileDirectory.setLayoutManager(new LinearLayoutManager(this));
//        rvFileDirectory.addItemDecoration(new G_ViewHolders().new DividerItemDecoration(this));

        rvFileDirectory.addOnItemTouchListener(new RecyclerTouchListener(this, rvFileDirectory));

        bnFileOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.notifyDataSetChanged();
                finishWithResult();
            }
        });

        bnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPath = tvFileNamePath.getText().toString().substring(0, tvFileNamePath.getText().toString().lastIndexOf("/"));
                rvFileDirectory.stopScroll();
                new GetFileLists().execute(newPath);
            }
        });

        bnFileNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View edittext = LayoutInflater.from(FileDirectoryPickerActivity.this).inflate(R.layout.edittext_only, null);
                final EditText editText2 = (EditText) edittext.findViewById(R.id.edittext);
                editText2.setText("new_folder");
                final AlertDialog.Builder builder = new AlertDialog.Builder(FileDirectoryPickerActivity.this);
                builder.setTitle("Folder Name");
                builder.setView(edittext);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String myString = editText2.getText().toString().trim().replaceAll("[^a-zA-Z0-9.-]", "_");
                        File newFolder = new File(tvFileNamePath.getText().toString() + "/" + myString);
                        if (!newFolder.mkdirs()) {
                            AlertDialog.Builder message = new AlertDialog.Builder(FileDirectoryPickerActivity.this);
                            message.setMessage("Folder " + myString + " already exists");
                            message.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            message.show();
                        } else {
                            if (rvFileDirectory.getVisibility() == View.GONE) {
                                rvFileDirectory.setVisibility(View.VISIBLE);
                            }
                            mFileList.add(0, newFolder);
                            mAdapter.notifyItemInserted(0);
                            rvFileDirectory.scrollToPosition(0);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
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
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                        else
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    }
                });
            }
        });

        File parentDir = new File(mSettings.getDatabasePath());
        if(!parentDir.exists()) {
            mSettings.setDatabasePath("/storage");
        }

        tvFileNamePath.setText(mSettings.getDatabasePath());

        mAdapter = new FileDirectoryAdapter();
        rvFileDirectory.setAdapter(mAdapter);

        new GetFileLists().execute(mSettings.getDatabasePath());

    }

    private void finishWithResult() {
        Bundle conData = new Bundle();
        conData.putString("path", tvFileNamePath.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void finishWithResult(String filePathName) {
        Bundle conData = new Bundle();
        conData.putString("file", filePathName);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        DialogInterface.OnClickListener nega = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        DialogInterface.OnClickListener posi = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(FileDirectoryPickerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_EXTERNAL_STORAGE);
                            }
                        };
//                        new GlobalFunctions().showAlertMessage(this, "This app needs access to your current location to give you directions to your chosen clinic/hospital. If you decline, this app will not able to generate routes and give directions. Tap 'Try again' to show the message one more time."
//                                , "Try again", posi, "I really decline", nega);
                        AlertDialog.Builder builder = new AlertDialog.Builder(FileDirectoryPickerActivity.this);
                        builder.setTitle("Permission Warning");
                        builder.setMessage("Declining to this permission prohibits you from importing database file from external storage and saving database file to your external storage. Tap 'Try again' to show permission message one more time.");
                        builder.setPositiveButton("Try Again", posi);
                        builder.setNegativeButton("I really decline", nega);
                        builder.setCancelable(false);
                        builder.show();
                    } else {
                        DialogInterface.OnClickListener settings = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myAppSettings = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivity(myAppSettings);
                            }
                        };
                        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(FileDirectoryPickerActivity.this);
                        builder.setMessage("You have denied the permission for accessing your external storage, please proceed to Settings and allow storage access.");
                        builder.setPositiveButton("Settings", settings);
                        builder.setNegativeButton("Cancel", cancel);
                        builder.setCancelable(false);
                        builder.show();
                    }
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class GetFileLists extends AsyncTask<String, File, Void> {

        @Override
        protected void onPreExecute() {
            rvFileDirectory.setVisibility(View.GONE);
            tvFolderEmpty.setVisibility(View.GONE);
            mFileList = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            File parentDir = new File(params[0]);
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if(params[0].equalsIgnoreCase("/storage")){
                        if(!file.getName().contains("emulated")&&file.listFiles()!=null&&file.listFiles().length>0){
                            publishProgress(file);
                        }
                    }else {
                        publishProgress(file);
                    }
                } else if (file.getName().endsWith(mFiletype) && mMode == MODE_FILE) {
                    publishProgress(file);
                }
            }
            mSettings.setDatabasePath(params[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(File... values) {
            mFileList.add(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void fileDirectoryAdapter) {
            mAdapter.notifyDataSetChanged();
            if (mSettings.getDatabasePath().equalsIgnoreCase("/storage")) {
                disableButton(bnBack);
                disableButton(bnFileNew);
                disableButton(bnFileOK);
            } else {
                if(mMode==MODE_DIRECTORY) {
                    enableButton(bnBack);
                    enableButton(bnFileNew);
                    enableButton(bnFileOK);
                } else {
                    enableButton(bnBack);
                    enableButton(bnFileNew);
                    disableButton(bnFileOK);
                }
            }
            if (mFileList.size() > 0) {
                rvFileDirectory.setVisibility(View.VISIBLE);
            } else {
                rvFileDirectory.setVisibility(View.GONE);
                if (mMode == MODE_DIRECTORY)
                    tvFolderEmpty.setText("This folder has no sub-folders.");
                else tvFolderEmpty.setText("This folder is empty.");
                tvFolderEmpty.setVisibility(View.VISIBLE);
            }
            tvFileNamePath.setText(mSettings.getDatabasePath());
            super.onPostExecute(fileDirectoryAdapter);
        }


    }

    void disableButton(ImageButton imageButton){
        imageButton.setBackground(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.filepicker_button_disabled));
        imageButton.setClickable(false);
    }

    void enableButton(ImageButton imageButton){
        imageButton.setBackground(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.filepicker_button_enabled));
        imageButton.setClickable(true);
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

//                    if (itemSelected > -1) {
//                        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(FileDirectoryPickerActivity.this, R.style.AppTheme_Dialog_Message);
//                        confirmDelete.setCancelable(true);
//                        confirmDelete.setPositiveButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        confirmDelete.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (mFileList.get(itemSelected).isDirectory()) {
//
//                                } else {
//
//                                }
//                            }
//                        });
//                        if (mFileList.get(itemSelected).isDirectory()) {
//
//                        }else{
//
//                        }
//                    }
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    itemSelected = recyclerView.getChildAdapterPosition(child);

                    if (itemSelected > -1) {
                        if (mFileList.get(itemSelected).isDirectory()) {
                            enterDirectory(itemSelected);
                        } else {
                            AlertDialog.Builder confirmFile = new AlertDialog.Builder(FileDirectoryPickerActivity.this, R.style.AppTheme_Dialog_Message);
                            confirmFile.setMessage("Import " + mFileList.get(itemSelected).getName() + "?");
                            confirmFile.setCancelable(true);
                            confirmFile.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishWithResult(mFileList.get(itemSelected).getAbsolutePath());
                                }
                            });
                            confirmFile.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            confirmFile.show();
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

        private void enterDirectory(int itemSelected) {
            if (!mSettings.getDatabasePath().equalsIgnoreCase("/storage") && !mFileList.get(itemSelected).getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                new GetFileLists().execute(mFileList.get(itemSelected).getAbsolutePath());
            }else{
                /*** REQUEST PERMISSION FOR EXTERNAL STORAGE ***/
                if (ActivityCompat.checkSelfPermission(FileDirectoryPickerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FileDirectoryPickerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FileDirectoryPickerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_WRITE_EXTERNAL_STORAGE);
                }else{
                    new GetFileLists().execute(mFileList.get(itemSelected).getAbsolutePath());
                }
            }
        }

//        private boolean deleteFileDirectory(File file){
//            try{
//                if(file.isDirectory()){
//                    File[] files = file.listFiles();
//                    if(files.length>0) {
//                        for (File f : files) {
//                            deleteFileDirectory(f);
//                        }
//                    }else {
//                        return file.delete();
//                    }
//                }else{
//                    return file.delete();
//                }
//            }catch (Exception exc){
//                return false;
//            }
//        }
    }


    private class FileDirectoryAdapter extends RecyclerView.Adapter<FileDirectoryAdapter.CustomViewHolder> {

        FileDirectoryAdapter() {

        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivFileDirectory;
            public TextView tvFileDirectoryName;
            public TextView tvFileDirectoryInfo;

            public CustomViewHolder(View view) {
                super(view);
                ivFileDirectory = (ImageView) view.findViewById(R.id.ivFileDirectory);
                tvFileDirectoryName = (TextView) view.findViewById(R.id.tvFileDirectoryName);
                tvFileDirectoryInfo = (TextView) view.findViewById(R.id.tvFileDirectoryInfo);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(FileDirectoryPickerActivity.this).inflate(R.layout.item_file_directory, parent, false);
            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            if (mSettings.getDatabasePath().equalsIgnoreCase("/storage")) {
                File fileDirectory = mFileList.get(position);
                if(fileDirectory.getName().toLowerCase().contains("sdcard")||fileDirectory.getName().toLowerCase().contains("sd_card")||fileDirectory.getName().toLowerCase().contains("card")){
                    holder.ivFileDirectory.setImageDrawable(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.ic_external));
                }else {
                    holder.ivFileDirectory.setImageDrawable(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.ic_folder));
                }
                holder.tvFileDirectoryInfo.setText(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(fileDirectory.lastModified())));
            } else {
                File fileDirectory = mFileList.get(position);
                if (fileDirectory.isDirectory()) {
                    holder.ivFileDirectory.setImageDrawable(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.ic_folder));
                    holder.tvFileDirectoryInfo.setText(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(fileDirectory.lastModified())));
                } else {
                    holder.ivFileDirectory.setImageDrawable(ContextCompat.getDrawable(FileDirectoryPickerActivity.this, R.drawable.ic_file));
                    String value = "";
                    long Filesize = fileDirectory.length() / 1024;//call function and convert bytes into Kb
                    if (Filesize >= 1024)
                        value = Filesize / 1024 + " Mb";
                    else
                        value = Filesize + " Kb";
                    holder.tvFileDirectoryInfo.setText(value);
                }
            }
            holder.tvFileDirectoryName.setText(mFileList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mFileList.size();
        }

        public int getPosition(File file){
            int count = -1;
            for (int i=0; i<mFileList.size(); i++) {
                if(mFileList.get(0)==file){
                    count = i;
                }
            }
            return count;
        }

        String getAvailableInternalStorage() {
            String storageSize = "";
            Log.d("Storage", Environment.getDataDirectory().getAbsolutePath());
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long bytesAvailable = stat.getFreeBlocksLong() * stat.getBlockSizeLong();
            long megAvailable = bytesAvailable / 1048576;
            if (megAvailable >= 1024)
                storageSize = megAvailable / 1024 + "." + megAvailable % 1024 + " Gb";
            else
                storageSize = megAvailable + "." + megAvailable % 1024 + " Mb";
            return storageSize;
        }

        String getTotalInternalStorage() {
            String storageSize = "";
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
            long megAvailable = bytesAvailable / 1048576;
            if (megAvailable >= 1024)
                storageSize = megAvailable / 1024 + "." + megAvailable % 1024 + " Gb";
            else
                storageSize = megAvailable + "." + megAvailable % 1024 + " Mb";
            return storageSize;
        }

        String getAvailableExternalStorage() {
            String storageSize = "";
            Log.d("Storage", Environment.getExternalStorageDirectory().getAbsolutePath());
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = stat.getFreeBlocksLong() * stat.getBlockSizeLong();
            long megAvailable = bytesAvailable / 1048576;
            if (megAvailable >= 1024)
                storageSize = megAvailable / 1024 + "." + megAvailable % 1024 + " Gb";
            else
                storageSize = megAvailable + "." + megAvailable % 1024 + " Mb";
            return storageSize;
        }

        String getTotalExternalStorage() {
            String storageSize = "";
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
            long megAvailable = bytesAvailable / 1048576;
            if (megAvailable >= 1024)
                storageSize = megAvailable / 1024 + "." + megAvailable % 1024 + " Gb";
            else
                storageSize = megAvailable + "." + megAvailable % 1024 + " Mb";
            return storageSize;
        }

    }

}
