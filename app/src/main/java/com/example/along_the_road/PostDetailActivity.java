package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostDetailActivity extends AppCompatActivity {

    private final static String TAG = "PostDetailActivity";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "postId";
    private static final String TAG_NAME = "name";
    private static final String TAG_UID = "uid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTNET = "content";
    private static final String TAG_LIKE = "count(*)";
    private static final String TAG_IMAGE = "image";
    private static String IP_ADDRESS = "";

    // Other component
    private ImageView iv_review_image;
    private TextView tv_review_title;
    private TextView tv_review_user;
    private TextView tv_review_content;
    private Button btn_review_delete;
    private Button btn_review_update;
    private Button btn_review_like;

    private int PostId;
    private int PostLike;

    private String JSONString;
    private JSONArray reviews = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A7AFF"))); //툴바 배경색

        initAllComponent();

        Intent intent = getIntent();
        if (intent != null) {
            String TempID = intent.getStringExtra("PostId");
            String uid = firebaseAuth.getCurrentUser().getUid();

            PostId = Integer.parseInt(TempID);
            GetData task = new GetData();
            task.execute(Integer.toString(PostId));

            selectLikeData sltask = new selectLikeData();
            sltask.execute(TempID, uid);
        }

        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 화면으로 이동
                Intent detail_to_update = new Intent(getApplicationContext(), PostUpdateActivity.class);
                detail_to_update.putExtra("PostId", Long.toString(PostId));

                startActivity(detail_to_update);
            }
        });

        btn_review_update.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        btn_review_update.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_review_update.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP :
                        btn_review_update.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_review_update.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
            }
        });

        btn_review_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 바디
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.AlertDialogStyle);
                // 메세지
                deleteBuilder.setTitle("삭제하시겠습니까?");

                deleteBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 파이어베이스에서 삭제하는 코드 필요
//                        firebaseDatabase.getReference("reviews").child(PostId + "").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(getApplicationContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                        deleteData task = new deleteData();
                        task.execute(Integer.toString(PostId));

                        finish();

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                deleteBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });

                deleteBuilder.show();

            }
        });

        btn_review_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        btn_review_delete.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_review_delete.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP :
                        btn_review_delete.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_review_delete.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
            }
        });

        if(firebaseAuth.getCurrentUser() != null) {
            btn_review_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firebaseAuth.getCurrentUser() != null) {
//                    PostLike += 1;
//                    firebaseDatabase.getReference("reviews/" + PostId + "/like").setValue(PostLike);
//                    // 좋아요 누르는거 어떻게 컨트롤할지가 필요!

                        String postId = Integer.toString(PostId);
                        String uid = firebaseAuth.getCurrentUser().getUid();

                        selectLikeData sltask = new selectLikeData();
                        sltask.execute(postId, uid);

                        if (PostLike == 0) {
                            // 아직 추천을 하지 않았다면

                            InsertData task = new InsertData();
                            task.execute("http://" + IP_ADDRESS + "/insertRecommendPost.php", postId, uid);
                            PostLike += 1;

                            Toast.makeText(getApplicationContext(), "해당 글을 추천하셨습니다!", Toast.LENGTH_SHORT).show();

                            GetLikeData ltask = new GetLikeData();
                            ltask.execute(Integer.toString(PostId));

                        } else {
                            // 추천을 했다면

                            deleteLikeData task = new deleteLikeData();
                            task.execute(postId, uid);
                            PostLike = 0;

                            GetLikeData ltask = new GetLikeData();
                            ltask.execute(Integer.toString(PostId));

                        }

                    }
                }
            });
        }

        btn_review_like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        btn_review_like.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_review_like.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP :
                        btn_review_like.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_review_like.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
            }
        });

    }

    public void initAllComponent() {

        iv_review_image = findViewById(R.id.iv_review_image);

        tv_review_title = findViewById(R.id.tv_review_title);
        tv_review_user = findViewById(R.id.tv_review_user);
        tv_review_content = findViewById(R.id.tv_review_content);

        btn_review_update = findViewById(R.id.btn_review_update);
        btn_review_delete = findViewById(R.id.btn_review_delete);
        btn_review_like = findViewById(R.id.btn_review_like);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public void onResume() {
        super.onResume();

        GetData task = new GetData();
        task.execute(Integer.toString(PostId));

        GetLikeData ltask = new GetLikeData();
        ltask.execute(Integer.toString(PostId));

    }

//    public void selectFirebase(int index) {
////
////        final int id = index;
////
////        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
////        firebaseDatabase.getReference("reviews/" + index).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                    if(dataSnapshot.getKey().equals("title"))
////                        tv_review_title.setText(dataSnapshot.getValue().toString());
////                    else if(dataSnapshot.getKey().equals("name"))
////                        tv_review_user.setText(dataSnapshot.getValue().toString());
////                    else if(dataSnapshot.getKey().equals("content"))
////                        tv_review_content.setText(dataSnapshot.getValue().toString());
////                    else if(dataSnapshot.getKey().equals("image")) {
////                        String image = dataSnapshot.getValue().toString();
////                        byte[] b = binaryStringToByteArray(image);
////                        Log.d(TAG, "b : " + b);
////                        ByteArrayInputStream is = new ByteArrayInputStream(b);
////                        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
////                        iv_review_image.setImageDrawable(reviewImage);
////                        iv_review_image.setVisibility(View.VISIBLE);
////                    } else if(dataSnapshot.getKey().equals("like")) {
////                        String like = dataSnapshot.getValue().toString();
////                        PostLike = Integer.parseInt(like);
////                        btn_review_like.setText("추천 " + PostLike);
////                    } else if(dataSnapshot.getKey().equals("uid")) {
////                        if (firebaseAuth.getCurrentUser() != null) {
////                            if (firebaseAuth.getCurrentUser().getUid().equals(dataSnapshot.getValue().toString())) {
////                                btn_review_update.setVisibility(View.VISIBLE);
////                                btn_review_delete.setVisibility(View.VISIBLE);
////                                btn_review_like.setVisibility(View.GONE);
////                            } else {
////                                btn_review_update.setVisibility(View.GONE);
////                                btn_review_delete.setVisibility(View.GONE);
////                                btn_review_like.setVisibility(View.VISIBLE);
////                            }
////                        } else if (firebaseAuth.getCurrentUser() == null) {
////                            btn_review_update.setVisibility(View.GONE);
////                            btn_review_delete.setVisibility(View.GONE);
////                            btn_review_like.setVisibility(View.VISIBLE);
////                        }
////                    }
////                }
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
////
////    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login :
                Intent revdetail_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revdetail_to_login);
                return true;
            case R.id.menu_signup :
                Intent revdetail_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revdetail_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(PostDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revdetail = new Intent(getApplicationContext(), PostDetailActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revdetail);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetLikeData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                // 오류 시
            }
            else {

                JSONString = result;
                showLikeResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String postId = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/selectAllRecommendPost.php";
            String postParameters = "postId=" + postId;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private class selectLikeData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                // 오류 시
            }
            else {

                JSONString = result;
                selectLikeResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String postId = params[0];
            String uid = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/selectRecommendPost.php";
            String postParameters = "postId=" + postId + "&uid=" + uid;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showLikeResult(){
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String like = item.getString(TAG_LIKE);

                btn_review_like.setText("추천  " + like);

            }

        } catch (JSONException e) {

            Log.d(TAG, "showLikeResult : ", e);
        }

    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                // 오류 시
            }
            else {

                JSONString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String postId = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/selectReviews.php";
            String postParameters = "postId=" + postId;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void selectLikeResult(){
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String like = item.getString(TAG_LIKE);
                PostLike = Integer.parseInt(like);

            }

        } catch (JSONException e) {

            Log.d(TAG, "selectLikeResult : ", e);
        }

    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String uid = item.getString(TAG_UID);
                String name = item.getString(TAG_NAME);
                String title = item.getString(TAG_TITLE);
                String content = item.getString(TAG_CONTNET);

                if (firebaseAuth.getCurrentUser() != null) {
                    if (firebaseAuth.getCurrentUser().getUid().equals(uid)) {
                        btn_review_update.setVisibility(View.VISIBLE);
                        btn_review_delete.setVisibility(View.VISIBLE);
                        btn_review_like.setVisibility(View.GONE);
                    } else {
                        btn_review_update.setVisibility(View.GONE);
                        btn_review_delete.setVisibility(View.GONE);
                        btn_review_like.setVisibility(View.VISIBLE);
                    }
                } else if (firebaseAuth.getCurrentUser() == null) {
                    btn_review_update.setVisibility(View.GONE);
                    btn_review_delete.setVisibility(View.GONE);
                    btn_review_like.setVisibility(View.VISIBLE);
                }

                tv_review_title.setText(title);
                tv_review_content.setText(content);
                tv_review_user.setText(name);

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    private class deleteData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String postId = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/deleteReviews.php";
            String postParameters = "postId=" + postId;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "DeleteData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String postId = (String)params[1];
            String uid = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "postId=" + postId + "&uid=" + uid;

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

    private class deleteLikeData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String postId = params[0];
            String uid = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/deleteRecommendPost.php";
            String postParameters = "postId=" + postId + "&uid=" + uid;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "DeleteLikeData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

}
