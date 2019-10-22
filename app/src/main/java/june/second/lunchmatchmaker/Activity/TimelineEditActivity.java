package june.second.lunchmatchmaker.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import june.second.lunchmatchmaker.Etc.NewJsonUtil;
import june.second.lunchmatchmaker.Item.Timeline;
import june.second.lunchmatchmaker.R;

public class TimelineEditActivity extends AppCompatActivity {
    //디버그용
    String here = "TimelineEditActivity";

    //액티비티 구성요소의 객체 선언-----------------------------------------------------------------
    ImageView imageBack;    //뒤로가는 화살표 이미지
    ImageView imageCheck;   //체크 표시눌러서 매치 추가하는 버튼 이미지
    Button buttonTimeSet;   //매치 시간을 변경할때 누르는 버튼
    TextView textDate;       //매치 날짜를 나타내주는 텍스트
    EditText editMatchTitle; //매치 제목을 나타내주는 editText
    TextView textTime;      //매치 시간을 나타내주는 텍스트
    //----------------------------------------------------------------------------------------------



    //매치의 최대 인원수를 설정할때 쓰는 객체 선언--------------------------------------------------
    private Spinner spinnerPeople;      //인원수 고르는 스피너 객체 선언
    ArrayList<String> arrayListPeople;  //인원수들을 나타낸 ArrayList ex) 2명, 3명 ... 6명
    ArrayAdapter<String> arrayAdapter;  //스피너에 적용할 어댑터 객체 선언
    //----------------------------------------------------------------------------------------------






    //데이트 피커-----------------------------------------------------------------------------------
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

    NewJsonUtil newJsonUtil = new NewJsonUtil();
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_edit);
        Log.w(here +"  onCreate", "onCreate");

        //액티비티내의 뷰 연동 및 클릭시의 동작 설정================================================
        editMatchTitle = findViewById(R.id.matchTitle); //타임라인 제목을 편집하는 editText
        textDate = findViewById(R.id.matchDate);        //타임라인 날짜를 나타내는 textView


        //편집할 타임라인 값 앞에서 받아와서 편집하기-----------------------------------------------
        //인텐트 받아오기
        Intent getIntent = getIntent();
        //편집할 타임라인 제목 받아오기
        String matchTitle = getIntent.getStringExtra("matchTitle");
        //편집할 타임라인 날짜 받아오기
        String timelineDateNTime = getIntent.getStringExtra("timelineDateNTime");
        //편집할 타임라인 위치 받아오기
        int timelineItemPosition = getIntent.getIntExtra("position",0);
        //받아온 타임라인 값으로 타임라인 객체 만들기
        Timeline getTimeline = new Timeline(timelineDateNTime, matchTitle);
        //편집할 타임라인 제목으로 editText 초기화
        editMatchTitle.setText(matchTitle);
        //------------------------------------------------------------------------------------------


         //뒤로가는 화살표 이미지
        // ProfileNTimelineActivity 액티비티로 이동
        imageBack = findViewById(R.id.back);
        imageBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);

        });

        //체크 표시로 클릭시에
        // ProfileNTimelineActivity 액티비티로 이동
        imageCheck = findViewById(R.id.check);
        imageCheck.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);

            //수정 완료했으니 수정한 값 넘겨주기
            //수정값 - 타임라인 제목/날짜/타임라인 아이템 위치
            intent.putExtra("order", "timelineEdit");
            intent.putExtra("matchTitle", editMatchTitle.getText().toString());
            intent.putExtra("timelineDateNTime", textDate.getText().toString()+ "  " +textTime.getText().toString());
            intent.putExtra("position", timelineItemPosition);



            //쉐어드 json 형식으로 저장 불러오--기--------------------------------------------------
            SharedPreferences prefTimeline = getSharedPreferences("prefTimeline", MODE_PRIVATE);
            SharedPreferences.Editor timelineEditor = prefTimeline.edit();

            //쉐어드에서 스트링 가져오기 ( json 형식으로 되어있는)
            String timelineDataString = prefTimeline.getString("timeline", "");

            ArrayList<Timeline> timelineArrayList = new ArrayList<Timeline>();


            //가져온 쉐어드 데이터화
            newJsonUtil.newJsonToDataOfTimeline(timelineDataString, timelineArrayList);

            Comparator<Timeline> lineupTimeline = new Comparator<Timeline>() {
                @Override
                public int compare(Timeline timeline1 , Timeline timeline2) {
                    int ret ;

                    //년 기준으로 내림차순
                    if (timeline1.getTimelineYear().compareTo(timeline2.getTimelineYear()) <0 ) {
                        ret = 1;
                    }
                    //년 기준으로 내림차순
                    else if (timeline1.getTimelineYear().compareTo(timeline2.getTimelineYear())  ==0 ) {
                        //달 기준으로 내림차순
                        if (timeline1.getTimelineMonth() < (timeline2.getTimelineMonth()) ) {
                            ret = 1;
                        }
                        //달 기준으로 내림차순
                        else if (timeline1.getTimelineMonth() ==  (timeline2.getTimelineMonth()) ) {
                            //일 기준으로 내림차순
                            if (timeline1.getTimelineDate() < (timeline2.getTimelineDate())) {
                                ret = 1;
                            }
                            //일 기준으로 내림차순
                            else if (timeline1.getTimelineDate() == (timeline2.getTimelineDate()) ) {

                                //오전/오후 기준으로 내림차순
                                if (timeline1.getTimelineNoon().compareTo(timeline2.getTimelineNoon()) <0 ) {
                                    ret = 1;
                                }
                                //오전/오후 기준으로 내림차순
                                else if (timeline1.getTimelineNoon().compareTo(timeline2.getTimelineNoon())  ==0 ) {
                                    //시간 기준으로 내림차순
                                    if (timeline1.getTimelineHour() < (timeline2.getTimelineHour()) ) {
                                        ret = 1;
                                    }
                                    //시간 기준으로 내림차순
                                    else if (timeline1.getTimelineHour() == (timeline2.getTimelineHour())   ) {
                                        //분 기준으로 내림차순
                                        if (timeline1.getTimelineMin() < (timeline2.getTimelineMin()) ) {
                                            ret = 1;
                                        }
                                        //분 기준으로 내림차순
                                        else if (timeline1.getTimelineMin() ==(timeline2.getTimelineMin())   ) {
                                            ret = 0;
                                        }
                                        else {  //분 기준으로 내림차순
                                            ret = -1;
                                        }
                                    }
                                    else {  //시간 기준으로 내림차순
                                        ret = -1;
                                    }
                                }
                                else {  //오전/오후 기준으로 내림차순
                                    ret = -1;
                                }
                            }
                            else {  //일 기준으로 내림차순
                                ret = -1;
                            }
                        }
                        else {  //달 기준으로 내림차순
                            ret = -1;
                        }
                    } else { //년 기준으로 내림차순
                        ret = -1;
                    }
                    return ret;
                }
            } ;

            Collections.sort(timelineArrayList, lineupTimeline);
            timelineArrayList.remove(timelineItemPosition);
            Timeline editTimeline = new Timeline(textDate.getText().toString()+ "  " +textTime.getText().toString(), editMatchTitle.getText().toString());
            timelineArrayList.add(editTimeline);

            //데이터 다시 쉐어드화
            String dataString = "{ timeArrayList : [ ";
            for (int i = 0; i < timelineArrayList.size() - 1; i++) {
                dataString = dataString.concat(timelineArrayList.get(i).toString() + " , ");
            }
            dataString = dataString.concat(timelineArrayList.get(timelineArrayList.size() - 1).toString() + " ] } ");

            Log.w(here, "dataString @" + dataString);

            timelineEditor.putString("timeline",dataString );
            timelineEditor.commit();






            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
        });


        //삭제(휴지통) 표시 클릭시에
        // ProfileNTimelineActivity 액티비티로 이동
        findViewById(R.id.layoutTimelineDel).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProfileNTimelineActivity.class);
            //삭제하기전에 삭제 확인하는 메세지 보내주기
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("삭제하시겠습니까?" ).setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'YES'
                            //삭제 완료했으니 삭제할 값 넘어가는 ProfileNTimelineActivity 으로 넘겨주기


                            //쉐어드 json 형식으로 저장 불러오기------------------------------------
                            SharedPreferences prefTimeline = getSharedPreferences("prefTimeline", MODE_PRIVATE);
                            SharedPreferences.Editor timelineEditor = prefTimeline.edit();

                            //쉐어드에서 스트링 가져오기 ( json 형식으로 되어있는)
                            String timelineDataString = prefTimeline.getString("timeline", "");

                            ArrayList<Timeline> timelineArrayList = new ArrayList<Timeline>();


                            //가져온 쉐어드 데이터화
                            newJsonUtil.newJsonToDataOfTimeline(timelineDataString, timelineArrayList);

                            Comparator<Timeline> lineupTimeline = new Comparator<Timeline>() {
                                @Override
                                public int compare(Timeline timeline1 , Timeline timeline2) {
                                    int ret ;

                                    //년 기준으로 내림차순
                                    if (timeline1.getTimelineYear().compareTo(timeline2.getTimelineYear()) <0 ) {
                                        ret = 1;
                                    }
                                    //년 기준으로 내림차순
                                    else if (timeline1.getTimelineYear().compareTo(timeline2.getTimelineYear())  ==0 ) {
                                        //달 기준으로 내림차순
                                        if (timeline1.getTimelineMonth() < (timeline2.getTimelineMonth()) ) {
                                            ret = 1;
                                        }
                                        //달 기준으로 내림차순
                                        else if (timeline1.getTimelineMonth() ==  (timeline2.getTimelineMonth()) ) {
                                            //일 기준으로 내림차순
                                            if (timeline1.getTimelineDate() < (timeline2.getTimelineDate())) {
                                                ret = 1;
                                            }
                                            //일 기준으로 내림차순
                                            else if (timeline1.getTimelineDate() == (timeline2.getTimelineDate()) ) {

                                                //오전/오후 기준으로 내림차순
                                                if (timeline1.getTimelineNoon().compareTo(timeline2.getTimelineNoon()) <0 ) {
                                                    ret = 1;
                                                }
                                                //오전/오후 기준으로 내림차순
                                                else if (timeline1.getTimelineNoon().compareTo(timeline2.getTimelineNoon())  ==0 ) {
                                                    //시간 기준으로 내림차순
                                                    if (timeline1.getTimelineHour() < (timeline2.getTimelineHour()) ) {
                                                        ret = 1;
                                                    }
                                                    //시간 기준으로 내림차순
                                                    else if (timeline1.getTimelineHour() == (timeline2.getTimelineHour())   ) {
                                                        //분 기준으로 내림차순
                                                        if (timeline1.getTimelineMin() < (timeline2.getTimelineMin()) ) {
                                                            ret = 1;
                                                        }
                                                        //분 기준으로 내림차순
                                                        else if (timeline1.getTimelineMin() ==(timeline2.getTimelineMin())   ) {
                                                            ret = 0;
                                                        }
                                                        else {  //분 기준으로 내림차순
                                                            ret = -1;
                                                        }
                                                    }
                                                    else {  //시간 기준으로 내림차순
                                                        ret = -1;
                                                    }
                                                }
                                                else {  //오전/오후 기준으로 내림차순
                                                    ret = -1;
                                                }
                                            }
                                            else {  //일 기준으로 내림차순
                                                ret = -1;
                                            }
                                        }
                                        else {  //달 기준으로 내림차순
                                            ret = -1;
                                        }
                                    } else { //년 기준으로 내림차순
                                        ret = -1;
                                    }
                                    return ret;
                                }
                            } ;

                            Collections.sort(timelineArrayList, lineupTimeline);
                            timelineArrayList.remove(timelineItemPosition);


                            //데이터 다시 쉐어드화
                            String dataString = "{ timeArrayList : [ ";
                            for (int i = 0; i < timelineArrayList.size() - 1; i++) {
                                dataString = dataString.concat(timelineArrayList.get(i).toString() + " , ");
                            }
                            dataString = dataString.concat(timelineArrayList.get(timelineArrayList.size() - 1).toString() + " ] } ");

                            Log.w(here, "dataString @" + dataString);

                            timelineEditor.putString("timeline",dataString );
                            timelineEditor.commit();




                            //플래그로 저장 임시 구현
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
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



        //타임피커 : 매치 시간 설정하는 값들 -------------------------------------------------------
        //매치 시간을 나타내주는 텍스트 ex) 오전 11시 30분
        textTime = findViewById(R.id.matchTime);


        // 현재 시간으로 타임 피커를 설정
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);



        //시간 텍스트 설정해주기  ex) 오전 11시 30분
         textTime.setText(getTimeline.getTimelineNoon() + " "+ getTimeline.getTimelineHour() + "시 " + getTimeline.getTimelineMin() + "분");
         //받아온 시간에다 오후이면 12시간 더해주기
         //(타임피커는 24시간으로 받아서 오전,오후로 나뉘어져서)
         //오전이면 그대로 시간 설정
        int getTime;
        if(getTimeline.getTimelineNoon().equals("오전") ){
            getTime= getTimeline.getTimelineHour();
        }
        else{
            getTime =getTimeline.getTimelineHour() +12;
        }

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
        }, getTime, getTimeline.getTimelineMin(), false
             );
        picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        //시간을 설정하는 버튼
        buttonTimeSet = findViewById(R.id.btnTimeSet);
        buttonTimeSet.setOnClickListener(view -> {
            //버튼 누를때 타임 피커 생성되게
            picker .show();
        });

        //------------------------------------------------------------------------------------------

        //데이트 피커-------------------------------------------------------------------------------

        //받아온 타임라인의 날짜로 날짜 나태내주는 텍스트 초기화해주기
        textDate.setText(getTimeline.getTimelineYear() +"/" +getTimeline.getTimelineMonth() + "/" + getTimeline.getTimelineDate());

        //받아온 타임라인의 날짜로 데이트 피커 시작하게 받아온 값으로 초기화해주기
        Button btnSetDate = findViewById(R.id.btnDateSet);
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(TimelineEditActivity.this, myDatePicker, Integer.parseInt(getTimeline.getTimelineYear()), getTimeline.getTimelineMonth() -1, getTimeline.getTimelineDate()).show();
            }
        });


        //------------------------------------------------------------------------------------------
    }

    //날짜 양식 맞추어 주는 메소드
    //캘린더에서 시간 받아서 자동으로 자신이 설정한 양식으로 맞추어준다
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";    // 출력형식   2018/11/28
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        TextView et_date =  findViewById(R.id.matchDate);
        et_date.setText(sdf.format(myCalendar.getTime()));
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(here +  "  new_intent_check", here +" : OnNewIntetn start?");


    }



}


