package com.example.antonio.provaanagrafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;

import com.example.antonio.provaanagrafe.data.Todo;
import com.example.antonio.provaanagrafe.network.POSTOperations;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Antonio on 16/02/2015.
 * Classe che contiene tutti i dati che sono necessari per l'esecuzione dell'applicazione
 * Il contenuto deve essere disponibile sia dal service che dall'activity
 */
public class Assets {
    public final static String mainActivityStarted = "com.example.antonio.provaanagrafe.MainActivity.ACTIVITY_STARTED";
    public final static String actionNetworkOperation = "com.example.antonio.provaanagrafe.MainActivity.NETWORK_OPERATION";
    public final static String bootCompleted = "android.intent.action.BOOT_COMPLETED";
    public final static String connectivityChanged = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String FileName = "ConfigFile.json";
    public static List<Todo> Todos = new LinkedList<>();
    private static Assets instance = null;
    private final String TAG = Assets.class.getSimpleName();
    public boolean serviceRunning, activityRunning;
    public POSTOperations utils;
    public SyncService syncInstance;
    boolean connessioneAttiva;
    Notification.Builder listaModNotifica;
    NotificationManager manager;

    private Assets() {
        utils = new POSTOperations();
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public static boolean controllaConnessione(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo NetInfo = connMgr
                .getActiveNetworkInfo();
        if (NetInfo != null && NetInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

 /*   public void initFromJsonConfigFile(File origin) {
        String JsonData = ReadFile(origin, FileName);
        if (!JsonData.isEmpty()) {
            setFromJsonConfig(JsonData);
        }
        // aggiornaActivity();
    }*/

    public List<Todo> getTodos() {
        return Todos;
    }

    public void mostraDialogConnessione(boolean show) {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null)
            if (show) {
                instance.StartLoadingDialog();
            } else {
                instance.StopLoadingDialog();
            }
    }

    public void aggiornaActivity() {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null)
            instance.UpdateTable();
    }

    public void removeTodoFromActivity(Todo toRemove) {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null)
            instance.baseAdapter.remove(toRemove);
    }

    public void addTodoToActivity(Todo toAdd) {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null)
            instance.AddUser(toAdd);
    }

    public void MakeNotification() {
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        PendingIntent intent = PendingIntent.getActivity(syncInstance, 0, new Intent(syncInstance, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        listaModNotifica = new Notification.Builder(syncInstance)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("La lista è stata modificata")
                .setContentTitle("Anagrafe")
                .setLights(Color.BLUE, 500, 500)
                .setVibrate(pattern)
                .setContentIntent(intent)
                .setAutoCancel(true);
        manager = (NotificationManager) syncInstance.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, listaModNotifica.build());
    }

 /*   public String getJsonConfig() {
        JSONObject jsonConfig = new JSONObject();
        // jsonConfig.put("UsersList", JSONArray.toJSONString(ListaUtenti));
        jsonConfig.put("LastUpdateTimeStamp", JSONValue.toJSONString(POSTOperations.lastUpdateTimeStamp));
        return jsonConfig.toJSONString();
    }

    public void WriteFile(File destination, String name, String content) {
        File file = new File(destination, name);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            Log.d(TAG, "Writed:" + content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

 /*   public String ReadFile(File origin, String name) {
        File file = new File(origin, name);
        byte[] buffer = new byte[(int) file.length()];
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                inputStream.read(buffer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(buffer);
    }*/

   /* public void setFromJsonConfig(String JsonString) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(JsonString);
        if (jsonObject != null) {
            Timestamp timestamp = Timestamp.valueOf((String) jsonObject.get("LastUpdateTimeStamp"));
            if (timestamp != null) {
                POSTOperations.lastUpdateTimeStamp = timestamp;
            }
            JSONArray jsonArray = (JSONArray) JSONValue.parse((String) jsonObject.get("UsersList"));
            if (jsonArray != null) {
                for(Object jsonValue:jsonArray){
                    HashMap<String,String> utente=(HashMap) jsonValue;
                    ListaUtenti.add(new Utente(utente.get(Utente.campi[0]),utente.get(Utente.campi[1])));
                }
            }
        }
    }*/
}
