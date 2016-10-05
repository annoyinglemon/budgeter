package com.lemond.kurt.budgeter.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by kurt_capatan on 2/4/2016.
 */
public class DateUtilities {
    private String currentDate;

    public static final int JANUARY = 31;
    public static final int FEBRUARY = 28;
    public static final int FEBRUARY_LEAP_YEAR = 29;
    public static final int MARCH = 31;
    public static final int APRIL = 30;
    public static final int MAY = 31;
    public static final int JUNE = 30;
    public static final int JULY = 31;
    public static final int AUGUST = 31;
    public static final int SEPTEMBER = 30;
    public static final int OCTOBER = 31;
    public static final int NOVEMBER = 30;
    public static final int DECEMBER = 31;


    public DateUtilities(String currentDate) {
        this.currentDate = currentDate;
    }

    private Calendar getCalendarForNow() {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = format.parse(currentDate);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException exc) {

        }
        return null;
    }

    public String getMonthDateBeginning() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(calendar.getTime());
    }

    public String getMonthDateEnd() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(calendar.getTime());
    }

    private void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public String getCurrentDate() {
        return currentDate;
    }


    public int getCurrentMonthNumberOfDays() {
        String month = this.currentDate.substring(5, 7);
        int year = Integer.parseInt(this.currentDate.substring(0, 4));
        switch (month) {
            case "01":
                return JANUARY;
            case "02":
                if (year % 4 == 0) {
                    return FEBRUARY_LEAP_YEAR;
                } else {
                    return FEBRUARY;
                }
            case "03":
                return MARCH;
            case "04":
                return APRIL;
            case "05":
                return MAY;
            case "06":
                return JUNE;
            case "07":
                return JULY;
            case "08":
                return AUGUST;
            case "09":
                return SEPTEMBER;
            case "10":
                return OCTOBER;
            case "11":
                return NOVEMBER;
            case "12":
                return DECEMBER;
            default:
                return 30;
        }
    }

    public int remainingCurrentMonthDays() {
        String currentDay = this.currentDate.substring(8);
        int dayCurrent = 0;
        if (currentDay.startsWith("0")) {
            dayCurrent = Integer.parseInt(currentDay.substring(1));
        } else {
            dayCurrent = Integer.parseInt(currentDay);
        }
        return getCurrentMonthNumberOfDays() - dayCurrent;
    }

    /**
     * converts sqlite-formatted date string
     * (yyyy-MM-dd) to readable format
     * (MMM dd, yyyy)
     *
     * @param dateStr date as string object
     *                with the format yyyy-MM-dd
     * @return Readable date string with the format
     * MMM dd, yyyy
     **/
    public static String makeReadableFormat(String dateStr) {
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
            final SimpleDateFormat readable_format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return readable_format.format(date1);
        } catch (ParseException exc) {
            return dateStr;
        }
    }

    public static String makeWholeDateFormat(String dateStr) {
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
            final SimpleDateFormat readable_format = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            return readable_format.format(date1);
        } catch (ParseException exc) {
            return dateStr;
        }
    }

    /**
     *
     * @param dateStr must be in yyyy-MM format
     * @return
     */
    public static String makeWholeDateFormatNoDate(String dateStr) {
        try {
            Date date1 = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(dateStr);
            final SimpleDateFormat readable_format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            return readable_format.format(date1);
        } catch (ParseException exc) {
            return dateStr;
        }
    }

    public static String makeWholeDateFormat(Date date) {
        final SimpleDateFormat readable_format = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return readable_format.format(date);
    }

    public static String makeSQLiteFormat(Date date) {
        final SimpleDateFormat readable_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return readable_format.format(date);
    }
}
