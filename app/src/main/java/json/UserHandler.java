package json;

import org.json.JSONException;
import org.json.JSONObject;

import datamodels.User;

public class UserHandler {
    private String response;

    public UserHandler(String response) {
        this.response = response;
    }

    public User handle() {
        try {
            JSONObject jsonObject = new JSONObject(response);
            User user = handleUser(jsonObject);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private User handleUser(JSONObject jsonObject) {
        User user;
        try {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String password = jsonObject.getString("password");
            String beaconId = jsonObject.getString("beacon_id");
            int beaconMajor = jsonObject.getInt("beacon_major");
            int beaconMinor = jsonObject.getInt("beacon_minor");

            user = new User(id)
                    .setName(name)
                    .setPassword(password)
                    .setBeaconId(beaconId)
                    .setBeaconMajor(beaconMajor)
                    .setBeaconMinor(beaconMinor);
        } catch (JSONException e) {
            user = null;
            e.printStackTrace();
        }

        return user;
    }
}
