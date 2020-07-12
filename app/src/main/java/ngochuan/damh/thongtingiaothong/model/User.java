package ngochuan.damh.thongtingiaothong.model;

public class User {

    public String id;
    public String name;
    public String type; // BTV, CTV

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

}



