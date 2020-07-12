package ngochuan.damh.thongtingiaothong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ngochuan.damh.thongtingiaothong.model.SharedData;
import ngochuan.damh.thongtingiaothong.model.User;

public class profile extends AppCompatActivity {
    TextView name,phone;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = SharedData.getInstance().user;
        setContentView(R.layout.activity_profile);

        name= (TextView) findViewById(R.id.name);
        phone= (TextView) findViewById(R.id.phone);

        if (user != null) {
            name.setText("Họ tên: "+ user.name);
            phone.setText("Số điện thoại: "+ user.id);
        }

    }
}
