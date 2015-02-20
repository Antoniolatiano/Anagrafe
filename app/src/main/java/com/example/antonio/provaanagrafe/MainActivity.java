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

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static MainActivity instance;
    HTTPUtils utils;
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
        utils = new HTTPUtils();
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
        if (assets.connessioneAttiva) {
            if (assets.serviceRunning) {
                UpdateTable();
            }
        } else
            Loaddialog = ProgressDialog.show(this, "", "In attesa della connessione", true);
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

    public void UpdateTable() {
        Log.d(MainActivity.class.getSimpleName(), "Richiesto Update Tabella");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<Utente> toAdd = assets.ottieniListaUtenti();
                if (!toAdd.isEmpty()) {
                    baseAdapter.clear();
                    baseAdapter.addAll(toAdd);
                    baseAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

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
        Log.d("Elimina", "Eliminazione posizione:" + position);
        final Utente toDelete = (Utente) mAdapter.getItem(position);
        baseAdapter.remove(toDelete);
        baseAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (utils.rimuoviUtente(toDelete.getNome(), toDelete.getCognome()) == HTTPUtils.operazione_effettuata)
                        MakeToast("Utente eliminato", Toast.LENGTH_SHORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void ModifyUser(final int position) {
        Log.d("Modifica", "Modifica posizione:" + position);
        final Utente toMod = (Utente) mAdapter.getItem(position);
        dialog.showModUserDialog(toMod, position);
    }

    public void AddUser(Utente toAdd) {
        baseAdapter.add(toAdd);
        baseAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    public void StartLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Loaddialog = ProgressDialog.show(instance, "", "In attesa della connessione", true);
            }
        });
    }

    public void StopLoadingDialog() {
        if (Loaddialog != null && Loaddialog.isShowing())
            Loaddialog.dismiss();
    }
}
