package ngochuan.damh.thongtingiaothong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ngochuan.damh.thongtingiaothong.model.User;

public class profile extends AppCompatActivity {
    TextView name,phone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        name= (TextView) findViewById(R.id.name);
//        phone= (TextView) findViewById(R.id.phone);
//        name.setText("Họ tên: "+ user.name);
//        phone.setText("Số điện thoại: "+ user.id);
    }
}
