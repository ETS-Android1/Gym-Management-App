package com.aina.dummydatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


class SendMessage extends AsyncTask<String, Void, Void> {

    private Exception exception;

    protected Void doInBackground(String... params) {
        try {
            // Construct data
            String apiKey = "apikey=" + "yLfTYJ4iajg-dwj4lcYRfgizQyySeY8BYgxaFWmVcB";
            String message = "&message=" + params[1];
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + params[0];

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
        } catch (Exception e) {
            Log.d("Error: ",e.toString());
        }
        return null;
    }
}