package june.second.lunchmatchmaker.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import june.second.lunchmatchmaker.Item.RealMatch;
import june.second.lunchmatchmaker.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchTitleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchTitleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;


    //파이어 베이스---------------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------

    int verticalPosition;
    TextView matchMainTitle;
    public MatchTitleFragment() {
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
    public static MatchTitleFragment newInstance(int param1) {
        MatchTitleFragment fragment = new MatchTitleFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_match_title, container, false);

        //아이디를 저장할때 포지션값에 1을 더해서 저장하기때문에 다시 1을 빼준다
        verticalPosition = Integer.parseInt(String.valueOf(container.getId()))-1;

        //매치들을 저장한 객체리스트에서 해당 포지션값에 맞는 데이터 가져오기
        RealMatch realMatch = MatchListActivity.realMatchArrayList.get(verticalPosition);



        matchMainTitle = v.findViewById(R.id.matchMainTitle);
        TextView maxPeople = v.findViewById(R.id.maxPeople);
        TextView matchTime = v.findViewById(R.id.time);
        TextView nowPeople = v.findViewById(R.id.nowPeople);
        TextView matchPlace = v.findViewById(R.id.place);

        v.setOnClickListener(view -> {
            //디버깅용
            Log.w("MatchTitleFragment", "verticalPosition: "+ verticalPosition);
            Toast.makeText(getContext(), verticalPosition+"", Toast.LENGTH_LONG);
        });


        //매치 타이틀 프래그먼트를 길게 눌렀을때
        //=> 매치 편집 액티비티로 이동
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(getContext(), MatchEditActivity.class);
                //플래그로 저장 임시 구현
                intent.putExtra("matchPosition", verticalPosition);
                Log.w("MatchContentFragment", "setOnLongClickListener verticalPosition: "+ verticalPosition);
                startActivity(intent);
                return false;
            }
        });


        //파이어베이스로 매치 제목 설정해주기
        databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex()).trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RealMatch realMatch =  dataSnapshot.getValue(RealMatch.class);

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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //대화 키워드 - 데이터랑 뷰 연동------------------------------------------------------------
        //키워드 값 없을시 공백으로 만들어주고 있으면 앞에 # 붙여서 키워드 느낌나게 한다
        TextView keywordFirst = v.findViewById(R.id.keyword1);
        if (realMatch.getMatchKeywordFirst() == null || realMatch.getMatchKeywordFirst().length() == 0) {
            // 값이 없는 경우 처리
            keywordFirst.setText("");
        }
        else{
            // 값이 있는 경우 처리
            if(spaceCheck(realMatch.getMatchKeywordFirst())){
                //전체 공백이면
                keywordFirst.setText("");
            }
            else {
                //키워드 값이 존재하면 #하고 같이 값 넣어주기
                //키워드 느낌을 내기 위해서
                keywordFirst.setText("# "+realMatch.getMatchKeywordFirst());
            }
        }

        TextView keywordSecond = v.findViewById(R.id.keyword2);
        if (realMatch.getMatchKeywordSecond() == null || realMatch.getMatchKeywordSecond().length() == 0) {
            // 값이 없는 경우 처리
            keywordSecond.setText("");
        }
        else {
            if(TextUtils.isEmpty(realMatch.getMatchKeywordSecond() )){
            }
            else{
                // 값이 있는 경우 처리
                if(spaceCheck(realMatch.getMatchKeywordSecond())){
                    //전체 공백이면
                    keywordSecond.setText("");
                }
                else {
                    //키워드 값이 존재하면 #하고 같이 값 넣어주기
                    //키워드 느낌을 내기 위해서
                    keywordSecond.setText("# "+realMatch.getMatchKeywordSecond());

                }
            }

        }


        TextView keywordThird = v.findViewById(R.id.keyword3);
        if (realMatch.getMatchKeywordThird() == null || realMatch.getMatchKeywordThird().length() == 0) {
            // 값이 없는 경우 처리
            keywordThird.setText("");
        }
        else{
            // 값이 있는 경우 처리
            if(spaceCheck(realMatch.getMatchKeywordThird())){
                //전체 공백이면
                keywordThird.setText("");
            }
            else {
                //키워드 값이 존재하면 #하고 같이 값 넣어주기
                //키워드 느낌을 내기 위해서
                keywordThird.setText("# "+realMatch.getMatchKeywordThird());
            }
        }
        //------------------------------------------------------------------------------------------


        return v;
    }


    //공백 체크
    //내용 전체가 공백일 때 true 리턴
    public boolean spaceCheck(String spaceCheck)
    {
        for(int i = 0 ; i < spaceCheck.length() ; i++)
        {
            if(spaceCheck.charAt(i) != ' ')
                return false;
        }
        return true;
    }
}
