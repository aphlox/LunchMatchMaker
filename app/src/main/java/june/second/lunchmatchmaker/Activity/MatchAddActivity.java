package june.second.lunchmatchmaker.Activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import june.second.lunchmatchmaker.Item.Match;
import june.second.lunchmatchmaker.Item.RealMatch;
import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;

public class   MatchAddActivity extends AppCompatActivity {
    //디버그용
    String here = "MatchAddActivity";

    //액티비티 구성요소의 객체 선언-----------------------------------------------------------------
    ImageView imageBack;    //뒤로가는 화살표 이미지
    ImageView imageCheck;   //체크 표시눌러서 매치 추가하는 버튼 이미지
    Button buttonTimeSet;   //매치 시간을 변경할때 누르는 버튼
    TextView textTime;      //매치 시간을 나타내주는 텍스트
    //----------------------------------------------------------------------------------------------



    //매치의 최대 인원수를 설정할때 쓰는 객체 선언--------------------------------------------------
    private Spinner spinnerPeople;      //인원수 고르는 스피너 객체 선언
    ArrayList<String> arrayListPeople;  //인원수들을 나타낸 ArrayList ex) 2명, 3명 ... 6명
    ArrayAdapter<String> arrayAdapter;  //스피너에 적용할 어댑터 객체 선언
    //----------------------------------------------------------------------------------------------



    //데이터 저장용---------------------------------------------------------------------------------
    EditText editMatchTitle;
    String matchTitle;
    EditText editMatchPlace;
    int matchPeople;
    ArrayList<String> matchKeyword = new ArrayList<>();
    EditText editMatchKeywordFirst;
    EditText editMatchKeywordSecond;
    EditText editMatchKeywordThird;

    Double lat;
    Double lng;
    //----------------------------------------------------------------------------------------------



    //파이어 베이스 저장----------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_add);
        Log.w(here +"  onCreate", "onCreate");


        //앞에서 매치 add버튼 눌렀을때--------------------------------------------------------------
        //위도와 경도를 intent 로 받아온다
        Intent getIntent = getIntent();
        lat = getIntent.getDoubleExtra("lat", 0);
        lng = getIntent.getDoubleExtra("lng", 0);
        Log.w(here +"  lat  ", lat.toString());
        Log.w(here +"  lng  ",  lng.toString());
        //------------------------------------------------------------------------------------------



        //스피너------------------------------------------------------------------------------------
        //스피너에서 설정할 인원수 2명 ~ 4명
        arrayListPeople = new ArrayList<>();
        arrayListPeople.add("2명");
        arrayListPeople.add("3명");
        arrayListPeople.add("4명");


        //인원수 어댑터 설정
        arrayAdapter = new ArrayAdapter<>(  getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,  arrayListPeople);

        //스피너 설정
        spinnerPeople = findViewById(R.id.peopleSpinner);
        spinnerPeople.setAdapter(arrayAdapter);  //스피너의 어댑터 설정
        //스피너 아이템 리스너 설정
        spinnerPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //데이터 저장할때 여기서 인원수 값 설정해주어야 됨
                matchPeople = Integer.parseInt(arrayListPeople.get(i).substring(0,1));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //------------------------------------------------------------------------------------------


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정----------------------------------------------------
        editMatchPlace = findViewById(R.id.matchPlace);
        editMatchKeywordFirst = findViewById(R.id.matchKeywordFirst);
        editMatchKeywordSecond= findViewById(R.id.matchKeywordSecond);
        editMatchKeywordThird= findViewById(R.id.matchKeywordThird);





         //뒤로가는 화살표 이미지
        imageBack = findViewById(R.id.back);
        imageBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainMapActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);

        });


        //체크 표시로 클릭시에
        // MainMap 액티비티로 이동
        // 현재 액티비티내의 데이터로 MainMap 액티비티에 마커 생성하기
        imageCheck = findViewById(R.id.check);
        imageCheck.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainMapActivity.class);



            //일단 인텐트로 데이터 보내서 마커 추가해주기 (임시 구현)
            editMatchTitle = findViewById(R.id.matchTitle);
            matchTitle = editMatchTitle.getText().toString();
            Log.w(here + "  matchTitle", matchTitle);
            intent.putExtra("order", "add");
            intent.putExtra("matchTitle", matchTitle);
            intent.putExtra( "lat", lat);
            intent.putExtra("lng", lng);


            //json 쉐어드 저장----------------------------------------------------------------------
            //매치 값을 저장한 쉐어드 불러오기
            SharedPreferences prefMatch = getSharedPreferences("prefMatch", MODE_PRIVATE);
            SharedPreferences.Editor prefMatchEditor = prefMatch.edit();

            //키값은 저장될때마다 마지막으로 저장된 키값+1 해줘서 중복안되게 하기
            //키값 가져오기
            int matchIndex =  (prefMatch.getInt("lastIndex",10) +1);

//        matchKeyword.add("행복");
            matchKeyword.add(editMatchKeywordFirst.getText().toString());
            matchKeyword.add(editMatchKeywordSecond.getText().toString());
            matchKeyword.add(editMatchKeywordThird.getText().toString());

            LatLng coord = new LatLng(lat,lng);


            //추가 되는 매치 json object 로 만들고 문자열로 내보내기
            Match match = new Match(matchIndex,editMatchTitle.getText().toString(),textTime.getText().toString() ,editMatchPlace.getText().toString(),matchPeople,matchKeyword,coord);
            JSONObject matchJsonObject = new JSONObject();
            matchDataToJson(matchJsonObject, match);
            Log.w(here, "matchDataToJson  @"+  matchJsonObject.toString());

            //Gson 부분적으로만 데이터 받아올수 있는지 테스트
            /*
           Gson gson = new Gson();
            String gsonStringMatch = gson.toJson(match);
            Log.w(here, "gsonStringMatch  @"+  gsonStringMatch);
            MatchTest gsonMatch = gson.fromJson(gsonStringMatch , MatchTest.class);
            Log.w(here, "gsonMatch: " + gsonMatch.getMatchTitle());
            Log.w(here, "gsonMatchToString  @"+  gsonMatch.toString());*/


            //JsonArray 활용하려다 보류
            /*
            JSONArray matchJsonArray = new JSONArray();

                matchJsonArray.put(matchJsonObject);

            Log.w(here, "matchJsonArray  @"+  matchJsonArray.toString());
*/


            Log.w(here, "matchIndex: " + matchIndex);
            prefMatchEditor.putInt("lastIndex", matchIndex); //마지막으로 저장한 인덱스(키값) 저장
            prefMatchEditor.putString(Integer.toString(matchIndex), matchJsonObject.toString()); // 키값으로 매치 객체 스트링으로 저장
            prefMatchEditor.commit();

            //파이어 베이스=========================================================================

            //현재 유저 불러오기
            SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
            SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

            try {
                JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser","    "));
                User nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                        , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                        , nowUserJsonObject.getString("userComment"), nowUserJsonObject.getString("userProfilePath"));

                //리얼매치 객체 만들어서 파이어베이스에 저장
                //리얼매치 생성자에 들어가는 것들
                // public RealMatch(int matchIndex, String matchTitle, String matchTime, String matchPlace,
                // int matchMaxPeople, double lat, double lng, String makerId) {
                RealMatch realMatch = new RealMatch(matchIndex, editMatchTitle.getText().toString(), textTime.getText().toString(), editMatchPlace.getText().toString(),
                        match.getMatchPeople(), coord.latitude, coord.longitude, nowUser.getUserId(), matchKeyword.get(0), matchKeyword.get(1), matchKeyword.get(2));
                realMatch.setFirstMemberNickname(nowUser.getUserNickName());
                realMatch.setMatchNowPeople(1);
                databaseReference.child("realMatch").child(Integer.toString(match.getMatchIndex())).setValue(realMatch);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        });
        //------------------------------------------------------------------------------------------



        //타임피커 : 매치 시간 설정하는 값들 -------------------------------------------------------
        //매치 시간을 나타내주는 텍스트 ex) 오전 11시 30분
        textTime = findViewById(R.id.matchTime);


        // 현재 시간으로 타임 피커를 설정
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int realHour =0;        //24시간에서 오전, 오후로 할 때 실제로 나타낼 시간 14시 -> 2시
        String realNoon ="";    //오전 오후 나타내줄 스트링


        //24시간으로 나타나서 12시 기준으로 이하면 오전, 이후면 오후로 나타내준다
        //오후일때는 ex) 13시, 17시로 나타나기 때문에 12시간을 빼준다.
        //오전일 때
        if(hour <= 12){
            realNoon = "오전 ";
            realHour = hour;
        }//오후일 때
        else{
            realNoon = "오후 ";
            realHour = hour -12;
        }

        //시간 텍스트 설정해주기  ex) 오전 11시 30분
        textTime.setText(realNoon + realHour + "시 " + minute + "분");



        //시간을 설정할때 쓸 타임피커 다이얼로그
        //Theme_Holo_Light_Dialog_NoActionBar 해야 내가 원하는 스피너 형태로 됨
        TimePickerDialog picker = new TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                int realHour =0;     //24시간에서 오전, 오후로 할 때 실제로 나타낼 시간 14시 -> 2시
                String realNoon =""; //오전 오후 나타내줄 스트링

                //24시간으로 나타나서 12시 기준으로 이하면 오전, 이후면 오후로 나타내준다
                //오후일때는 ex) 13시, 17시로 나타나기 때문에 12시간을 빼준다.
                //오전일 때
                if(sHour <13){
                    realNoon = "오전 ";
                    realHour = sHour;
                }//오후일 때
                else{
                    realNoon = "오후 ";
                    realHour = sHour -12;
                }
                //시간 텍스트 설정해주기  ex) 오전 11시 30분
                textTime.setText(realNoon + realHour + "시 " + sMinute + "분");
            }
        }, 2, 3, false
             );
        picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        //시간을 설정하는 버튼
        buttonTimeSet = findViewById(R.id.btnDateSet);
        buttonTimeSet.setOnClickListener(view -> {
            //버튼 누를때 타임 피커 생성되게
            picker .show();
        });

        //------------------------------------------------------------------------------------------
    }


    private void matchDataToJson(JSONObject jsonObject, Match match) {


        try {
            jsonObject.put("matchIndex", match.getMatchIndex());
            jsonObject.put("matchTitle", match.getMatchTitle());
            jsonObject.put("matchTime", match.getMatchTime());
            jsonObject.put("matchPlace", match.getMatchPlace());
            jsonObject.put("matchPeople", match.getMatchPeople());
            jsonObject.put("matchKeyword", match.getMatchKeyword());
            jsonObject.put("matchMarker",match.getLatLng());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(here +  "  new_intent_check", "Match_add_Activity : OnNewIntetn start?");
        //앞에서 받은 위도 경도 값 안에서 저장하기
        setIntent(intent);

        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        Log.w(here +"  lat  ", lat.toString());
        Log.w(here +"  lng  ",  lng.toString());

    }





    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here +"  onRestart", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(here + "  onStart", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here +"  onResume", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here +"  onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here + "  onStop", "onStop ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here +"  onDestroy", "onDestroy");
    }




}


