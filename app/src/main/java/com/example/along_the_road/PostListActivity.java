package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.along_the_road.adapters.PostAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.along_the_road.R.drawable.main_menu_white;

public class PostListActivity extends AppCompatActivity {

    private static final String TAG = "PostListActivity";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "postId";
    private static final String TAG_NAME = "name";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LIKE = "count(*)";
    private static String IP_ADDRESS = "IP ADDRESS";

    // Review list
    private RecyclerView rv_review_container;
    private PostAdapter adapter;
    private ListReview listReview;

    private String JSONString;
    private JSONArray reviews = null;

    // Other component
    private EditText et_search_text;
    private ImageButton ib_write_review;

    // Database
    private String username = "";
    private int PostId = -1;
    private int PostLike = -1;
    private boolean state = false;

    private FirebaseAuth firebaseAuth;
    private DBOpenHelper dbOpenHelper;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String area = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

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
            username = intent.getStringExtra("username");
        }

//        getData("http://" + IP_ADDRESS + "/connection.php");

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                int _id = adapter.getItem(pos).get_id();

                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("PostId", Integer.toString(_id));
                // 수정, 삭제 시에 어떻게 할건지

                onPause();
                startActivity(intent);
            }
        });

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
            }
        });

        ib_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent list_to_create = new Intent(getApplicationContext(), InPostActivity.class);
                    list_to_create.putExtra("username", username);

                    startActivity(list_to_create);

                } else {

                    Toast.makeText(getApplicationContext(), "후기 작성을 위해서는 로그인이 필요합니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

        adapter.clearAllItem();
        getData("http://" + IP_ADDRESS + "/connection.php");
        getLikeData("http://" + IP_ADDRESS + "/selectAllRecommendPost.php");
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public void initAllComponent() {

        rv_review_container = findViewById(R.id.rv_review_container);
        rv_review_container.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_review_container.setLayoutManager(linearLayoutManager);

        adapter = new PostAdapter();
        rv_review_container.setAdapter(adapter);

        et_search_text = findViewById(R.id.et_search_text);
        ib_write_review = findViewById(R.id.ib_write_review);

        firebaseAuth = FirebaseAuth.getInstance();

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

//    public void recyclerviewSetting() {
//
//        firebaseDatabase.getReference("reviews").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                reviewlist = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));
//                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    firebaseDatabase.getReference("reviews/" + dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            int id = Integer.parseInt(dataSnapshot.getKey());
//                            String title = "";
//                            String name = "";
//                            int like = 0;
//
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                if (dataSnapshot.getKey().equals("title")) {
//                                    title = dataSnapshot.getValue().toString();
//                                } else if (dataSnapshot.getKey().equals("name")) {
//                                    name = dataSnapshot.getValue().toString();
//                                } else if (dataSnapshot.getKey().equals("like")) {
//                                    like = Integer.parseInt(dataSnapshot.getValue().toString());
//                                }
//
//                            }
//
//                            for (int i = 0; i < adapter.getItemCount(); i++) {
//                                if (id == adapter.getItem(i).get_id()) {
//                                    state = true;
//                                }
//                            }
//
//                            if (state == false) {
//                                listReview = new ListReview(id, title, name, like);
//                                adapter.addItem(listReview);
//                                adapter.notifyDataSetChanged();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.d(TAG, error.getMessage());
//                        }
//
//                    });
//
//
//                    state = false;
//
//                }
//
//                rv_review_container.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

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

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(JSONString);
            reviews = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject c = reviews.getJSONObject(i);
                String id = c.getString(TAG_ID);
                int postId = Integer.parseInt(id);
                PostId = postId;
                String name = c.getString(TAG_NAME);
                String title = c.getString(TAG_TITLE);

                listReview = new ListReview(postId, title, name, 0);
                adapter.addItem(listReview);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void showResult() {
        try {
            JSONObject jsonObj = new JSONObject(JSONString);
            reviews = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject c = reviews.getJSONObject(i);
                String id = c.getString(TAG_ID);
                int postId = Integer.parseInt(id);
                String like = c.getString(TAG_LIKE);
                PostLike = Integer.parseInt(like);

                int position = adapter.getItemPosition(postId);
                if(position != -1) {
                    adapter.addItemLocation(position, PostLike);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                Intent revlist_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revlist_to_login);
                return true;
            case R.id.menu_signup:
                Intent revlist_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revlist_to_signup);
                return true;
            case R.id.menu_logout:
                final ProgressDialog mDialog = new ProgressDialog(PostListActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                String uid = firebaseAuth.getCurrentUser().getUid();
                dbOpenHelper.deleteColumn(uid);

                FirebaseAuth.getInstance().signOut();

                Intent logout_to_revlist = new Intent(getApplicationContext(), PostListActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revlist);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(PostListActivity.this,
                        "로딩중입니다.", null, true, true);

            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {

                progressDialog.dismiss();

                JSONString = result;

                Log.d(TAG, "response - " + result);
                showList();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void getLikeData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                progressDialog = ProgressDialog.show(PostListActivity.this,
//                        "로딩중입니다.", null, true, true);

            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {

//                progressDialog.dismiss();

                JSONString = result;

                Log.d(TAG, "response - " + result);
                showResult();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}
