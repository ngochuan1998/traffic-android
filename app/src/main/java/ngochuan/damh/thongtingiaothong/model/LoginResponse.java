package ngochuan.damh.thongtingiaothong.model;

public class LoginResponse {

    public boolean success;

    public  User user;

    public LoginResponse() {
        this.success = false;
        this.user = null;
    }
}
