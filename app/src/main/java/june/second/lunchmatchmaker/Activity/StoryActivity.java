package june.second.lunchmatchmaker.Activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import june.second.lunchmatchmaker.Gesturedetector.ItemTouchListenerDispatcher;
import june.second.lunchmatchmaker.Gesturedetector.SmallMediumGestureDetector;
import june.second.lunchmatchmaker.Item.Story;
import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.View.MediumRecyclerViewHolder;
import june.second.lunchmatchmaker.View.SmallRecyclerViewHolder;


public class StoryActivity extends AppCompatActivity {
    //디버깅을 위한 string 값
    String here = "StoryActivity";



    //스토리(갤러리) 구성요소=======================================================================
    //스토리(갤러리)--------------------------------------------------------------------------------
    //스토리 객체 리스트
    Story story;
    static ArrayList<Story> storyArrayList = new ArrayList<Story>();

    private RecyclerView smallRecyclerView;
    private RecyclerView mediumRecyclerView;
    private GridLayoutManager smallGridLayoutManager;
    private GridLayoutManager mediumGridLayoutManager;
    private RecyclerView.Adapter mediumAdapter;
    private RecyclerView.OnItemTouchListener onItemTouchListener;
    private FrameLayout containerView;
    private FrameLayout fullScreenContainer;
    private LinearLayout overlayBarLayout;
    private LinearLayout profileOverLayout;
    private ImageView makerProfile;
    private TextView makerNameNAge;


    private static final int SPAN_SMALL = 4;
    private static final int SPAN_MEDIUM = 3;
    //----------------------------------------------------------------------------------------------

    //오버레이--------------------------------------------------------------------------------------
    //이미지 눌렀을때 크게 보이게 하려고 오버레이 설정
    //오버레이때 메뉴 조정하기 위한 값
    private boolean overlayMenuVisible = false;

    //오버레이 메뉴에서 아이템을 삭제하기 위해서
    //해당 아이템의 위치를 알 수 있는 변수 선언
    private int broadPosition;

    static TextView textOverlay;
    static ViewGroupOverlay overlay;
    static View realView;
    //----------------------------------------------------------------------------------------------
    //==============================================================================================


    //데이터 저장-----------------------------------------------------------------------------------
    SharedPreferences prefGallery; //이미지(갤러리) 저장하는 쉐어드 객체
    SharedPreferences.Editor prefGalleryEditor; //이미지(갤러리) 쉐어드 에디터

    //----------------------------------------------------------------------------------------------

    //기타------------------------------------------------------------------------------------------

    private FloatingActionButton fabWriteStory;     //매치 추가해주는 fab
    private LottieAnimationView heartAnimationView;
    private boolean heartCondition =false;
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.w(here, here + "  onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //스토리(갤러리) 구성요소 초기화------------------------------------------------------------
        smallRecyclerView = findViewById(R.id.smallRecyclerView);    //이미지가 작은 크기일때의 리사이클러뷰
        mediumRecyclerView = findViewById(R.id.mediumRecyclerView);  //이미지가 큰 크기일때의 리사이클러뷰
        containerView = (FrameLayout) findViewById(R.id.container);   //리사이클러뷰와 fab 추가버튼 담겨있는 프레임 레이아웃
        fullScreenContainer = (FrameLayout) findViewById(R.id.fullScreenImageContainer); //오버레이를 담는 프레임 레이아웃
        overlay = fullScreenContainer.getOverlay(); //fullScreenContainer 에 오버레이 담기
        textOverlay = findViewById(R.id.textOverlay); //오버레이 되었을때 사진에 담긴 글을 나타내는 textView
        overlayBarLayout = findViewById(R.id.textOverLayout); //textOverlay 를 담고있는 리니어 레이아웃
        makerProfile = findViewById(R.id.makerProfile);
        makerNameNAge = findViewById(R.id.makerNameNAge);
        profileOverLayout= findViewById(R.id.profileOverLayout);

        //로띠 하트 애니메이션
        heartAnimationView = findViewById(R.id.heartAnimationView);
        heartAnimationView.setAnimation("drawing_a_love.json");
        heartAnimationView.setOnClickListener(v -> {
            Log.w(here, "heartCondition: "+heartCondition );
            if(heartCondition){
                heartCondition = !heartCondition;
                heartAnimationView.setProgress(0);
            }
            else{
                heartAnimationView.playAnimation();
            }

        });


        heartAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                heartCondition = !heartCondition;




            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {


            }
        });

        //작은 이미지일때는 리사이클러뷰의 이미지들 가로에 SPAN_SMALL (4개)만큼 배열
        smallGridLayoutManager = new GridLayoutManager(this, SPAN_SMALL);
        smallRecyclerView.setLayoutManager(smallGridLayoutManager);

        //큰 이미지일때는 가로에 SPAN_MEDIUM (3개)만큼 배열
        mediumGridLayoutManager = new GridLayoutManager(this, SPAN_MEDIUM);
        mediumRecyclerView.setLayoutManager(mediumGridLayoutManager);

        //작은거, 중간 사이즈 리사이클러뷰 어댑터 설정하는 메소드 실행
        setSmallAdapter();
        setMediumAdapter();


        //pivot 을 왼쪽 위로 설정한다
        //설정하지 않으면 가운데가 디폴트 값인데 그러면 가운데에서부터 모든 방향으로 커진다
        //내가 주려는 자연스럽게 커지는 효과를 주기위해서 왼쪽위로 pivot 을 설정한다
        smallRecyclerView.setPivotX(0);
        smallRecyclerView.setPivotY(0);
        mediumRecyclerView.setPivotY(0);
        mediumRecyclerView.setPivotX(0);

        //크기 변화할때의 smallRecyclerView 와 mediumRecyclerView 의 크기 조절와 밝기 조절 및 전환
        //자연스러운 변화를 위해
        final ScaleGestureDetector scaleGestureDetector =
                new ScaleGestureDetector(this, new SmallMediumGestureDetector(smallRecyclerView, mediumRecyclerView));
        onItemTouchListener = (RecyclerView.OnItemTouchListener) new ItemTouchListenerDispatcher(this, scaleGestureDetector, scaleGestureDetector);
        smallRecyclerView.addOnItemTouchListener(onItemTouchListener);
        mediumRecyclerView.addOnItemTouchListener(onItemTouchListener);
        //------------------------------------------------------------------------------------------


        //툴바 설정---------------------------------------------------------------------------------
        Toolbar tb = findViewById(R.id.toolbar_story);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("스토리");

        //------------------------------------------------------------------------------------------


        //액티비티내의 뷰 연동 및 클릭시의 동작 설정------------------------------------------------
        //스토리 쓰러가는 floating button
        fabWriteStory = findViewById(R.id.fabWriteStory);

        //추가 버튼 누를때 동작 변화
        //StoryWriteActivity 로 이동
        fabWriteStory.setOnClickListener(v -> {
            Intent intent = new Intent(this, StoryWriteActivity.class);
            startActivity(intent);
        });


        //하단바에서 매치로 가는 버튼(이미지)
        findViewById(R.id.storyToMatchImage).setOnClickListener(v -> {
            Intent intent = new Intent(this, MatchListActivity.class);
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });

        //하단바에서 프로필로 가는 버튼(이미지)
        findViewById(R.id.storyToProfileImage).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);
            startActivity(intent);
            //액티비티 전환 애니메이션 없애기
            overridePendingTransition(0, 0);

        });

        //------------------------------------------------------------------------------------------

        //데이터 저장-------------------------------------------------------------------------------
        prefGallery = getSharedPreferences("imageGallery", MODE_PRIVATE); //SharedPreferences 초기화-(갤러리 저장용)
        prefGalleryEditor = prefGallery.edit();     //SharedPreferences (갤러리 저장용) editor 초기화

        //------------------------------------------------------------------------------------------






/*        //애니메이션 추가하려다 시간 부족으로 보류
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(10000);
        animator.setRemoveDuration(10000);
        animator.setMoveDuration(10000);
        animator.setChangeDuration(10000);
        smallRecyclerView.setItemAnimator(animator);
        mediumRecyclerView.setItemAnimator(animator);*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //메뉴 inflater 가져옴. xml로 메뉴를 정의하기위해
        MenuInflater inflater = getMenuInflater();

        //overlayMenuVisible 이 true 일때 오버레이일때의 메뉴를 보여준다
        //아닐시에는 기본 메뉴를 보여준다
        if (overlayMenuVisible) {

            //xml 로 메뉴를 만드는 부분
            inflater.inflate(R.menu.toolbar_story_overlay, menu);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //뒤로가는 홈키 안보이게 설정
            //각 메뉴때마다 동작을 정의하기 어려워서 아예 안 보이게 설정
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {

            //xml 로 메뉴를 만드는 부분
            inflater.inflate(R.menu.toolbar_story_basic, menu);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home: { //toolbar 의 back 키 눌렀을 때 동작
                //MatchListActivity 로 이동
                Intent intent = new Intent(getApplicationContext(), MatchListActivity.class);
                //플래그로 저장 임시 구현
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                return true;
            }
            case R.id.actionEdit: //수정 눌렀을때 StoryEditActivity 로 이동
                //수정할 아이템 위치값도 같이 인텐트로 보내주기
                Intent intent = new Intent(this, StoryEditActivity.class);
                //플래그로 저장 임시 구현
                intent.putExtra("position", broadPosition);
                intent.putExtra("key", storyArrayList.get(broadPosition).getStoryKey());
                startActivity(intent);
                return true;

            case R.id.actionDel: //삭제 눌렀을때 해당 스토리 삭제하고
                //오버레이 취소하면서 그에 따른 효과적용해주기
                //오버레이때의 앱바로 바꾸기
                overlayMenuVisible = false;
                invalidateOptionsMenu();

                //스토리 삭제및 어댑터로 데이터 갱신 갱신하기
                prefGalleryEditor.remove(storyArrayList.get(broadPosition).getStoryKey());
                //쉐어드에 있는 데이터로 갱신이 바로 안되었기 때문에(resume 에서 쉐어드에 있는 값으로 갱신한다)
                //갤러리에서 보이는 이미지들을 갱신해서 보여주기 위해
                //바로 storyArrayList 에서 지워준다
                storyArrayList.remove(broadPosition);

                prefGalleryEditor.commit();

                smallRecyclerView.getAdapter().notifyItemRemoved(broadPosition);
                mediumRecyclerView.getAdapter().notifyItemRemoved(broadPosition);
                mediumRecyclerView.getAdapter().notifyItemRangeChanged(broadPosition, storyArrayList.size());
                mediumRecyclerView.getAdapter().notifyItemRangeChanged(broadPosition, storyArrayList.size());


                //오버레이 정리하기
                overlay.clear();
                //오버레이 담는 프레임 레이아웃 안 보이게 설정
                fullScreenContainer.setVisibility(View.GONE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //중간 사이즈 이미지 리사이클러뷰 어댑터 설정
    private void setMediumAdapter() {
        mediumAdapter = new RecyclerView.Adapter<MediumRecyclerViewHolder>() {

            //중간 사이즈 이미지 아이템으로 뷰홀더 만들기
            @Override
            public MediumRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_medium, parent, false);
                return new MediumRecyclerViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(MediumRecyclerViewHolder holder, int position) {

                //뷰는 해당 순서에 맞는 스토리의 이미지를 가져와서 그린다 (스토리 객체 리스트에서)
                holder.setImageResource(StringToBitMap(storyArrayList.get(position).getStoryValue()));



                //아이템을 누르면 해당 아이템의 이미지가 오버레이로 커지면서 크게 볼 수 있게 한다
                //그리고 그렇게 커진 오버레이를 누르면 다시 작아지면서 원래 아이템 위치로 이동하는
                //애니메이션이 작동한다.
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //다른 액비티에서도 오버레이 이미지를 편집하기 위해서
                        //static 으로 설정한 realView 를 이용해서 구현
                        realView = view;

                        //오버레이 메뉴에서 아이템을 삭제하기 위해서
                        //해당 아이템의 위치를 broadPosition 변수에 담는다
                        //오버레이 툴바에서 (position)을 받아올 수 없어서 이렇게 전달
                        broadPosition = position;

                        //textOverlay(오버레이 되었을 때 해당 사진에 대한 글)
                        //에 해당 스토리 아이템의 글 설정해기
                        textOverlay.setText(storyArrayList.get(position).getStoryContent());
                        //해당 스토리에 맞는 작성자 프로필 넣어주기
                        setImage(storyArrayList.get(position).getStoryMakerProfileImage());
                        Log.w(here, "onClick: setImage"+ storyArrayList.get(position).getStoryMakerProfileImage() );


                        makerNameNAge.setText(storyArrayList.get(position).getStoryMakerNickname());
                        makerNameNAge.setTextSize(30);
                        Log.w(here, "onClick: makerNameNAge"+makerNameNAge.getText() );
                        //오버레이 커지는 애니메이션을 넣기 위한 값들 계산
                        final float originX = realView.getX();
                        final float originY = realView.getY();
                        float parentCenterX = (containerView.getWidth() + containerView.getX()) / 2;
                        float parentCenterY = (containerView.getHeight() + containerView.getY()) / 2;
                        final int deltaScale = containerView.getMeasuredWidth() / realView.getMeasuredWidth();


                        //fullScreenContainer에서 오버레이를 받습니다.
                        //다른 아이템이 이전에 확대됐는지 알 수 없으니 일단 오버레이의 상태를 지웁니다.
                        overlay.clear();

                        //이제 새 아이템을 추가합니다. 여기서 itemView 매개 변수는 onClick 안에 있습니다.
                        overlay.add(realView);

                        // 화면을 덮는 fullScreenContainer 이 보이고 배경 투명으로 설정
                        // 그리고 이제 해당 itemView 를 움직여서 전체 화면으로 만듬
                        fullScreenContainer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        fullScreenContainer.setVisibility(View.VISIBLE);
                        view.animate().x(parentCenterX - realView.getWidth() / 2)
                                .y(parentCenterY - realView.getHeight() / 2)
                                .scaleX(deltaScale).scaleY(deltaScale).withEndAction(new Runnable() {
                            //withEndAction 에는 클릭 리스너를 설정해서
                            //mediumRecyclerView 인 갤러리로 돌아갈 수 있게 함
                            @Override
                            public void run() {
                                //오버레이때 앱바 바꾸기--------------------------------------------
                                overlayMenuVisible = true;  //오버레이 앱바 보이는 상태로 설정
                                invalidateOptionsMenu();    //앱바 갱신하기

                                //------------------------------------------------------------------
                                //오버레이 아이템에 대한 글을 보여주는 레이아웃(overlayBarLayout)을
                                //보이게 설정
                                overlayBarLayout.setVisibility(View.VISIBLE);
                                profileOverLayout.setVisibility(View.VISIBLE);
                                //화면을 덮는 fullScreenContainer 의 배경 흰색으로 설정
                                fullScreenContainer.setBackgroundColor(
                                        getResources().getColor(android.R.color.white));

                                fullScreenContainer.setOnClickListener(new View.OnClickListener() {
                                    //클릭하면 작아지면서 원래 위치로 돌아가게 설정
                                    //이때 fullScreenContainer 배경화면 투명
                                    //overlayBarLayout 안보이게 설정
                                    @Override
                                    public void onClick(View v) {
                                        overlay.add(realView);
                                        fullScreenContainer.setBackgroundColor(
                                                getResources().getColor(android.R.color.transparent));
                                        overlayBarLayout.setVisibility(View.INVISIBLE);
                                        profileOverLayout.setVisibility(View.INVISIBLE);

                                        realView.animate().x(originX).y(originY).scaleY(1).scaleX(1).withEndAction(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //아이템뷰가 제자리로 돌아간 후에
                                                        //오버레이때 앱바 바꾸기--------------------
                                                        overlayMenuVisible = false;  //오버레이 앱바 안 보이는 상태로 설정
                                                        invalidateOptionsMenu();    //앱바 갱신하기

                                                        //------------------------------------------
                                                        //오버레이 삭제
                                                        overlay.remove(realView);
                                                        //fullScreenContainer 안 보이게 설정
                                                        fullScreenContainer.setVisibility(View.GONE);
                                                    }
                                                }).start();
                                    }
                                });
                            }
                        });

                    }
                });
            }

            @Override
            public int getItemCount() {
                return storyArrayList.size();
            }
        };

        //mediumRecyclerView 어댑터 설정 및 투명하게 설정
        // 초기에 mediumRecyclerView 가 투명하고 smallRecyclerView 이 보이게 설정
        mediumRecyclerView.setAdapter(mediumAdapter);
        mediumRecyclerView.setAlpha(0);
    }


    //작은 사이즈 이미지 리사이클러뷰 어댑터 설정
    private void setSmallAdapter() {
        smallRecyclerView.setAdapter(new RecyclerView.Adapter<SmallRecyclerViewHolder>() {

            //작은 사이즈 이미지 아이템으로 뷰홀더 만들기
            @Override
            public SmallRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_small, parent, false);
                return new SmallRecyclerViewHolder(imageView);
            }


            @Override
            public void onBindViewHolder(SmallRecyclerViewHolder holder, int position) {
                //뷰는 해당 순서에 맞는 스토리의 이미지를 가져와서 그린다 (스토리 객체 리스트에서)
                holder.setImageResource(StringToBitMap(storyArrayList.get(position).getStoryValue()));

                //아이템을 누르면 해당 아이템의 이미지가 오버레이로 커지면서 크게 볼 수 있게 한다
                //그리고 그렇게 커진 오버레이를 누르면 다시 작아지면서 원래 아이템 위치로 이동하는
                //애니메이션이 작동한다
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //다른 액비티에서도 오버레이 이미지를 편집하기 위해서
                        //static 으로 설정한 realView 를 이용해서 구현
                        realView = view;

                        //오버레이 메뉴에서 아이템을 삭제하기 위해서
                        //해당 아이템의 위치를 broadPosition 변수에 담는다
                        //오버레이 툴바에서 (position)을 받아올 수 없어서 이렇게 전달
                        broadPosition = position;

                        //textOverlay(오버레이 되었을 때 해당 사진에 대한 글)
                        //에 해당 스토리 아이템의 글 설정해기
                        textOverlay.setText(storyArrayList.get(position).getStoryContent());

                        //해당 스토리에 맞는 작성자 프로필 넣어주기
                        setImage(storyArrayList.get(position).getStoryMakerProfileImage());


                        Log.w(here, "onClick: setImage"+ storyArrayList.get(position).getStoryMakerProfileImage() );


                        makerNameNAge.setText(storyArrayList.get(position).getStoryMakerNickname());
                        makerNameNAge.setTextSize(30);
                        Log.w(here, "onClick: makerNameNAge"+makerNameNAge.getText() );
                        //오버레이 커지는 애니메이션을 넣기 위한 값들 계산
                        final float originX = view.getX();
                        final float originY = view.getY();
                        float parentCenterX = (containerView.getWidth() + containerView.getX()) / 2;
                        float parentCenterY = (containerView.getHeight() + containerView.getY()) / 2;
                        final int deltaScale = containerView.getMeasuredWidth() / view.getMeasuredWidth();

                        //fullScreenContainer에서 오버레이를 받습니다.
                        //다른 아이템이 이전에 확대됐는지 알 수 없으니 일단 오버레이의 상태를 지웁니다.
                        overlay.clear();

                        //이제 새 아이템을 추가합니다. 여기서 itemView 매개 변수는 onClick 안에 있음
                        overlay.add(realView);

                        // 화면을 덮는 fullScreenContainer 이 보이고 배경 투명으로 설정
                        // 그리고 이제 해당 itemView 를 움직여서 전체 화면으로 만듬
                        fullScreenContainer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        fullScreenContainer.setVisibility(View.VISIBLE);
                        realView.animate().x(parentCenterX - realView.getWidth() / 2)
                                .y(parentCenterY - realView.getHeight() / 2)
                                .scaleX(deltaScale).scaleY(deltaScale).withEndAction(new Runnable() {
                            //withEndAction 에는 클릭 리스너를 설정해서
                            //mediumRecyclerView 인 갤러리로 돌아갈 수 있게 함
                            @Override
                            public void run() {
                                //오버레이때 앱바 바꾸기--------------------------------------------
                                overlayMenuVisible = true;  //오버레이 앱바 보이는 상태로 설정
                                invalidateOptionsMenu();    //앱바 갱신하기
                                //------------------------------------------------------------------

                                //오버레이 아이템에 대한 글을 보여주는 레이아웃(overlayBarLayout)을
                                //보이게 설정
                                overlayBarLayout.setVisibility(View.VISIBLE);
                                profileOverLayout.setVisibility(View.VISIBLE);

                                //화면을 덮는 fullScreenContainer 의 배경 흰색으로 설정
                                fullScreenContainer.setBackgroundColor(
                                        getResources().getColor(android.R.color.white));
                                fullScreenContainer.setOnClickListener(new View.OnClickListener() {
                                    //클릭하면 작아지면서 원래 위치로 돌아가게 설정
                                    //이때 fullScreenContainer 배경화면 투명
                                    //overlayBarLayout 안보이게 설정
                                    @Override
                                    public void onClick(View v) {
                                        overlay.add(view);
                                        fullScreenContainer.setBackgroundColor(
                                                getResources().getColor(android.R.color.transparent));
                                        overlayBarLayout.setVisibility(View.INVISIBLE);
                                        profileOverLayout.setVisibility(View.INVISIBLE);

                                        view.animate().x(originX).y(originY).scaleY(1).scaleX(1).withEndAction(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //아이템뷰가 제자리로 돌아간 후에
                                                        //오버레이때 앱바 바꾸기--------------------
                                                        overlayMenuVisible = false;  //오버레이 앱바 안 보이는 상태로 설정
                                                        invalidateOptionsMenu();    //앱바 갱신하기

                                                        //------------------------------------------
                                                        //오버레이 삭제
                                                        overlay.remove(realView);
                                                        //fullScreenContainer 안 보이게 설정
                                                        fullScreenContainer.setVisibility(View.GONE);

                                                    }
                                                }).start();
                                    }
                                });
                            }
                        });
                    }
                });


            }

            @Override
            public int getItemCount() {
                return storyArrayList.size();
            }
        });


    }


    //스트링을 비트맵으로 바꾸어주는 메소드
    public Bitmap StringToBitMap(String encodedString) {

        try {

            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return bitmap;

        } catch (Exception e) {

            e.getMessage();

            return null;

        }

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


        //shared데이터 저장
        storyArrayList.clear();
        Map<String, ?> allEntries = prefGallery.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            try {
                if(entry.getKey().contains("lastIndex") ){
                    Log.w(here, " lastposition  ");

                }
                else {

                    story = new Story(entry.getKey(), entry.getValue().toString().split("\\|", 4)[0], entry.getValue().toString().split("\\|", 4)[1],
                            entry.getValue().toString().split("\\|", 4)[2], entry.getValue().toString().split("\\|", 4)[3]);
                    Log.w(here, "story.getKey()  :  " + entry.getKey());
                    Log.w(here, "story storyMakerProfileImage :  " + entry.getValue().toString().split("\\|", 4)[0]);
                    Log.w(here, "story storyMakerNickname :  " + entry.getValue().toString().split("\\|", 4)[1]);
                    Log.w(here, "story imageToString :  " + entry.getValue().toString().split("\\|", 4)[2]);
                    Log.w(here, "story Content  :  " + entry.getValue().toString().split("\\|", 4)[3]);
                    storyArrayList.add(story);
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                // 중복되지 않는 키값을 위해 마지막으로 저장한 키값에다 +1 해서 lastposition 이라는 키값에 저장해두는데
                // 위에서 스토리 객체를 만들때 모든 키 값을 불러와서 split 으로 나누기때문에 ArrayIndexOutOfBoundsException가 뜬다
                //lastposition 는 숫자로만 이루어져있어서
                //그래서 여기서 에러나는 것을 방지하기 위해서 try catch문을 사용했다.
                Log.w(here, "ArrayIndexOutOfBoundsException => lastposition  ");
                e.printStackTrace();
            }


        }

        //storyArrayList 정렬
        Comparator<Story> sortStory = new Comparator<Story>() {
            @Override
            public int compare(Story story1, Story story2) {
                int ret = 0;

                if (Integer.parseInt(story1.getStoryKey()) < Integer.parseInt(story2.getStoryKey()))
                    ret = 1;
                else if (Integer.parseInt(story1.getStoryKey()) < Integer.parseInt(story2.getStoryKey()))
                    ret = 0;
                else
                    ret = -1;

                return ret;

                // 위의 코드를 간단히 만드는 방법.
                // return (item2.getNo() - item1.getNo()) ;
            }
        };


        Collections.sort(storyArrayList, sortStory);


        //액티비티가 새로 사용자에게 보여지기전에
        //smallRecyclerView 와 mediumRecyclerView 데이터 바뀐거 갱신해주기
        smallRecyclerView.getAdapter().notifyDataSetChanged();
        mediumRecyclerView.getAdapter().notifyDataSetChanged();
//                SharedPreferences pref1 = getSharedPreferences("image", MODE_PRIVATE);
//
//                String image =  pref1.getString("imagestrings", "");
//
//                bitmapSave = StringToBitMap(image);
        Log.w(here, here + "  onResume");
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


    private void setImage(String profilePath) {

        //이미지 크기 조절


        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(profilePath, options);
        Log.w(here, "setImage : userProfilePath " + profilePath);
        makerProfile.setImageBitmap(originalBm);



/*        makerProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            makerProfile.setClipToOutline(true);
        }*/


    }


}




