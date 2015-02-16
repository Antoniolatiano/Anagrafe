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
    boolean running = true;
    private Assets assets = null;
    private Handler handler;

    public SyncService() {
        super("SyncService");
        handler = new Handler();
        assets = Assets.getInstance();
        assets.Syncinstance = this;
        assets.serviceRunning = true;
        Log.d("SyncService", "Service Started");
        MakeToast("Avvio Service", Toast.LENGTH_SHORT);
    }

    public static void SetDebug(boolean debug) {
        SyncService.debug = debug;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!assets.connessioneAttiva) {
            assets.serviceRunning = false;
        }
        int n = 1;
        while (running) {
            if (assets.aggiornaListaUtenti()) {//controllo se la lista è stata modificata
                MakeToast("Ho bisogno di aggiornare la lista", Toast.LENGTH_SHORT);
                if (assets.activityRunning) //controllo se l'activity principale è in memoria
                    assets.aggiornaActivity();
                else
                    assets.MakeNotification();
            } else {
                MakeToast("Non ho bisogno di aggiornare la lista", Toast.LENGTH_SHORT);
            }
            if (debug) {
                MakeToast("Aggiornamento " + n + " della lista", Toast.LENGTH_SHORT);
            }
            n++;
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
        running = false;
        debug = false;
        assets.serviceRunning = false;
    }

    public void MakeToast(final String Text, final int Duration) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SyncService.this, Text, Duration).show();
            }
        });
    }
}
