package com.example.antonio.provaanagrafe;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Antonio on 07/02/2015.
 */
public class SyncService extends IntentService {

    static boolean debug = false;
    private Assets assets = null;
    private Handler handler;

    public SyncService() {
        super("SyncService");
        handler = new Handler();
        assets = Assets.getInstance();
        assets.serviceRunning = true;
        Log.d("SyncService", "Service Started");
        MakeToast("Avvio Service", Toast.LENGTH_SHORT);
    }

    public static void SetDebug(boolean debug) {
        SyncService.debug = debug;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (assets.serviceRunning) {
            if (assets.aggiornaListaUtenti()) {//controllo se la lista è stata modificata
                MakeToast("Ho bisogno di aggiornare la lista", Toast.LENGTH_SHORT);
                if (assets.activityRunning) //controllo se l'activity principale è in memoria
                    assets.aggiornaActivity();
                else
                    assets.MakeNotification(this);
            } else {
                MakeToast("Non ho bisogno di aggiornare la lista", Toast.LENGTH_SHORT);
            }
            Log.d("SyncService", "Updating");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SyncService", "Destroyed");
        MakeToast("Distruzione Service", Toast.LENGTH_SHORT);
        debug = false;
        assets.serviceRunning = false;
    }

    public void MakeToast(final String Text, final int Duration) {
        if (debug) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SyncService.this, Text, Duration).show();
                }
            });
        }
    }
}
