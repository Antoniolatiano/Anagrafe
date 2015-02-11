package com.example.antonio.provaanagrafe;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Antonio on 07/02/2015.
 */
public class SyncService extends IntentService {

    static boolean debug = false;
    MainActivity context;
    boolean running = true;


    public SyncService() {
        super("SyncService");
        context = MainActivity.getInstance();
        if (context == null) {
            stopSelf();
        }
        Log.d("SyncService", "Service Started");
        context.MakeToast("Avvio Service", Toast.LENGTH_SHORT);
    }

    public static void SetDebug(boolean debug) {
        SyncService.debug = debug;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo NetInfo = connMgr
                .getActiveNetworkInfo();
        if (NetInfo == null || !NetInfo.isConnected()) {
            stopSelf();
        } else {
            context.MakeToast("c'è connessione internet", Toast.LENGTH_SHORT);
        }
        int n = 1;
        while (running) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.UpdateTable();
                }
            });
            if (debug) {
                context.MakeToast("Aggiornamento " + n + " della lista", Toast.LENGTH_SHORT);
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
    public void onDestroy() {
        Log.d("SyncService", "Destroyed");
        context.MakeToast("Distruzione Service", Toast.LENGTH_SHORT);
        running = false;
        debug = false;
    }
}
