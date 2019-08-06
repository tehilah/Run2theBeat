package com.example.run2thebeat;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FormatDateTimeDist {

    public static String getTimeOfDay(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        int day = c.get(Calendar.DAY_OF_WEEK);
        String dayOfMonthStr = getDay(day);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return dayOfMonthStr + " morning run";
        }else if(timeOfDay >= 12 && timeOfDay <= 16){
            return dayOfMonthStr + " afternoon run";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return dayOfMonthStr + " evening run";
        }else{
            return dayOfMonthStr + " night run";
        }
    }

    public static String getDay(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }

    public static String getTime(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)-(60* TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static String getDist(double dist) {
        return String.format(Locale.getDefault(), "%.04s",
                dist / 1000);
    }

    public static String getAvgPace(double minutes){
        int seconds = (int) ((minutes % 1) * 60);
        int min = (int) minutes;
        return String.format(Locale.getDefault(), "%d.%02d",min, seconds);
    }
}
