package com.olo.ding.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequester {
    public static String requestGET(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            String result = inputStream2String(inputStream);
            inputStream.close();
            conn.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String requestPOST(String urlStr, String body, String contentType) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", contentType);
            conn.connect();
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = conn.getInputStream();
            String result = inputStream2String(inputStream);
            inputStream.close();
            conn.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String inputStream2String(InputStream stream) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String temp = reader.readLine();
            while (temp != null) {
                result.append(temp);
                temp = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
