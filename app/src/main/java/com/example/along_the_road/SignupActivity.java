package com.example.along_the_road;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private static String IP_ADDRESS = "IP ADDRESS";

    private EditText ed_email_field;
    private EditText ed_username_field;
    private EditText ed_pw_field;
    private EditText ed_pwcheck_field;
    private Button btn_register_done;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        ed_email_field = findViewById(R.id.ed_email_field);
        ed_pw_field = findViewById(R.id.ed_pw_field);
        ed_pwcheck_field = findViewById(R.id.ed_pwcheck_field);
        ed_username_field = findViewById(R.id.ed_username_field);

        btn_register_done = findViewById(R.id.btn_register_done);

        btn_register_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //가입 정보 가져오기
                String email = ed_email_field.getText().toString();
                String pwd = ed_pw_field.getText().toString();
                String pwdcheck = ed_pwcheck_field.getText().toString();

                if(!email.isEmpty() && !pwd.isEmpty() && !pwdcheck.isEmpty()) {

                    if (pwd.equals(pwdcheck)) {

                        Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                        final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
                        mDialog.setMessage("회원가입 중입니다.");
                        mDialog.show();

                        //파이어베이스에 신규계정 등록하기
                        firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            mDialog.dismiss();

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            String email = user.getEmail();
                                            String uid = user.getUid();
                                            String name = ed_username_field.getText().toString();
                                            String pwd = ed_pw_field.getText().toString();

//                                            //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
//                                            HashMap<Object, String> hashMap = new HashMap<>();
//                                            hashMap.put("email", email);
//                                            hashMap.put("uid", uid);
//                                            hashMap.put("name", name);
//                                            hashMap.put("pwd", pwd);
//
//                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                            DatabaseReference reference = database.getReference("users");
//                                            reference.child(uid).setValue(hashMap);

                                            InsertData Itask = new InsertData();
                                            Itask.execute("http://" + IP_ADDRESS + "/insertUser.php", uid, email, pwd, name);

                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();

                                            // 파이어베이스 연동이 안 되어서 임시 조치
//                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                        startActivity(intent);
                                        }
                                    }
                                });

                        //비밀번호 오류시
                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "작성폼을 모두 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignupActivity.this,
                    "회원 정보 저장 중입니다.", null, true, true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //가입이 이루어졌을시 가입 화면을 빠져나감.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(SignupActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String uid = (String)params[1];
            String email = (String)params[2];
            String password = (String)params[3];
            String name = (String)params[4];

            String serverURL = (String)params[0];
            String postParameters = "uid=" + uid + "&email=" + email + "&password=" +password + "&name=" + name;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}