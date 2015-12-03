//
//* To change this license header, choose License Headers in Project Properties.
//* To change this template file, choose Tools | Templates
//* and open the template in the editor.
//
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
import uk.bcu.services.ItunesSearchService;

import uk.bcu.services.ServiceListener;

/**
 *
 * @author Yacub
 */
public class ItunesSearchableActivity extends ListActivity implements ServiceListener {

    private ArrayList<JSONObject> searchResults;
    private Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        searchResults = new ArrayList<JSONObject>();

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

            this.finish();
        }
    }

    //The doSearch method is the method that
    //should trigger a search, in our case we simply
    //add the search query to the views list.
    public void doSearch(String query) {
        String[] result = new String[]{"Searching . . . "};

        ItunesSearchService service = new ItunesSearchService(query);

        service.addListener((ServiceListener) this);
        thread = new Thread(service);
        thread.start();

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.music_list_cell,
                R.id.text,
                result));

    }

    public void ServiceComplete(AbstractService service) {
        if (!service.hasError()) {
            ItunesSearchService musicService = (ItunesSearchService) service;
            String[] result = new String[musicService.getResults().length()];

            searchResults.clear();
            for (int i = 0; i < musicService.getResults().length(); i++) {
                try {
                    searchResults.add(musicService.getResults().getJSONObject(i));
                    result[i] = musicService.getResults().getJSONObject(i).getString("collectionName");

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
