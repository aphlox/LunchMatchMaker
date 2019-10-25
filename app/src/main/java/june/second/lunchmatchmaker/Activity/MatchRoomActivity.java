package june.second.lunchmatchmaker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import june.second.lunchmatchmaker.Item.RealMatch;
import june.second.lunchmatchmaker.Item.User;
import june.second.lunchmatchmaker.R;

import static android.content.ContentValues.TAG;
import static june.second.lunchmatchmaker.Activity.MatchListActivity.realMatchArrayList;



public class MatchRoomActivity extends AppCompatActivity {
    String here = "MatchRoomActivity";


    //파이어 베이스---------------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------


    int matchPosition;
    TextView matchMainTitle;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(here, "onCreate ");

        setContentView(R.layout.activity_match_room);
        //매치 편집하기 위해 편집할 아이템의 포지션값 받아오고--------------------------------------
        //매치 객체리스트에서 해당 아이템 가져오기
        Intent getIntent = getIntent();
        matchPosition = getIntent.getIntExtra("matchPosition",0);
        RealMatch realMatch = realMatchArrayList.get(matchPosition);
        //------------------------------------------------------------------------------------------


        //뒤로가는 화살표 이미지
        // ProfileNTimelineActivity 액티비티로 이동
        findViewById(R.id.back).setOnClickListener(view -> {
            Intent intent = new Intent(this, MatchListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        });


        /*
        //Glide 적용하는 이미지를 크롤링해서 가져와야돼서 보류
        placeImage = findViewById(R.id.placeImage);
        Glide.with(this).load("http://ldb.phinf.naver.net/20190810_148/1565366691827UwmhA_JPEG/o7Y7-DD7F4FEAS0NGUx0m7_L.jpg").into(placeImage); //Glide을 이용해서 이미지뷰에 url에 있는 이미지를 세팅해줌*/


        matchMainTitle = findViewById(R.id.matchMainTitle);
        TextView maxPeople = findViewById(R.id.maxPeople);
        TextView matchTime = findViewById(R.id.time);
        TextView nowPeople = findViewById(R.id.nowPeople);
        TextView matchPlace = findViewById(R.id.place);


        //제목 길이에 따라서 글자크기 조절해주기
        if(realMatch.getMatchTitle().length() <8){
            matchMainTitle.setText(realMatch.getMatchTitle());
            matchMainTitle.setTextSize(45);
        }
        else if(realMatch.getMatchTitle().length() <15){
            matchMainTitle.setText(realMatch.getMatchTitle());
            matchMainTitle.setTextSize(30);
        }
        else {
            matchMainTitle.setText(realMatch.getMatchTitle());
        }

        nowPeople.setText(Integer.toString(realMatch.getMatchNowPeople()));
        maxPeople.setText(Integer.toString(realMatch.getMatchMaxPeople()));
        matchTime.setText(realMatch.getMatchTime());
        matchPlace.setText(realMatch.getMatchPlace());

        TextView member1 = findViewById(R.id.member1);
        member1.setText(realMatch.getFirstMemberNickname());
        TextView member2 = findViewById(R.id.member2);
        member2.setText(realMatch.getSecondMemberNickname());
        TextView member3 = findViewById(R.id.member3);
        member3.setText(realMatch.getThirdMemberNickname());
        TextView member4 = findViewById(R.id.member4);
        member4.setText(realMatch.getFourthMemberNickname());

        //매치 참가 버튼 눌렀을때
        //=> 현재 유저 불러오고 매치에 자리가 남아있는지, 이미 참가되어있는지 체크해서 참가시키기
        Button btnMatchAccept = findViewById(R.id.btnMatchAccept);
        btnMatchAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //현재 접속되어있는 유저 데이터 불러오기
                SharedPreferences prefNowUser = getSharedPreferences("prefNowUser", MODE_PRIVATE);
                SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

                try {
                    JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser", "    "));
                    User nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                            , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                            , nowUserJsonObject.getString("userComment"), nowUserJsonObject.getString("userProfilePath"));

                    //현재 접속한 유저 불러왔으면
                    //매치에 참가시키기
                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex()).trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //파이어베이스로 해당 매치 객체로 가져오기
                            RealMatch realMatch = dataSnapshot.getValue(RealMatch.class);
                            Log.w(TAG, "onDataChange: matchRoom.getMatchNowPeople()" + realMatch.getMatchNowPeople());
                            Log.w(TAG, "onDataChange: matchRoom.getMatchMaxPeople()" + realMatch.getMatchMaxPeople());


                            //방이 아직 안 찬 경우
                            if (realMatch.getMatchNowPeople() < realMatch.getMatchMaxPeople()) {

                                Log.w(TAG, "onDataChange: matchRoom.getFirstMemberId()" + realMatch.getFirstMemberId());
                                Log.w(TAG, "onDataChange: matchRoom.getSecondMemberId()" + realMatch.getSecondMemberId());
                                Log.w(TAG, "onDataChange: matchRoom.getThirdMemberId()" + realMatch.getThirdMemberId());
                                Log.w(TAG, "onDataChange: matchRoom.getFourthMemberId()" + realMatch.getFourthMemberId());

                                //첫번째가 없으면 매치가 취소된 상태이므로
                                //(첫번째는 만든 사람의 이름이 들어간다)
                                //(매치 참가 취소나 편집은 아직 보류상태)
                                //두번째부터 체크
                                if (realMatch.getFirstMemberId() == null || realMatch.getFirstMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: First");


                                } else if (nowUser.getUserId().trim().equals(realMatch.getFirstMemberId().trim())) {
                                    //이미 참가했을때
                                    Toast.makeText(getApplicationContext(), "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getSecondMemberId() == null || realMatch.getSecondMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Second");

                                    realMatch.setSecondMemberId(nowUser.getUserId());
                                    realMatch.setSecondMemberNickname(nowUser.getUserNickName());
                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(getApplicationContext(), "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;


                                } else if (nowUser.getUserId().trim().equals(realMatch.getSecondMemberId().trim())) {
                                    //이미 참가했을때
                                    Toast.makeText(getApplicationContext(), "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getThirdMemberId() == null || realMatch.getThirdMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Third");
                                    realMatch.setThirdMemberId(nowUser.getUserId());
                                    realMatch.setThirdMemberNickname(nowUser.getUserNickName());

                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(getApplicationContext(), "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;


                                } else if (nowUser.getUserId().trim().equals(realMatch.getThirdMemberId().trim())) {
                                    //이미 참가했을때
                                    Toast.makeText(getApplicationContext(), "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getFourthMemberId() == null || realMatch.getFourthMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Fourth");
                                    realMatch.setFourthMemberId(nowUser.getUserId());
                                    realMatch.setFourthMemberNickname(nowUser.getUserNickName());
                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(getApplicationContext(), "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;

                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "이 매치는 이미 사람이 다 모였습니다", Toast.LENGTH_LONG).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //파이어베이스 현재인원 불러오기
//                    databaseReference.child("matchRoom").child(Integer.toString(match.getMatchIndex()).trim()).addValueEventListener();
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
