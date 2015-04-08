package datamodels;

/**
 * Created by Shamyyoun on 3/17/2015.
 */
public class Log {
    private String day;
    private String dayName;
    private String loginTime;
    private String logoutTime;
    private boolean weekend;
    private boolean holiday;
    private boolean complete;
    private boolean missed;

    public Log() {
    }

    public Log(String day, String dayName, String loginTime, String logoutTime) {
        this.day = day;
        this.dayName = dayName;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    public String getDay() {
        return day;
    }

    public Log setDay(String day) {
        this.day = day;
        return this;
    }

    public String getDayName() {
        return dayName;
    }

    public Log setDayName(String dayName) {
        this.dayName = dayName;
        return this;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public Log setLoginTime(String loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public Log setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
        return this;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public Log setWeekend(boolean weekend) {
        this.weekend = weekend;
        return this;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public Log setHoliday(boolean holiday) {
        this.holiday = holiday;
        return this;
    }

    public boolean isComplete() {
        return complete;
    }

    public Log setComplete(boolean complete) {
        this.complete = complete;
        return this;
    }

    public boolean isMissed() {
        return missed;
    }

    public Log setMissed(boolean missed) {
        this.missed = missed;
        return this;
    }
}
