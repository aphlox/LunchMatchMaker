package june.second.lunchmatchmaker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import june.second.lunchmatchmaker.Item.Product;
import june.second.lunchmatchmaker.Item.RealMatch;
import june.second.lunchmatchmaker.R;

import static june.second.lunchmatchmaker.Activity.MainMapActivity.realMatchArrayList;

public class MatchListActivity extends AppCompatActivity {
    //디버깅을 위한 string 값
    String here = "MatchListActivity";

    //데이터 저장-------------------------------------------------------------------------------
    RecyclerView rvMatchList;
    ProductCardAdapter mCardAdapter;
    //------------------------------------------------------------------------------------------

    //파이어 베이스---------------------------------------------------------------------------------
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //----------------------------------------------------------------------------------------------


    //인디케이터 시도-------------------------------------------------------------------------------
    private LayoutInflater inflater;
    private View row;
    private View matchFragment;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        Log.w(here, here + "  onCreate");


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        //지도 이미지 눌러서 MainMapActivity 로 이동
        findViewById(R.id.mapButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMapActivity.class);

            //플래그로 저장 임시 구현
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        });


        //하단바에서 스토리로 가는 버튼(이미지)
        findViewById(R.id.matchToStoryImage).setOnClickListener(v -> {
            Intent intent = new Intent(this, StoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });

        //하단바에서 프로필로 가는 버튼(이미지)
        findViewById(R.id.matchToProfileImage).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });


        //------------------------------------------------------------------------------------------


        //뷰페이저 리사이클러뷰 구현(리사이클러뷰 아이템이 뷰페이저)================================
        //아이템(뷰페이저가 될) 객체 리스트 및 객체 선언
        ArrayList<Product> items = new ArrayList<>();
        Product product = new Product();

        //매치 리스트 사이즈 만큼 리스트 구현하기
        for (int i = 0; i < realMatchArrayList.size(); i++) {
            items.add(product);
        }

        //리사이클러뷰------------------------------------------------------------------------------
        //레이아웃에 있는 리사이클러뷰와 연동
        rvMatchList = findViewById(R.id.rvMatchList);

        //구분선 넣어주기
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
        rvMatchList.addItemDecoration(dividerItemDecoration);

        //레이아웃 매니저 설정
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMatchList.setLayoutManager(mLayoutManager);

        //리사이클러뷰 어댑터 설정
        mCardAdapter = new ProductCardAdapter(getSupportFragmentManager(), items);
        rvMatchList.setAdapter(mCardAdapter);

        //스크롤 리스너 -> x축, y축 둘다 스크롤 인식
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisible >= totalItemCount - 1) {
                    Log.w("TAG", "lastVisibled");
                }
            }
        };

        rvMatchList.addOnScrollListener(onScrollListener);
        //------------------------------------------------------------------------------------------


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(here, here + "  onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(here, here + "  onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(here, here + "  onResume");


        //인디케이터 시도
        /*
        inflater = getLayoutInflater();
        row = inflater.inflate(R.layout.row, null);
        matchFragment = inflater.inflate(R.layout.fragment_match_content, null);
        if(row != null){
            Log.v("00", "00");
        }




        // 뷰페이저 인디케이트
        int indicatorWidth = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()) + 0.5f);
        int indicatorHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics()) + 0.5f);
        int indicatorMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getResources().getDisplayMetrics()) + 0.5f);




        ViewPager viewpager = row.findViewById(R.id.vp);

        CircleIndicator indicator = matchFragment.findViewById(R.id.indicator);
        Config config = new Config.Builder().width(indicatorWidth)
                .height(indicatorHeight)
                .margin(indicatorMargin)
                .animator(R.animator.indicator_animator)
                .animatorReverse(R.animator.indicator_animator_reverse)
                .drawable(R.drawable.black_radius_square)
                .build();
        indicator.initialize(config);

        viewpager.setAdapter(mCardAdapter);
        viewpager.setA
        indicator.setViewPager(viewpager);*/


        //매치 값을 저장한 쉐어드 불러오기
        SharedPreferences prefMatch = getSharedPreferences("prefMatch", MODE_PRIVATE);
        SharedPreferences.Editor prefMatchEditor = prefMatch.edit();

        //matchArrayList => 매치 저장 전환중이라
        MainMapActivity.matchArrayList.clear();

        //매치 리스트 갱신
        //매치 리스트 삭제후 다시 저장
        databaseReference.child("realMatch").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                realMatchArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RealMatch realMatch = snapshot.getValue(RealMatch.class);

                    realMatchArrayList.add(realMatch);

                }
                Log.w(here, "realMatchArrayList.size :  " + realMatchArrayList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rvMatchList.getAdapter().notifyDataSetChanged();
        mCardAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(here, here + "  onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(here, here + "  onStop ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(here, here + "  onDestroy");
    }
}


