package com.example.antonio.provaanagrafe;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.antonio.provaanagrafe.network.AsyncNetworkThread;
import com.example.antonio.provaanagrafe.network.NetworkOperations;

/**
 * Created by Antonio on 07/02/2015.
 */
public class SyncService extends IntentService {

    static boolean debug = false;
    private Assets assets = null;
    private AsyncNetworkThread networkThread;

    public SyncService() {
        super("SyncService");
    }

    public static void SetDebug(boolean debug) {
        SyncService.debug = debug;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (assets.serviceRunning) {
            if (networkThread == null || networkThread.getStatus() == AsyncTask.Status.FINISHED) {
                Log.d("SyncService", "Instantiate new AsyncTask");
                networkThread = null;
                networkThread = new AsyncNetworkThread(assets);
                networkThread.execute(new NetworkOperations(NetworkOperations.EnumOperations.UPDATE_FROM_SERVICE, null));
            }
            Log.d("SyncService", "Updating");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        assets = Assets.getInstance();
        //assets.initFromJsonConfigFile(getFilesDir());
        assets.syncInstance = this;
        Log.d("SyncService", "Service Started");
        MakeToast("Avvio Service", Toast.LENGTH_SHORT);
        assets.serviceRunning = true;
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("SyncService", "Destroyed");
        MakeToast("Distruzione Service", Toast.LENGTH_SHORT);
        debug = false;
        assets.serviceRunning = false;
        //assets.WriteFile(getFilesDir(), Assets.FileName, assets.getJsonConfig());
    }

    public void MakeToast(final String Text, final int Duration) {
        if (debug) {
            Toast.makeText(SyncService.this, Text, Duration).show();
        }
    }
}
