package com.example.along_the_road;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.along_the_road.R.drawable.main_menu_white;

public class PostUpdateActivity extends AppCompatActivity {

    private static final String TAG = "ReviewCreateActivity";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTNET = "content";
    private static final String TAG_IMAGE = "image";
    private static String IP_ADDRESS = "IP ADDRESS";

    private static final int REQUEST_CODE = 1001;

    private RelativeLayout rl_image_container;

    private TextView tv_post_content_length;
    private EditText et_review_title;
    private EditText et_review_content;
    private ImageView iv_review_image;
    private ImageButton ib_image_remove;
    private Button btn_image_upload;
    private Button btn_review_update;
    private Button btn_review_cancel;

    private FirebaseAuth firebaseAuth;
    private DBOpenHelper dbOpenHelper;

    private int PostId;
    private String JSONString;
    private JSONArray reviews = null;

    private Bitmap img;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String username = "";
    private String area = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 메뉴버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(main_menu_white); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A7AFF"))); //툴바 배경색

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        //네비게이션 드로어 헤더 설정
        LinearLayout ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_item, null);
        ll_navigation_container.setBackground(getResources().getDrawable(R.color.basic_color_3A7AFF));
        ll_navigation_container.setPadding(20, 150, 40, 50);
        ll_navigation_container.setOrientation(LinearLayout.VERTICAL);
        ll_navigation_container.setGravity(Gravity.BOTTOM);
        ll_navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquarebold.ttf");
        // 커스텀폰트 오류 발생, 시스템폰트 사용으로 변경
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(this.getAssets(), "nanumsquarebold.ttf");
        } catch (Exception e) {
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        }

        final TextView tv_username = new TextView(this);
        tv_username.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_username.setTextSize(18);
        tv_username.setTypeface(typeface);
        tv_username.setPadding(0, 2, 0, 2);
        param1.setMargins(20, 20, 20, 5);
        tv_username.setLayoutParams(param1);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_useremail = new TextView(this);
        tv_useremail.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_useremail.setTextSize(15);
        tv_useremail.setTypeface(typeface);
        tv_useremail.setPadding(0, 2, 0, 2);
        param2.setMargins(20, 0, 20, 10);
        tv_useremail.setLayoutParams(param2);

        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_login_or_out = new TextView(this);
        tv_login_or_out.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_login_or_out.setTextSize(14);
        tv_login_or_out.setTypeface(typeface);
        tv_login_or_out.setPadding(0, 2, 0, 2);
        param3.setMargins(20, 30, 20, 20);
        tv_login_or_out.setGravity(Gravity.RIGHT);

        tv_login_or_out.setLayoutParams(param3);

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            dbOpenHelper = new DBOpenHelper(this);
            dbOpenHelper.open();

            Cursor iCursor = dbOpenHelper.selectColumn(uid);

            while (iCursor.moveToNext()) {

                String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
                username = tempName;
                String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
                String tempCity = iCursor.getString(iCursor.getColumnIndex("city"));
                if(!iCursor.getString(iCursor.getColumnIndex("city")).equals("null")) {
                    area = tempCity;
                }

                tv_username.setText(tempName + " 님");
                tv_useremail.setText(tempEmail);

            }

//            tv_useremail.setText(firebaseUser.getEmail());
            tv_login_or_out.setText("로그아웃");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    dbOpenHelper.deleteColumn(uid);

                    firebaseAuth.signOut();

                    Intent navi_to_logout = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(navi_to_logout);
                }
            });

        } else if (firebaseUser == null) {

            tv_username.setText("로그인이 필요합니다.");
            tv_useremail.setVisibility(View.GONE);

            tv_login_or_out.setText("로그인");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent navi_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(navi_to_login);
                }
            });

        }

        ll_navigation_container.addView(tv_username);
        ll_navigation_container.addView(tv_useremail);

        ll_navigation_container.addView(tv_login_or_out);

        navigationView.addHeaderView(ll_navigation_container);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                //길 찾기
                if (id == R.id.item_directions) {
                    startActivity(new Intent(getApplicationContext(), TrafficSearchActivity.class));
                }
                //내 여행 계획 보기
                if (id == R.id.item_checkplan) {
                    startActivity(new Intent(getApplicationContext(), UserPlanActivity.class));
                }
                //후기 구경하기
                if (id == R.id.item_review) {
                    Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                    intent.putExtra("name", username);

                    startActivity(intent);
                }

                return true;
            }
        });

        initAllComponent();

        Intent intent = getIntent();
        if (intent != null) {
            String TempId = intent.getStringExtra("PostId");
            PostId = Integer.parseInt(TempId);
            GetData task = new GetData();
            task.execute(Integer.toString(PostId));
        }

        et_review_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_review_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 100) {
                    s.delete(99,100);
                }
            }
        });

        et_review_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_post_content_length.setText("(" + s.length() + " / 5000)");
                if(s.length() >= 5000) {
                    s.delete(4999, 5000);
                }
            }
        });

        if(et_review_title.getText().toString().isEmpty() && et_review_content.getText().toString().isEmpty()) {
            if (et_review_title.getText().toString().isEmpty() && et_review_content.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "제목과 내용 모두 작성하셔야 합니다.", Toast.LENGTH_SHORT).show();
            } else if (et_review_title.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "제목을 작성하셔야 합니다.", Toast.LENGTH_SHORT).show();
            } else if (et_review_content.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "내용을 작성하셔야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }

        btn_image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updateFirebase(PostId);
                String title = et_review_title.getText().toString();
                String content = et_review_content.getText().toString();
                String image = "";

                if (iv_review_image.getDrawable() != null) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    image = "&image=" + byteArrayToBinaryString(bytes);

                }

                UpdateData task = new UpdateData();
                task.execute(Integer.toString(PostId), title, content, image);

                finish();
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

        btn_review_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_review_cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        btn_review_cancel.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_review_cancel.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP :
                        btn_review_cancel.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_review_cancel.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
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

        rl_image_container = findViewById(R.id.rl_image_container);

        tv_post_content_length = findViewById(R.id.tv_post_content_length);
        et_review_title = findViewById(R.id.et_review_title);
        et_review_content = findViewById(R.id.et_review_content);
        iv_review_image = findViewById(R.id.iv_review_image);
        ib_image_remove = findViewById(R.id.ib_image_remove);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        btn_review_update = findViewById(R.id.btn_review_update);
        btn_review_cancel = findViewById(R.id.btn_review_cancel);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

//    public void selectFirebase(int index) {
//
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.getReference("reviews/" + index).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String title = "";
//                String content = "";
//                int like = 0;
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if (dataSnapshot.getKey().equals("title")) {
//                        title = dataSnapshot.getValue().toString();
//                        Log.d(TAG, "title : " + title);
//                        et_review_title.setText(title);
//                    } else if (dataSnapshot.getKey().equals("content")) {
//                        content = dataSnapshot.getValue().toString();
//                        et_review_content.setText(content);
//                    } else if (dataSnapshot.getKey().equals("image")) {
//                        String image = dataSnapshot.getValue().toString();
//                        byte[] b = binaryStringToByteArray(image);
//                        Log.d(TAG, "b : " + b);
//                        ByteArrayInputStream is = new ByteArrayInputStream(b);
//                        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
//                        iv_review_image.setImageDrawable(reviewImage);
//                    }
//                }
//
//                if(iv_review_image.getDrawable() != null) {
//                    rl_image_container.setVisibility(View.VISIBLE);
//                } else {
//                    rl_image_container.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });
//
//    }

//    public void updateFirebase(int index) {
//        String title = et_review_title.getText().toString();
//        String content = et_review_content.getText().toString();
//        Drawable image = iv_review_image.getDrawable();
//
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseDatabase.getReference("reviews/" + index).child("title").setValue(title);
//        firebaseDatabase.getReference("reviews/" + index).child("content").setValue(content);
//        if(image != null) {
//            byte[] reviewImage = null;
//            String simage = "";
//            Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            reviewImage = stream.toByteArray();
//            simage = byteArrayToBinaryString(reviewImage);
//            firebaseDatabase.getReference("reviews/" + index).child("image").setValue(simage);
//        } else {
//            firebaseDatabase.getReference("reviews/" + index).child("image").setValue(null);
//        }
//
//    }

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

    public static Bitmap StringToBitmap(String ImageString) {
        try {
//            String decodedString = URLDecoder.decode(encodedString, "utf-8");
//            Log.d(TAG, "decodedString : " + decodedString);
//            byte[] encodeByte = Base64.decode(decodedString, Base64.DEFAULT);
            byte[] bytes = binaryStringToByteArray(ImageString);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 500, 400, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 400, 300, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 300, 200, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 200, 150, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostUpdateActivity.this,
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

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString(TAG_TITLE);
                String content = item.getString(TAG_CONTNET);
                String image = null;
                if(!item.isNull(TAG_IMAGE)) {
                    image = item.getString(TAG_IMAGE);
                }

                if(image != null) {
                    Bitmap bitmap = StringToBitmap(image);
                    iv_review_image.setImageBitmap(bitmap);
                    rl_image_container.setVisibility(View.VISIBLE);
                }
                et_review_title.setText(title);
                et_review_content.setText(content);

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    class UpdateData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(PostUpdateActivity.this,
//                    "수정 된 정보를 저장중입니다.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String postId = (String)params[0];
            String title = (String)params[1];
            String content = (String)params[2];
            String image = (String)params[3];

            String serverURL = "http://" + IP_ADDRESS + "/updateReviews.php";
            String postParameters = "postId=" + postId + "&title=" + title + "&content=" + content + image;


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

                Log.d(TAG, "UpdateData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
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
                    img = BitmapFactory.decodeStream(in);
                    img = resize(img);
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

    @Override
    public void onBackPressed() {

        //뒤로가기 버튼으로 네비게이션 드로어 닫기
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.menu_login:
                Intent revupdate_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revupdate_to_login);
                return true;
            case R.id.menu_signup:
                Intent revupdate_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revupdate_to_signup);
                return true;
            case R.id.menu_logout:
                final ProgressDialog mDialog = new ProgressDialog(PostUpdateActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                String uid = firebaseAuth.getCurrentUser().getUid();
                dbOpenHelper.deleteColumn(uid);

                FirebaseAuth.getInstance().signOut();

                Intent logout_to_revupdate = new Intent(getApplicationContext(), PostUpdateActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revupdate);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
