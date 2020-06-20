package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TrafficSearchActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private RelativeLayout container;
    private RelativeLayout Another_Route_Layout;
    private RelativeLayout Route_Layout;
    private LinearLayout flow_container;
    private FlowLayout Route_fl;
    private FlowLayout Another_fl;

    private Spinner spinner = null;
    private String[] spinnerArr = null;
    private String selected_spinner = null;

    private EditText dep_loc = null;
    private EditText arr_loc = null;

    private Button send = null;

    private GoogleMap mMap; // 구글 지도
    private Marker start_m; // 시작 마커
    private Marker end_m; // 도착 마커
    private Marker[][] marker_arr; // 중간 마커 배열
    private LatLng End_location; // 도착 위치 표시

    private Drawable img = null;

    /****************************** Directions API 관련 변수 *******************************/
    private static final String API_KEY = "";
    private String str_url = null; // URL
    private String option = null;
    private String step = null;
    private String[] full_time;
    private String[] hours;
    private String[] min;
    private String departure_lat = null;
    private String departure_lng = null;
    private String[][] goingS_lat;
    private String[][] goingS_lng;
    private String[][] goingE_lat;
    private String[][] goingE_lng;
    private String arrival_lat = null;
    private String arrival_lng = null;
    private String[][] TransitName;
    private String[][] getPolyline;
    private String getOverview = null;

    private int r_list_len = 0;
    private int[] list_len = null;
    private int fl_count = 0;
    private int R_fl_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_traffic_search);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        initView();

        dep_loc = findViewById(R.id.depart_loc);
        dep_loc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int KeyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && KeyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText enter_action = findViewById(R.id.arrive_loc);

                    enter_action.requestFocus();
                    return true;
                }
                return false;
            }
        });

        arr_loc = findViewById(R.id.arrive_loc);
        send = findViewById(R.id.send);
        arr_loc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int KeyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && KeyCode == KeyEvent.KEYCODE_ENTER) {
                    send.performClick();
                    return true;
                }
                return false;
            }
        });

        container = findViewById(R.id.container);
        Route_Layout = findViewById(R.id.Route_Layout);
        Another_Route_Layout = findViewById(R.id.Another_Route_Layout);
        flow_container = findViewById(R.id.flow_container);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void initView() {
        spinner = findViewById(R.id.spinner_menu);
        spinnerArr = getResources().getStringArray(R.array.traffic);
        selected_spinner = spinnerArr[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.traffic, R.layout.spinner_item);
        spinner.setAdapter(spinnerLargerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        option = "";
                        selected_spinner = spinnerArr[0];
                        break;
                    case 1:
                        option = "&transit_routing_preference=fewer_transfers";
                        selected_spinner = spinnerArr[1];
                        break;
                    case 2:
                        option = "&transit_routing_preference=less_walking";
                        selected_spinner = spinnerArr[2];
                        break;
                    default:
                        option = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void Change_EditText(View view) {

        if(!dep_loc.getText().equals("") && !arr_loc.getText().equals("")) {
            dep_loc = findViewById(R.id.depart_loc);
            arr_loc = findViewById(R.id.arrive_loc);
            TextView temp = new TextView(this);

            String d = dep_loc.getText().toString();
            String a = arr_loc.getText().toString();

            System.out.println("출발-도착 :" + d + "-" + "a");

            temp.setText(d);
            String t = temp.getText().toString();
            dep_loc.setText(a);
            arr_loc.setText(t);
        }

    }

    public void sendClick(View view) { // 검색 버튼을 클릭하면

        mMap.clear(); // 맵을 clear

        r_list_len = 0;
        list_len = null; // 다시 값 초기화

        container.setVisibility(container.VISIBLE);

        dep_loc = findViewById(R.id.depart_loc);
        arr_loc = findViewById(R.id.arrive_loc);

        String depart = dep_loc.getText().toString();
        String arrival = arr_loc.getText().toString();

        if (!depart.isEmpty() && !arrival.isEmpty()) {

            directions(depart, arrival);

            if (getOverview != null) {
                ArrayList<LatLng> entire_path = decodePolyPoints(getOverview);

                for (int i = 0; i < entire_path.size(); i++) {
                    if (i == 0) {
                        mMap.addMarker(new MarkerOptions().position(entire_path.get(i)).title("출발"));
                    } else if (i >= entire_path.size() - 1) {
                        mMap.addMarker(new MarkerOptions().position(entire_path.get(i)).title("도착"));
                    }
                }

                Polyline line = null;

                if (line == null) {
                    line = mMap.addPolyline(new PolylineOptions()
                            .color(Color.rgb(58, 122, 255))
                            .geodesic(true)
                            .addAll(entire_path));
                } else {
                    line.remove();
                    line = mMap.addPolyline(new PolylineOptions()
                            .color(Color.rgb(58, 122, 255))
                            .geodesic(true)
                            .addAll(entire_path));
                }
            }

            onMapReady(mMap);

            if (End_location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(End_location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        } else {
            if (!depart.isEmpty() && arrival.isEmpty())
                Toast.makeText(getApplicationContext(), "도착지를 작성해주세요.", Toast.LENGTH_SHORT).show();
            else if (depart.isEmpty() && !arrival.isEmpty())
                Toast.makeText(getApplicationContext(), "출발지를 작성해주세요.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "출발지와 도착지를 작성해주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    public void directions(String depart, String arrival) {

        str_url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + depart + "&destination=" + arrival + "&mode=transit" + "&departure_time=now" +
                option + "&alternatives=true&key=" + API_KEY;

        String resultText = "값이 없음";

        try {

            if(fl_count >= 1) {
                flow_container.removeAllViews();
                fl_count = 0;
            }
            if(R_fl_count >= 1) {
                Route_fl.removeAllViews();
                R_fl_count = 0;
            }

            resultText = new Task().execute().get(); // URL에 있는 내용을 받아옴

            JSONObject jsonObject = new JSONObject(resultText);
            String routes = jsonObject.getString("routes");
            JSONArray routesArray = new JSONArray(routes);

            r_list_len = routesArray.length();

            list_len = new int[r_list_len]; // route의 개수만큼 배열 동적 생성

            full_time = new String[r_list_len];
            hours = new String[r_list_len];
            min = new String[r_list_len];

            goingS_lat = new String[r_list_len][20]; // route의 개수만큼 그리고 그 안에 자잘한 route들을 최대 20으로 배열을 생성
            goingS_lng = new String[r_list_len][20];
            goingE_lat = new String[r_list_len][20];
            goingE_lng = new String[r_list_len][20];
            getPolyline = new String[r_list_len][20];
            TransitName = new String[r_list_len][20];

            marker_arr = new Marker[2][20];

            JSONObject preferredObject = routesArray.getJSONObject(0);
            String singleRoute = preferredObject.getString("overview_polyline");
            JSONObject pointsObject = new JSONObject(singleRoute);
            String points = pointsObject.getString("points");
            getOverview = points;

            for (int j = 0; j < routesArray.length(); j++) { // 배열들 생성 및 초기화

                JSONObject subJsonObject = routesArray.getJSONObject(j);

                String legs = subJsonObject.getString("legs");
                JSONArray LegArray = new JSONArray(legs);
                JSONObject legJsonObject = LegArray.getJSONObject(0);

                String steps = legJsonObject.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);

                list_len[j] = stepsArray.length(); // j번째 route에 step이 몇개인지 저장

                for (int i = 0; i < list_len[j]; i++) {

                    goingS_lat[j][i] = null;
                    goingS_lng[j][i] = null;
                    goingE_lat[j][i] = null;
                    goingE_lng[j][i] = null;
                    getPolyline[j][i] = null;
                    TransitName[j][i] = null;
                    marker_arr[0][i] = null;
                    marker_arr[1][i] = null;
                }

            }

            for (int j = 0; j < routesArray.length(); j++) {

                JSONObject subJsonObject = routesArray.getJSONObject(j);

                String legs = subJsonObject.getString("legs");
                JSONArray LegArray = new JSONArray(legs);
                JSONObject legJsonObject = LegArray.getJSONObject(0);

                String leg_duration = legJsonObject.getString("duration");
                JSONObject legdurObject = new JSONObject(leg_duration);
                String amountDuration = legdurObject.getString("text");
                String[] set_time = amountDuration.split(" ").clone();
                for (int k = 0; k < set_time.length; k++) {
                    if (set_time[k].equals("hour") || set_time[k].equals("hours")) {
                        hours[j] = set_time[k - 1] + "시간";
                    } else if (set_time[k].equals("mins") || set_time[k].equals("min")) {
                        min[j] = set_time[k - 1] + "분";
                    }
                }

                if (hours[j] == null || hours[j].isEmpty()) {
                    full_time[j] = min[j];
                } else {
                    full_time[j] = hours[j] + " " + min[j];
                }

                if(j > 0) {
                    TextView time = new TextView(this);
                    time.setText(full_time[j]);

                    time.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)time.getLayoutParams();
                    params.gravity = Gravity.RIGHT;
                    time.setLayoutParams(params);
                    time.setTextSize(28);

                    Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
                    time.setTypeface(typeface);

                    time.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                    time.setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));
                    time.setPadding(5, 0, 5, 0);

                    flow_container.addView(time);
                }

                Another_fl = new FlowLayout(TrafficSearchActivity.this);
                FlowLayout.LayoutParams param = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                // param.bottomMargin = 50;
                Another_fl.setLayoutParams(param);

                Another_fl.setOrientation(FlowLayout.HORIZONTAL);
                Another_fl.setBackgroundColor(Color.WHITE);
                flow_container.addView(Another_fl);

                String steps = legJsonObject.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);

                String[] getTravelMode = new String[list_len[j]]; // j번째 route의 step 수만큼 배열 동적 생성
                String[] getInstructions = new String[list_len[j]]; // 기차인지 판별하는 변수
                String isTrain = null;
                String[] getDuration = new String[list_len[j]];
                String[] arrival_name = new String[list_len[j]];
                String[] depart_name = new String[list_len[j]];
                String[] getTransit = new String[list_len[j]];

                for (int i = 0; i < list_len[j]; i++) {

                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    getTravelMode[i] = stepsObject.getString("travel_mode");

                    getInstructions[i] = stepsObject.getString("html_instructions");
                    isTrain = getInstructions[i].split(" ")[0];

                    String end_location = stepsObject.getString("end_location");
                    JSONObject endJsonObject = new JSONObject(end_location);
                    if (i >= list_len[j] - 1) {
                        arrival_lat = endJsonObject.getString("lat");
                        arrival_lng = endJsonObject.getString("lng");

                        Double End_lat = Double.parseDouble(arrival_lat);
                        Double End_lng = Double.parseDouble(arrival_lng);
                        End_location = new LatLng(End_lat, End_lng);
                    } else {
                        goingE_lat[j][i] = endJsonObject.getString("lat");
                        goingE_lng[j][i] = endJsonObject.getString("lng");
                    }

                    String start_location = stepsObject.getString("start_location");
                    JSONObject startJsonObject = new JSONObject(start_location);
                    if (i == 0) {
                        departure_lat = startJsonObject.getString("lat");
                        departure_lng = startJsonObject.getString("lng");
                    } else {
                        goingS_lat[j][i] = startJsonObject.getString("lat");
                        goingS_lng[j][i] = startJsonObject.getString("lng");
                    }

                    String polyline = stepsObject.getString("polyline");
                    JSONObject polyJsonObject = new JSONObject(polyline);
                    getPolyline[j][i] = polyJsonObject.getString("points"); // 인코딩 된 포인트를 얻어옴

                    String duration = stepsObject.getString("duration");
                    JSONObject durJsonObject = new JSONObject(duration);
                    String tempDuration = durJsonObject.getString("text");
                    getDuration[i] = tempDuration.split(" ")[0];

                    if (getTravelMode[i].equals("TRANSIT")) {

                        String transit_details = stepsObject.getString("transit_details");
                        JSONObject transitObject = new JSONObject(transit_details);

                        String arrival_stop = transitObject.getString("arrival_stop");
                        JSONObject arrivalObject = new JSONObject(arrival_stop);
                        arrival_name[i] = arrivalObject.getString("name");

                        String depart_stop = transitObject.getString("departure_stop");
                        JSONObject departObject = new JSONObject(depart_stop);
                        depart_name[i] = departObject.getString("name");

                        String line = transitObject.getString("line");
                        JSONObject lineObject = new JSONObject(line);
                        if (isTrain.equals("Train")) {
                            getTransit[i] = lineObject.getString("name");
                        } else if (isTrain.equals("Bus") || isTrain.equals("Subway")) {
                            getTransit[i] = lineObject.getString("short_name");
                            if (isTrain.equals("Subway") && getTransit[i].equals("1")) {
                                getTransit[i] += "호선";
                            }
                        }
                        TransitName[j][i] = getTransit[i];

                    }

                    if (i < list_len[j] - 1) {
                        if (getTravelMode[i].equals("WALKING")) {
                            step = "도보 " + getDuration[i] + "분 > ";

                        } else if (getTravelMode[i].equals("TRANSIT")) {
                            step = getTransit[i] + " > ";
                        }
                    } else {
                        if (getTravelMode[i].equals("WALKING")) {
                            step = "도보 " + getDuration[i] + "분";

                        } else if (getTravelMode[i].equals("TRANSIT")) {
                            step = getTransit[i];
                        }
                    }

                    Resources res = getResources();

                    switch (isTrain) {
                        case "Train":
                        case "Subway":
                            img = ResourcesCompat.getDrawable(res, R.drawable.subway_100, null);
                            break;
                        case "Bus":
                            img = ResourcesCompat.getDrawable(res, R.drawable.bus_96, null);
                            break;
                        default:
                            img = ResourcesCompat.getDrawable(res, R.drawable.walk_96, null);
                    }

                    method_view(step, full_time[j], img, j, i);
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<LatLng> decodePolyPoints(String encodedPath) {
        int len = encodedPath.length();

        final ArrayList<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    public void method_view(String a, String t, Drawable img, int j, int i) {

        TextView ith_route = null;

        if (j == 0) { // j가 0이라면 추천 경로이므로

            Route_fl = findViewById(R.id.Route_fl);

            ith_route = new TextView(this);
            ith_route.setText(a);
            ith_route.setTextSize(22);
            ith_route.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));

            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
            ith_route.setTypeface(typeface);

            ith_route.setTextColor(Color.parseColor("#6D6D6D"));

            int h = 90;
            int w = 90;
            img.setBounds(0, 0, w, h);
            ith_route.setCompoundDrawables(img, null, null, null);

            ith_route.setGravity(Gravity.CENTER_VERTICAL);
            ith_route.setPadding(0, 0, 0, 13);

            TextView time = findViewById(R.id.during_time);
            time.setText(t);

            R_fl_count += 1;
            Route_fl.addView(ith_route);
            Route_Layout.setVisibility(View.VISIBLE);

        } else if (j > 0) { // j가 0보다 크다면 다른 경로이므로

            ith_route = new TextView(this);
            ith_route.setText(a);
            ith_route.setTextSize(22);

            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
            ith_route.setTypeface(typeface);

            int h = 90;
            int w = 90;
            img.setBounds(0, 0, w, h);
            ith_route.setCompoundDrawables(img, null, null, null);

            ith_route.setGravity(Gravity.CENTER_VERTICAL);
            ith_route.setPadding(0, 0, 0, 13);

            fl_count += 1;
            Another_fl.addView(ith_route);
            Another_Route_Layout.setVisibility(View.VISIBLE);

        }

        final int t_num = j;

        ith_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();

                int j = t_num;

                double dlatitude = Double.parseDouble(departure_lat);
                double dlngtitude = Double.parseDouble(departure_lng);

                LatLng Start = new LatLng(dlatitude, dlngtitude);

                start_m = mMap.addMarker(new MarkerOptions().position(Start).title("출발"));

                for (int i = 0; i < list_len[j]; i++) {

                    ArrayList<LatLng> path_points = decodePolyPoints(getPolyline[j][i]); // 폴리라인 포인트 디코드 후 ArrayList에 저장

                    Polyline line = null;

                    if (line == null) {
                        line = mMap.addPolyline(new PolylineOptions()
                                .color(Color.rgb(58, 122, 255))
                                .geodesic(true)
                                .addAll(path_points));
                    } else {
                        line.remove();
                        line = mMap.addPolyline(new PolylineOptions()
                                .color(Color.rgb(58, 122, 255))
                                .geodesic(true)
                                .addAll(path_points));
                    }

                    if (goingE_lat[j][i] != null) {

                        double gelatitude = Double.parseDouble(goingE_lat[j][i]);
                        double gelngtitude = Double.parseDouble(goingE_lng[j][i]);
                        String Transit_n = TransitName[j][i];
                        String next_Transit_n = null;

                        if (i + 1 < list_len[j]) {
                            if (TransitName[j][i + 1] != null)
                                next_Transit_n = TransitName[j][i + 1];
                        }

                        LatLng GoingE = new LatLng(gelatitude, gelngtitude);

                        if (Transit_n == null) {
                            Transit_n = "도보";
                            if (next_Transit_n != null) {
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 후, " + next_Transit_n + " 승차"));
                            } else {
                                if (i == list_len[j] - 1) {
                                    marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 후, 도착"));
                                }
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n));
                            }
                        } else {
                            if (i == list_len[j] - 1) {
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 하차 후, 도착"));
                            }
                            marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 하차"));
                        }

                        onMapReady(mMap);
                    }

                    if (goingS_lat[j][i] != null) {

                        double gslatitude = Double.parseDouble(goingS_lat[j][i]);
                        double gslngtitude = Double.parseDouble(goingS_lng[j][i]);
                        String Transit_n = TransitName[j][i];
                        String prev_Transit_n = null;
                        if (i != 0) {
                            prev_Transit_n = TransitName[j][i - 1];
                        }

                        LatLng GoingS = new LatLng(gslatitude, gslngtitude);

                        if (Transit_n == null) {
                            Transit_n = "도보";
                            // 도보 전 지하철 하차, 버스 하차 등을 표시할 수 있도록
                            if (prev_Transit_n != null) {
                                marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(prev_Transit_n + "하차 후, " + Transit_n));
                            } else {
                                marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(Transit_n));
                            }
                        } else {
                            marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(Transit_n + " 승차"));
                        }

                        onMapReady(mMap);
                    }
                }

                double alatitude = Double.parseDouble(arrival_lat);
                double alngtitude = Double.parseDouble(arrival_lng);

                LatLng End = new LatLng(alatitude, alngtitude);

                end_m = mMap.addMarker(new MarkerOptions().position(End).title("도착"));

                onMapReady(mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(End));

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(str_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }

}