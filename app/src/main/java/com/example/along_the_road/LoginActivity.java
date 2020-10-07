package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button btn_login;

    private TextView tv_go_register;
    private TextView tv_go_nonmember;
    private TextView tv_notification_wrong;

    private EditText ed_email_field;
    private EditText ed_pw_field;

    private FirebaseAuth firebaseAuth;
    private DBOpenHelper dbOpenHelper;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_UID = "uid";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray user = null;

    private String uid = "";
    private String email = "";
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //버튼 등록하기
        tv_go_register = findViewById(R.id.tv_go_register);
        tv_go_nonmember = findViewById(R.id.tv_go_nonmember);

        tv_notification_wrong = findViewById(R.id.tv_notification_wrong);

        btn_login = findViewById(R.id.btn_login);
        ed_email_field = findViewById(R.id.ed_email_field);
        ed_pw_field = findViewById(R.id.ed_pw_field);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        //가입 버튼이 눌리면
        tv_go_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), SignupActivity.class));

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
                final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("로그인 중입니다.");
                mDialog.show();

                final String email = ed_email_field.getText().toString();
                final String pwd = ed_pw_field.getText().toString();

                if(!email.isEmpty() && !pwd.isEmpty()) {

                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();

                                        String muid = firebaseAuth.getCurrentUser().getUid();

                                        GetData gtask = new GetData();
                                        gtask.execute(muid);

                                    } else {
                                        mDialog.dismiss();
                                        tv_notification_wrong.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 모두 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "회원 정보 저장 중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {
                // 오류 시
            } else {
                JSONString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String uid = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/selectUser.php";
            String postParameters = "uid=" + uid;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "SelectData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                uid = item.getString(TAG_UID);
                email = item.getString(TAG_EMAIL);
                name = item.getString(TAG_NAME);

                dbOpenHelper.insertColumn(uid, email, name);

            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }

}