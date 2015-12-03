/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.bcu;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import uk.bcu.services.AbstractService;
import uk.bcu.services.MusicSearchService;

import uk.bcu.services.ServiceListener;

/**
 *
 * @author Yacub
 */
public class SearchableActivity extends ListActivity implements ServiceListener {

    private ArrayList<JSONObject> searchResults;
    private Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        searchResults = new ArrayList<JSONObject>();

        //Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position < searchResults.size()) {
            Intent result = new Intent();
            result.setAction(SEARCH_BROADCAST);
            result.putExtra("result", searchResults.get(position).toString());
            this.sendBroadcast(result);

            //close and return to my Track Library
            this.finish();
        }
    }

    public void doSearch(String query) {
        String[] result = new String[]{"Searching ... "};

        MusicSearchService service = new MusicSearchService(query);
        service.addListener(this);
        thread = new Thread(service);
        thread.start();

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.music_list_cell,
                R.id.text,
                result));
    }

    public void ServiceComplete(AbstractService service) {
        if (!service.hasError()) {
            MusicSearchService tarckService = (MusicSearchService) service;
            String[] result = new String[tarckService.getResults().length()];
   
            searchResults.clear();

            for (int i = 0; i < tarckService.getResults().length(); i++) {
                try {
                    searchResults.add(tarckService.getResults().getJSONObject(i));
                    result[i] = tarckService.getResults().getJSONObject(i).getString("name");
                

                } catch (JSONException ex) {
                    result[i] = "Error";
                }
            }
            setListAdapter(new ArrayAdapter<String>(this,
                    R.layout.music_list_cell,   
                    R.id.text,
                    result));
          


        } else {
            String[] result = new String[]{"No Results"};

            setListAdapter(new ArrayAdapter<String>(this,
                    R.layout.music_list_cell,
                    R.id.text,
                    result));
        }
    }
    public static final String SEARCH_BROADCAST = "search_result_selected";
}
