package datamodels;

/**
 * Created by Shamyyoun on 3/17/2015.
 */
public class User {
    private String id;
    private String name;
    private String password;
    private String beaconId;
    private int beaconMajor;
    private int beaconMinor;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public User setBeaconId(String beaconId) {
        this.beaconId = beaconId;
        return this;
    }

    public int getBeaconMajor() {
        return beaconMajor;
    }

    public User setBeaconMajor(int beaconMajor) {
        this.beaconMajor = beaconMajor;
        return this;
    }

    public int getBeaconMinor() {
        return beaconMinor;
    }

    public User setBeaconMinor(int beaconMinor) {
        this.beaconMinor = beaconMinor;
        return this;
    }
}
