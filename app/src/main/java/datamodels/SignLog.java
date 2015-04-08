package datamodels;

/**
 * Created by Shamyyoun on 3/28/2015.
 */
public class SignLog {
    public static final int TYPE_SIGN_IN = 1;
    public static final int TYPE_SIGN_OUT = 2;

    private int id;
    private String name;
    private String password;
    private String userId;
    private String day;
    private String time;
    private int type;

    public SignLog(String userId, int type) {
        this.userId = userId;
        this.type = type;
    }

    public SignLog(String name, String password, String userId, String day, String time, int type) {
        this.name = name;
        this.password = password;
        this.userId = userId;
        this.day = day;
        this.time = time;
        this.type = type;

        // set default id
        id = -1;
    }

    public SignLog setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SignLog setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SignLog setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public SignLog setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getDay() {
        return day;
    }

    public SignLog setDay(String day) {
        this.day = day;
        return this;
    }

    public String getTime() {
        return time;
    }

    public SignLog setTime(String time) {
        this.time = time;
        return this;
    }

    public SignLog setType(int type) {
        this.type = type;
        return this;
    }

    public int getType() {
        return type;
    }
}
