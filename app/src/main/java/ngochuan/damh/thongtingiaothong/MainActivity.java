package ngochuan.damh.thongtingiaothong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ngochuan.damh.thongtingiaothong.Retrofit.IMyService;
import ngochuan.damh.thongtingiaothong.Retrofit.RetrofitClient;
import ngochuan.damh.thongtingiaothong.model.User;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    Button tomap;
    EditText id, password;
    TextView reg;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    User user;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);

        tomap = (Button) findViewById(R.id.login);
        tomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(id.getText().toString(),
                        password.getText().toString());
            }
        });
        reg = (TextView) findViewById(R.id.reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View register_layout = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.register_layout, null);

                new MaterialStyledDialog.Builder(MainActivity.this)
                        .setTitle("ĐĂNG KÝ")
                        .setDescription("Vui lòng điền đủ thông tin")
                        .setCustomView(register_layout)
                        .setNegativeText("Hủy")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Xác nhận")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText id = (EditText) register_layout.findViewById(R.id.id);
                                EditText password = (EditText) register_layout.findViewById(R.id.password);
                                EditText name = (EditText) register_layout.findViewById(R.id.name);

                                if (TextUtils.isEmpty(id.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "ID cannot be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (TextUtils.isEmpty(password.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Password cannot be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (TextUtils.isEmpty(name.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Name cannot be null", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                registerUser(id.getText().toString(),
                                        password.getText().toString(),
                                        name.getText().toString());
                            }
                        }).show();
            }
        });
    }

    private void registerUser(String id, String password, String name) {
        compositeDisposable.add(iMyService.registerUser(id, password, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String respone) throws Exception {
                        Toast.makeText(MainActivity.this, "" + respone, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loginUser(String id, String password) {
        this.user = new User(id);
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID cannot be null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Pass cannot be null", Toast.LENGTH_SHORT).show();
            return;
        }
        compositeDisposable.add(
                iMyService.loginUser(id, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String respone) throws Exception {
                                Toast.makeText(MainActivity.this, "" + respone, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        })
        );
    }
}
