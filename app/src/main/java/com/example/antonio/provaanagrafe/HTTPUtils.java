package com.example.antonio.provaanagrafe;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class HTTPUtils {
    public final static int operazione_effettuata = 200;
    public final static int utente_già_registrato = 409;
    public final static int errore_modifica_utente = 408;


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

    List<Utente> ottieniUtenti() {
        final List<Utente> UtentiReg = new LinkedList<>();
        final String url = "http://dreamcloud.altervista.org/rispostaMod.php";
        final StringBuffer response = new StringBuffer();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    if (con != null) {
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
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                //print result
                System.out.println(response.toString());
                JSONArray utenti = (JSONArray) JSONValue.parse(response.toString());
                if (utenti != null) {
                    for (Object utente : utenti) {
                        Utente toadd = new Utente(((HashMap<?, ?>) utente).get("nome").toString(), ((HashMap<?, ?>) utente).get("cognome").toString());
                        UtentiReg.add(toadd);
                    }
                }
            }
        });
        thread.start();
        while (thread.isAlive()) {
        }
        return UtentiReg;
    }
}
