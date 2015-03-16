package com.example.antonio.provaanagrafe.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HTTPHelper {

    private final static String POST_HANDLE_REQUEST_ADDRESS = "http://dreamcloud.altervista.org/MyToDoReminder/requestHandler.php";

    protected HttpURLConnection doPOST(final String postParameters) throws IOException {
        final URL address = new URL(POST_HANDLE_REQUEST_ADDRESS);
        final HttpURLConnection con = (HttpURLConnection) address.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParameters);
        wr.flush();
        wr.close();
        Integer responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + address.getPath());
        System.out.println("Post parameters : " + postParameters);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Response header : " + con.getHeaderField(0));
        return con;
    }


}
