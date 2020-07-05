package com.example.along_the_road;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MEMBER_RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MEMBER_RegisterActivity";

    private EditText ed_email_field;
    private EditText ed_username_field;
    private EditText ed_pw_field;
    private EditText ed_pwcheck_field;
    private Button btn_register_done;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__register);

        firebaseAuth =  FirebaseAuth.getInstance();

        ed_email_field = findViewById(R.id.ed_email_field);
        ed_pw_field = findViewById(R.id.ed_pw_field);
        ed_pwcheck_field = findViewById(R.id.ed_pwcheck_field);
        ed_username_field = findViewById(R.id.ed_username_field);

        btn_register_done = findViewById(R.id.btn_register_done);

        btn_register_done.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                final String email = ed_email_field.getText().toString().trim();
                String pwd= ed_pw_field.getText().toString().trim();
                String pwdcheck = ed_pwcheck_field.getText().toString().trim();


                if(pwd.equals(pwdcheck)) {
                    Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(MEMBER_RegisterActivity.this);
                    mDialog.setMessage("회원가입 중입니다.");
                    mDialog.show();

                    //파이어베이스에 신규계정 등록하기
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MEMBER_RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //가입 성공시
                            if (task.isSuccessful()) {
//                                mDialog.dismiss();
//
//                                FirebaseUser user = firebaseAuth.getCurrentUser();
//                                String email = user.getEmail();
//                                String uid = user.getUid();
//                                String name = ed_username_field.getText().toString().trim();
//
//                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
//                                HashMap<Object,String> hashMap = new HashMap<>();
//
//                                hashMap.put("uid",uid);
//                                hashMap.put("email",email);
//                                hashMap.put("name",name);
//
//                                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                DatabaseReference reference = database.getReference("Users");
//                                reference.child(uid).setValue(hashMap);

                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUI(user);

                                //가입이 이루어졌을 시 가입 화면을 빠져나감.
                                Intent intent = new Intent(MEMBER_RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감.

                            }

                        }
                    });

                    //비밀번호 오류시
                }else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            Toast.makeText(this,"회원가입 되었습니다.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MEMBER_LoginActivity.class));
        }else {
            Toast.makeText(this,"회원가입에 실패했습니다.",Toast.LENGTH_LONG).show();
        }
    }

}