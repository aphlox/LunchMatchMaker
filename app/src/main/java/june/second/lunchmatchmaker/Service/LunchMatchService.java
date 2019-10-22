package june.second.lunchmatchmaker.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import june.second.lunchmatchmaker.Receiver.JoinApprovalReceiver;

public class LunchMatchService extends Service {

    private BroadcastReceiver joinApprovalReceiver;
    public static final String JOIN_APPROVAL = "june.second.lunchmatchmaker.action.ACTION_JOIN_APPROVAL";
    public static final String JOIN_RESULT = "june.second.lunchmatchmaker.action.ACTION_JOIN_RESULT";


    public LunchMatchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //서비스안에 회원가입 승인 요청에 대한 결과를 담은 신호인
        //JOIN_RESULT 를 받는 인텐트 필터와 리시버를 구현한다.
        IntentFilter filter = new IntentFilter();
        filter.addAction(JOIN_RESULT);
        joinApprovalReceiver = new JoinApprovalReceiver();
        registerReceiver(joinApprovalReceiver, filter);
        Toast.makeText(getApplicationContext(), "LunchMatchService : onCreate", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent == null)
        {
            //서비스안에 회원가입 승인 요청에 대한 결과를 담은 신호인
            //JOIN_RESULT 를 받는 인텐트 필터와 리시버를 구현한다.
            IntentFilter filter = new IntentFilter();
            filter.addAction(JOIN_RESULT);
            joinApprovalReceiver = new JoinApprovalReceiver();
            registerReceiver(joinApprovalReceiver, filter);
            Toast.makeText(getApplicationContext(), "LunchMatchService : onStartCommand - intent null", Toast.LENGTH_SHORT).show();

        }
//        Toast.makeText(getApplicationContext(), "LunchMatchService : onStartCommand", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(joinApprovalReceiver);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
}
