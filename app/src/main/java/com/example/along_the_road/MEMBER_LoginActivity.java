package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MEMBER_LoginActivity extends AppCompatActivity {

    private Button btn_login;

    private TextView tv_go_register;
    private TextView tv_go_nonmember;
    private TextView tv_notification_wrong;

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
        tv_go_nonmember = findViewById(R.id.tv_go_nonmember);

        tv_notification_wrong = findViewById(R.id.tv_notification_wrong);

        btn_login = findViewById(R.id.btn_login);
        ed_email_field = findViewById(R.id.ed_email_field);
        ed_pw_field = findViewById(R.id.ed_pw_field);

        //가입 버튼이 눌리면
        tv_go_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MEMBER_RegisterActivity.class));

            }
        });

        tv_go_nonmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });

        //로그인 버튼이 눌리면
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(MEMBER_LoginActivity.this);
                mDialog.setMessage("로그인 중입니다.");
                mDialog.show();

                final String email = ed_email_field.getText().toString();
                final String pwd = ed_pw_field.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(MEMBER_LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mDialog.dismiss();

                                    Intent intent = new Intent(MEMBER_LoginActivity.this, MainActivity.class);
                                    startActivity(intent);

                                }else{
                                    mDialog.dismiss();
                                    tv_notification_wrong.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();


                                    // 파이어베이스 연동이 안 되어서 임시 조치
                                    Intent intent = new Intent(MEMBER_LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });
    }

}