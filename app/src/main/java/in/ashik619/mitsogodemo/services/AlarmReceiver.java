package in.ashik619.mitsogodemo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dilip on 3/5/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e("ALRM","alrm");
        Intent intent1 = new Intent(context,UpdateService.class);
        intent1.putExtra("isWeather",intent.getBooleanExtra("isWeather",false));
        intent1.putExtra("isStorage",intent.getBooleanExtra("isStorage",false));
        intent1.putExtra("isNetworkType",intent.getBooleanExtra("isNetworkType",false));
        intent1.putExtra("isDevice",intent.getBooleanExtra("isDevice",false));
        intent1.putExtra("isBattery",intent.getBooleanExtra("isBattery",false));
        context.startService(intent1);
    }
}
