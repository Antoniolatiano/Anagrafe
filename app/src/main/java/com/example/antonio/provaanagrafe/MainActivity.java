package com.example.antonio.provaanagrafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.antonio.provaanagrafe.data.Todo;
import com.example.antonio.provaanagrafe.network.NetworkOperations;
import com.example.antonio.provaanagrafe.network.POSTOperations;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;


public class MainActivity extends ActionBarActivity {
    private static MainActivity instance;
    POSTOperations utils;
    ListView mListView;
    ProgressDialog Loaddialog;
    MyAdapter baseAdapter;
    SwipeActionAdapter mAdapter;
    Assets assets;
    private UserDialog dialog;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        utils = new POSTOperations();
        dialog = new UserDialog(this);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.my_list_view);
        baseAdapter = new MyAdapter(this);
        mAdapter = new SwipeActionAdapter(baseAdapter);
        mAdapter.setSwipeActionListener(new SwipeActionListener(this, mAdapter)).setListView(mListView);
        mListView.setAdapter(mAdapter);
        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
        assets = Assets.getInstance();
    }

    @Override
    protected void onStop() {
        assets.activityRunning = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        sendBroadcast(new Intent(Assets.mainActivityStarted));
        requestUpdate();
/*        if (assets.connessioneAttiva) {
         //   if (assets.serviceRunning) {
                requestUpdate();
         //   }
        } else
            Loaddialog = ProgressDialog.show(this, "", "In attesa della connessione", true);*/
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_service:
                if (MyBroadcastReceiver.isMyServiceRunning(instance, SyncService.class))
                    stopService(new Intent(this, SyncService.class));
                return true;
            case R.id.toogle_debug:
                if (SyncService.debug) {
                    SyncService.SetDebug(false);
                    item.setTitle("Activate Debug");
                } else {
                    SyncService.SetDebug(true);
                    item.setTitle("Deactivate Debug");
                }
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void requestUpdate() {
        Intent i = new Intent(Assets.actionNetworkOperation);
        i.putExtra("operation", NetworkOperations.EnumOperations.UPDATE);
        sendBroadcast(i);
    }
    public void UpdateTable() {
        Log.d(MainActivity.class.getSimpleName(), "Richiesto Update Tabella");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    baseAdapter.clear();
                baseAdapter.addAll(assets.getTodos());
                    baseAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }
        });
        StopUserDialog();
    }

    public void AddUserClicked(View view) {
        dialog.showDialog();
    }

    public void MakeToast(final String Text, final int Duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, Text, Duration).show();
            }
        });
    }

    public void DeleteUser(final int position) {
        Log.d("Elimina", "Eliminazione Promemoria:" + position);
        final Todo toDelete = (Todo) mAdapter.getItem(position);
        Intent delete = new Intent(Assets.actionNetworkOperation);
        delete.putExtra("operation", NetworkOperations.EnumOperations.REMOVE_TODO);
        delete.putExtra("data", toDelete);
        sendBroadcast(delete);
    }

    public void ModifyUser(final int position) {
    /*    Log.d("Modifica", "Modifica posizione:" + position);
        final Utente toMod = (Utente) mAdapter.getItem(position);
        dialog.showModUserDialog(toMod, position);*/
    }

    public void AddUser(Todo toAdd) {
        baseAdapter.add(toAdd);
        baseAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    public void StartLoadingDialog() {
        if (Loaddialog == null || !Loaddialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Loaddialog = ProgressDialog.show(MainActivity.getInstance(), "", "In attesa della connessione", true);
                }
            });
        }
    }

    public void StopLoadingDialog() {
        if (Loaddialog != null && Loaddialog.isShowing())
            Loaddialog.dismiss();
    }

    public void StopUserDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
