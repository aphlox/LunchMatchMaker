package june.second.lunchmatchmaker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Service.LunchMatchService;
import june.second.lunchmatchmaker.Service.RealService;

public class LoginActivity extends AppCompatActivity {
    //디버깅용 현재 액티비티 문자값
    private String here = "LoginActivity";


    //브로드캐스트 + 서비스----------------------------------------------------------------------------
    //생명주기 회원가입 승인 요청하는 브로드캐스트 신호
    public static final String JOIN_APPROVAL = "june.second.lunchmatchmaker.action.ACTION_JOIN_APPROVAL";
    //서비스 시작하는 인텐트
    private Intent serviceIntent;
    //----------------------------------------------------------------------------------------------


    //로그인 관련 변수------------------------------------------------------------------------------
    private TextView accountFind;
    private TextInputEditText etInputId;
    private TextInputEditText etInputPw;
    private CheckBox autoLoginCheckBox;
    private User user;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //자동 로그인--------------------------------------------------------------------------------
        //접속한 유저의 자동로그인 상태가 true 이고
        //접속 유저의 상태가 남아있으면 바로 자동로그인한다
        SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
        SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

        if(prefNowUser.getBoolean("autoLogin",false)){
            if (prefNowUser.getString("nowUser", "").length() > 0) {
                Intent intent = new Intent(this, MatchListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        }
        //------------------------------------------------------------------------------------------


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
        etInputId = findViewById(R.id.inputId);
        etInputPw = findViewById(R.id.inputPw);


        //TODO - 계정찾기
        accountFind = findViewById(R.id.accountFind);
        accountFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AccountFindActivity.class);
                startActivity(intent);
            }
        });

        //이메일로 가입하기
        findViewById(R.id.go_email_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterEmailCheckActivity.class);

                startActivity(intent);

            }
        });

        //로그인====================================================================================
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //자동 로그인 체크값 현재유저에 저장하기
                if(autoLoginCheckBox.isChecked()){
                    prefNowUserEditor.putBoolean("autoLogin", true);
                    prefNowUserEditor.commit();
                }
                else {
                    prefNowUserEditor.putBoolean("autoLogin", false);
                    prefNowUserEditor.commit();
                }


                //로그인때 유저 아이디와 비밀번호 확인을 위해 유저정보 쉐어드 불러오기--------------
                SharedPreferences prefUser = getSharedPreferences("prefUser", MODE_PRIVATE);
                SharedPreferences.Editor prefUserEditor = prefUser.edit();


                Map<String, ?> allEntries = prefUser.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    try {
                        JSONObject userJsonObject = new JSONObject(entry.getValue().toString());
                        user = new User(userJsonObject.getBoolean("userApproval"), userJsonObject.getString("userId"), userJsonObject.getString("userPw"), userJsonObject.getString("userName")
                                , userJsonObject.getString("userGender"), userJsonObject.getString("userBirthday"), userJsonObject.getString("userNickName")
                                , userJsonObject.getString("userComment"));


                        //로그인 - 사용자가 로그인 액티비티에 입력한 값과
                        //쉐어드에 저장된 사용자의 아이디와 비밀번호를 비교해서 같으면 로그인 시켜준다.
                        if (etInputId.getText().toString().trim().equals(user.getUserId().trim())) {

                            if (etInputPw.getText().toString().trim().equals(user.getUserPw().toString().trim())) {

                                //사용자가 회원가입 승인이 되었으면 매치리스트로 이동 => 본격적으로 회원으로 활동가능
                                if (user.isUserApproval()) {

                                    //로그인 될때 해당 유저를 접속 유저(prefNowUser)에 저장
                                    SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
                                    SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();
                                    JSONObject nowUserJsonObject = new JSONObject();
                                    userDataToJson(nowUserJsonObject, user);
                                    prefNowUserEditor.putString("nowUser", nowUserJsonObject.toString()); // 키값으로 매치 객체 스트링으로 저장
                                    prefNowUserEditor.commit();

                                    //매치리스트로 이동
                                    Intent intent = new Intent(LoginActivity.this, MatchListActivity.class);
                                    startActivity(intent);
                                }
                                //사용자가 회원가입 승인이 아직 안되었으면 승인을 기다려야한다는 액티비티(RegisterResultActivity)로 이동시킨다
                                else {
                                    Intent intent = new Intent(LoginActivity.this, RegisterResultActivity.class);

                                    startActivity(intent);
                                }
                            } else {
                                //TODO 누적수 체크해서 한번만 토스트 띄우게 로직 변경
//                            Toast.makeText(LoginActivity.this, "비밀번호가 맞지 않습니다", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            //TODO 누적수 체크해서 한번만 토스트 띄우게 로직 변경
//                        Toast.makeText(LoginActivity.this , "없는 아이디 입니디", Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        //==========================================================================================


        //서비스====================================================================================
        //서비스 실행
        Intent startServiceIntent = new Intent(this, LunchMatchService.class);
        startService(startServiceIntent);


        //죽지 않는 서비스--------------------------------------------------------------------------
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }

        if (RealService.serviceIntent == null) {
            serviceIntent = new Intent(this, RealService.class);
            startService(serviceIntent);
        } else {
            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
        }
        //------------------------------------------------------------------------------------------


    }

    //유저를 json object 로 바꾸어주는 메소드
    private void userDataToJson(JSONObject jsonObject, User user) {

        try {
            jsonObject.put("userApproval", user.isUserApproval());
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("userPw", user.getUserPw());
            jsonObject.put("userName", user.getUserName());
            jsonObject.put("userGender", user.getUserGender());
            jsonObject.put("userBirthday", user.getUserBirthday());
            jsonObject.put("userNickName", user.getUserNickName());
            jsonObject.put("userComment", user.getUserComment());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Main", "LoginActivity_onDestroy");

        //죽지 않는 서비스
        if (serviceIntent != null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }


    //생명주기 디버깅용=============================================================================
    /*
    @Override
    protected void onStart() {
        super.onStart();

        Log.i("Main" ,"LoginActivity_onStart");
        //Toast.makeText(this, "LoginActivity_onStart", Toast.LENGTH_SHORT).show();
        println("onStart 호출됨");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("Main" ,"LoginActivity_onRestart");
        //Toast.makeText(this, "LoginActivity_onRestart", Toast.LENGTH_SHORT).show();
        println("onRestart 호출됨");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("Main" ,"LoginActivity_onStop");
        //Toast.makeText(this, "LoginActivity_onStop", Toast.LENGTH_SHORT).show();
        println("onStop 호출됨");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Toast.makeText(this, "LoginActivity_onDestroy", Toast.LENGTH_SHORT).show();
        println("onDestroy 호출됨");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("Main", "LoginActivity_onPause");
        //Toast.makeText(this, "LoginActivity_onPause", Toast.LENGTH_SHORT).show();
        println("onPause 호출됨");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Main" ,"LoginActivity_onResume");
        //Toast.makeText(this, "LoginActivity_onResume", Toast.LENGTH_SHORT).show();
        println("onResume 호출됨");
    }*/
    //==============================================================================================


}
