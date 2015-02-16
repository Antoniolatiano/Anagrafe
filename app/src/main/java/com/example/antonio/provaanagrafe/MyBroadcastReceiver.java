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
public class MyBroadcastReceiver extends BroadcastReceiver {

    Assets assets = Assets.getInstance();

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
        Log.d(MyBroadcastReceiver.class.getSimpleName(), "action: " + intent.getAction());
        switch (intent.getAction()) {
            case Assets.bootCompleted:
            case Assets.mainActivityStarted:
                assets.activityRunning = true;
                //inizializzare tutto, (se non già fatto)
            case Assets.connectivityChanged:
                assets.connessioneAttiva = controllaConnessione(context);
                Log.d("NetworkChangeReceiver", "Connettivity Changed");
                if (assets.connessioneAttiva) {
                    if (!isMyServiceRunning(SyncService.class)) {
                        context.startService(new Intent(context, SyncService.class));
                        assets.mostraDialogConnessione(false);
                        Log.d("NetworkChangeReceiver", "Service Started");
                    }
                } else {
                    if (isMyServiceRunning(SyncService.class)) {
                        context.stopService(new Intent(context, SyncService.class));
                        assets.mostraDialogConnessione(true);
                        Log.d("NetworkChangeReceiver", "Service Stopped");
                    }
                }
                break;
            case Assets.mainActivityStopped:
                assets.activityRunning = false;
            default:
                break;
        }

    }

    private boolean controllaConnessione(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo NetInfo = connMgr
                .getActiveNetworkInfo();
        if (NetInfo != null)
            Log.d("NetworkChangeReceiver", "connected=" + NetInfo.isConnected());
        if (NetInfo != null && NetInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
