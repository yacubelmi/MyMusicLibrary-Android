/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.bcu;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.bcu.model.MusicData;
import uk.bcu.sqlite.MySQLiteHelper;

/**
 *
 * @author Yacub
 */
public class ItuneActivity extends ListActivity {

    private ArrayList<JSONObject> itunes;
    private BroadcastReceiver receiver;
    private Object mShareActionProvider;
    private Intent sharingIntent;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itunes = loadmusics();
        setContentView(R.layout.main);
        MySQLiteHelper db = new MySQLiteHelper(this);

        Log.d("Insert: ", "Inserting ..");

        String[] CELLS = new String[itunes.size()];

        for (int i = 0; i < itunes.size(); i++) {
            try {
                CELLS[i] = itunes.get(i).getString("trackName");

            } catch (JSONException ex) {
            }
        }

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.music_list_cell_1,
                R.id.text,
                CELLS));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ItunesSearchableActivity.SEARCH_BROADCAST);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonString = intent.getExtras().getString("result");
                try {
                    JSONObject object = new JSONObject(jsonString);
                    addmusic(object);
                } catch (JSONException ex) {
                }
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu_1, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected Identify single menu
     * item by it's id
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.itmSearch:
                Toast.makeText(ItuneActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                onSearchRequested();
                return true;

            case R.id.menu_share:
                Toast.makeText(ItuneActivity.this, "Share is Selected", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");

                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share via"));
                //startActivity(sendIntent);
                return true;

            case R.id.menu_delete:
                Toast.makeText(ItuneActivity.this, "LastFm music is Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;

            case R.id.menu_preferences:
                Toast.makeText(ItuneActivity.this, "SMS sent - Thanks for using the App", Toast.LENGTH_SHORT).show();
                sendSMS();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position < itunes.size()) {
            JSONObject music = itunes.get(position);

            Intent intent = new Intent(this, MusicDetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("itunes", music.toString());
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    private void addmusic(JSONObject music) {
        itunes.add(music);
        savemusics(itunes);
        //update list
        String[] CELLS = new String[itunes.size()];
        for (int i = 0; i < itunes.size(); i++) {
            try {

                CELLS[i] = itunes.get(i).getString("trackName");
            } catch (JSONException ex) {
            }
        }

        MySQLiteHelper db = new MySQLiteHelper(this);
        Log.d("Insert: ", "Inserting ..");
        db.Add_MusicData(new MusicData("Ravi", "9100000000"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

   // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // getMenuInflater().inflate(R.menu.main_menu,menu);
    // return true;
//}
   /* @Override
     public boolean onOptionsItemSelected(MenuItem item) {
     if (item.getItemId() == R.id.itmSearch){
     onSearchRequested();
     return true;
     }
     return false;
     }*/
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onSearchRequested();

            return true;

        }
        return false;
    }

    public void savemusics(ArrayList<JSONObject> itunesList) {
        try {
            JSONObject listWrapper = new JSONObject();
            JSONArray list = new JSONArray(itunes);
            listWrapper.put("itunes", list);

            String strList = listWrapper.toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(itunes_FILENAME, Context.MODE_PRIVATE);
                outputStream.write(strList.getBytes());
                outputStream.close();

            } catch (Exception e) {
            }

        } catch (JSONException ex) {
        }
    }

    public ArrayList<JSONObject> loadmusics() {
        ArrayList<JSONObject> musicList = new ArrayList<JSONObject>();
        try {
            StringBuilder strList = new StringBuilder();
            FileInputStream inputStream;

            try {
                inputStream = openFileInput(itunes_FILENAME);

                byte[] buffer = new byte[1024];

                while (inputStream.read(buffer) != -1) {
                    strList.append(new String(buffer));
                }
                inputStream.close();
            } catch (Exception e) {
            }
            JSONObject listWrapper = new JSONObject(strList.toString());
            JSONArray list = listWrapper.getJSONArray("itunes");

            for (int i = 0; i < list.length(); i++) {
                musicList.add(list.getJSONObject(i));
            }
        } catch (JSONException ex) {
        }
        return musicList;
    }

    public static final String itunes_FILENAME = "myitunes.json";

    //get current phone number of the phone
    private String getMyPhoneNO() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String yourNumber = mTelephonyMgr.getLine1Number();
        return yourNumber;
    }

    //txt web service
    private String sendSMS() {
        try {
            // Construct data
            String user = "username=" + "yacub34@hotmail.co.uk";
            String hash = "&hash=" + "aa8308f9743648cb1f178bf9e29acf49cf0bf697";
            String message = "&message=" + "Thanks for using Itune Web services - Yacub s11715700";
            String sender = "&sender=" + "Alert";
            String numbers = "&numbers=" + getMyPhoneNO();

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
            String data = user + hash + numbers + message + sender;
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

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
    }

}
