package com.example.antonio.provaanagrafe.network;

import com.example.antonio.provaanagrafe.data.Todo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class POSTOperations extends HTTPHelper {
    public final static int operazione_effettuata = 200;
    public final static int utente_già_registrato = 409;
    public final static int errore_modifica_utente = 408;

    private final static String POST_NEW_TODO = "writeNewToDo";
    private final static String POST_GET_ALL_TODO = "getAllToDo";
    private final static String POST_GET_TIMESTAMP = "getLastTimestamp";
    private final static String POST_REMOVE_TODO = "removeToDo";
    private final static String POST_SET_COMPLETED_TODO = "setCompleted";


    public static Timestamp lastUpdateTimeStamp = new Timestamp(1000);

    public int writeToDo(String TodoMessage, String userIndentifier) {
        String postParam = "message=" + TodoMessage + "&userIdentifier=" + userIndentifier + "&operation=" + POST_NEW_TODO;
        try {
            return doPOST(postParam).getResponseCode();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 400;
        }
    }

    public int setTodoCompleted(String TodoMessage, String userIndentifier) {
        String postParam = "message=" + TodoMessage + "&userIdentifier=" + userIndentifier + "&operation=" + POST_SET_COMPLETED_TODO;
        try {
            return doPOST(postParam).getResponseCode();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 400;
        }
    }

    public int removeToDo(String TodoMessage, String userIndentifier) {
        String postParam = "message=" + TodoMessage + "&userIdentifier=" + userIndentifier + "&operation=" + POST_REMOVE_TODO;
        try {
            return doPOST(postParam).getResponseCode();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 400;
        }
    }


    public List<Todo> getAllTodos() {
        HttpURLConnection connection;
        StringBuffer response = new StringBuffer();
        try {
            connection = doPOST("operation=" + POST_GET_ALL_TODO);
            Integer responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //print result
        System.out.println(response.toString());
        List<Todo> Todos = new LinkedList<>();
        JSONArray JsonTodos = (JSONArray) JSONValue.parse(response.toString());
        for (Object jsonTodo : JsonTodos) {
            HashMap<?, ?> Todo = (HashMap<?, ?>) jsonTodo;
            Todo toadd = new Todo(Todo.get("TodoMessage").toString(), Todo.get("User").toString(), Todo.get("TodoCompleted").toString().equals("1"));
            Todos.add(toadd);
        }
        return Todos;
    }

    public boolean needUpdate() {
        Timestamp lastOperation = getLastOperationTimeStamp();
        if (lastUpdateTimeStamp.before(lastOperation)) {//se l'ultima operazione elaborata è più vecchia dell'ultima operazione effettuata
            lastUpdateTimeStamp = lastOperation;
            return true;
        } else {
            return false;
        }
    }

    private Timestamp getLastOperationTimeStamp() {
        HttpURLConnection connection;
        StringBuffer response = new StringBuffer();
        try {
            connection = doPOST("operation=" + POST_GET_TIMESTAMP);
            Integer responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(response.toString());
        JSONObject NewtimeStamp = (JSONObject) JSONValue.parse(response.toString());
        if (NewtimeStamp != null) {
            return Timestamp.valueOf(NewtimeStamp.get("DataOperazione").toString());
        } else
            return null;
    }
}
