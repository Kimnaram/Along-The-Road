package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MEMBER_LoginActivity extends AppCompatActivity {

    private Button btn_login;

    private TextView tv_go_register;

    private EditText ed_email_field;
    private EditText ed_pw_field;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__login);

        firebaseAuth = FirebaseAuth.getInstance();

        //버튼 등록하기
        tv_go_register = findViewById(R.id.tv_go_register);
        btn_login = findViewById(R.id.btn_login);
        ed_email_field = findViewById(R.id.ed_email_field);
        ed_pw_field = findViewById(R.id.ed_pw_field);

        //가입 버튼이 눌리면
        tv_go_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //intent함수를 통해 register액티비티 함수를 호출한다.
                startActivity(new Intent(getApplicationContext(), MEMBER_RegisterActivity.class));

            }
        });

        //로그인 버튼이 눌리면
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = ed_email_field.getText().toString().trim();
                String pwd = ed_pw_field.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(MEMBER_LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "로그인 오류", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });
    }

}