package june.second.lunchmatchmaker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Service.LunchMatchService;

public class RegisterResultActivity extends AppCompatActivity {
    String here = "RegisterResultActivity";

    private TextView mMessageTextView;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_result);


        Intent startServiceIntent = new Intent(this, LunchMatchService.class);
        startService(startServiceIntent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent startServiceIntent = new Intent(this, LunchMatchService.class);
        startService(startServiceIntent);

        //현재 대기 유저 데이터 쉐어드 가져오기
        SharedPreferences prefWaitUser = getSharedPreferences("prefWaitUser", MODE_PRIVATE);
        SharedPreferences.Editor prefWaitUserEditor = prefWaitUser.edit();

            try {
                JSONObject userJsonObject = new JSONObject(prefWaitUser.getString("waitUser",""));
                user = new User(userJsonObject.getBoolean("userApproval"), userJsonObject.getString("userId"), userJsonObject.getString("userPw"), userJsonObject.getString("userName")
                        , userJsonObject.getString("userGender"), userJsonObject.getString("userBirthday"), userJsonObject.getString("userNickName")
                        , userJsonObject.getString("userComment") , userJsonObject.getString("userProfilePath"));
                Log.w(here, "userDataToJson  @"+  userJsonObject.toString());

                if (user.isUserApproval()) {
                    //로그인 될때 대기 유저를 prefNowUser 에 접속 유저로 저장해놓기
                    SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
                    SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();
                    prefNowUserEditor.putString("nowUser", userJsonObject.toString()); // 키값으로 매치 객체 스트링으로 저장
                    prefNowUserEditor.commit();
                    Log.w(here, "userDataToJson  @"+  userJsonObject.toString());



                    Intent intent = new Intent(this, MatchListActivity.class);
                    startActivity(intent);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

}
