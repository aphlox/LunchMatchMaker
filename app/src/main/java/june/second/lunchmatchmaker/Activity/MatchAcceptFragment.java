package june.second.lunchmatchmaker.Activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchAcceptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchAcceptFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    //context 가져오기  - 토스트 띄우기 위해서
    private Context context;

    //파이어 베이스 저장----------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------


    public MatchAcceptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MatchContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchAcceptFragment newInstance(int param1) {
        MatchAcceptFragment fragment = new MatchAcceptFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match_accept, container, false);

        //context
        context = container.getContext();

        //아이디를 저장할때 포지션값에 1을 더해서 저장하기때문에 다시 1을 빼준다
        int verticalPosition = Integer.parseInt(String.valueOf(container.getId())) - 1;

        //매치들을 저장한 객체리스트에서 해당 포지션값에 맞는 데이터 가져오기
        RealMatch realMatch = MainMapActivity.realMatchArrayList.get(verticalPosition);


        //매치 참가 버튼 눌렀을때
        //=> 현재 유저 불러오고 매치에 자리가 남아있는지, 이미 참가되어있는지 체크해서 참가시키기
        Button btnMatchAccept = v.findViewById(R.id.btnMatchAccept);
        btnMatchAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //현재 접속되어있는 유저 데이터 불러오기
                SharedPreferences prefNowUser = getContext().getSharedPreferences("prefNowUser", MODE_PRIVATE);
                SharedPreferences.Editor prefNowUserEditor = prefNowUser.edit();

                try {
                    JSONObject nowUserJsonObject = new JSONObject(prefNowUser.getString("nowUser", "    "));
                    User nowUser = new User(nowUserJsonObject.getBoolean("userApproval"), nowUserJsonObject.getString("userId"), nowUserJsonObject.getString("userPw"), nowUserJsonObject.getString("userName")
                            , nowUserJsonObject.getString("userGender"), nowUserJsonObject.getString("userBirthday"), nowUserJsonObject.getString("userNickName")
                            , nowUserJsonObject.getString("userComment"));

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
                                    Toast.makeText(context, "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getSecondMemberId() == null || realMatch.getSecondMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Second");

                                    realMatch.setSecondMemberId(nowUser.getUserId());
                                    realMatch.setSecondMemberNickname(nowUser.getUserNickName());
                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(context, "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;


                                } else if (nowUser.getUserId().trim().equals(realMatch.getSecondMemberId().trim())) {
                                    //이미 참가했을때
                                    Toast.makeText(context, "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getThirdMemberId() == null || realMatch.getThirdMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Third");
                                    realMatch.setThirdMemberId(nowUser.getUserId());
                                    realMatch.setThirdMemberNickname(nowUser.getUserNickName());

                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(context, "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;


                                } else if (nowUser.getUserId().trim().equals(realMatch.getThirdMemberId().trim())) {
                                    //이미 참가했을때
                                    Toast.makeText(context, "이미 참가한 매치입니다", Toast.LENGTH_LONG).show();

                                } else if (realMatch.getFourthMemberId() == null || realMatch.getFourthMemberId().length() == 0) {
                                    Log.w(TAG, "onDataChange: Fourth");
                                    realMatch.setFourthMemberId(nowUser.getUserId());
                                    realMatch.setFourthMemberNickname(nowUser.getUserNickName());
                                    //참가하면 현재 참가인원 +1
                                    realMatch.setMatchNowPeople(realMatch.getMatchNowPeople() + 1);

                                    databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex())).setValue(realMatch);
                                    Toast.makeText(context, "매치에 참가했습니다!", Toast.LENGTH_LONG).show();

                                    return;

                                }


                            } else {
                                Toast.makeText(context, "이 매치는 이미 사람이 다 모였습니다", Toast.LENGTH_LONG).show();
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

        return v;
    }


}
