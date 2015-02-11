package com.example.antonio.provaanagrafe;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Antonio on 07/02/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    MainActivity instance = MainActivity.getInstance();

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MainActivity.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NetworkChangeReceiver", "Connettivity Changed");
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo NetInfo = connMgr
                .getActiveNetworkInfo();
        if (NetInfo != null)
            Log.d("NetworkChangeReceiver", "connected=" + NetInfo.isConnected());
        if (MainActivity.getInstance() != null) {
            if (NetInfo != null && NetInfo.isConnected()) {
                if (!isMyServiceRunning(SyncService.class)) {
                    context.startService(new Intent(context, SyncService.class));
                    instance.StopLoadingDialog();
                    Log.d("NetworkChangeReceiver", "Service Started");
                }
            } else {
                if (isMyServiceRunning(SyncService.class)) {
                    context.stopService(new Intent(context, SyncService.class));
                    instance.StartLoadingDialog();
                    Log.d("NetworkChangeReceiver", "Service Stopped");
                }
            }
        }
    }
}
