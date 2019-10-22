package june.second.lunchmatchmaker.Gesturedetector;

import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
리사이클러뷰의 크기 조절 위해 사용되는 클래스



*/

public class SmallMediumGestureDetector implements ScaleGestureDetector.OnScaleGestureListener {
    private static final float TRANSITION_BOUNDARY = 1.09f;
    private static final float SMALL_MAX_SCALE_FACTOR = 1.25f;
    private static final int SPAN_SLOP = 7;

    @NonNull
    private final RecyclerView smallRecyclerView;
    @NonNull private final RecyclerView mediumRecyclerView;

    private float scaleFactor;
    private float scaleFactorMedium;
    public boolean isInProgress;

    private float oldScaleEnd;
    private float newScaleEnd;


    public SmallMediumGestureDetector(@NonNull RecyclerView smallRecyclerView, @NonNull RecyclerView mediumRecyclerView) {
        this.smallRecyclerView = smallRecyclerView;
        this.mediumRecyclerView = mediumRecyclerView;
    }

    @Override
    /*scaleFactor는 ScaleGestureDetector에서 얻습니다.
    이를 클램프 함수에 사용해서 양 RecyclerView의 크기를 조절할 수 있는 최솟값과 최댓값으로 지정했습니다.
     물론 각각은 상대편의 역수이고, 서로 쌍이 맞아야겠죠? 그렇지 않으면 전환 중에 같은 크기가 아니라서
      페이드인/아웃 효과가 지저분해질 겁니다. 이 조건만 지킨다면 숫자나 함수는 원하는 대로 조정해도 좋습니다.
    */
    public boolean onScale(@NonNull ScaleGestureDetector detector) {
        Log.w("SmallMediumGestureDet","onScale : " +scaleFactor);

        // gestureTolerance - 인체에서 자연스럽게 일어날 수 있는 떨림을 방지하기 위해
        if (gestureTolerance(detector)) {
            //small
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1f, Math.min(scaleFactor, SMALL_MAX_SCALE_FACTOR));
            isInProgress = scaleFactor > 1;
            smallRecyclerView.setScaleX(scaleFactor);
            smallRecyclerView.setScaleY(scaleFactor);

            //medium
            scaleFactorMedium *= detector.getScaleFactor();
            scaleFactorMedium = Math.max(0.8f, Math.min(scaleFactorMedium, 1f));
            mediumRecyclerView.setScaleX(scaleFactorMedium);
            mediumRecyclerView.setScaleY(scaleFactorMedium);

            //alpha
            mediumRecyclerView.setAlpha((scaleFactor - 1) / (0.25f));
            smallRecyclerView.setAlpha(1 - (scaleFactor - 1) / (0.25f));

        }
        return true;
    }

    private boolean gestureTolerance(@NonNull ScaleGestureDetector detector) {
        Log.w("SmallMediumGestureDet","gestureTolerance");

        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        Log.w("SmallMediumGestureDet","onScaleBegin : " +scaleFactor);

        mediumRecyclerView.setVisibility(View.VISIBLE);
        smallRecyclerView.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    /*만약 사용자가 전환 중에 터치를 중단하면 어떻게 될까요?
     따로 구현하지 않으면 전환 중 상태 그대로 멈출 테고 아마 양 RecyclerView가 모두 보일 겁니다.
      아마 아주 이상하게 보이겠죠?
      어느 한 상태로 자동으로 넘어가도록 전환을 종료하는 겁니다.
    */
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        Log.w("SmallMediumGestureDet","onScaleEnd :" +scaleFactor);


        newScaleEnd = scaleFactor;
        if (IsScaleInProgress()) {
            if (scaleFactor < TRANSITION_BOUNDARY) {
                transitionFromMediumToSmall();
                scaleFactor = 0;
                scaleFactorMedium = 0;
                newScaleEnd = 1.0f;
            } else {
                transitionFromSmallToMedium();
                scaleFactor = SMALL_MAX_SCALE_FACTOR;
                scaleFactorMedium = 1f;
                newScaleEnd = 1.25f;
            }
        }

        if(newScaleEnd != oldScaleEnd && newScaleEnd == 1.25f){
            transitionFromSmallToMedium();
            oldScaleEnd = newScaleEnd;
        }
        else if(newScaleEnd != oldScaleEnd && newScaleEnd == 1.0f){
            transitionFromMediumToSmall();
            oldScaleEnd = newScaleEnd;
        }


    }

    private boolean IsScaleInProgress() {
        Log.w("IsScaleInProgress","IsScaleInProgress");

        return scaleFactor < SMALL_MAX_SCALE_FACTOR && scaleFactor > 1f;
    }

    private void transitionFromSmallToMedium() {
        Log.w("SmallMediumGestureDet","transitionFromSmallToMedium");

        Log.w("Scale", "transitionFromSmallToMedium: ");
        mediumRecyclerView.animate().scaleX(1f).scaleY(1f).alpha(1f).withStartAction(() -> smallRecyclerView.animate().scaleY(SMALL_MAX_SCALE_FACTOR).scaleX(SMALL_MAX_SCALE_FACTOR).alpha(0f)
                .start()).withEndAction(() -> smallRecyclerView.setVisibility(View.INVISIBLE)).start();
    }

    private void transitionFromMediumToSmall() {
        Log.w("SmallMediumGestureDet","transitionFromMediumToSmall");

        Log.w("Scale", "transitionFromMediumToSmall: ");
        smallRecyclerView.animate().scaleX(1f).scaleY(1f).alpha(1f).withStartAction(() -> mediumRecyclerView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0).start()).withEndAction(() -> mediumRecyclerView.setVisibility(View.INVISIBLE)).start();
    }

}
