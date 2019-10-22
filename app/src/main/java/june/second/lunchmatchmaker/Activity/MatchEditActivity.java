package june.second.lunchmatchmaker.Activity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import com.naver.maps.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import june.second.lunchmatchmaker.Item.Match;
import june.second.lunchmatchmaker.R;

public class MatchEditActivity extends AppCompatActivity {
    //디버그용
    String here = "MatchEditActivity";

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
    EditText editMatchPlace;
    EditText editMatchKeywordFirst;
    EditText editMatchKeywordSecond;
    EditText editMatchKeywordThird;
    int matchPeople;
    ArrayList<String> matchKeyword = new ArrayList<>();

    int matchPosition;
    //----------------------------------------------------------------------------------------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_edit);
        Log.w(here +"  onCreate", "onCreate");


        //매치 편집하기 위해 편집할 아이템의 포지션값 받아오고--------------------------------------
        //매치 객체리스트에서 해당 아이템 가져오기
        Intent getIntent = getIntent();
        matchPosition = getIntent.getIntExtra("matchPosition",0);
        Match getMatch = MainMapActivity.matchArrayList.get(matchPosition);
        //------------------------------------------------------------------------------------------


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정================================================
        editMatchTitle = findViewById(R.id.matchEditTitle);
        editMatchPlace = findViewById(R.id.matchPlace);
        editMatchKeywordFirst = findViewById(R.id.matchKeywordFirst);
        editMatchKeywordSecond= findViewById(R.id.matchKeywordSecond);
        editMatchKeywordThird= findViewById(R.id.matchKeywordThird);
        textTime = findViewById(R.id.matchTime); //매치 시간을 나타내주는 텍스트 ex) 오전 11시 30분


        //값 세팅해주기
        editMatchTitle.setText(getMatch.getMatchTitle());
        editMatchPlace.setText(getMatch.getMatchPlace());
        editMatchKeywordFirst.setText(getMatch.getMatchKeyword().get(0));
        editMatchKeywordSecond.setText(getMatch.getMatchKeyword().get(1));
        editMatchKeywordThird.setText(getMatch.getMatchKeyword().get(2));
        textTime.setText(getMatch.getMatchTime());


        //체크 표시 클릭시에 현재 액티비티에 입력한 값 매치 데이터 값으로 매치 편집하기-------------
        //+MainMap 액티비티로 이동
        imageCheck = findViewById(R.id.check);
        imageCheck.setOnClickListener(view -> {
            Intent intent = new Intent(this, MatchListActivity.class);


            //json 쉐어드 저장----------------------------------------------------------------------
            //매치 값을 저장한 쉐어드 불러오기
            SharedPreferences prefMatch = getSharedPreferences("prefMatch", MODE_PRIVATE);
            SharedPreferences.Editor prefMatchEditor = prefMatch.edit();

            //매치에는 대화 키워드 값들이 리스트 객체로 들어가기때문에 => 리스트에 넣어서 저장
            matchKeyword.add(editMatchKeywordFirst.getText().toString());
            matchKeyword.add(editMatchKeywordSecond.getText().toString());
            matchKeyword.add(editMatchKeywordThird.getText().toString());

            //추가 되는 매치 json object 로 만들고 문자열로 내보내기
            Match match = new Match(getMatch.getMatchIndex(),editMatchTitle.getText().toString(),textTime.getText().toString() ,editMatchPlace.getText().toString(),matchPeople,matchKeyword, getMatch.getLatLng());
            JSONObject matchJsonObject = new JSONObject();
            //매치의 값들을 Json 으로 변환해주는 메소드
            matchDataToJson(matchJsonObject, match);
            Log.w(here, "matchDataToJson  @"+  matchJsonObject.toString());

            // 매치 인덱스(키값)으로 매치 객체 스트링으로 저장
            Log.w(here, "matchIndex: " + getMatch.getMatchIndex());
            prefMatchEditor.putString(Integer.toString(getMatch.getMatchIndex()), matchJsonObject.toString());
            prefMatchEditor.commit();

            startActivity(intent);
        });
        //------------------------------------------------------------------------------------------


        //------------------------------------------------------------------------------------------
        //삭제(휴지통) 표시 클릭시에
        // ProfileNTimelineActivity 액티비티로 이동
        findViewById(R.id.layoutTimelineDel).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MatchListActivity.class);
            //삭제하기전에 삭제 확인하는 메세지 보내주기
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("삭제하시겠습니까?" ).setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'YES'


                            //매치 값을 저장한 쉐어드 불러오기
                            SharedPreferences prefMatch = getSharedPreferences("prefMatch", MODE_PRIVATE);
                            SharedPreferences.Editor prefMatchEditor = prefMatch.edit();
                            //현재 불러온 매치 쉐어드에서 삭제
                            prefMatchEditor.remove(Integer.toString(getMatch.getMatchIndex()));
                            prefMatchEditor.commit();

                            //삭제후에 => 매치 리스트 갱신해주기
                            //* 매치리스트 resume 에서 갱신하면 이 부분 없애도 될거 같은데..
                            //일단 구현했으니 삭제 보류

                            //매치 리스트 갱신
                            //매치 리스트 삭제후 다시 저장
                            MainMapActivity.matchArrayList.clear();


                            Map<String, ?> allEntries = prefMatch.getAll();
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                                Log.w("map values", entry.getKey() + ": " + entry.getValue().toString());
                                try {
                                    JSONObject matchJsonObject = new JSONObject(entry.getValue().toString());
                                    ArrayList<String> matchKeywordArray = new ArrayList<String>();

                                    //json 받아온 스트링값을 다시 매치 키워드 리스트 객체로 만들어주기
                                    matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[0]);
                                    matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[1]);
                                    matchKeywordArray.add(matchJsonObject.getString("matchKeyword").substring(1, matchJsonObject.getString("matchKeyword").length() - 1).split(",")[2]);

                                    //json 받아온 스트링값을 다시 LatLng 객체로 만들어주기
                                    Double lat = Double.parseDouble(matchJsonObject.getString("matchMarker").split(",")[0].split("=")[1]);
                                    Double lng = Double.parseDouble(matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].
                                            substring(0, matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].length() - 1));
                                    LatLng coord = new LatLng(lat, lng);

                                    Match match = new Match(matchJsonObject.getInt("matchIndex"), matchJsonObject.getString("matchTitle"),
                                            matchJsonObject.getString("matchTime"), matchJsonObject.getString("matchPlace"), matchJsonObject.getInt("matchPeople"), matchKeywordArray, coord);

                                    //matchJsonObject 디버깅 도구
                                    /*
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
                                            substring(0, matchJsonObject.getString("matchMarker").split(",")[1].split("=")[1].length() - 1));*/

                                    MainMapActivity.matchArrayList.add(match);
                                    Log.w(here, "matchArrayList: " +MainMapActivity.matchArrayList.size());

                                } catch (ArrayIndexOutOfBoundsException | JSONException e) {
                                    // 중복되지 않는 키값을 위해 마지막으로 저장한 키값에다 +1 해서 lastposition 이라는 키값에 저장해두는데
                                    // 위에서 매치 객체를 만들때 모든 키 값을 불러와서 split 으로 나누기때문에 lastposition 에서 ArrayIndexOutOfBoundsException가 뜬다
                                    //lastposition 는 구분자 없이 숫자로만 이루어져있기때문에
                                    //그래서 여기서 에러나는 것을 방지하기 위해서 try catch문을 사용했다.
                                    Log.w(here, "ArrayIndexOutOfBoundsException => lastposition  ");
                                    e.printStackTrace();
                                }
                            }


                            startActivity(intent);
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            return;
                        }
                    });

            AlertDialog alert = alert_confirm.create();
            alert.show();

        });
        //------------------------------------------------------------------------------------------


        //뒤로가는 화살표 이미지-------------------------------------------------------------------
        imageBack = findViewById(R.id.back);
        imageBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MatchListActivity.class);
            startActivity(intent);

        });
        //------------------------------------------------------------------------------------------
        //==========================================================================================





        //타임피커 : 매치 시간 설정하는 값들 -------------------------------------------------------


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

        //스피너------------------------------------------------------------------------------------
        //스피너에서 설정할 인원수 2명 ~ 6명
        arrayListPeople = new ArrayList<>();
        arrayListPeople.add("2명");
        arrayListPeople.add("3명");
        arrayListPeople.add("4명");
        arrayListPeople.add("5명");
        arrayListPeople.add("6명");

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

    }


    //매치 데이터를 jsonObject 에 넣어주는 메소드
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


