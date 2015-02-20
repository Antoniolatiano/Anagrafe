package com.example.antonio.provaanagrafe;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Antonio on 07/02/2015.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private static Intent serviceIntent;
    private final String TAG = MyBroadcastReceiver.class.getSimpleName();
    private Assets assets = Assets.getInstance();

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (serviceIntent == null) {
            Toast.makeText(context, "Inizializzo ServiceIntent", Toast.LENGTH_LONG);
            serviceIntent = new Intent(context, SyncService.class);
        }
        if (intent.getAction() == Assets.mainActivityStarted) {
            assets.activityRunning = true;
            Toast.makeText(context, "Activity avviata", Toast.LENGTH_LONG);
            Log.d(TAG, "Activity Avviata");
        } else if (intent.getAction() == Assets.bootCompleted) {
            Log.d(TAG, "Boot Completato");
        } else if (intent.getAction() == Assets.connectivityChanged) {
            Log.d(TAG, "Stato connessione modificato");
            Toast.makeText(context, "Stato connessione modificato", Toast.LENGTH_LONG);
        }

        assets.connessioneAttiva = Assets.controllaConnessione(context);

        if (assets.connessioneAttiva) {
            Log.d(TAG, "Connessione Presente");
            assets.mostraDialogConnessione(false);
            if (!isMyServiceRunning(context, SyncService.class)) {
                context.startService(serviceIntent);
                Log.d(TAG, "Service Started");
            }
        } else {
            Log.d(TAG, "Connessione Assente");
            assets.mostraDialogConnessione(true);
            if (isMyServiceRunning(context, SyncService.class)) {
                context.stopService(serviceIntent);
                Log.d(TAG, "Service Stopped");
            }
        }
    }
}
