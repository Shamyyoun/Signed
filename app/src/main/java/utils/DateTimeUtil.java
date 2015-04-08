package utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Shamyyoun on 4/2/2015.
 */
public class DateTimeUtil {

    public static String getTimeInUserFormat() {
        return getTimeInUserFormat(Calendar.getInstance(Locale.getDefault()));
    }

    public static String getTimeInUserFormat(Calendar calendar) {
        String hour = "" + (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR));

        String minute = "" + calendar.get(Calendar.MINUTE);
        if (minute.length() == 1)
            minute = "0" + minute;

        String am = (calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");

        return hour + ":" + minute + " " + am;
    }
}
