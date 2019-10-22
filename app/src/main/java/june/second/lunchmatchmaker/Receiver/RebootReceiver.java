package june.second.lunchmatchmaker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import june.second.lunchmatchmaker.Service.RealService;
import june.second.lunchmatchmaker.Service.RestartService;

public class RebootReceiver extends BroadcastReceiver {
    public static final String JOIN_APPROVAL = "june.second.lunchmatchmaker.action.ACTION_JOIN_APPROVAL";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, RealService.class);
            context.startService(in);
        }


/*        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Toast.makeText(context, "BOOT_COMPLETED", Toast.LENGTH_SHORT).show();
            Intent startServiceIntent = new Intent(context, LunchMatchService.class);
            context.startService(startServiceIntent);
        }*/
    }
}
