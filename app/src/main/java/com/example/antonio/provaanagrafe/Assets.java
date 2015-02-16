package com.example.antonio.provaanagrafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Antonio on 16/02/2015.
 * Classe che contiene tutti i dati che sono necessari per l'esecuzione dell'applicazione
 * Il contenuto deve essere disponibile sia dal service che dall'activity
 */
public class Assets {
    public final static String mainActivityStarted = "com.example.antonio.provaanagrafe.MainActivity.ACTIVITY_STARTED";
    public final static String mainActivityStopped = "com.example.antonio.provaanagrafe.MainActivity.ACTIVITY_STOPPED";
    public final static String bootCompleted = "android.intent.action.BOOT_COMPLETED";
    public final static String connectivityChanged = "android.net.conn.CONNECTIVITY_CHANGE";
    private static Assets instance = null;
    public SyncService Syncinstance;
    public boolean serviceRunning, activityRunning;
    boolean connessioneAttiva;
    Notification.Builder listaModNotifica;
    NotificationManager manager;
    private List<Utente> ListaUtenti = new LinkedList<>();
    private HTTPUtils utils;

    private Assets() {
        utils = new HTTPUtils();
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public List<Utente> ottieniListaUtenti() {
        return ListaUtenti;
    }

    /**
     * Controlla se è necessario che aggiorni la lista, se è necessario la aggiorna
     * altrimenti no
     *
     * @return true se ha aggiornato, false se non è necessario
     */
    public boolean aggiornaListaUtenti() {
        if (connessioneAttiva && utils.needUpdate()) {
            ListaUtenti = utils.ottieniUtenti();
            return true;
        }
        return false;
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

    public void MakeNotification() {
        listaModNotifica = new Notification.Builder(Syncinstance)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("La lista è stata modificata")
                .setContentTitle("Anagrafe");
        manager = (NotificationManager) Syncinstance.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, listaModNotifica.build());
    }
}
