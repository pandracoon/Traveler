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

public class LoginActivity extends AppCompatActivity {

  EditText emailEdit, passwordEdit;
  Button signButton, loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_login);

    emailEdit = findViewById(R.id.editText);
    emailEdit.setText("whworjs7946@korea.ac.kr");
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

  public void login(String email, String password) {//Todo: 서버랑 연결해서 수정해야할 부분

    if (email.equals("whworjs7946@korea.ac.kr")) {
      if (password.equals("1234")) {
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.putExtra("email", email);
        Toast.makeText(getApplicationContext(), email + "님 환영합니다!", Toast.LENGTH_SHORT)
            .show();
        startActivity(loginIntent);
        finish();
      }
    } else {
      Toast.makeText(getApplicationContext(), "해당하는 이메일 또는 비밀번호가 존재하지 않습니다.", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void signUp() {
    Intent signUpIntent = new Intent(this, SignUpActivity.class);
    startActivity(signUpIntent);
  }

}
