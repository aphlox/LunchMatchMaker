package june.second.lunchmatchmaker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import june.second.lunchmatchmaker.Item.User;

import static android.content.Context.MODE_PRIVATE;

public class JoinApprovalReceiver extends BroadcastReceiver {
    public static final String JOIN_APPROVAL = "june.second.lunchmatchmaker.action.ACTION_JOIN_APPROVAL";
    public static final String JOIN_RESULT = "june.second.lunchmatchmaker.action.ACTION_JOIN_RESULT";
    User user;
    @Override
    public void onReceive(Context context, Intent intent) {

        //회원 가입 승인 요청에 대한 수락/거절을 담은 신호가 왔을 때
        if (JOIN_RESULT.equals(intent.getAction())) {


            //회원 가입시에 뜰 방송
            //노티?? 보내기?

            String userId = intent.getStringExtra("userId");
            String joinResult = intent.getStringExtra("joinResult");

            //유저 데이터를 저장한 쉐어드 불러오기
            SharedPreferences prefUser = context.getSharedPreferences("prefUser", MODE_PRIVATE);
            SharedPreferences.Editor prefUserEditor = prefUser.edit();

            //현재 대기 유저 데이터 쉐어드 가져오기
            SharedPreferences prefWaitUser = context.getSharedPreferences("prefWaitUser", MODE_PRIVATE);
            SharedPreferences.Editor prefWaitUserEditor = prefWaitUser.edit();


            Map<String, ?> allEntries = prefUser.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                try {
                    JSONObject userJsonObject = new JSONObject(entry.getValue().toString());
                    user = new User(userJsonObject.getBoolean("userApproval"), userJsonObject.getString("userId"), userJsonObject.getString("userPw"), userJsonObject.getString("userName")
                            , userJsonObject.getString("userGender"), userJsonObject.getString("userBirthday"), userJsonObject.getString("userNickName")
                            , userJsonObject.getString("userComment"));

                    //유저 데이터들의 아이디들과 승인/거절 신호에 담긴 아이디와 비교
                    if(userJsonObject.getString("userId").trim().equals(userId.trim()) || user.getUserId().trim().equals(userId.trim()) ){
                        //승인되었으면
                        if("ok".equals(joinResult)){
                            Toast.makeText(context, "축하합니다. 회원 가입 되었습니다. ", Toast.LENGTH_SHORT).show();

                            //userApproval 을 true 로 바꾸어줘서 정회원(활동가능)으로 바꾸어준다.
                            user = new User(true, userJsonObject.getString("userId"), userJsonObject.getString("userPw"), userJsonObject.getString("userName")
                                    , userJsonObject.getString("userGender"), userJsonObject.getString("userBirthday"), userJsonObject.getString("userNickName")
                                    , userJsonObject.getString("userComment"));
                            JSONObject newUserJsonObject = new JSONObject();
                            userDataToJson(newUserJsonObject, user);
                            Log.w("JoinApprovalReceiver", "userDataToJson  @"+  newUserJsonObject.toString());
                            prefUserEditor.putString(user.getUserId(), newUserJsonObject.toString()); // 키값으로 매치 객체 스트링으로 저장
                            prefUserEditor.commit();

                            prefWaitUserEditor.putString("waitUser", newUserJsonObject.toString());
                            prefWaitUserEditor.commit();

                        }
                        //거절이면
                        else if("no".equals(joinResult)){
                            //거절 메세지만 띄어준다. => userApproval (활동가능)는 기본으로 false 값이기 때문에
                            //따로 값을 변경시켜주진 않는다
                            Toast.makeText(context, "죄송합니다. 가입 승인이 거절되었습니다. ", Toast.LENGTH_SHORT).show();
                        }

                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }

            //디버깅 체크용
/*
            if("ok".equals(joinResult)) {
                Toast.makeText(context, "축하합니다. 회원 가입 되었습니다. ", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, userId, Toast.LENGTH_SHORT).show();
            }
*/



            //이후의 브로드캐스트의 전파를 막기
//            abortBroadcast();
        }
    }


    private void userDataToJson(JSONObject jsonObject, User user) {


        try {
            jsonObject.put("userApproval", user.isUserApproval());
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("userPw", user.getUserPw());
            jsonObject.put("userName", user.getUserName() );
            jsonObject.put("userGender", user.getUserGender());
            jsonObject.put("userBirthday", user.getUserBirthday());
            jsonObject.put("userNickName", user.getUserNickName() );
            jsonObject.put("userComment", user.getUserComment());



        } catch (JSONException e) {
            e.printStackTrace();
        }
//        receiveObject(jsonObject);
    }
}
