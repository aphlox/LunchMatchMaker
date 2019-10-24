package june.second.lunchmatchmaker.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MatchInformationFragment extends ListFragment {
    //디버깅을 위한 string 값
    String here = "MatchInformationFragment";

    //다른 액티비티와 프래그먼트와 통신하기 위한 인터페이스 ----------------------------------------
    //액티비티와 통신하기 위한 인터페이스 구현
    interface markerSelectedListener {
        void markerSelected(int position);
    }
    //----------------------------------------------------------------------------------------------
    //객체 선언 -> 현재 MainMapActivity 와 연동
    private markerSelectedListener mListener;
    //----------------------------------------------------------------------------------------------


    //어댑터 및 들어갈 데이터(arrayListTag)의 실시간 갱신을 위해 static 으로 선언-------------------
    //(다른 액티비티에서도 어댑터와 데이터의 갱신을 요청할 수 있게 하려고)
    public static ArrayAdapter<String> adapter;
    public static ArrayList<String> arrayListTag = new ArrayList<>();
    //----------------------------------------------------------------------------------------------




    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.w(here+"  onAttach", here+"onAttach");

        //액티비티와 통신하기 위한 인터페이스를 사용하기 위해---------------------------------------
        //해당 액티비티에 인터페이스가 구현되어있는지 체크하고 안 되어 있으면
        //구현하라고 메세지 띄어주기
        try {
            mListener = (markerSelectedListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(((Activity) context).getLocalClassName() + "는 OnColorSelectedListener를 구현해야합니다");
        }
        //------------------------------------------------------------------------------------------

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.w(here+"  onViewCreated", here+"onViewCreated");


        //마커 객체 리스트의 마커들이 가지고 있는 태그들을 순서대로 arrayListTag에 넣어준다.
        for (int i = 0; i <MatchListActivity.realMatchArrayList.size(); i++) {
            arrayListTag.add(MatchListActivity.realMatchArrayList.get(i).getMatchTitle());
        }


        //프래그먼트 안의 리스트 디자인-------------------------------------------------------------
        ListView listView = getListView();
        listView.setBackgroundColor(Color.parseColor("#FFFDE7"));   //배경설정
        listView.setDivider(new ColorDrawable(Color.WHITE));            //구분선 색상 흰색으로 설정
        listView.setDividerHeight(3); // 3 pixels height                //구분석 굵기 설정
        //------------------------------------------------------------------------------------------


        //어댑터 선언 및 설정-----------------------------------------------------------------------
        //arrayListTag(마커들의 태그들)리스트를 보여주게 구현됨
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayListTag);
        setListAdapter(adapter);
        //------------------------------------------------------------------------------------------
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.w(here+"  onListItemClick", here+"onListItemClick");

        //리스너 존재할시---------------------------------------------------------------------------
        //사용자가 프래그먼트에서 선택한 position 값을 markerSelected 메소드가 구현된 액티비티로 반환
        if(mListener != null){
            mListener.markerSelected(position);
        }
        //------------------------------------------------------------------------------------------

        /*
        //나중에 구현할때 활용 참고
        //해당 리스트 포지션으로 아이템 받아오는 것
        ArrayAdapter<String> adapter =  (ArrayAdapter<String>) l.getAdapter();
        String colorString = adapter.getItem(position);
        int color = Color.RED;
        switch (colorString){
            case "Red":
                color = Color.RED;
                break;
            case "Green":
                color = Color.GREEN;
                break;
            case "Blue":
                color = Color.BLUE;
                break;
        }*/
    }
}
