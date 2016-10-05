package com.lemond.kurt.budgeter.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

import com.lemond.kurt.budgeter.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kurt_capatan on 5/12/2016.
 */
public class G_Functions {

    public static int RandomColor(Context context){
        /*
            all colors are 500
            0 - Red
            1 - Pink
            2 - Purple
            3 - Blue
            4 - Cyan
            5 - Green
            6 - Orange
         */
        Random rand = new Random();
        int randomNumber = rand.nextInt(12); // 14.
        switch (randomNumber){
            case 0:
                return ContextCompat.getColor(context, R.color.md_pink_500);
            case 1:
                return ContextCompat.getColor(context, R.color.md_purple_500);
            case 2:
                return ContextCompat.getColor(context, R.color.md_deep_purple_500);
            case 3:
                return ContextCompat.getColor(context, R.color.md_indigo_500);
            case 4:
                return ContextCompat.getColor(context, R.color.md_blue_500);
            case 5:
                return ContextCompat.getColor(context, R.color.md_light_blue_500);
            case 6:
                return ContextCompat.getColor(context, R.color.md_cyan_500);
            case 7:
                return ContextCompat.getColor(context, R.color.md_green_500);
            case 8:
                return ContextCompat.getColor(context, R.color.md_lime_500);
            case 9:
                return ContextCompat.getColor(context, R.color.md_amber_500);
            case 10:
                return ContextCompat.getColor(context, R.color.md_orange_500);
            case 11:
                return ContextCompat.getColor(context, R.color.md_deep_orange_500);
            default:
                return ContextCompat.getColor(context, R.color.md_blue_grey_500);
        }
    }

    public static int dpToPx_xdpi(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static int pxToDp_xdpi(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static String formatNumber(double realNumber){
        try {
            String number = new DecimalFormat("#,##0.00").format(realNumber);
            if(!number.equals(".00")) return number;
            else return"0.00";
        }catch (Exception ex){
            return String.format("%1$.2f", realNumber);
        }
    }

    public static double parseNumber(String realNumber){
        try {
            return NumberFormat.getInstance().parse(realNumber).doubleValue();
        }catch (ParseException e){
            return 0.00;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void lockOrientation(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int height;
        int width;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            height = display.getHeight();
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }
        switch (rotation) {
            case Surface.ROTATION_90:
                if (width > height)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT/* reversePortait */);
                break;
            case Surface.ROTATION_180:
                if (height > width)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT/* reversePortait */);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE/* reverseLandscape */);
                break;
            case Surface.ROTATION_270:
                if (width > height)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE/* reverseLandscape */);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default :
                if (height > width)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void unlockOrientation(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
