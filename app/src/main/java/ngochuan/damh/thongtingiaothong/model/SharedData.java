package ngochuan.damh.thongtingiaothong.model;

import com.google.android.gms.common.data.DataHolder;

public class SharedData {

    public User user = null;

    private static final SharedData holder = new SharedData();
    public static SharedData getInstance() {return holder;}

}
