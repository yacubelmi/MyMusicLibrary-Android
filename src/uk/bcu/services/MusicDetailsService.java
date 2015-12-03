/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.bcu.services;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 *
 * @author Yacub
 */
public class MusicDetailsService extends AbstractService {

    private String movieId;
    private JSONObject movie;
    private JSONObject JSONresult;

    public MusicDetailsService(String movieId) {
        this.movieId = movieId;
    }

    public JSONObject getResult() {
        return JSONresult;
    }

    public void run() {
        String api_key = "f06e41f0aad377a1aebfe76927318181";
        String url = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=" + api_key + "&mbid=" + movieId + "&format=json";

        Log.i("URL", url);

        boolean error = false;
        HttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpResponse data = httpclient.execute(new HttpGet(url));
            HttpEntity entity = data.getEntity();
            String result = EntityUtils.toString(entity, "UTF8");

            JSONresult = new JSONObject(result);

             //if (Integer.valueOf(json.getJSONObject("track").getString("opensearch)) > 0) {
            //  results = json.getJSONObject("results").getJSONObject("trackmatches").getJSONArray("track");
        } catch (Exception e) {
            System.out.println(e.toString());
            movie = null;
            error = true;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        super.serviceComplete(error);
    }

}
