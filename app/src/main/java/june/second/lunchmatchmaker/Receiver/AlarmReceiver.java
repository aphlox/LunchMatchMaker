package june.second.lunchmatchmaker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import june.second.lunchmatchmaker.Service.RealService;
import june.second.lunchmatchmaker.Service.RestartService;

public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, RealService.class);
            context.startService(in);
        }
    }

}
