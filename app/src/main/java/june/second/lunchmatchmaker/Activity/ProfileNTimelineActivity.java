package june.second.lunchmatchmaker.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import june.second.lunchmatchmaker.Etc.NewJsonUtil;
import june.second.lunchmatchmaker.Item.Timeline;
import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Receiver.JoinApprovalReceiver;

public class ProfileNTimelineActivity extends AppCompatActivity  {
    //디버깅을 위한 string 값
    String here = "ProfileNTimelineActivity";




    User nowUser;

    //프로필----------------------------------------------------------------------------------------
    private TextView textNickname;  //프로필 자신 닉네임
    private TextView textIntroduce; //프로필 자기 소개
    private ImageView image_profile; // 프로필 이미지
    private Button btnProfileEdit;  // 프로필 편집 버튼
    private  Uri selectedImageUri;  //프로필 사진 가져올때 uri 받아오는 변수
    private final int GET_GALLERY_IMAGE = 200; //프로필 가져올때 requestCode
    String userProfilePath;

    //----------------------------------------------------------------------------------------------


    //리스트뷰--------------------------------------------------------------------------------------
    //글쓰기 원칙을 담는 데이터 (글쓰기원칙의 제목과 내용)
    ArrayList<Timeline> dataArrayListTimeline = new ArrayList<>();

    //어댑터
    final TimelineAdapter timelineAdapter = new TimelineAdapter(dataArrayListTimeline);
    //----------------------------------------------------------------------------------------------

    //데이터 저장-----------------------------------------------------------------------------------
    SharedPreferences prefTimeline; // 타임라인 저장하는 쉐어드 객체
    SharedPreferences.Editor prefTimelineEditor; // 타임라인 쉐어드 에디터
    NewJsonUtil newJsonUtil = new NewJsonUtil();
    //----------------------------------------------------------------------------------------------


    //기타------------------------------------------------------------------------------------------
    private ImageView image_back;   //뒤로가는 버튼 이미지뷰
    private ImageView image_setting;//설정으로 들어가는 버튼 이미지뷰
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_n_timeline);
        Log.w(here , here +  "  onCreate");
        //권한 허용 메소드
        tedPermission();


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        //프로필 파트
        textNickname = findViewById(R.id.nickname);     //닉네임 텍스트
        textIntroduce = findViewById(R.id.introduce);   //닉네임 소개

        //하단바에서 매치 스토리 액티비티로 가는 버튼(이미지)
        findViewById(R.id.profileToStoryImage).setOnClickListener(v ->{
            Intent intent = new Intent(this, StoryActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });

        //하단바에서 매치 액티비티로 가는 버튼(이미지)
        findViewById(R.id.profileToMatchImage).setOnClickListener(v ->{
            Intent intent = new Intent(this, MatchListActivity.class);
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });


        //타임라인 추가 버튼(레이아웃)
        //=> TimelineAddActivity 로 이동
        findViewById(R.id.timelineAddBar).setOnClickListener(v -> {
            Intent intent = new Intent(this, TimelineAddActivity.class);
            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
                 startActivity(intent);
        });

        //설정(톱니바퀴) 버튼 누르면 SettingActivity 로 이동
        image_setting = findViewById(R.id.setting);
        image_setting.setOnClickListener(v -> {
            Intent intent2 = new Intent(this, SettingActivity.class);
            startActivity(intent2);
        });

/*        //프로필 편집하는 버튼
        // => ProfileEditActivity
        btnProfileEdit = findViewById(R.id.button);
        btnProfileEdit.setOnClickListener(v -> {
            Intent intent3 = new Intent(this, ProfileEditActivity.class);
            startActivity(intent3);
        });*/


/*        //프로필 사진 눌러서 사진 가져와서 설정하기
        image_profile = findViewById(R.id.profileImage);
        image_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //ACTION_PICK 사용해서 사진 가져오기
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(Intent.createChooser(intent, "Get Album"), GET_GALLERY_IMAGE);
            }
        });*/


        //로그아웃 버튼
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            //삭제하기전에 삭제 확인하는 메세지 보내주기
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("로그아웃 하시겠습니까?" ).setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //현재 유저 불러오기
                            //로그인 될때 해당 유저를 prefNowUser 에 접속 유저로 저장해놓은것
                            SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
                            SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();
                            prefNowUserEditor.remove("nowUser");
                            prefNowUserEditor.commit();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);


                        } }).setNegativeButton("취소",
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



        //프로필 설정하기---------------------------------------------------------------------------
        //현재 유저 불러오기
        //로그인 될때 해당 유저를 prefNowUser 에 접속 유저로 저장해놓은것
        SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
        SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

        try {
            JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser", "    "));
            nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                    , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                    , nowUserJsonObject.getString("userComment"), nowUserJsonObject.getString("userProfilePath"));

            textNickname.setText(nowUser.getUserNickName());    //프로필 설정
            textIntroduce.setText(nowUser.getUserComment());    //자기소개 설정
            TextView tvNowUserName = findViewById(R.id.tvNowUserName);
            tvNowUserName.setText(nowUser.getUserName());
            TextView tvNowUserGender = findViewById(R.id.tvNowUserGender);
            tvNowUserGender.setText(nowUser.getUserGender());
            TextView tvNowUserBirthday = findViewById(R.id.tvNowUserBirthday);
            tvNowUserBirthday.setText(nowUser.getUserBirthday());
            userProfilePath = nowUser.getUserProfilePath();


        } catch (
                JSONException e) {
            e.printStackTrace();
        }




        //------------------------------------------------------------------------------------------



        //리스트 뷰<타임라인>-----------------------------------------------------------------------
        //리스트 뷰 초기화 및 어댑터 설정
        final ListView listViewTimeline = findViewById(R.id.timelineListView);
        listViewTimeline.setAdapter(timelineAdapter);

        //리스트 뷰<타임라인> 클릭 설정
        //타임라인 누르면 해당 타임라인으로 편집하러 이동하면서
        //인텐트로 편집할때 필요한 값도 보내준다(타임라인 제목, 시간, 해당 아이템 위치)
        listViewTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //히스토리(타임라인)편집하러갈때
                // 타임라인 제목 / 타임라인 날짜 + 시간/ 누른 타임라인 아이템 위치
                Intent intent = new Intent(getApplicationContext(), TimelineEditActivity.class);
                intent.putExtra("matchTitle", dataArrayListTimeline.get(i).getTimelineTitle() );
                intent.putExtra("timelineDateNTime", dataArrayListTimeline.get(i).getTimelineDateNTime() );
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------


        //데이터 저장-------------------------------------------------------------------------------
        prefTimeline = getSharedPreferences("timeline", MODE_PRIVATE); //SharedPreferences 초기화-(타임라인 저장용)
        prefTimelineEditor = prefTimeline.edit();     //SharedPreferences (타임라인 저장용) editor 초기화

        //------------------------------------------------------------------------------------------

        //타임라인 초기 샘플값 ---------------------------------------------------------------------
//        dataArrayListTimeline.add(new Timeline("2018/7/03  오후 2시 40분", "강아지 좋아하는 사람~ 같이 디저트 먹으면서 대화해요ㅎㅎ "));
//        dataArrayListTimeline.add(new Timeline("2017/11/20  오전 2시 00분", "재즈 좋아하는 사람들 모여라 "));
//        dataArrayListTimeline.add(new Timeline("2018/7/03  오후 2시 00분", "오늘 비오는데 파전 땡기네여 "));
//        dataArrayListTimeline.add(new Timeline("2017/8/20  오후 2시 00분", "라멘집에서 롤 얘기 나누실분?ㅋㅋ "));
//        dataArrayListTimeline.add(new Timeline("2020/10/03  오후 2시 00분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/07  오후 2시 00분", "이태원 뉴욕피자 먹으면서 아무 애기ㄱㄱ"));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오후 2시 40분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오전 11시 20분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오전 11시 10분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오전 11시 50분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오전 11시 30분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오후 5시 20분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람  "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오전 6시 10분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/05  오후 2시 00분", "남성역 롯데리아에서 간단하게 잡담 나누고 싶은 사람 "));
//        dataArrayListTimeline.add(new Timeline("2019/10/01  오후 2시 00분", "광화문 샤이바나에서 밥먹으면서 여행 얘기 나누실 분? "));
//        dataArrayListTimeline.add(new Timeline("2019/6/11  오후 2시 00분", "은혜식당에서 밥 먹으면서 일상 얘기 나누자! "));
        //------------------------------------------------------------------------------------------
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //갤러리에서 사진 가져오기
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
             selectedImageUri = data.getData();
            image_profile.setImageURI(selectedImageUri);
        }
    }




    //타임라인 리스트 뷰 어댑터---------------------------------------------------------------------
    public class TimelineAdapter extends BaseAdapter {

        //삭제 예정
//        private Map<String, Integer> mPrincipleGalleryMap;

        //글감 천천히 나타나는 애니메이션( 자동으로 바꾸어 줄때)
        Animation appearAnimation;



        private List<Timeline> timelineDataArrayList;

        public TimelineAdapter(ArrayList<Timeline> data) {
            this.timelineDataArrayList = data;

        }


        @Override
        public int getCount() {
            return timelineDataArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return timelineDataArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            //타임라인 아이템 초기화 ---------------------------------------------------------------
            //타임라인 아이템으로 뷰 그리기
            //타임라인 아이템의 날짜와 제목 뷰와 연동해주기
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_timeline, viewGroup, false);

            //타임라인 객체리스트에서 순서 맞게 데이터 가져와서 설정해주기
            Timeline timeline = timelineDataArrayList.get(i);
            TextView textTimelineDate = view.findViewById(R.id.textTimelineDate);
            TextView textTimelineTitle = view.findViewById(R.id.textTimelineTitle);
            textTimelineDate.setText(timeline.getTimelineDateNTime());
            textTimelineTitle.setText(timeline.getTimelineTitle());
            //--------------------------------------------------------------------------------------

            //리스트 뷰에 애니메이션 적용-----------------------------------------------------------
            //애니메이션에 천천히 나타나는 효과 적용
            appearAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.textappear);
            //타임라인 아이템에 애니메이션 적용
            //타임라인 제목, 날짜, 타임라인 선에 적용
            textTimelineDate.startAnimation(appearAnimation);
            textTimelineTitle.startAnimation(appearAnimation);

            //첫번째 타임라인일때 타임라인 위로 이어지는 선이 안그려지게 설정
            if(i == 0){
                view.findViewById(R.id.upLine).setVisibility(View.INVISIBLE);
            }
            else {
                view.findViewById(R.id.upLine).startAnimation(appearAnimation);
            }

            //타임라인 마커에 천천히 나타나는 애니메이션 적용
            view.findViewById(R.id.middleCircle).startAnimation(appearAnimation);

            //마지막 타임라인일때 타임라인 아래로 이어지는 선이 안그려지게 설정
            if(i == (dataArrayListTimeline.size()-1)){
                view.findViewById(R.id.downLine).setVisibility(View.INVISIBLE);
            }
            else {
                view.findViewById(R.id.downLine).startAnimation(appearAnimation);
            }
            return view;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here+"  onStop", "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here+"  onResume", "onResume");


        setImage();
        //쉐어드 json 형식으로 저장 추가하기--------------------------------------------------
        SharedPreferences prefTimeline = getSharedPreferences(nowUser.getUserId()+"prefTimeline", MODE_PRIVATE);
        SharedPreferences.Editor timelineEditor = prefTimeline.edit();

        //쉐어드에서 스트링 가져오기 ( json 형식으로 되어있는)
        String timelineDataString = prefTimeline.getString("timeline", "");


        dataArrayListTimeline.clear();
        timelineAdapter.notifyDataSetChanged();

        //가져온 쉐어드 데이터화
        newJsonUtil.newJsonToDataOfTimeline(timelineDataString, dataArrayListTimeline);



        //리스트 뷰(타임라인) 정렬
        //정렬 순서
        // 년 -> 달 -> 일 -> 오전/오후 -> 시간 -> 분
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


        Collections.sort(dataArrayListTimeline, lineupTimeline);
        timelineAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here , here +  "  onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(here , here +  "  onStart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here , here +  "  onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here , here +  "  onDestroy");
    }



    public void sendMyBroadcast(View view) {
        Intent intent = new Intent(JoinApprovalReceiver.JOIN_APPROVAL);
        sendBroadcast(intent);
    }

    //데이터 저장 임시구현
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(here +  "  new_intent_check", here +" : onNewIntent start?");


        //인텐트에서 명령 타임라인 시간, 제목, 위치(타임라인 아이템의 위치) 받아오기
        setIntent(intent);

//      타임라인 디버깅
        /*
        Log.w(here," timelineDateNTime - :" + timelineDateNTime+"!") ;
        Log.w(here," timelineDateNTime - YEAR  :" + timelineDateNTime.split("/")[0]+"!") ;
        Log.w(here," timelineDateNTime - MONTH  :" + timelineDateNTime.split("/")[1]+"!");
        Log.w(here," timelineDateNTime - DATE  :" + timelineDateNTime.split("/")[2].split("  ")[0].split(" ")[0]+"!");
        Log.w(here," timelineDateNTime - NOON  :" + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[0]+"!");
        Log.w(here," timelineDateNTime - HOUR  :" + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[1].split("시")[0]+"!");
        Log.w(here," timelineDateNTime - MIN  :"   + timelineDateNTime.split("/")[2].split("  ")[1].split(" ")[2].split("분")[0]+"!");*/


        //타임라인 갱신
        //(아이템 변경 후여서)
        timelineAdapter.notifyDataSetChanged();
    }


    private void setImage() {

        //이미지 크기 조절
        ImageView imageView = findViewById(R.id.profileImage);


        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(userProfilePath, options);
        Log.w(here, "setImage : userProfilePath " + userProfilePath);

        imageView.setImageBitmap(originalBm);



    }

    //권한 허용 메소드
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }





}
