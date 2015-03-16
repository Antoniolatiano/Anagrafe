package com.example.antonio.provaanagrafe.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.antonio.provaanagrafe.Assets;
import com.example.antonio.provaanagrafe.data.Todo;

/**
 * Created by Antonio on 13/03/2015.
 */
public class AsyncNetworkThread extends AsyncTask<NetworkOperations, Void, NetworkOperations> {

    private final String TAG = getClass().getSimpleName();
    private Assets assets = null;

    public AsyncNetworkThread(Assets assets) {
        this.assets = assets;
    }

    @Override
    protected NetworkOperations doInBackground(final NetworkOperations... toExecute) {
        switch (toExecute[0].getOperation()) {
            case UPDATE_FROM_SERVICE:
                if (assets.utils.needUpdate()) {
                    Assets.Todos = assets.utils.getAllTodos();
                    Log.d(TAG, "esecuzione UPDATE");
                    return new NetworkOperations(toExecute[0].getOperation(), new Todo("update", "update"));
                }
                break;
            case UPDATE:
                Assets.Todos = assets.utils.getAllTodos();
                Log.d(TAG, "esecuzione UPDATE");
                break;
            case ADD_TODO:
                assets.utils.writeToDo(toExecute[0].getData().getMessage(), toExecute[0].getData().getUserID());
                Log.d(TAG, "esecuzione ADDTODO");
                Assets.Todos = assets.utils.getAllTodos();
                break;
            case REMOVE_TODO:
                assets.utils.removeToDo(toExecute[0].getData().getMessage(), toExecute[0].getData().getUserID());
                Log.d(TAG, "esecuzione REMOVETODO");
                Assets.Todos = assets.utils.getAllTodos();
                break;
        }
        return toExecute[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(NetworkOperations executed) {
        switch (executed.getOperation()) {
            case REMOVE_TODO:
                assets.removeTodoFromActivity(executed.getData());
                break;
            case ADD_TODO:
                assets.addTodoToActivity(executed.getData());
                break;
            case UPDATE:
                assets.aggiornaActivity();
                Log.d(TAG, "UPDATE terminato");
                break;
            case UPDATE_FROM_SERVICE:
                if (executed.getData() != null && executed.getData().getMessage() == "update") {
                    if (assets.activityRunning) {
                        assets.aggiornaActivity();
                    } else if (assets.serviceRunning) {
                        assets.MakeNotification();
                    }
                }
        }
        super.onPostExecute(executed);
    }
}
