package june.second.lunchmatchmaker.Activity;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
 * Use the {@link MatchMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchMemberFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    int verticalPosition;

    //파이어 베이스---------------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------

    public MatchMemberFragment() {
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
    public static MatchMemberFragment newInstance(int param1) {
        MatchMemberFragment fragment = new MatchMemberFragment();
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
        View v = inflater.inflate(R.layout.fragment_match_member, container, false);

        //아이디를 저장할때 포지션값에 1을 더해서 저장하기때문에 다시 1을 빼준다
        verticalPosition = Integer.parseInt(String.valueOf(container.getId())) - 1;

        //매치들을 저장한 객체리스트에서 해당 포지션값에 맞는 데이터 가져오기
        RealMatch realMatch = MainMapActivity.realMatchArrayList.get(verticalPosition);
        Log.w("MatchMemberFragment", "onCreate container.getId(): " + container.getId());

        //파이어베이스에서 데이터 가져와 참가한 닉네임들 설정해주기
        databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex()).trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RealMatch realMatch = dataSnapshot.getValue(RealMatch.class);

                TextView member1 = v.findViewById(R.id.member1);
                member1.setText(realMatch.getFirstMemberNickname());
                TextView member2 = v.findViewById(R.id.member2);
                member2.setText(realMatch.getSecondMemberNickname());
                TextView member3 = v.findViewById(R.id.member3);
                member3.setText(realMatch.getThirdMemberNickname());
                TextView member4 = v.findViewById(R.id.member4);
                member4.setText(realMatch.getFourthMemberNickname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        v.setOnClickListener(view -> {

            //디버깅용
            Log.w("MatchMemberFragment", "verticalPosition: " + verticalPosition);
            Toast.makeText(getContext(), verticalPosition + "", Toast.LENGTH_LONG);

        });


        return v;
    }

}
