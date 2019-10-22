package june.second.lunchmatchmaker.Activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import june.second.lunchmatchmaker.R;
import june.second.lunchmatchmaker.Receiver.JoinApprovalReceiver;

public class SettingActivity extends AppCompatActivity {

    private ImageView imageBack;
    private BroadcastReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        //뒤로 가는 버튼 누르면 MatchListActivity 로 이동
        imageBack = findViewById(R.id.settingBackButton);
        imageBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileNTimelineActivity.class);
            startActivity(intent);
        });


        mReceiver = new JoinApprovalReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(JoinApprovalReceiver.JOIN_APPROVAL);
        filter.setPriority(100);
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
    }




}
