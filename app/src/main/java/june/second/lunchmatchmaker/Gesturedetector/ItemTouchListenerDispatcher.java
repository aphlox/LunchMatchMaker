package june.second.lunchmatchmaker.Gesturedetector;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import june.second.lunchmatchmaker.R;
/*
모든 터치 이벤트를 받게 하기 위해 이 클래스를 만들었음.
뷰의 상태에 따라 하나 혹은 여러 개의 클래스에 해당 터치를 위임합니다


*/
public class ItemTouchListenerDispatcher implements RecyclerView.OnItemTouchListener {
    @NonNull
    private final ScaleGestureDetector galleryGestureDetector;
    @NonNull
    private final ScaleGestureDetector fullScreenGestureDetector;
    private float currentSpan;

    // 드래그 모드인지 핀치줌 모드인지 구분
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // 드래그시 좌표 저장
    int posX1=0, posX2=0, posY1=0, posY2=0;

    // 핀치시 두좌표간의 거리 저장
    float oldDist = 1f;
    float newDist = 1f;


    float oldSpan = 1f;
    float newSpan = 1f;

    public ItemTouchListenerDispatcher(@NonNull Context context,
            @NonNull final ScaleGestureDetector fullScreenGestureDetector,
            @NonNull ScaleGestureDetector galleryGestureDetector) {
        this.galleryGestureDetector = galleryGestureDetector;
        this.fullScreenGestureDetector = fullScreenGestureDetector;
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.w("ItemTouchListenerDis","onInterceptTouchEvent");

        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.w("ItemTouchListenerDis","onTouchEvent");


//        int act = e.getAction();
//        String strMsg = "";
//        switch(act & MotionEvent.ACTION_MASK) {
//
//            case MotionEvent.ACTION_DOWN: //첫번째 손가락 터치(드래그 용도)
//                posX1 = (int) e.getX();
//                posY1 = (int) e.getY();
//
//                Log.w("zoom", "mode=DRAG" );
//                mode = DRAG;
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if(mode == DRAG) { // 드래그 중
//                    posX2 = (int) e.getX();
//                    posY2 = (int) e.getY();
//
//                    if(Math.abs(posX2-posX1)>20 || Math.abs(posY2-posY1)>20) {
//                        posX1 = posX2;
//                        posY1 = posY2;
//                        strMsg = "drag";
//                        Log.w("ItemTouchListenerDis", strMsg  );
//                    }
//
//                } else if (mode == ZOOM) { // 핀치 중
//                    newDist = spacing(e);
//                    Log.w("zoom", "newDist=" + newDist);
//                    Log.w("zoom", "oldDist=" + oldDist);
//                    if (newDist - oldDist > 20) { // zoom in
//                        oldDist = newDist;
//                        strMsg = "zoom in";
//                        Log.w("ItemTouchListenerDis", strMsg  );
//                    } else if(oldDist - newDist > 20) { // zoom out
//                        oldDist = newDist;
//                        strMsg = "zoom out";
//                        Log.w("ItemTouchListenerDis", strMsg  );
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP: // 첫번째 손가락을 떼었을 경우
//            case MotionEvent.ACTION_POINTER_UP: // 두번째 손가락을 떼었을 경우
//                mode = NONE;
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                //두번째 손가락 터치(손가락 2개를 인식하였기 때문에 핀치 줌으로 판별)
//                mode = ZOOM;
//                newDist = spacing(e);
//                oldDist = spacing(e);
//                Log.w("zoom", "newDist=" + newDist);
//                Log.w("zoom", "oldDist=" + oldDist);
//                Log.w("zoom", "mode=ZOOM");
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            default :
//                break;
//
//        }

        try {
            newSpan = spacing(e);

        } catch (IllegalArgumentException e1) {
            Log.w("ItemTouchListenerDis","IllegalArgumentException");
        }
        Log.w("currentSpan",""+currentSpan);

        currentSpan =  oldSpan - newSpan;
        oldSpan = newSpan;


//        currentSpan = getSpan(e);


        //리사이클러뷰의 아이디를 이용해서 어떤 리사이클러뷰가 터치 이벤트를 받을지 알 수 있게한다
        switch (rv.getId()) {
            case R.id.mediumRecyclerView: {
                //스팬 도 사용했습니다. 스팬은 마지막 스팬과 현재 스팬의 차이값을 뜻하는데, 손가락 간의 거리로 핀치와 확대를 할 수 있게 합니다.
                if (currentSpan < 0) {
                    Log.w("ItemTouchListenerDis","mediumRecyclerView currentSpan < 0)");

                    //galleryGestureDetector => 리사이클러뷰 크기 조절을 위해 사용하는 것
                    //중간 크기에서 축소돼서 작은 크기로 갑니다. 터치하면 전체 화면이 된다
                    galleryGestureDetector.onTouchEvent(e);
                } else if (currentSpan == 0) {
                    Log.w("ItemTouchListenerDis","mediumRecyclerView currentSpan == 0)");

                    final View childViewUnder = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childViewUnder != null) {
                        childViewUnder.performClick();
                    }
                }
                else{
                    Log.w("ItemTouchListenerDis","mediumRecyclerView currentSpan < 0)");

                }
                break;
            }
            case R.id.smallRecyclerView: {
                Log.w("ItemTouchListenerDis","smallRecyclerView");
                //smallRecyclerView가 터치를 받는다면 확대돼서 mediumRecyclerView로만 갈 수 있음

//                galleryGestureDetector.onTouchEvent(e);

                if (currentSpan < 0) { //줌 인
                    Log.w("ItemTouchListenerDis","smallRecyclerView currentSpan < 0");
                    galleryGestureDetector.onTouchEvent(e);

                } else if (currentSpan == 0) {
                    Log.w("ItemTouchListenerDis","smallRecyclerView currentSpan == 0)");

                    final View childViewUnder = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childViewUnder != null) {
                        childViewUnder.performClick();
                    }
                }
                else{
                    Log.w("ItemTouchListenerDis","smallRecyclerView currentSpan > 0)");
                    galleryGestureDetector.onTouchEvent(e);

                }
                break;


            }
            case R.id.fullScreenImageContainer: {
                Log.w("ItemTouchListenerDis","fullScreenImageContainer currentSpan");

            }
            case R.id.story_layout : {
                Log.w("ItemTouchListenerDis","story_layout currentSpan");

            }
            case R.id.toolbar_story : {
                Log.w("ItemTouchListenerDis","toolbar_story currentSpan");

            }
            case R.id.fabWriteStory: {
                Log.w("ItemTouchListenerDis","fabStory currentSpan");

            }

            default: {
                Log.w("default","default currentSpan");

                break;
            }

        }
    }
/*삭제 예정
    private float getSpan(@NonNull MotionEvent e) {
        Log.w("ItemTouchListenerDis","getSpan");


        float x = e.getX(0) - e.getX(1);

        float y = e.getY(0) - e.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }*/


    private float spacing(MotionEvent event) {


        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
