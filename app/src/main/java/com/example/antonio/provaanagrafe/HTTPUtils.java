package com.example.antonio.provaanagrafe;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class HTTPUtils {
    public final static int operazione_effettuata = 200;
    public final static int utente_già_registrato = 409;
    public final static int errore_modifica_utente = 408;
    private Timestamp lastUpdateTimeStamp = null, lastOperationTimeStamp;

    int aggiungiUtente(String Nome, String Cognome) throws IOException {
        String url = "http://dreamcloud.altervista.org/aggiungi.php";
        return eseguiPOST(Nome, Cognome, new URL(url));
    }

    int rimuoviUtente(String Nome, String Cognome) throws IOException {
        String url = "http://dreamcloud.altervista.org/elimina.php";
        return eseguiPOST(Nome, Cognome, new URL(url));
    }

    private int eseguiPOST(String nome, String cognome, URL indirizzo) throws IOException {
        HttpURLConnection con = (HttpURLConnection) indirizzo.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        String urlParameters = "nome=" + nome + "&cognome=" + cognome;
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        Integer responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + indirizzo.getPath());
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Response header : " + con.getHeaderField(0));

        return responseCode;
    }

    List<Utente> ottieniUtenti() {
        final String url = "http://dreamcloud.altervista.org/rispostaMod.php";
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL obj = null;
                try {
                    obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    con.setDoOutput(true);
                    Integer responseCode = con.getResponseCode();
                    System.out.println("\nGetting response from URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while (thread.isAlive()) {
        }
        //print result
        System.out.println(response.toString());
        List<Utente> UtentiReg = new LinkedList<>();
        JSONArray utenti = (JSONArray) JSONValue.parse(response.toString());
        for (Object utente : utenti) {
            Utente toadd = new Utente(((HashMap<?, ?>) utente).get("nome").toString(), ((HashMap<?, ?>) utente).get("cognome").toString());
            UtentiReg.add(toadd);
        }
        lastUpdateTimeStamp = lastOperationTimeStamp;
        return UtentiReg;
    }

    int modificaUtente(Utente oldUser,Utente newUser) throws IOException {
        String url = "http://dreamcloud.altervista.org/modifica.php";
        URL indirizzo=new URL(url);
        HttpURLConnection con = (HttpURLConnection) indirizzo.openConnection();
        //add request header
        con.setRequestMethod("POST");
        String urlParameters = "nome=" + newUser.getNome() + "&cognome=" + newUser.getCognome() +
                "&oldnome=" + oldUser.getNome() + "&oldcognome=" + oldUser.getCognome() ;
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        Integer responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + indirizzo.getPath());
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Response header : " + con.getHeaderField(0));

        return responseCode;
    }

    public boolean needUpdate() {
        lastOperationTimeStamp = getLastOperationTimeStamp();
        if (lastUpdateTimeStamp == null || lastUpdateTimeStamp.before(lastOperationTimeStamp)) {//se l'ultima operazione elaborata è più vecchia dell'ultima operazione effettuata
            return true;
        } else {
            return false;
        }
    }

    public Timestamp getLastOperationTimeStamp() {
        final String url = "http://dreamcloud.altervista.org/ottieniTimestamp.php";
        final StringBuffer response = new StringBuffer();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL obj = null;
                try {
                    obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setDoOutput(true);
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    Integer responseCode = con.getResponseCode();
                    System.out.println("\nGetting response from URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        while (thread.isAlive()) {
        }
        //print result
        System.out.println(response.toString());
        JSONObject NewtimeStamp = (JSONObject) JSONValue.parse(response.toString());
        if (NewtimeStamp != null) {
            return Timestamp.valueOf(NewtimeStamp.get("DataOperazione").toString());
        } else
            return null;
    }
}
