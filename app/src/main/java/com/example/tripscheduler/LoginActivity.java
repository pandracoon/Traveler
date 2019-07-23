package com.example.tripscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  EditText emailEdit, passwordEdit;
  Button signButton, loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_login);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    emailEdit = findViewById(R.id.editText);
    emailEdit.setText("bob@gmail.com");
    passwordEdit = findViewById(R.id.editText2);
    passwordEdit.setText("1234");

    signButton = findViewById(R.id.button4);  //Todo: (단순 UI)로그인 버튼 활성화 시키기
    loginButton = findViewById(R.id.button3);

    loginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        login(email, password);
      }
    });

    signButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        signUp();

      }
    });

  }

  public void login(String email, String password) {
    compositeDisposable.add(iAppService.user_get_one(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .subscribe(new Consumer<String>() {
              @Override
              public void accept(String data) throws Exception {
                Log.e("user_get_one", data);

                if (data.equals("0")) {
                  System.out.println("No data existed");
                  Toast.makeText(getApplicationContext(), "해당하는 이메일 또는 비밀번호가 존재하지 않습니다.", Toast.LENGTH_SHORT)
                          .show();
                }

                else {
                  JsonParser jsonParser = new JsonParser();
                  JsonObject jsonObject = (JsonObject) jsonParser.parse(data);

                  Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                  loginIntent.putExtra("email", jsonObject.get("email").toString());
                  Toast.makeText(getApplicationContext(), jsonObject.get("name") + "님 환영합니다!", Toast.LENGTH_SHORT)
                          .show();
                  startActivity(loginIntent);
                  finish();
                }
              }
            }));
  }

  public void signUp() {
    Intent signUpIntent = new Intent(this, SignUpActivity.class);
    startActivity(signUpIntent);
  }

  @Override
  public void onStop() {
    Log.e("onStop", "Resource returned");
    compositeDisposable.clear();
    super.onStop();
  }
}
