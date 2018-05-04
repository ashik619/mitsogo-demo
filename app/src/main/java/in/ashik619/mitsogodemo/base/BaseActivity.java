package in.ashik619.mitsogodemo.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dilip on 29/12/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
        ));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(connectivityReceiver);
        super.onPause();
    }

    protected abstract void onNetworkConnected();
    protected abstract void onNoNetworkConnected();

    public class ConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isOnline(context)){
                onNetworkConnected();
            }else onNoNetworkConnected();

        }
        private boolean isOnline(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        }
    }

}
