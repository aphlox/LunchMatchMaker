package june.second.lunchmatchmaker.Activity;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;

import june.second.lunchmatchmaker.Item.Product;
import june.second.lunchmatchmaker.R;

/**
 * Created by kevin on 2017. 10. 24..
 */

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ViewHolder> {


    //객체 선언-------------------------------------------------------------------------------------
    private ArrayList<Product> items;
    private FragmentManager fragmentManager;

    //프래그먼트, 프랙그먼트 리스트에 저장
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    //뷰페이저 리사이클러뷰에서 스크롤하면
    //리사이클러뷰 아이템이 재활용되어 나중에 다시 볼때
    //뷰페이저 넘긴 상태가 초기화된다.
    //뷰페이저 넘긴 상태를 기억하기 위해서 해쉬맵으로 저장해놓기
    HashMap<Integer, Integer> mViewPagerState = new HashMap<>();


    //리사이클러뷰에서의 아이템 포지션값
    static int verticalPosition;
    //----------------------------------------------------------------------------------------------


    public ProductCardAdapter(FragmentManager fragmentManager, ArrayList<Product> items) {
        this.items = items;
        this.fragmentManager = fragmentManager;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //레이아웃 row 로 리사이클러뷰 구성하기
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {


        //프래그먼트 어댑터 설정
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(fragmentManager);
        viewHolder.vp.setAdapter(bannerPagerAdapter);


/*
        // 구현 로직 보류
        // 다른 방식으로 일단 구현돼서

        //뷰페이저안에 넣을 프래그먼트들
        MatchContentFragment blankFragment = MatchContentFragment.newInstance(1);
        bannerPagerAdapter.addItem(blankFragment);
        bannerPagerAdapter.addItem(blankFragment);

*/


        verticalPosition = viewHolder.getAdapterPosition();

        //viewpager는 내부적으로 id를 할당하는 것이 없어서
        //set id를 통해서 임의의 유니크한 값들을 저마다 설정해주어야 됨
        //안 그러면 맨 첫번째의 viewpager만 동작하고 나머지 것들은 동작을 하지 않는다(내부적으로 id를 할당하는 것이 없어서
        viewHolder.vp.setId(i+1);
        if (mViewPagerState.containsKey(i)) {
            viewHolder.vp.setCurrentItem(mViewPagerState.get(i));
        }
        Log.w("ProductCardAdapter", "onBindViewHolder- mViewPagerState.containsKey(i) : "+mViewPagerState.containsKey(i));
        Log.w("ProductCardAdapter", "onBindViewHolder- mViewPagerState.get(i) : "+mViewPagerState.get(i));


    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    //리사이클러뷰 재활용되기 때문에 스크롤해서 뷰 재활용되는 경우
    //넘겼던 뷰페이저의 위치가 리셋 된다 그래서
    //해당 viewholder가 가지고 있는 viewpager의 현재 위치를 기억해 둔다음에,
    //다시 해당 row가 불러와질때 저장되어있던 위치로 viewpager를 다시 설정
    @Override
    public void onViewRecycled(ViewHolder holder) {
        mViewPagerState.put(holder.getAdapterPosition(), holder.vp.getCurrentItem());
        super.onViewRecycled(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewPager vp;

        public ViewHolder(View itemView) {
            super(itemView);
            vp = itemView.findViewById(R.id.vp);
        }
    }

    public class BannerPagerAdapter extends FragmentPagerAdapter {
        public BannerPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        //뷰페이저에 들어갈 프래그먼트 가져오는 메소드
        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return MatchTitleFragment.newInstance(position); //첫번째 - 매치 제목 프래그먼트

            } else if (position == 1) {
                return MatchMemberFragment.newInstance(position); //세번째 - 매치 멤버/수락 프래그먼트
            }

            return MatchMemberFragment.newInstance(position);


        }

        public void addItem(Fragment fragment) {
            fragments.add(fragment);
        }


        //리사이클러뷰 내의 뷰페이저 개수 설정
        @Override
        public int getCount() {
            return 2;
        }
    }


}
