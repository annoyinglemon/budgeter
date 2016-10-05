package com.lemond.kurt.budgeter.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kurt_capatan on 7/27/2016.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;

    private PendingIntent updateSavingsIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()!=null&&(intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")||intent.getAction().equals("android.intent.action.TIME_SET"))) {
            cancelAlarm(context);
            setAlarm(context);
        }else{
            Intent service = new Intent(context, UpdateSavingsService.class);
            startWakefulService(context, service);
        }
    }
    /**
     * Sets a repeating alarm that runs once a day at approximately 11:49 p.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        updateSavingsIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 51);
        calendar.set(Calendar.SECOND, 0);
        // repeat alarm twice per day @ 11:51:00 p.m. (+5 minutes)
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, updateSavingsIntent);


        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(updateSavingsIntent);
            updateSavingsIntent.cancel();
        }
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
