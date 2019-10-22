package june.second.lunchmatchmaker.Activity;


import android.os.Bundle;
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
 * Use the {@link MatchContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchContentFragment extends Fragment {
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

    public MatchContentFragment() {
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
    public static MatchContentFragment newInstance(int param1) {
        MatchContentFragment fragment = new MatchContentFragment();
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
        View v = inflater.inflate(R.layout.fragment_match_content, container, false);

        verticalPosition = Integer.parseInt(String.valueOf(container.getId()))-1;

        //매치들을 저장한 객체리스트에서 해당 포지션값에 맞는 데이터 가져오기
        RealMatch realMatch = MainMapActivity.realMatchArrayList.get(verticalPosition);


        v.setOnClickListener(view -> {
            //디버깅용
            Log.w("MatchContentFragment", "verticalPosition: " + verticalPosition);
            Toast.makeText(getContext(), verticalPosition + "", Toast.LENGTH_LONG);
        });


        TextView maxPeople = v.findViewById(R.id.maxPeople);
        TextView matchTime = v.findViewById(R.id.time);
        TextView nowPeople = v.findViewById(R.id.nowPeople);
        TextView matchPlace = v.findViewById(R.id.place);

        //파이어베이스에서 가져온 값으로 설정해주기
        databaseReference.child("realMatch").child(Integer.toString(realMatch.getMatchIndex()).trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                RealMatch realMatch =  dataSnapshot.getValue(RealMatch.class);

                nowPeople.setText(Integer.toString(realMatch.getMatchNowPeople()));
                maxPeople.setText(Integer.toString(realMatch.getMatchMaxPeople()));
                matchTime.setText(realMatch.getMatchTime());
                matchPlace.setText(realMatch.getMatchPlace());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //장소 텍스트를 누르면 => 해당 매치가 가지고 있는 위치로 지도 이동
        //이때 다른 지도 어플을 부를 수도 있고 내 어플내에서 찾을 수 도 있음
        /*  matchPlace.setOnClickListener(view -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            LatLng coord = match.getLatLng();
            intent.setData(Uri.parse("geo: " + coord.latitude + "," + coord.longitude + ""));
            intent.putExtra("order", "find");
            intent.putExtra("lat", coord.latitude);
            intent.putExtra("lng", coord.longitude);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });*/


        return v;
    }

}
