package com.example.tripscheduler;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tripscheduler.Server.IAppService;
import com.example.tripscheduler.Server.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

  private final String SERVER = "http://143.248.36.205:3000";
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private IAppService iAppService;

  EditText nameEdit, emailEdit, passwordEdit, passCheckEdit;
  Button signUpButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_signup);

    Retrofit retrofitClient = RetrofitClient.getInstance();
    iAppService = retrofitClient.create(IAppService.class);

    Toolbar toolbar = findViewById(R.id.signUpToolBar);
    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("회원가입");

    signUpButton = findViewById(R.id.button);
    nameEdit = findViewById(R.id.editText3);
    emailEdit = findViewById(R.id.editText4);
    passwordEdit = findViewById(R.id.editText5);
    passCheckEdit = findViewById(R.id.editText6);

    signUpButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String name = nameEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String passCheck = passCheckEdit.getText().toString();

        signUp(name, email, password, passCheck);
      }
    });


  }

  @Override
  public boolean onOptionsItemSelected(
      @NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void signUp(String name, String email, String password, String passCheck) {

    if (!password.equals(passCheck)) {
      Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인이 다릅니다.", Toast.LENGTH_SHORT)
          .show();
    } else {
      compositeDisposable.add(iAppService.user_insert_one(email, password, name)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .retry()
              .subscribe(new Consumer<String>() {
                @Override
                public void accept(String data) throws Exception {
                  Log.e("user_insert", data);
                }
              }));


      Toast.makeText(getApplicationContext(), "회원가입에 성공하었습니다.", Toast.LENGTH_SHORT)
          .show();
      finish();
    }


  }
}
