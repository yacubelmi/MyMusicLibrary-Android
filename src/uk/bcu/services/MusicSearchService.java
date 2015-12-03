/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.bcu.services;

import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Yacub
 */
public class MusicSearchService extends AbstractService {
  private String query;
    private JSONArray results;
    
    public MusicSearchService(String query) {
        this.query = URLEncoder.encode(query);
    }
    
    public JSONArray getResults() {
        return results;
        
    }
    
    public void run() {
        String api_key = "f06e41f0aad377a1aebfe76927318181";
        String url = "http://ws.audioscrobbler.com/2.0/?method=album.search&album="+query+"&api_key="+api_key+"&format=json";
       
        boolean error = false;
        HttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpResponse data = httpclient.execute(new HttpGet(url));
            HttpEntity entity = data.getEntity();
            String result = EntityUtils.toString(entity, "UTF8");
            
            JSONObject json = new JSONObject(result);
            if (Integer.valueOf(json.getJSONObject("results").getString("opensearch:totalResults")) > 0) { 
                results = json.getJSONObject("results").getJSONObject("albummatches").getJSONArray("album");
            } else {
                error = true;
            }
            } catch (Exception e) {
                results = null;
                error = true;
            } finally {
                httpclient.getConnectionManager().shutdown();
        }
        
        super.serviceComplete(error);
    }
}
