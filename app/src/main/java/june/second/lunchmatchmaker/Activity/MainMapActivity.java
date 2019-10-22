package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import june.second.lunchmatchmaker.Item.Match;
import june.second.lunchmatchmaker.Item.Place;
import june.second.lunchmatchmaker.Item.RealMatch;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Service.LunchMatchService;


public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback, MatchInformationFragment.markerSelectedListener {


    //디버깅용 현재 액티비티 문자값
    String here = "MainMapActivity";


    //현재 위치를 알기위한 값들---------------------------------------------------------------------
    //위치 요청할때 사용하는 값들
    private static final int LOCATION_REQUEST_INTERVAL = 1000;  //위치 요청하는 간격
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {               //현재 위치를 알기위한 권한
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private Double lat; //위도
    private Double lng; //경도

    //위치 실시간 추적 boolean 값들
    private boolean trackingEnabled;
    private boolean locationEnabled;
    private boolean waiting;


    //----------------------------------------------------------------------------------------------


    //객체 선언=====================================================================================
    private NaverMap map;                                       //지도 객체 선언


    //매치 객체 리스트
    static ArrayList<Match> matchArrayList = new ArrayList<Match>();
    static ArrayList<RealMatch> realMatchArrayList = new ArrayList<RealMatch>();


    // *fab = FloatingActionButton
    private FloatingActionButton fabLocation;                  //실시간 위치 추척바꾸어주는 fab
    private FloatingActionButton fabAdd;                       //매치 추가해주는 fab
    private FloatingActionButton fabNext;                      //매치리스트 액티비티로 넘어가는 fab
    private FloatingActionButton fabRecommend;                 //맛집 추천받는 fab
    private FloatingActionButton fabRecommendChange;           //맛집 추천 받아서 순위별로 보는 fab



    //마커의 정보창 객체----------------------------------------------------------------------------
    private InfoWindow matchInfoWindow = new InfoWindow();           //매치보는 정보창 객체

    //맛집 추천받았을때 내가 눌렀을때 정보창 객체
    private InfoWindow placeInfoWindow = new InfoWindow();

    //맛집 추천받았을때 순위별로 볼때 보이는 정보창 객체
    //7개씩 보여주는데 한번에 정보창을 띄워주려면 각각 객체로 선언해줘야됨
    private InfoWindow placeInfoWindowFist = new InfoWindow();
    private InfoWindow placeInfoWindowSecond = new InfoWindow();
    private InfoWindow placeInfoWindowThird = new InfoWindow();
    private InfoWindow placeInfoWindowFourth = new InfoWindow();
    private InfoWindow placeInfoWindowFifth = new InfoWindow();
    private InfoWindow placeInfoWindowSixth = new InfoWindow();
    private InfoWindow placeInfoWindowSeventh = new InfoWindow();
    //----------------------------------------------------------------------------------------------
    //==============================================================================================


    //마커 생성및 관리를 위한 값들------------------------------------------------------------------
    //마커를 생성할 곳을 지정해줄 크로스헤어
    private final PointF crosshairPoint = new PointF(Float.NaN, Float.NaN);

    //마커를 보관할 객체 배열
    public static List<Marker> matchMarkerList = new ArrayList<Marker>();
    public static List<Marker> placeMarkerList = new ArrayList<Marker>();

    //마커를 보일게할지 안보일게할지 결정하는 상태값
    private boolean markerVisible = true;
    //----------------------------------------------------------------------------------------------


    //맛집 검색을 위한 값들=========================================================================
    //맛집들을 넣어줄 객체 리스트
    static ArrayList<Place> placeArrayList = new ArrayList<Place>();

    //화면상의 왼쪽 아래와 오른쪽 위 좌표를 가져온다
    //네이버 플레이스로 맛집 검색을 하는데 두 좌표 사이의 음식점들을 검색하기 때문
    private final PointF crosshairPointLeftDown = new PointF(Float.NaN, Float.NaN);
    private final PointF crosshairPointRightUp = new PointF(Float.NaN, Float.NaN);
    LatLng crosshairLeftDownLatLng;
    LatLng crosshairRightUpLatLng;

    private int selectRange =0 ;    //맛집 순위별로 보여줄때 selectRange 을 기준으로 보여줌
    private TextView tvRecommendCount; //검색한 맛집 수를 나타내주는 텍스트

    //검색한 맛집을 순위별로 나타낼때 몇위부터 몇위까지 보여주는 텍스트
    //ex) 1위 ~ 7위
    private TextView tvRecommendValue;
    //맛집을 검색을 안하고 순위별로 보여달라고 하면 에러떠서
    //검색 안하고 순위별로 보여달라고 할때
    //이 값이 false 가 되어서 검색하고나서 순위별로 보여준다
    //true 이면 검색된 리스트에서 순위별로 보여준다.
    private Boolean RecommendRightNow = false; //


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Log.w(here + "  onCreate", "onCreate");


        //네이버 지도 프래그먼트 선언
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.mapFragment, mapFragment).commit();
        }

        //비동기로 Navermap 객체를 얻어올 수 있음
        mapFragment.getMapAsync(this);


        //현재 위치 동기화일 때---------------------------------------------------------------------
        //why. onCreate 에 있는 이유 처음시작할때 내 위치에서 시작하고 싶어서
        tryEnableLocation();     //동기화해주는 메서드
        trackingEnabled = true;  //동기화 상태 trackingEnabled : true 로 조절
        fabLocation = findViewById(R.id.fab);  //fabLocation = fab = *floating action button => 줄여서
        fabLocation.setImageResource(R.drawable.ic_location_disabled_black_24dp); //동기화 이미지 설정
        //------------------------------------------------------------------------------------------

        //fab 레이아웃의 뷰하고 연결해주기
        fabAdd = findViewById(R.id.fabAdd);
        fabNext = findViewById(R.id.fabMoveToList);
        fabRecommend = findViewById(R.id.fabRecommend);
        fabRecommendChange = findViewById(R.id.fabRecommendChange);
        tvRecommendValue = findViewById(R.id.recommendValue);
        tvRecommendCount = findViewById(R.id.recommendCount);


        Intent startServiceIntent = new Intent(this, LunchMatchService.class);
        startService(startServiceIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.w(here + "  onStart", "onStart");
        //#실험
        //내 위치 추적 가능하게
        if (trackingEnabled) {
            enableLocation();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here + "  onResume", "onResume");

        //매치 값들 불러오기------------------------------------------------------------------------
        //매치 값을 저장한 쉐어드 불러오기
        SharedPreferences prefMatch = getSharedPreferences("prefMatch", MODE_PRIVATE);
        SharedPreferences.Editor prefMatchEditor = prefMatch.edit();

        matchArrayList.clear();

        //쉐어드에서 불러온 매치 json 값들 매치 객체로 만들어서 matchArrayList 에 저장하기
        Map<String, ?> allEntries = prefMatch.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            Log.w("map values", entry.getKey() + ": " + entry.getValue().toString());
            try {
                JSONObject matchJsonObject = new JSONObject(entry.getValue().toString());
                ArrayList<String> matchKeywordArray = new ArrayList<String>();
                matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[0]);
                matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[1]);
                matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[2]);

                //json 받아온 스트링값을 다시 LatLng 객체로 만들어주기
                Double lat = Double.parseDouble(matchJsonObject.getString("matchMarker").split(",")[0].split("=")[1]);
                Double lng = Double.parseDouble(matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].
                        substring(0, matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].length() - 1));
                LatLng coord = new LatLng(lat, lng);

                Marker marker = new Marker();
                Match match = new Match(matchJsonObject.getInt("matchIndex"), matchJsonObject.getString("matchTitle"),
                        matchJsonObject.getString("matchTime"), matchJsonObject.getString("matchPlace"), matchJsonObject.getInt("matchPeople"), matchKeywordArray, coord);

                Log.w(here, "matchJsonObject.getInt(\"matchIndex\"): " + matchJsonObject.getInt("matchIndex"));
                Log.w(here, "matchJsonObject.getString(\"matchTitle\"): " + matchJsonObject.getString("matchTitle"));
                Log.w(here, "matchJsonObject.getString(\"matchTime\"): " + matchJsonObject.getString("matchTime"));
                Log.w(here, "matchJsonObject.getString(\"matchPlace\"): " + matchJsonObject.getString("matchPlace"));
                Log.w(here, "matchJsonObject.getInt(\"matchPeople\"): " + matchJsonObject.getInt("matchPeople"));
                Log.w(here, "(matchJsonObject.getString(\"matchKeyword\") : " + matchJsonObject.getString("matchKeyword"));
                Log.w(here, "(matchJsonObject.getString(\"matchKeyword1\") : " + matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[0]);
                Log.w(here, "(matchJsonObject.getString(\"matchKeyword2\") : " + matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[1]);
                Log.w(here, "(matchJsonObject.getString(\"matchKeyword3\") : " + matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[2]);
                Log.w(here, "(matchJsonObject.getString(\"matchMarker\" : Lat : " + matchJsonObject.getString("matchMarker").split(",")[0].split("=")[1]);
                Log.w(here, "(matchJsonObject.getString(\"matchMarker\" : Lng : " + matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].
                        substring(0, matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].length() - 1));

                matchArrayList.add(match);
                Log.w(here, "matchArrayList: " + matchArrayList.size());


            } catch (ArrayIndexOutOfBoundsException | JSONException e) {
                // 중복되지 않는 키값을 위해 마지막으로 저장한 키값에다 +1 해서 lastposition 이라는 키값에 저장해두는데
                // 위에서 스토리 객체를 만들때 모든 키 값을 불러와서 split 으로 나누기때문에 ArrayIndexOutOfBoundsException가 뜬다
                //lastposition 는 숫자로만 이루어져있어서
                //그래서 여기서 에러나는 것을 방지하기 위해서 try catch문을 사용했다.
                Log.w(here, "ArrayIndexOutOfBoundsException => lastposition  ");
                e.printStackTrace();
            }


        }

        //매치 마커 리스트도 갱신해주기-------------------------------------------------------------
        matchMarkerList.clear();
        for (int i = 0; i < matchArrayList.size(); i++) {
            addMarker(map, matchArrayList.get(i).getLatLng(), matchInfoWindow, i);
        }
        setMarkers(map, matchMarkerList);
        //------------------------------------------------------------------------------------------


        //Fragment 갱신-----------------------------------------------------------------------------
        //프래그먼트의 어댑터 갱신
        MatchInformationFragment.adapter.clear();                       //어댑터의 데이터 정리(삭제)
        for (int i = 0; i < matchArrayList.size(); i++) {  //어댑터에 마커들 다시 새로 추가해주기
            MatchInformationFragment.arrayListTag.add(matchArrayList.get(i).getMatchTitle());
        }
        MatchInformationFragment.adapter.notifyDataSetChanged();  //어댑터 갱신

        // 프래그먼트 갱신
        Fragment frg = null;
        //Tag로 MatchInformationFragment 가져오기
        frg = getSupportFragmentManager().findFragmentByTag("fragment_match_information");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
        //------------------------------------------------------------------------------------------


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here + "  onPause", "onPause");

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here + "  onStop", "onStop");
        //내 위치 추적 해제하기
        disableLocation();

    }


    //현재위치 알려주는 메소드
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (map == null) {
                return;
            }
            //최신 위치
            Location lastLocation = locationResult.getLastLocation();
            //위도(lat) 경도(lng) 객체
            LatLng coord = new LatLng(lastLocation);
            //내 위치 나태내주는 작은 원(위치 오버레이 -locationOverlay)
            LocationOverlay locationOverlay = map.getLocationOverlay();
            //내 위치를 나타내주는 원 최신 위치의 위도 경도
            locationOverlay.setPosition(coord);
            locationOverlay.setBearing(lastLocation.getBearing());

            //내 위치에 따라 카메라 바로 이동하게 설정
            map.moveCamera(CameraUpdate.scrollTo(coord));
            if (waiting) {
                waiting = false;
                fabLocation.setImageResource(R.drawable.ic_location_disabled_black_24dp);
                locationOverlay.setVisible(true);
            }
        }
    };

    //NaverMap 객체가 준비되면 onMapReady() 콜백 메서드가 호출됨
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.w(here + "  onMapReady", "onMapReady");

        //네이버 지도 객체 선언
        this.map = naverMap;

        //매치 정보창 어댑터 만들고 설정해주기------------------------------------------------------
        InfoWindow.DefaultViewAdapter matchInfoWindowAdapter = new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_match, null, false);
                //마커의 태그를 기준으로 가져오는 건 =>
                //태그를 저장할때 매치리스트(matchArrayList)에서의 인덱스로 저장했기 때문


                TextView matchInfoName = view.findViewById(R.id.matchInfoName);
                matchInfoName.setText(matchArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMatchTitle());

                TextView matchInfoTime = view.findViewById(R.id.matchInfoTime);
                matchInfoTime.setText(matchArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMatchTime());

                //인원수 표시는 일단 보류 (파이어베이스에서 값을 가져오는것 + 디자인 때문에)
/*                TextView matchInfoPeople = view.findViewById(R.id.matchInfoPeople);
                matchInfoPeople.setText(matchArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMatchPeople());*/

                TextView matchInfoPlace = view.findViewById(R.id.matchInfoPlace);
                matchInfoPlace.setText(matchArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMatchPlace());

                TextView matchInfoReview = view.findViewById(R.id.matchInfoKeyword);
                matchInfoReview.setText(matchArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMatchKeyword().get(0));


                return view;
            }
        };

        matchInfoWindow.setAdapter(matchInfoWindowAdapter);
        //------------------------------------------------------------------------------------------


        //크롤링해서 보여주는 맛집 정보창 어댑터 만들고 설정해주기----------------------------------
        InfoWindow.DefaultViewAdapter placeInfoWindowAdapter = new InfoWindow.DefaultViewAdapter(this) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_place, null, false);
                //마커의 태그를 기준으로 가져오는 건 =>
                //태그를 저장할때 맛집리스트(placeArrayList)에서의 인덱스로 저장했기 때문


                TextView placeName = view.findViewById(R.id.placeName);
                placeName.setText(placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getName());

                TextView placeCategory = view.findViewById(R.id.placeCategory);
                placeCategory.setText(placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getCategory());

                TextView placePrice = view.findViewById(R.id.placePrice);
                placePrice.setText(placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getPriceCategory());

                TextView placeReviewCount = view.findViewById(R.id.placeReviewCount);
                placeReviewCount.setText("리뷰  " + placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getTotalReviewCount());

                TextView placeReview = view.findViewById(R.id.placeReview);
                if (placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMicroReview() == null
                        || placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMicroReview().length() == 0) {
                    placeReview.setVisibility(View.GONE);
                } else {
                    placeReview.setText(placeArrayList.get(Integer.parseInt(infoWindow.getMarker().getTag().toString())).getMicroReview());
                }

                return view;
            }
        };

        placeInfoWindow.setAdapter(placeInfoWindowAdapter);
        //------------------------------------------------------------------------------------------


        //카메라 리스너-----------------------------------------------------------------------------
        //카메라 움직일때마다 바로 변화 적용시키는 부분
        naverMap.addOnCameraChangeListener((reason, animated) -> {
//            LatLng coord = marker.getPosition();
//            PointF point = naverMap.getProjection().toScreenLocation(coord);
//            marker.setCaptionText(getString(R.string.format_point_coord,
//                    point.x, point.y, coord.latitude, coord.longitude));

            //크로스헤어 좌표 업데이트
            updateCrosshairCoord();
        });
        //------------------------------------------------------------------------------------------

        //크로스헤어================================================================================
        //매치를 생성하는 크로스헤어는 액티비티의 가운데에 오도록 설정
        View crosshair = findViewById(R.id.crosshair);
        crosshair.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            crosshairPoint.set(crosshair.getX() + crosshair.getWidth() / 2f,
                    crosshair.getY() + crosshair.getHeight() / 2f);
            updateCrosshairCoord();
        });

        //보이는 지도에서 왼쪽아래와 오른쪽위의 보이지 않는 크로스헤어------------------------------
        //인테넷을 통해 음식점을 검색 하는데 이때 두 좌표 사이에 있는 음식점들을 검색한다.
        //그래서 지도의 왼쪽아래와 오른쪽위의 좌표를 가져온다
        View crosshairLeftDown = findViewById(R.id.crosshairLeftDown);
        crosshairLeftDown.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            crosshairPointLeftDown.set(crosshairLeftDown.getX() + crosshairLeftDown.getWidth() / 2f,
                    crosshairLeftDown.getY() + crosshairLeftDown.getHeight() / 2f);
            updateCrosshairCoord();
        });
        View crosshairRightUp = findViewById(R.id.crosshairRightUp);
        crosshairRightUp.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            crosshairPointRightUp.set(crosshairRightUp.getX() + crosshairRightUp.getWidth() / 2f,
                    crosshairRightUp.getY() + crosshairRightUp.getHeight() / 2f);
            updateCrosshairCoord();
        });
        //------------------------------------------------------------------------------------------
        //==========================================================================================


        //가까이 볼 때 실내지도도 보이게 설정
        naverMap.setIndoorEnabled(true);


        //fab(플로팅 액션 버튼- floating action button) 동작 설정===================================

        //fabLocation 누를 때의 동작 변화 설정------------------------------------------------------
        fabLocation.setOnClickListener(v -> {
            if (trackingEnabled) {
                //원래 위치 추적중이였다면
                //위치 추적 해제
                disableLocation();
                //위치 추적 해제시의 이미지로 바꾸기
                fabLocation.setImageResource(R.drawable.ic_my_location_black_24dp);
            } else {
                //원래 위치 추적중이 아니였다면
                //위치 추적에 들어가기
                //위치 추적에 시간이 걸릴때 CircularProgressDrawable 보이게
                // 동그랗게 돌면서 로딩할때 나타나는 이미지
                CircularProgressDrawable progressDrawable = new CircularProgressDrawable(this);
                progressDrawable.setStyle(CircularProgressDrawable.LARGE);
                progressDrawable.setColorSchemeColors(Color.WHITE);
                progressDrawable.start();
                fabLocation.setImageDrawable(progressDrawable);
                //위치 추적 시작(시도)
                tryEnableLocation();
            }
            //위치 추적 상태 반전주기
            trackingEnabled = !trackingEnabled;
        });
        //------------------------------------------------------------------------------------------


        //추가 버튼 누를때 동작 변화----------------------------------------------------------------
        fabAdd.setOnClickListener(v -> {
            //이동
            Intent intent = new Intent(this, MatchAddActivity.class);

            //데이터 임시 추가
            LatLng coord = map.getProjection().fromScreenLocation(crosshairPoint);
            Log.w(here + "  coord", coord.toString());
            intent.putExtra("lat", coord.latitude);
            intent.putExtra("lng", coord.longitude);


            startActivity(intent);
        });
        //------------------------------------------------------------------------------------------

        //추가 버튼 길게 누를때 동작 변화-----------------------------------------------------------
        fabAdd.setOnLongClickListener(v -> {
            if (markerVisible) {
                //마커들이 보이는 상태이면
                //마커들을 모두 지도에 표시해주기
                setMarkers(map, matchMarkerList);
                markerVisible = !markerVisible;
            } else {
                //마커들이 안 보이는 상태이면
                //마커들을 모두 지도에 표시 안 해주기
                setMarkers(null, matchMarkerList);
                markerVisible = !markerVisible;
            }
            //true 이면 long 클릭일때 일반 클릭이 중복돼서 안 들어간다
            return true;
        });
        //------------------------------------------------------------------------------------------

        //리스트 그림 버튼 누를때 동작 변화---------------------------------------------------------
        //매치 리스트 액티비티로 이동
        fabNext.setOnClickListener(v -> {
            Intent intent = new Intent(MainMapActivity.this, MatchListActivity.class);
            startActivity(intent);
        });
        //------------------------------------------------------------------------------------------


        //fabRecommend 누를 때의 동작 변화 설정-----------------------------------------------------
        //맛집추천
        fabRecommend.setOnClickListener(v -> {
            //현재 유저가 보고 있는 지도의 왼쪽아래와 오른쪽위에 좌표값을 가져온다
            crosshairLeftDownLatLng = map.getProjection().fromScreenLocation(crosshairPointLeftDown);
            crosshairRightUpLatLng = map.getProjection().fromScreenLocation(crosshairPointRightUp);
            Log.w(here, "doInBackground: crosshairLeftDown latitude  :  " + crosshairLeftDownLatLng.latitude +
                    "    crosshairLeftDown longitude  :  " + crosshairLeftDownLatLng.longitude);
            Log.w(here, "doInBackground: crosshairRightup latitude  :  " + crosshairRightUpLatLng.latitude +
                    "    crosshairRightup longitude  :  " + crosshairRightUpLatLng.longitude);


            //AsyncTask 작동시킴(맛집 파싱)
            new RestaurantCrawling().execute();
            tvRecommendValue.setText("");

        });
        //------------------------------------------------------------------------------------------


        //fabRecommendChange 누를 때의 동작 변화 설정-----------------------------------------------
        //맛집 추천 순위별로 보여주기
        fabRecommendChange.setOnClickListener(view -> {

            //검색된 맛집이 없을때 맛집 검색하고 순위별로 부여주기
            if(placeMarkerList ==null || placeMarkerList.size() ==0 ){
                //AsyncTask 작동시킴(맛집 파싱)
                RecommendRightNow = true;
                //누른 당시의 보이는 지도의 왼쪽아래와 오른쪽위 좌표값 가져오기
                //맛집 크롤링을 두 좌표 사이의 값으로 검색하기 때문에
                crosshairLeftDownLatLng = map.getProjection().fromScreenLocation(crosshairPointLeftDown);
                crosshairRightUpLatLng = map.getProjection().fromScreenLocation(crosshairPointRightUp);
                new RestaurantCrawling().execute();
            }
            //검색된 맛집이 있으면 순위별로 맛집 보여주기
            //누를때마다 보여지는 순위가 바뀐다.
            //ex) 처음에 1~7위 보여주고
            //여기서 누르면 8~14위 보여줌
            else{
                recommendChange();
            }

        });
        //------------------------------------------------------------------------------------------

        //맛집 추천받았을때 순위별로 볼때 보이는 정보창 객체----------------------------------------
        //7개씩 보여주는데 한번에 정보창을 띄워주려면 각각 객체로 선언해줘야됨
        //각각 맛집 어댑터 설정 및 투명도 70%보이게 설정
        //가시성을 위해
        placeInfoWindowFist.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowSecond.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowThird.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowFourth.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowFifth.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowSixth.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowSeventh.setAdapter(placeInfoWindowAdapter);
        placeInfoWindowFist.setAlpha(0.7f);
        placeInfoWindowSecond.setAlpha(0.7f);
        placeInfoWindowThird.setAlpha(0.7f);
        placeInfoWindowFourth.setAlpha(0.7f);
        placeInfoWindowFifth.setAlpha(0.7f);
        placeInfoWindowSixth.setAlpha(0.7f);
        placeInfoWindowSeventh.setAlpha(0.7f);
        //------------------------------------------------------------------------------------------



        //------------------------------------------------------------------------------------------
    }

    //순위 바꾸어주는 메소드------------------------------------------------------------------------
    //맛집 추천 순위별로 보여줄때
    public void recommendChange() {

        //맛집은 7개씩 보여준다
        int selectMin = selectRange*7;
        int selectMax = selectRange*7+7;

        //보여지는 범위가 인덱스를 넘어가면
        //범위의 최대를 마지막 인덱스 값(사이즈 -1)으로 바꾸어준다.
        //그리고 다음에 누를때 다시 처음부터(1위~7위) 보여주게
        //selectRange 값을 0으로 바꾸어준다
        if(selectMax  >  placeMarkerList.size() -1 ){

            selectMax = placeMarkerList.size();
            selectRange =0;
        }
        else{
            selectRange = selectRange+1;

        }

        //ex) 1위 ~ 7위
        //ex) 15 ~ 24위
        String recommendText = (selectMin+1) + "  ~  " + selectMax+"위";
        tvRecommendValue.setText(recommendText);

        Log.w(here, "fabRecommendChange - recommendText : "+ recommendText );
        Log.w(here, "fabRecommendChange - selectMin : "+ selectMin );
        Log.w(here, "fabRecommendChange - selectMax  : "+ selectMax );
        Log.w(here, "fabRecommendChange - placeMarkerList.size  : "+ placeMarkerList.size() );

        //보여지는 범위내에 있는 맛집들을 마커보이게 설정한다.
        //+ 정보창 객체 연결
        int selectScale = selectMax - selectMin;
        for (int i = 0; i < selectScale; i++) {
            switch (i){
                case 0 :
                    Marker markerFirst = placeMarkerList.get(selectMin);
                    markerFirst.setMap(map);
                    placeInfoWindowFist.open(markerFirst);
                    break;
                case 1 :
                    Marker markerSecond = placeMarkerList.get(selectMin + 1);
                    markerSecond.setMap(map);
                    placeInfoWindowSecond.open(markerSecond);
                    break;
                case 2 :
                    Marker markerThird = placeMarkerList.get(selectMin + 2);
                    markerThird.setMap(map);
                    placeInfoWindowThird.open(markerThird);
                    break;
                case 3 :
                    Marker markerFourth = placeMarkerList.get(selectMin + 3);
                    markerFourth.setMap(map);
                    placeInfoWindowFourth.open(markerFourth);
                    break;
                case 4 :
                    Marker markerFifth = placeMarkerList.get(selectMin + 4);
                    markerFifth.setMap(map);
                    placeInfoWindowFifth.open(markerFifth);
                    break;
                case 5 :
                    Marker markerSixth = placeMarkerList.get(selectMin + 5);
                    markerSixth.setMap(map);
                    placeInfoWindowSixth.open(markerSixth);
                    break;
                case 6 :
                    Marker markerSeventh = placeMarkerList.get(selectMin + 6);
                    markerSeventh.setMap(map);
                    placeInfoWindowSeventh.open(markerSeventh);
                    break;
                default:
                    break;
            }
        }

        //보여지는 범위가 아닌 맛집들을 마커가 안 보이게 설정한다.
        for (int i = 0; i < placeMarkerList.size(); i++) {
            if(  i<selectMin || selectMax<= i ){
                placeMarkerList.get(i).setMap(null);
            }
        }
    }


    //맛집 크롤링 에이씽크 태스크-------------------------------------------------------------------
    private class RestaurantCrawling extends AsyncTask<Void, Integer, ArrayList<Place>> {

        //진행바 표시
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행다이로그 시작
            progressDialog = new ProgressDialog(MainMapActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시 기다려 주세요.");
            progressDialog.show();

            //값 초기화-----------------------------------------------------------------------------
            selectRange =0;             //맛집 추천범위 초기화
            placeArrayList.clear();     //맛집 리스트
            setMarkers(null, placeMarkerList);  //새로 맛집 검색하는 거니깐 전에 있던 맛집 마커들 삭제
            placeMarkerList.clear();    //맛집 마커리스트 초기화
            //--------------------------------------------------------------------------------------
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... voids) {

            try {
//                Document doc = Jsoup.connect("https://store.naver.com/restaurants/list?bounds=126.9670136  %3B  37.4819729  %3B  126.9793506  %3B  37.4872828  &filterId=r02190105&query=%EB%A7%9B%EC%A7%91").get();
                //네이버 플레이스에서 값들을 크롤링해오는데 링크를 분석해보면 두 좌표 사이간의 음식점들을 검색해준다
                //+ query=%EB%A7%9B%EC%A7%91 => 검색키워드를 나타내는데 여기서는 맛집을 뜻함
                //그리고 뒤에 보면 sortingOrder=reviewCount 가 있는데 리뷰순으로 정렬해서 보여줌
                Document doc = Jsoup.connect("https://store.naver.com/restaurants/list?bounds=" + crosshairLeftDownLatLng.longitude + "%3B" + crosshairLeftDownLatLng.latitude + "%3B"
                        + crosshairRightUpLatLng.longitude + "%3B" + crosshairRightUpLatLng.latitude + "&filterId=r02190105&query=%EB%A7%9B%EC%A7%91&sortingOrder=reviewCount").get();
                Log.w(here, "doInBackground - doc :  " + "https://store.naver.com/restaurants/list?bounds=" + crosshairLeftDownLatLng.longitude + "%3B" + crosshairLeftDownLatLng.latitude + "%3B"
                        + crosshairRightUpLatLng.longitude + "%3B" + crosshairRightUpLatLng.latitude + "&filterId=r02190105&query=%EB%A7%9B%EC%A7%91");

                //*네이버플레이스에서는 음식점 리스트를 객체에 담아서 관리하고 있음
                //해당 객체에 접근
                Elements mElementDataSize = doc.select("script");//필요한 녀석만 꼬집어서 지정
                Log.w(here, "getElement: " + mElementDataSize.get(2).data());
                String placeState = mElementDataSize.get(2).data();

                //마지막 맛집 앞에서 값을 끊은 후 concat 으로 문자열을 자른 후에도 json 구조를 유지하게 해준다.
                String placeStateParse = placeState.substring(placeState.indexOf("{\"items"), placeState.lastIndexOf(",{\"id")).concat("]}");

                //가져온 값들 로그값으로 체크-------------------------------------------------------
                String logPlaceStateParse = placeStateParse;
                //log 길떄 체크하는거
                int log_index = 1;
                try {
                    while (logPlaceStateParse.length() > 0) {
                        if (logPlaceStateParse.length() > 4000) {
                            Log.w(here, "json - " + log_index + " : "
                                    + logPlaceStateParse.substring(0, 4000));
                            logPlaceStateParse = logPlaceStateParse.substring(4000);
                            log_index++;
                        } else {
                            Log.w(here, "json - " + log_index + " :" + logPlaceStateParse);
                            break;
                        }
                    }
                } catch (Exception e) {
                }
                //----------------------------------------------------------------------------------



                while (-1 != placeStateParse.indexOf(",{\"id")) {
                    try {

                        try {
                            //크롤링으로 가져온 json 객체를 gson 으로 파싱
                            Gson gson = new Gson();
                            //안에 있는 값들은
                            // {id : ~~~~~, name : ~~~~, category : ~~~ ... },{id : ~~~~~, name : ~~~~ ,category : ~~~ ... ),{id : ~~.....
                            // 대략 위와 같은 구조를 가지고 있음
                            JSONObject placeJsonObject = new JSONObject(placeStateParse.substring(placeStateParse.indexOf("{\"id"), placeStateParse.indexOf(",{\"id")));
                            Log.w(here, "placeJsonObject : " + placeJsonObject.toString());
                            Place place = gson.fromJson(String.valueOf(placeJsonObject), Place.class);
                            Log.w(here, "place -  Name  :  " + place.getName());
                            Log.w(here, "place -  getCategory  :  " + place.getCategory());
                            Log.w(here, "place -  getPriceCategory  :  " + place.getPriceCategory());
                            Log.w(here, "place -  getMicroReview  :  " + place.getMicroReview());
                            Log.w(here, "place -  getTotalReviewCount  :  " + place.getTotalReviewCount());
                            Log.w(here, "place -  getX  :  " + place.getX());
                            Log.w(here, "place -  getY  :  " + place.getY());

                            //맛집 리스트에 맛집 객체 추가
                            placeArrayList.add(place);
                        } catch (JSONException e) {
                            Log.w(here, "JSONException ");
                            e.printStackTrace();
                        }
                        //20은 임의의 수 => 지금 가져온 객체가 아닌 바로 뒤의 객체부터 가져오게
                        // 뒤의 객체가 시작하는 인덱스부터 끝까지 문자열을 잘라서 가져온다
                        // 한 객체의 길이가 워낙 길기때문에 20이면 바로뒤의 객체를 빠지지 않고 가져온다
                        placeStateParse = placeStateParse.substring(placeStateParse.indexOf("{\"id", 20));
//                        Log.w(here, "placeStateParse: " + placeStateParse);
                    } catch (StringIndexOutOfBoundsException e) {
                        //객체 문자열 자르다 오류나면 캐치하게 설정
                        Log.w(here, "StringIndexOutOfBoundsException ");
                        break;
                    }
                }
                Log.w(here, "while Finish ");
                Log.w(here, "placeArrayList.size  :  " + placeArrayList.size());


            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < placeArrayList.size(); i++) {
                publishProgress(i);
            }
            return placeArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> restaurantsList) {

/*            for (int i = 0; i < placeArrayList.size(); i++) {
                addMarker(map, new LatLng(placeArrayList.get(i).getY(), placeArrayList.get(i).getX()), infoWindow, placeArrayList.get(i).getName());

            }*/


            if(RecommendRightNow){

                progressDialog.dismiss();

                //RecommendRightNow 이 true 인건
                //사용자가 맛집을 검색을 안하고 바로 순위별로 추천을 눌렀기 때문이다.
                //그래서 맛집 검색을하고 바로 순위별로 추천해주는 메소드를 실행시켜주고
                //다시 RecommendRightNow 값을 false 로 바꾸어준다.
                recommendChange();
                RecommendRightNow =false;

            }
            else{
                progressDialog.dismiss();
            }

            //맛집 크롤링후의 몇개의 음식점이 검색되었는지 텍스트와 토스트로 알려주기
            tvRecommendCount.setText(Integer.toString(placeMarkerList.size())+"개의 음식점");
            Toast.makeText(getApplicationContext() , placeMarkerList.size() +"개의 음식점을 찾았습니다!", Toast.LENGTH_LONG).show();


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            //업데이트 될때마다 마커 추가해주기
            addMarkerByRecommed(map, new LatLng(placeArrayList.get(progress[0]).getY(), placeArrayList.get(progress[0]).getX()), placeInfoWindow, progress[0]);

//          클러스터링용
            /*
            addMarkerByClustering(map, new LatLng(placeArrayList.get(progress[0]).getY(), placeArrayList.get(progress[0]).getX()), infoWindow, progress[0]);*/

//            progressDialog.setProgress((100 / placeArrayList.size()) * progress[0]);


        }
    }
    //----------------------------------------------------------------------------------------------


    //마커를 생성하고 지도위에 표시하는 메서드
    //String matchTitle 나중에 여기 데이터 저장 객체 들어가야됨 (지금은 임시)
    private void addMarker(NaverMap naverMap, LatLng latLng, InfoWindow infoWindow, int matchMarkerIndex) {

        // 마커를 생성합니다(latLng - 위도경도값 객체)
        Marker marker = new Marker(latLng);

        // 마커가 지도 위에 표시되도록 설정합니다
        marker.setMap(naverMap);

        //매치 마커는 맛집추천 해주는 마커들과 다르게
        //겹쳐도 무조건 보이게 설정하고 분홍색으로 설정
        marker.setZIndex(200);
        marker.setHideCollidedMarkers(true);
        marker.setForceShowIcon(true);
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(getResources().getColor(R.color.recipeHotPink));


        //생성된 마커에 태그 지정해주기
        marker.setTag(matchMarkerIndex);

        //마커에 리스너 생성해주기
        marker.setOnClickListener(overlay -> {
            infoWindow.open(marker);
            return true;
        });

        // 생성된 마커를 마커 객체 리스트에 추가합니다
        matchMarkerList.add(marker);

        //디버깅- matchMarkerList 체크
//        Log.w("matchMarkerList", matchMarkerList.size() + "");

    }


    //마커를 생성하고 지도위에 표시하는 메서드
    //String matchTitle 나중에 여기 데이터 저장 객체 들어가야됨 (지금은 임시)
    private void addMarkerByRecommed(NaverMap naverMap, LatLng latLng, InfoWindow infoWindow, int placeIndex) {


        // 마커를 생성합니다(latLng - 위도경도값 객체)
        Marker marker = new Marker(latLng);

        // 마커가 지도 위에 표시되도록 설정합니다
        marker.setMap(naverMap);


        //맛집을 보여주는 마커들은 다른 마커들하고 겹칠때
        //안보이게된다.
        //그때 이때 순위별로도 차등을 둬서
        //높은 순위일수록 겹쳐도 보이게 해주고 색깔도 진한색으로 설정해준다.
        if (placeIndex < 20) {
            marker.setZIndex(100);
            marker.setHideCollidedMarkers(true);
            marker.setForceShowIcon(false);
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.parseColor("#519D9E"));

        } else if (placeIndex < 40) {
            marker.setZIndex(50);
            marker.setHideCollidedMarkers(true);
            marker.setForceShowIcon(false);
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.parseColor("#58C9B9"));
        } else if (placeIndex < 60) {
            marker.setZIndex(40);
            marker.setHideCollidedMarkers(true);
            marker.setForceShowIcon(false);
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.parseColor("#9DC8C8"));
        } else if (placeIndex < 80) {
            marker.setZIndex(30);
            marker.setHideCollidedMarkers(true);
            marker.setForceShowIcon(false);
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.parseColor("#9DC8C8"));
        } else {
            marker.setZIndex(20);
            marker.setHideCollidedMarkers(true);
            marker.setForceShowIcon(false);
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.parseColor("#9DC8C8"));
        }


        //생성된 마커에 태그 지정해주기
        marker.setTag(placeIndex);

        //마커에 리스너 생성해주기
        marker.setOnClickListener(overlay -> {
            infoWindow.open(marker);
            return true;
        });


        marker.setAlpha(0.7f);

        // 생성된 마커를 마커 객체 리스트에 추가합니다
        placeMarkerList.add(marker);

        //디버깅- matchMarkerList 체크
//        Log.w("matchMarkerList", matchMarkerList.size() + "");

    }


    // 리스트의 마커들을 한번에 관리하는 메소드
    private void setMarkers(NaverMap map, List<Marker> makerList) {
        for (int i = 0; i < makerList.size(); i++) {
            makerList.get(i).setMap(map);
        }
    }


    //현재위치 관련된 메소드들----------------------------------------------------------------------

    //위치 요청 메소드 (권한 체크 및 요청 + 위치 추적 메소드 실행하는 메소드)
    private void tryEnableLocation() {
        Log.w(here + "  tryEnableLocation", "tryEnableLocation");

        //현재위치 정보 받는 권한에 대해서 체크하기
        if (ContextCompat.checkSelfPermission(this, PERMISSIONS[0]) == PermissionChecker.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, PERMISSIONS[1]) == PermissionChecker.PERMISSION_GRANTED) {
            //현재위치로 가는 메소드
            enableLocation();
        } else {
            //현재위치에 대한 권한이 없으면 요청하기
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    //위치 추적 메소드
    private void enableLocation() {
        Log.w(here + "  enableLocation", "enableLocation");

        new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationRequest locationRequest = new LocationRequest();
                        //위치 요청 우선순위 높음
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        //위치 요청 간격 설정
                        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
                        locationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL);

                        LocationServices.getFusedLocationProviderClient(MainMapActivity.this)
                                .requestLocationUpdates(locationRequest, locationCallback, null);
                        locationEnabled = true;
                        waiting = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addApi(LocationServices.API)
                .build()
                .connect();
    }

    //위치 추적 해제 메소드
    private void disableLocation() {
        Log.w(here + "  disableLocation", "disableLocation");

        if (!locationEnabled) {
            return;
        }
        //위치 업데이트 해제
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        locationEnabled = false;
    }
    //----------------------------------------------------------------------------------------------


    //지도내에서 크로스헤어 좌표를 업데이트 해주는 메소드
    //지도내에서 마커 추가위해 활용
    private void updateCrosshairCoord() {
        if (map == null || Float.isNaN(crosshairPoint.x) || Float.isNaN(crosshairPoint.y)) {
            return;
        }
        if (map == null || Float.isNaN(crosshairPointLeftDown.x) || Float.isNaN(crosshairPointLeftDown.y)) {
            return;
        }
        if (map == null || Float.isNaN(crosshairPointRightUp.x) || Float.isNaN(crosshairPointRightUp.y)) {
            return;
        }
//        LatLng coord = map.getProjection().fromScreenLocation(crosshairPoint);
//        textView.setText(getString(R.string.format_point_coord,
//                crosshairPoint.x, crosshairPoint.y, coord.latitude, coord.longitude));
    }


    @Override
    //마커 고르면 마커의 정보창 뜨고 마커위치로 카메라 이동해주는 메소드
    public void markerSelected(int position) {
        //정보창 열 마커 / 리스트에서 고르기
        matchInfoWindow.open(matchMarkerList.get(position));

        //마커위치로 이동하는 카메라 업데이트 객체 생성
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(matchMarkerList.get(position).getPosition())
                .animate(CameraAnimation.Easing, 1800) // 카메라 이동시간 2초
                .finishCallback(() -> {
//                    Toast.makeText(this, "카메라 이동 완료", Toast.LENGTH_SHORT).show();
                })
                .cancelCallback(() -> {
//                    Toast.makeText(this, "카메라 이동 취소", Toast.LENGTH_SHORT).show();
                });

        //카메라 이동
        map.moveCamera(cameraUpdate);
    }


    //데이터 저장 임시구현
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(here + "  new_intent_check", "OnNewIntetn start?");


        //인텐트 받아서 마커 추가
        setIntent(intent);
        String order = intent.getStringExtra("order");

/*        if (order != null && order.equals("add")) {
            matchTitle = intent.getStringExtra("matchTitle");
            lat = intent.getDoubleExtra("lat", 0);
            lng = intent.getDoubleExtra("lng", 0);
            Log.w(here + "  matchTitle", matchTitle);
            Log.w(here + "  lat", lat.toString());
            Log.w(here + "  lng", lng.toString());
            LatLng addCoord = new LatLng(lat, lng);

            //마커 추가하기
            addMarker(map, addCoord, matchInfoWindow, matchTitle);
        }*/


        if (order != null && order.equals("find")) {
            lat = intent.getDoubleExtra("lat", 0);
            lng = intent.getDoubleExtra("lng", 0);
            Log.w(here + "  lat", lat.toString());
            Log.w(here + "  lng", lng.toString());
            LatLng findCoord = new LatLng(lat, lng);


            //마커위치로 이동하는 카메라 업데이트 객체 생성
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(findCoord)
                    .animate(CameraAnimation.Easing, 1800) // 카메라 이동시간 2초
                    .finishCallback(() -> {
//                    Toast.makeText(this, "카메라 이동 완료", Toast.LENGTH_SHORT).show();
                    })
                    .cancelCallback(() -> {
//                    Toast.makeText(this, "카메라 이동 취소", Toast.LENGTH_SHORT).show();
                    });

            //카메라 이동
            map.moveCamera(cameraUpdate);
        }


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here + "  onRestart", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here + "  onDestroy", "onDestroy");
    }


}
