package com.example.antonio.provaanagrafe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
    static boolean ServiceRunning = false;
    private static MainActivity instance;
    HTTPUtils utils;
    ListView mListView;
    ProgressDialog Loaddialog;
    SwipeRefreshLayout swipeRefresh;
    private MyAdapter baseAdapter;
    private SwipeActionAdapter mAdapter;
    private AddUserDialog dialog;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        utils = new HTTPUtils();
        dialog = new AddUserDialog(this);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.my_list_view);
        baseAdapter = new MyAdapter(this);
        mAdapter = new SwipeActionAdapter(baseAdapter);
        mAdapter.setSwipeActionListener(new SwipeActionListener(this, mAdapter)).setListView(mListView);
        mListView.setAdapter(mAdapter);
        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpdateTable();
                        swipeRefresh.setRefreshing(false);
                    }
                }, 500);
            }
        });

    }

    @Override
    protected void onResume() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo NetInfo = connMgr.getActiveNetworkInfo();
        UpdateTable();//fai il primo update
        if (!ServiceRunning) {
            if (!ServiceRunning && NetInfo != null && NetInfo.isConnected()) {
                startService(new Intent(this, SyncService.class));
                ServiceRunning = true;
            } else {
                Loaddialog = ProgressDialog.show(this, "", "In attesa della connessione", true);
            }
        } else
            MakeToast("Service già in esecuzione", Toast.LENGTH_SHORT);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.stop_service:
                if (NetworkChangeReceiver.isMyServiceRunning(SyncService.class))
                    stopService(new Intent(this, SyncService.class));
                return true;
            case R.id.toogle_debug:
                if (SyncService.debug)
                    SyncService.SetDebug(false);
                else
                    SyncService.SetDebug(true);
                return true;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void UpdateTable() {
        List<Utente> toAdd = utils.ottieniUtenti();
        if (!toAdd.isEmpty()) {
            baseAdapter.clear();
            baseAdapter.addAll(toAdd);
            baseAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
        }

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

    public void AddUser(Utente toAdd) {
        baseAdapter.add(toAdd);
        baseAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    public void StartLoadingDialog() {
        Loaddialog = ProgressDialog.show(this, "", "In attesa della connessione", true);
    }

    public void StopLoadingDialog() {
        if (Loaddialog.isShowing())
            Loaddialog.dismiss();
    }
}
