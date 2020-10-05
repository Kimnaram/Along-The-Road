package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UserPlanActivity extends AppCompatActivity {

    private final static String TAG = "UserPlanActivity";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_UID = "uid";
    private static final String TAG_CITY = "city";
    private static final String TAG_START_DATE = "start_date";
    private static final String TAG_END_DATE = "end_date";
    private static final String TAG_STAY = "stay";
    private static final String TAG_HOTEL_NAME = "hotel_name";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_URL = "url";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray plans = null;

    private RelativeLayout rl_info_popup;
    private RelativeLayout rl_popup_info_ok;
    private RelativeLayout rl_plan_container;
    private FlowLayout fl_course_list;

    private TextView tv_info_my_plan_area;
    private TextView tv_info_my_plan_day;
    private ImageView iv_hotel_image;
    private TextView tv_area_name;
    private TextView tv_start_date;
    private TextView tv_end_date;
    private TextView tv_hotel_name;
    private TextView tv_course_x;
    private TextView tv_popup_msg;
    private Button btn_remove_reservation;

    private Bitmap[] bitmap;

    private String username;

    private int img_no = 0;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_plan);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        initAllComponent();

        if (firebaseAuth.getCurrentUser() != null) {

            final String uid = firebaseAuth.getCurrentUser().getUid();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            GetData task = new GetData();
            task.execute(uid);

            if (!tv_info_my_plan_area.getText().toString().isEmpty() && tv_info_my_plan_day.getText().toString().isEmpty()) {
                tv_info_my_plan_day.setText("당일치기");
            }

//            // 만약 여행 계획이 없다면
//            if () {
//                // 다이얼로그 바디
//                AlertDialog.Builder alBuilder = new AlertDialog.Builder(UserPlanActivity.this, R.style.AlertDialogStyle);
//                // 메세지
//                alBuilder.setTitle("여행 계획이 존재하지 않습니다.");
//                alBuilder.setMessage("새로운 계획을 만드시겠습니까?");
//
//
//                alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        Intent plan_to_local = new Intent(getApplicationContext(), LocalSelectActivity.class);
//                        finish();
//                        startActivity(plan_to_local);
//
//                    }
//                });
//                // "아니오" 버튼을 누르면 실행되는 리스너
//                alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//
//                alBuilder.show();
//            }


            btn_remove_reservation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alBuilder = new AlertDialog.Builder(UserPlanActivity.this, R.style.AlertDialogStyle);
                    // 메세지
                    alBuilder.setTitle("삭제하시겠습니까?");

                    alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                            firebaseDatabase.getReference("users/" + uid + "/plan").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    tv_popup_msg.setText("삭제되었습니다.\n새로운 일정을 만들 수 있습니다!");
//                                    rl_info_popup.setVisibility(View.VISIBLE);
//                                    rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });

                            DeleteData deleteTask = new DeleteData();
                            deleteTask.execute(uid);

                        }
                    });
                    // "아니오" 버튼을 누르면 실행되는 리스너
                    alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

                    alBuilder.show();
                }
            });

            btn_remove_reservation.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN :
                            btn_remove_reservation.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                            btn_remove_reservation.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                            return false;

                        case MotionEvent.ACTION_UP :
                            btn_remove_reservation.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                            btn_remove_reservation.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                            return false;
                    }
                    return false;
                }
            });

//            if(bitmap.length > 0) {
//                iv_hotel_image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(img_no < bitmap.length) {
//                            ++img_no;
//                            iv_hotel_image.setImageBitmap(bitmap[img_no]);
//                        }
//                    }
//                });
//            }

        } else {
            rl_plan_container.setVisibility(View.GONE);
            tv_popup_msg.setText("로그인이 필요한 기능입니다.");
            rl_info_popup.setVisibility(View.VISIBLE);
            rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        rl_info_popup = findViewById(R.id.rl_info_popup);
        rl_popup_info_ok = findViewById(R.id.rl_popup_info_ok);
        rl_plan_container = findViewById(R.id.rl_plan_container);
        fl_course_list = findViewById(R.id.fl_course_list);

        tv_info_my_plan_area = findViewById(R.id.tv_info_my_plan_area);
        tv_info_my_plan_day = findViewById(R.id.tv_info_my_plan_day);

        iv_hotel_image = findViewById(R.id.iv_hotel_image);
        tv_area_name = findViewById(R.id.tv_area_name);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        tv_hotel_name = findViewById(R.id.tv_hotel_name);
        tv_course_x = findViewById(R.id.tv_course_x);
        tv_popup_msg = findViewById(R.id.tv_popup_msg);

        btn_remove_reservation = findViewById(R.id.btn_remove_reservation);

    }

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

//    private class getData extends AsyncTask<String, Void, String> {
//
//        String errorString = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog = ProgressDialog.show(UserPlanActivity.this,
//                    "로딩중입니다.", null, true, true);
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            progressDialog.dismiss();
//
//            Log.d(TAG, "response - " + result);
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            final String uid = firebaseAuth.getCurrentUser().getUid();
//
//            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//            firebaseDatabase.getReference("users/" + uid + "/plan").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        if (dataSnapshot.getKey().equals("city")) {
//                            tv_area_name.setText(dataSnapshot.getValue().toString());
//                            tv_info_my_plan_area.setText(dataSnapshot.getValue().toString() + "  에서의");
//                            tv_info_my_plan_area.setVisibility(View.VISIBLE);
//                        } else if (dataSnapshot.getKey().equals("startDate")) {
//                            tv_start_date.setText(dataSnapshot.getValue().toString());
//                        } else if (dataSnapshot.getKey().equals("endDate")) {
//                            tv_end_date.setText(dataSnapshot.getValue().toString());
//                        } else if (dataSnapshot.getKey().equals("stay")) {
//                            tv_info_my_plan_day.setText(dataSnapshot.getValue().toString());
//                            tv_info_my_plan_day.setVisibility(View.VISIBLE);
//                        } else if (dataSnapshot.getKey().equals("hotelName")) {
//                            tv_hotel_name.setText(dataSnapshot.getValue().toString());
//                        } else if (dataSnapshot.getKey().equals("hotelImage")) {
//                            String image = dataSnapshot.getValue().toString();
//                            byte[] b = binaryStringToByteArray(image);
//                            Log.d("UserPlanActivity", "b : " + b);
//                            ByteArrayInputStream is = new ByteArrayInputStream(b);
//                            Drawable hotelImage = Drawable.createFromStream(is, "hotelImage");
//                            iv_hotel_image.setImageDrawable(hotelImage);
//                        } else if (dataSnapshot.getKey().equals("course")) {
//                            int length = Integer.parseInt(Long.toString(dataSnapshot.getChildrenCount()));
//                            for (int i = 0; i < length; i++) {
//                                TextView tv_course_name = new TextView(UserPlanActivity.this);
//                                if (i < length - 1) {
//                                    tv_course_name.setText(dataSnapshot.child(Integer.toString(i)).getValue().toString() + " > ");
//                                } else if (i == length - 1) {
//                                    tv_course_name.setText(dataSnapshot.child(Integer.toString(i)).getValue().toString());
//                                }
//                                Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquarebold.ttf");
//                                tv_course_name.setTypeface(typeface);
//                                tv_course_name.setTextSize(17);
//                                fl_course_list.addView(tv_course_name);
//                            }
//                            tv_course_x.setVisibility(View.GONE);
//                        } else if (dataSnapshot.getKey().equals("courseImage")) {
//                            int length = Integer.parseInt(Long.toString(dataSnapshot.getChildrenCount()));
//                            for (int i = 0; i < length; i++) {
//                                bitmap = new Bitmap[length];
//                                final int no = i;
//                                if (iv_hotel_image.getDrawable() == null) {
//                                    Thread thread = new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            // TODO Auto-generated method stub
//                                            try {
//                                                URL url = new URL(dataSnapshot.child(Integer.toString(no)).getValue().toString());
//                                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                                conn.setDoInput(true);
//                                                conn.connect();
//
//                                                InputStream is = conn.getInputStream();
//                                                bitmap[no] = BitmapFactory.decodeStream(is);
//                                            } catch (MalformedURLException e) {
//                                                e.printStackTrace();
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//
//                                    thread.start();
//
//                                    try {
//                                        thread.join();
//
//                                        iv_hotel_image.setImageBitmap(bitmap[0]);
//                                        img_no = 0;
////                                        hotelImg = new BitmapDrawable(bitmap);
//
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    errorString = error.toString();
//                }
//            });
//
//            return errorString;
//
//        }
//    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(UserPlanActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {
                // 오류 시
            } else {

                if(result.contains("찾을 수 없습니다.")) {
                    tv_popup_msg.setText("여행 계획이 존재하지 않습니다.");
                    rl_info_popup.setVisibility(View.VISIBLE);
                    rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    rl_plan_container.setVisibility(View.VISIBLE);
                    JSONString = result;
                    showResult();
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String uid = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/selectPlan.php";
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

                String city = item.getString(TAG_CITY);
                String start_date = item.getString(TAG_START_DATE);
                String end_date = item.getString(TAG_END_DATE);
                String stay = item.getString(TAG_STAY);
                String hotel_name = item.getString(TAG_HOTEL_NAME);
                String image = item.getString(TAG_IMAGE);
                String url = item.getString(TAG_URL);

                Log.d(TAG, "image : " + image);

                if (image == null || image.isEmpty()) {
                    Log.d(TAG, "Image is null");
                } else {
                    Bitmap bitmap = StringToBitmap(image);
                    iv_hotel_image.setImageBitmap(bitmap);
                    iv_hotel_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv_hotel_image.setVisibility(View.VISIBLE);
                }

                tv_area_name.setText(city);
                tv_hotel_name.setText(hotel_name);
                tv_start_date.setText(start_date);
                tv_end_date.setText(end_date);
                tv_info_my_plan_area.setText(city + "  에서의");
                tv_info_my_plan_day.setText(stay);

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    private class DeleteData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(UserPlanActivity.this,
                    "삭제중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            tv_popup_msg.setText("삭제되었습니다.\n새로운 일정을 만들 수 있습니다!");
            rl_info_popup.setVisibility(View.VISIBLE);
            rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            Log.d(TAG, "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String uid = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/deletePlan.php";
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

                Log.d(TAG, "DeleteData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    @Override
    public void onBackPressed() {

        finish();
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

                final ProgressDialog mDialog = new ProgressDialog(UserPlanActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), UserPlanActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
