package june.second.lunchmatchmaker.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import june.second.lunchmatchmaker.Etc.NewJsonUtil;
import june.second.lunchmatchmaker.Item.Timeline;
import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;

public class TimelineAddActivity extends AppCompatActivity {
    //디버그용
    String here = "MatchAddActivity";

    User nowUser;


    //액티비티 구성요소의 객체 선언-----------------------------------------------------------------
    ImageView imageBack;    //뒤로가는 화살표 이미지
    ImageView imageCheck;   //체크 표시눌러서 매치 추가하는 버튼 이미지
    Button btnSetTime;      //매치 시간을 변경할때 누르는 버튼
    Button btnSetDate;      //매치 날짜를 변경할때 누르는 버튼
    TextView textTime;      //매치 시간을 나타내주는 텍스트
    TextView textDate;       //매치 날짜를 나타내주는 텍스트
    EditText editMatchTitle; //매치 제목을 나타내주는 editText

    //----------------------------------------------------------------------------------------------


    //데이트 피커 선언-------------------------------------------------------------------------------
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    //----------------------------------------------------------------------------------------------

    //데이터 저장-----------------------------------------------------------------------------------
    String timelineDataString;
    NewJsonUtil newJsonUtil = new NewJsonUtil();
    //----------------------------------------------------------------------------------------------


    //기타------------------------------------------------------------------------------------------
    private long mLastClickTime = 0;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_add);
        Log.w(here + "  onCreate", "onCreate");


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        editMatchTitle = findViewById(R.id.matchTitle);     //타임라인 제목을 편집하는 editText
        textDate = findViewById(R.id.matchDate);            //타임라인 날짜를 나타내는 textView
        //뒤로가는 화살표 이미지
        // ProfileNTimelineActivity 액티비티로 이동
        imageBack = findViewById(R.id.back);
        imageBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        int realHour = 0;        //24시간에서 오전, 오후로 할 때 실제로 나타낼 시간 14시 -> 2시
        String realNoon = "";    //오전 오후 나타내줄 스트링


        //24시간으로 나타나서 12시 기준으로 이하면 오전, 이후면 오후로 나타내준다
        //오후일때는 ex) 13시, 17시로 나타나기 때문에 12시간을 빼준다.
        //오전일 때
        if (hour <= 12) {
            realNoon = "오전 ";
            realHour = hour;
        }//오후일 때
        else {
            realNoon = "오후 ";
            realHour = hour - 12;
        }

        //시간 텍스트 설정해주기  ex) 오전 11시 30분
        textTime.setText(realNoon + realHour + "시 " + minute + "분");


        //시간을 설정할때 쓸 타임피커 다이얼로그
        //Theme_Holo_Light_Dialog_NoActionBar 해야 내가 원하는 스피너 형태로 됨
        TimePickerDialog picker = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        int realHour = 0;     //24시간에서 오전, 오후로 할 때 실제로 나타낼 시간 14시 -> 2시
                        String realNoon = ""; //오전 오후 나타내줄 스트링

                        //24시간으로 나타나서 12시 기준으로 이하면 오전, 이후면 오후로 나타내준다
                        //오후일때는 ex) 13시, 17시로 나타나기 때문에 12시간을 빼준다.
                        //오전일 때
                        if (sHour < 13) {
                            realNoon = "오전 ";
                            realHour = sHour;
                        }//오후일 때
                        else {
                            realNoon = "오후 ";
                            realHour = sHour - 12;
                        }
                        //시간 텍스트 설정해주기  ex) 오전 11시 30분
                        textTime.setText(realNoon + realHour + "시 " + sMinute + "분");
                    }
                }, hour, minute, false
        );

        picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        //시간을 설정하는 버튼
        btnSetTime = findViewById(R.id.btnTimeSet);
        btnSetTime.setOnClickListener(view -> {
            //버튼 누를때 타임 피커 생성되게
            picker.show();
        });

        //------------------------------------------------------------------------------------------


        //데이트 피커-------------------------------------------------------------------------------

        // 현재 날짜로 데이트 피커 초기화 설정
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH);


        //날짜 텍스트 설정해주기  ex) 오전 11시 30분
        textDate.setText(year + "/" + month + "/" + dayOfMonth);

        btnSetDate = findViewById(R.id.btnDateSet);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TimelineAddActivity.this, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //------------------------------------------------------------------------------------------


        //체크 표시로 클릭시에
        // ProfileNTimelineActivity 액티비티로 이동 및 인텐트 추가
        //인텐트에 타임라인 추가 명령 + 타임라인 날짜와 제목
        imageCheck = findViewById(R.id.check);
        imageCheck.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);


            //잘못해서 두번 눌리는 경우 방지
            // 1초 이내에 두번눌리는 경우는 오동작으로 인식해서 무시함
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            //타임라인 날짜
            intent.putExtra("timelineDateNTime", textDate.getText().toString() + "  " + textTime.getText().toString());
            //타임라인 제목
            intent.putExtra("matchTitle", editMatchTitle.getText().toString());
            //인텐트 추가 명령
            intent.putExtra("order", "add");

            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //json 형식 예제
/*            {
                "timelineArrayList" : [
                {
                    "timelineDateNTime" : "extimelineDateNTimebn " ,
                        "matchTitle" : "exmatchTitle"
                },
                {
                    "timelineDateNTime" : "extimelineDateNTimebn " ,
                        "matchTitle" : "exmatchTitle"
                },
                {
                    "timelineDateNTime" : "extimelineDateNTimebn " ,
                        "matchTitle" : "exmatchTitle"
                }
                ]

            }*/


            //현재 유저 불러오기
            //로그인 될때 해당 유저를 prefNowUser 에 접속 유저로 저장해놓은것
            SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
            SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

            try {
                JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser", "    "));
                nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                        , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                        , nowUserJsonObject.getString("userComment"), nowUserJsonObject.getString("userProfilePath"));

            } catch (
                    JSONException e) {
                e.printStackTrace();
            }

            //쉐어드 json 형식으로 저장 추가하기--------------------------------------------------
            SharedPreferences prefTimeline = getSharedPreferences(nowUser.getUserId()+"prefTimeline", MODE_PRIVATE);
            SharedPreferences.Editor timelineEditor = prefTimeline.edit();

            //쉐어드에서 스트링 가져오기 ( json 형식으로 되어있는)
            timelineDataString = prefTimeline.getString("timeline", "");

            ArrayList<Timeline> timelineArrayList = new ArrayList<Timeline>();

            //가져온 쉐어드 데이터화
            newJsonUtil.newJsonToDataOfTimeline(timelineDataString, timelineArrayList);

            timelineArrayList.add(new Timeline(textDate.getText().toString() + "  " + textTime.getText().toString(), editMatchTitle.getText().toString()));


//            timelineArrayList.add(new Timeline("2018/7/03  오후 2시 40분", "강아지 좋아하는 사람~ 같이 디저트 먹으면서 대화해요ㅎㅎ "));
//            timelineArrayList.add(new Timeline("2017/11/20  오전 2시 00분", "재즈 좋아하는 사람들 모여라 "));
//            timelineArrayList.add(new Timeline("2018/7/03  오후 2시 00분", "오늘 비오는데 파전 땡기네여 "));


            //데이터 다시 쉐어드화
            String dataString = "{ timeArrayList : [ ";
            for (int i = 0; i < timelineArrayList.size() - 1; i++) {
                dataString = dataString.concat(timelineArrayList.get(i).toString() + " , ");
            }
            dataString = dataString.concat(timelineArrayList.get(timelineArrayList.size() - 1).toString() + " ] } ");

            Log.w(here, "dataString @" + dataString);

            timelineEditor.putString("timeline",dataString );
            timelineEditor.commit();



            startActivity(intent);
        });

    }






    //날짜 양식 맞추어 주는 메소드
    //캘린더에서 시간 받아서 자동으로 자신이 설정한 양식으로 맞추어준다
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        textDate = findViewById(R.id.matchDate);
        textDate.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here + "  onRestart", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(here + "  onStart", "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here + "  onResume", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here + "  onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here + "  onStop", "onStop ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here + "  onDestroy", "onDestroy");
    }


}


