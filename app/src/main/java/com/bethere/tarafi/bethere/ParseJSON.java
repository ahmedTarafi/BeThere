package com.bethere.tarafi.bethere;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by user2 on 09/09/2016.
 */
public class ParseJSON extends Service {
    private static final String JSON_ARRAY = "result";
    private static final String ID = "id2";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private int TRACK=0 ;
    String data[] = new String[3];
    private Context myctx;
    ParseJSON(Context ctx){
        myctx = ctx;
    }

    String[] showData() {
        try {
            JSONObject json= null;
            try {
                json = (JSONObject) new JSONTokener(BackgroundWorker.MY_JSON).nextValue();
                JSONArray locations = json.getJSONArray("result");
            for (TRACK = 0; TRACK < locations.length(); TRACK++) {
                JSONObject jsonobject = locations.getJSONObject(TRACK);
                data[0] = jsonobject.getString(ID);
                data[1] = jsonobject.getString(LATITUDE);
                data[2] = jsonobject.getString(LONGITUDE);
            }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

        @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}