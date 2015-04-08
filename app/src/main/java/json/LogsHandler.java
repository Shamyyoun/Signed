package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import datamodels.Log;

public class LogsHandler {
    private String response;

    public LogsHandler(String response) {
        this.response = response;
    }

    public Log[] handle() {
        try {
            JSONArray jsonArray = new JSONArray(response);
            Log[] logs = new Log[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log offer = handleLogs(jsonObject);

                logs[i] = offer;
            }
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Log handleLogs(JSONObject jsonObject) {
        Log log;
        try {
            String day = jsonObject.getString("day");
            String dayName = jsonObject.getString("day_name");
            String loginTime = jsonObject.getString("login_time");
            String logoutTime = jsonObject.getString("logout_time");
            boolean weekend = jsonObject.has("weekend") ? jsonObject.getBoolean("weekend") : false;
            boolean holiday = jsonObject.has("holiday") ? jsonObject.getBoolean("holiday") : false;
            boolean complete = jsonObject.has("complete") ? jsonObject.getBoolean("complete") : false;
            boolean missed = jsonObject.has("missed") ? jsonObject.getBoolean("missed") : false;

            log = new Log(day, dayName, loginTime, logoutTime)
                    .setWeekend(weekend)
                    .setHoliday(holiday)
                    .setComplete(complete)
                    .setMissed(missed);

        } catch (JSONException e) {
            log = null;
            e.printStackTrace();
        }

        return log;
    }
}
