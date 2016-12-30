package com.bethere.tarafi.bethere;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by user2 on 19/08/2016.
 */
public class BackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;
    public static String MY_JSON="{\"result\":[{\"id\":\"1\",\"latitude\":\"31.436493\",\"longitude\":\"-9.280584\"}]}";
    BackgroundWorker (Context ctx){
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String send_url = "http://10.5.0.191/sdata.php";//10.0.2.2/sdata.php
        if(type.equals("send")){
            try {
                String lat = params[1];
                String lng = params[2];
                URL url = new URL(send_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("lat", "UTF-8")+"="+URLEncoder.encode(lat, "UTF-8")+"&"
                        +URLEncoder.encode("lng", "UTF-8")+"="+URLEncoder.encode(lng, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(type.equals("receive")){
            final String JSON_URL = "http://10.5.0.191/gdata.php";
            String uri = JSON_URL;

            BufferedReader bufferedReader;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                return sb.toString().trim();

            }catch(Exception e){
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Connection status");
    }
    @Override
    protected void onPostExecute(String result) {
        MY_JSON = result;
        alertDialog.setMessage(result);
        //alertDialog.show();
    }

    //@Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate();
        alertDialog.cancel();
    }
}
