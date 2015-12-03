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
public class ItunesSearchService extends AbstractService {
    
    private String query1;
    private JSONArray resultss;
    
    public ItunesSearchService(String query) {
        this.query1 = URLEncoder.encode(query);
    }
    
    public JSONArray getResults() {
        return resultss;
        
    }
    /** This is making api calls */
    public void run() {
        //String api_key = "096174e14f0efd0be330fce5f1f84de2";
        
        String url = //"https://itunes.apple.com/search?term="+query1+"&entity=album";
                "http://itunes.apple.com/search?term="+query1+"&media=movie";
                //"http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=" +
                     //   api_key+ "&q="+query;
        
        boolean error = false;
        HttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpResponse data = httpclient.execute(new HttpGet(url));
            HttpEntity entity = data.getEntity();
            String result = EntityUtils.toString(entity, "UTF8");
            
            JSONObject json = new JSONObject(result);
            if (json.getInt("resultCount")> 0) {
                resultss =  json.getJSONArray("results");
            } else {
                error = true;
            }
            } catch (Exception e) {
                resultss = null;
                error = true;
            } finally {
                httpclient.getConnectionManager().shutdown();
        }
        
        super.serviceComplete(error);
    }
    
    }
