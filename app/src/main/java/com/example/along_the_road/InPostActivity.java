package com.example.along_the_road;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class InPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InPostActivity";
    private static final int REQUEST_CODE = 1001;
    private static String IP_ADDRESS = "";

    private RelativeLayout rl_image_container;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private EditText mTitle, mContents;
    private TextView tv_image_upload;
    private ImageView iv_review_image;
    private ImageButton ib_image_remove;

    private String PostId = "";
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        initAllComponent();

        tv_image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        ib_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_review_image.setImageDrawable(null);
                rl_image_container.setVisibility(View.GONE);
            }
        });
    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        rl_image_container = findViewById(R.id.rl_image_container);
        mTitle = findViewById(R.id.post_title_edit);
        mContents = findViewById(R.id.post_contents_edit);
        tv_image_upload = findViewById(R.id.tv_image_upload);
        iv_review_image = findViewById(R.id.iv_review_image);
        ib_image_remove = findViewById(R.id.ib_image_remove);
        findViewById(R.id.post_save_button).setOnClickListener(this);

//        firebaseDatabase.getReference("reviews").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    reviewCount = Integer.parseInt(dataSnapshot.getKey());
//                    Log.d(TAG, "reviewCount : " + reviewCount);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    public void onClick(View v) {

        Drawable rimage = iv_review_image.getDrawable();
        String image = "";
        byte[] reviewImage = null;
        if (iv_review_image.getDrawable() != null) {

            Bitmap bitmap = ((BitmapDrawable) rimage).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            reviewImage = stream.toByteArray();
            image = byteArrayToBinaryString(reviewImage);

        }
//
//        int tempId = reviewCount + 1;
//        String postId = Integer.toString(tempId);
//        Log.d(TAG, "PostId : " + postId);
//
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();

        Intent intent = getIntent();
        if(intent != null) {
            username = intent.getStringExtra("username");
            PostId = intent.getStringExtra("PostId");
        }
//
//        HashMap<String,Object> hashMap = new HashMap<>();
//        hashMap.put("uid", uid);
//        hashMap.put("name", username);
//        hashMap.put("title", mTitle.getText().toString());
//        hashMap.put("content", mContents.getText().toString());
//        hashMap.put("like", Integer.toString(0));
//        if(iv_review_image != null) {
//            hashMap.put("image", image);
//        }
//        DatabaseReference reference = firebaseDatabase.getReference("reviews");
//        reference.child(postId).setValue(hashMap);

        String title = mTitle.getText().toString();
        String content = mContents.getText().toString();
        String name = username;
        Log.d(TAG, "name : " + name);

        InsertData task = new InsertData();
        task.execute("http://" + IP_ADDRESS + "/insertReviews.php", title, content, name, uid);

//        Intent create_to_detail = new Intent(getApplicationContext(), PostDetailActivity.class);
//        create_to_detail.putExtra("PostId", postId);
//
//        finish();
//        startActivity(create_to_detail);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    rl_image_container.setVisibility(View.VISIBLE);
                    iv_review_image.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
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
        switch (item.getItemId()) {
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            case R.id.menu_signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(InPostActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), InPostActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(InPostActivity.this,
                    "로딩중입니다.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Intent create_to_detail = new Intent(getApplicationContext(), PostDetailActivity.class);
            create_to_detail.putExtra("PostId", PostId);

            finish();
            startActivity(create_to_detail);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String title = (String)params[1];
            String content = (String)params[2];
            String name = (String)params[3];
            String uid = (String)params[4];

            String serverURL = (String)params[0];
            String postParameters = "title=" + title + "&content=" + content + "&name=" + name + "&uid=" + uid;


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
