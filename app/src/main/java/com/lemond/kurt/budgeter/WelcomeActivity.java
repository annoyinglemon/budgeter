package com.lemond.kurt.budgeter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lemond.kurt.budgeter.Utilities.DateUtilities;
import com.lemond.kurt.budgeter.Utilities.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CustomViewPagerAdapter vpAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button bnPrevious, btnNext;
    private SettingsManager mSettings;
    private EditText etMonthly, etDaily;
    private ImageView ivMoreInfo;
    private CheckBox cbDistri;
    private TextInputLayout tilDaily;
    int[] nav_color;
    private Bundle mSavedInstanceState;
    private String etMonthlyText = "", etDailyText = "";
    private boolean cbDistriChecked = false;
    private RelativeLayout purse1, purse2, purse3;
    private boolean position3_firstTime = true, position4_firstTime = true, position1_firstTime = true, position2_firstTime = true;
    private ImageView money_0, money_45, money_90;
    private TextView slide4_title, slide4_desc;
    int page_selected = -1;
    private TextView slide2_title, slide2_desc;
    private TextView slide3_title, slide3_desc;
    private TextView slide1_title;
    private LinearLayout slide1_desc;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //TODO: use settings manager for checking first time open
        // Checking for first time launch - before calling setContentView()
        mSettings = new SettingsManager(this);
        if (!mSettings.isFirstTimeAppLaunched()) {
            launchHomeScreen();
            finish();
        }


        setContentView(R.layout.activity_welcome);

        nav_color = getResources().getIntArray(R.array.nav_color);

        setNavColor(nav_color[0]);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        viewPager = (ViewPager) findViewById(R.id.vpWelcome);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        bnPrevious = (Button) findViewById(R.id.bnPrevious);
        btnNext = (Button) findViewById(R.id.bnNext);

        bnPrevious.setVisibility(View.GONE);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide0,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide1,
                R.layout.welcome_slide4};


        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();


        vpAdapter = new CustomViewPagerAdapter();
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setAdapter(vpAdapter);

        bnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              // checking for last page
                // if last page home screen will be launched
                int current = viewPager.getCurrentItem()-1;
                if (current >= 0) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = viewPager.getCurrentItem() + 1;
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("etMonthly", etMonthlyText);
        outState.putString("etDaily", etDailyText);
        outState.putBoolean("cbDistri", cbDistriChecked);
        super.onSaveInstanceState(outState);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        if(!mSettings.isFirstTimeAppLaunched()){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("current_date", "");
            startActivity(intent);
            finish();
            WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else {
            mSettings.setIsFirstTimeAppLaunched(false);
            mSettings.setDistributeMonthlySavings(cbDistri.isChecked());
            mSettings.setDistributeMockMonthlyBudget(cbDistri.isChecked());
            if (etMonthlyText.trim().isEmpty()) {
                mSettings.setMonthlyBudget(0.0);
                mSettings.setMockMonthlyBudget(0.0);
            } else {
                mSettings.setMonthlyBudget(Double.parseDouble(etMonthlyText.trim()));
                mSettings.setMockMonthlyBudget(Double.parseDouble(etMonthlyText.trim()));
            }
            if (etDailyText.trim().isEmpty() && !cbDistriChecked) {
                mSettings.setDailyBudget(0.0);
                mSettings.setMockDailyBudget(0.0);
            } else {
                if (!etMonthlyText.trim().isEmpty() && Double.parseDouble(etMonthlyText.trim()) > 0 && cbDistriChecked) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    mSettings.setDailyBudget(Double.parseDouble(etMonthlyText.trim()) / new DateUtilities(simpleDateFormat.format(new Date())).getCurrentMonthNumberOfDays());
                    mSettings.setMockDailyBudget(Double.parseDouble(etMonthlyText.trim()) / new DateUtilities(simpleDateFormat.format(new Date())).getCurrentMonthNumberOfDays());
                } else if (!etDailyText.trim().isEmpty() && !cbDistriChecked) {
                    mSettings.setDailyBudget(Double.parseDouble(etDailyText));
                    mSettings.setMockDailyBudget(Double.parseDouble(etDailyText));
                } else {
                    mSettings.setDailyBudget(0.0);
                    mSettings.setMockDailyBudget(0.0);
                }
            }
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("current_date", "");
            startActivity(intent);
            finish();
            WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            page_selected = position;
            setNavColor(nav_color[position]);

            if (position == 0) {
                bnPrevious.setVisibility(View.GONE);
            } else if (position == layouts.length - 1) {
                // last page. make button text to Start!
                btnNext.setText("Start!");
                bnPrevious.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText("Next");
                bnPrevious.setVisibility(View.VISIBLE);
            }
            if(position1_firstTime &&position==1&&slide2_title!=null&&slide2_desc!=null){
                final Animation animation2 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear_faster);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide2_desc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide2_desc.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                final Animation animation1 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear_faster);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide2_title.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide2_title.setVisibility(View.VISIBLE);
                        slide2_desc.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                slide2_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slide2_title.startAnimation(animation1);
                    }
                }, 500);
                position1_firstTime = false;
            }
            else if(position2_firstTime&&position==2&&slide3_title!=null&&slide3_desc!=null){
                final Animation animation2 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear_faster);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide3_desc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide3_desc.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                final Animation animation1 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear_faster);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide3_title.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide3_title.setVisibility(View.VISIBLE);
                        slide3_desc.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                slide3_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slide3_title.startAnimation(animation1);
                    }
                }, 500);
                position2_firstTime = false;
            }
            else if(position3_firstTime &&position==3&&purse1!=null&&purse2!=null&&purse3!=null&&slide1_title!=null&&slide1_desc!=null) {
                ArrayList<ImageView> ivList = new ArrayList<>();
                for (int i = purse1.getChildCount()-1; i >= 0; i--) {
                    ImageView child = (ImageView) purse1.getChildAt(i);
                    if(i!=purse1.getChildCount()-1){
                        child.bringToFront();
                    }
                    ivList.add(child);
                }
                coinDropAnimate(ivList);
                ArrayList<ImageView> ivList2 = new ArrayList<>();
                for (int i = purse2.getChildCount()-1; i >= 0; i--) {
                    ImageView child = (ImageView) purse2.getChildAt(i);
                    if(i!=purse2.getChildCount()-1){
                        child.bringToFront();
                    }
                    ivList2.add(child);
                }
                coinDropAnimate(ivList2);
                ArrayList<ImageView> ivList3 = new ArrayList<>();
                for (int i = purse3.getChildCount()-1; i >= 0; i--) {
                    ImageView child = (ImageView) purse3.getChildAt(i);
                    if(i!=purse3.getChildCount()-1){
                        child.bringToFront();
                    }
                    ivList3.add(child);
                }
                coinDropAnimate(ivList3);
                final Animation animation2 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade_in);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide1_desc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide1_desc.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                final Animation animation1 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear_faster);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide1_title.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide1_title.setVisibility(View.VISIBLE);
                        slide1_desc.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                slide1_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slide1_title.startAnimation(animation1);
                    }
                }, 500);
                position3_firstTime = false;
            }else if(position4_firstTime &&position==4&&money_45!=null&&money_90!=null&&slide4_title!=null&&slide4_desc!=null){
                money_45.animate().rotation(-45).setDuration(300);
                money_90.animate().rotation(-90).setDuration(500);
                final Animation animation2 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide4_desc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide4_desc.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                final Animation animation1 = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.text_appear);
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slide4_title.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slide4_title.setVisibility(View.VISIBLE);
                        slide4_desc.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                slide4_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slide4_title.startAnimation(animation1);
                    }
                }, 700);
                position4_firstTime = false;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        private void coinDropAnimate(ArrayList<ImageView> ivList){
            for(int i = ivList.size()-1; i >= 0 ; i--) {
                final ImageView view = ivList.get(i);

                // We calculate the delay for this Animation, each animation starts 100ms
                // after the previous one
                int delay = i * 100;
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageResource(R.drawable.coin);
                        Animation animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.drop_down);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                view.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        view.startAnimation(animation);
                    }
                }, delay);
            }
        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Making navigation bar transparent
     */
    private void setNavColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(color);
        }
    }

    /**
     * View pager adapter
     */
    public class CustomViewPagerAdapter extends PagerAdapter {


        private LayoutInflater layoutInflater;

        public CustomViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);


            if(position == 1){
                slide2_title = (TextView) view.findViewById(R.id.slide2_title);
                slide2_desc = (TextView) view.findViewById(R.id.slide2_desc);
                if (!position1_firstTime) {
                    slide2_title.setVisibility(View.VISIBLE);
                    slide2_desc.setVisibility(View.VISIBLE);
                }
            }
            else if(position == 2){
                slide3_title = (TextView) view.findViewById(R.id.slide3_title);
                slide3_desc = (TextView) view.findViewById(R.id.slide3_desc);
                if (!position2_firstTime) {
                    slide3_title.setVisibility(View.VISIBLE);
                    slide3_desc.setVisibility(View.VISIBLE);
                }
            }
            else if (position == 3) {

                slide1_title = (TextView) view.findViewById(R.id.slide1_title);
                slide1_desc = (LinearLayout) view.findViewById(R.id.slide1_desc);
                purse1 = (RelativeLayout) view.findViewById(R.id.purse1);
                purse2 = (RelativeLayout) view.findViewById(R.id.purse2);
                purse3 = (RelativeLayout) view.findViewById(R.id.purse3);

                if (!position3_firstTime){
                    for (int i = purse1.getChildCount()-1; i >= 0; i--) {
                        ImageView child = (ImageView) purse1.getChildAt(i);
                        if(i!=purse1.getChildCount()-1){
                            child.bringToFront();
                        }
                        child.setImageResource(R.drawable.coin);
                    }
                    for (int i = purse2.getChildCount()-1; i >= 0; i--) {
                        ImageView child = (ImageView) purse2.getChildAt(i);
                        if(i!=purse2.getChildCount()-1){
                            child.bringToFront();
                        }
                        child.setImageResource(R.drawable.coin);
                    }
                    for (int i = purse3.getChildCount()-1; i >= 0; i--) {
                        ImageView child = (ImageView) purse3.getChildAt(i);
                        if(i!=purse3.getChildCount()-1){
                            child.bringToFront();
                        }
                        child.setImageResource(R.drawable.coin);
                    }
                    slide1_title.setVisibility(View.VISIBLE);
                    slide1_desc.setVisibility(View.VISIBLE);
                }
                etMonthly = (EditText) view.findViewById(R.id.etMonthly);
                etDaily = (EditText) view.findViewById(R.id.etDaily);
                ivMoreInfo = (ImageView) view.findViewById(R.id.ivMoreInfo);
                cbDistri = (CheckBox) view.findViewById(R.id.cbDistri);
                tilDaily = (TextInputLayout) view.findViewById(R.id.tilDaily);

                etMonthly.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        etMonthlyText = etMonthly.getText().toString();
                    }
                });

                etDaily.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        etDailyText = etDaily.getText().toString();
                    }
                });

                cbDistri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cbDistriChecked = cbDistri.isChecked();
                    }
                });

                if (mSavedInstanceState != null) {

                    etMonthly = (EditText) view.findViewById(R.id.etMonthly);
                    etDaily = (EditText) view.findViewById(R.id.etDaily);
                    cbDistri = (CheckBox) view.findViewById(R.id.cbDistri);

                    etMonthlyText = mSavedInstanceState.getString("etMonthly");
                    etMonthly.setText(etMonthlyText);
                    etDailyText = mSavedInstanceState.getString("etDaily");
                    etDaily.setText(etDailyText);
                    cbDistriChecked = mSavedInstanceState.getBoolean("cbDistri");
                    cbDistri.setChecked(cbDistriChecked);
                    if (cbDistri.isChecked()) {
                        etDaily.setText("");
                        tilDaily.setAlpha(0f);
                    }

                }

                ivMoreInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                        builder.setMessage("Monthly budget will be divided by number of days in the current month. The quotient will be set as your daily budget.");
                        builder.setCancelable(true);
                        builder.show();
                    }
                });

                cbDistri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        etDaily.setEnabled(!isChecked);
                        if (!isChecked) {
                            tilDaily.animate().alpha(1).setDuration(350);
                        } else {
                            etDaily.setText("");
                            tilDaily.animate().alpha(0).setDuration(350);
                        }
                    }
                });

            } else if (position == 4){
                money_0 = (ImageView) view.findViewById(R.id.money_0);
                money_45 = (ImageView) view.findViewById(R.id.money_45);
                money_90 = (ImageView) view.findViewById(R.id.money_90);
                slide4_title = (TextView) view.findViewById(R.id.slide4_title);
                slide4_desc = (TextView) view.findViewById(R.id.slide4_desc);

                if (!position4_firstTime) {
//                RotateAnimation rotate_45 = new RotateAnimation(0, -45,  0f, 0f);
//                rotate_45.setDuration(300);
//                rotate_45.setFillAfter(true);
//                money_45.setAnimation(rotate_45);

//                RotateAnimation rotate_90 = new RotateAnimation(0, -90, 0f, 0f);
//                rotate_90.setDuration(400);
//                rotate_90.setFillAfter(true);
//                money_90.setAnimation(rotate_90);
                    money_45.animate().rotation(-45).setDuration(1);
                    money_90.animate().rotation(-90).setDuration(1);
                    slide4_title.setVisibility(View.VISIBLE);
                    slide4_desc.setVisibility(View.VISIBLE);
                }

            }


            if(etMonthly!=null)
                etMonthly.setText(etMonthlyText);
            if(etDaily!=null)
                etDaily.setText(etDailyText);
            if(cbDistri!=null)
                cbDistri.setChecked(cbDistriChecked);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            View view = (View) object;
//            container.removeView(view);
        }
    }
}


