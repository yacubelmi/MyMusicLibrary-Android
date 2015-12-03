package uk.bcu;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ListActivity {

    private ArrayList<JSONObject> musics;
    private BroadcastReceiver receiver;
    private Object mShareActionProvider;
    private Intent sharingIntent;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musics = loadmusics();
        super.setTitle("My Albums");
        cd = new ConnectionDetector(getApplicationContext());

        setContentView(R.layout.main);
        updateListView(musics);

        String[] CELLS = new String[musics.size()];
        for (int i = 0; i < musics.size(); i++) {
            try {
                CELLS[i] = musics.get(i).getString("name");
            } catch (JSONException ex) {
            }
        }

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.music_list_cell,
                R.id.text,
                CELLS));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SearchableActivity.SEARCH_BROADCAST);
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
        this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView arg0, View arg1, int pos, long id) {
                JSONObject music = musics.get(pos);
                removeFilm(music);
                return true;
            }

        });
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            showAlertDialog(MainActivity.this, "Internet Connection",
                    "You have internet connection", true);
        } else {
            showAlertDialog(MainActivity.this, "Internet Connection",
                    "Turn on your wifi or your moobile data to search", false);
        }

    }

    private void removeFilm(final JSONObject music) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Remove '" + music.getString("name") + "' from your list.").setTitle(R.string.remove_alert_title);
            builder.setPositiveButton(R.string.remove_alert_positive, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    musics.remove(music);
                    savemusics(musics);
                    updateListView(musics);
                }
            });
            builder.setNegativeButton(R.string.remove_alert_negative, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    updateListView(musics);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (JSONException ex) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmSearch:
                Toast.makeText(MainActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                onSearchRequested();
                return true;

            case R.id.menu_share:
                Toast.makeText(MainActivity.this, "Share is Selected", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");

                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share via"));
                //startActivity(sendIntent);
                return true;

            case R.id.internet:
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    showAlertDialog(MainActivity.this, "Internet Connection",
                            "You have internet connection", true);
                } else {
                    showAlertDialog(MainActivity.this, "Internet Connection",
                            "You have no internet connection", false);
                }
                return true;

            case R.id.menu_delete:
                Toast.makeText(MainActivity.this, "Itunes is Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ItuneActivity.class);
                startActivity(intent);

                return true;

            case R.id.menu_preferences:
                Toast.makeText(MainActivity.this, "SMS sent - Thanks for using the App", Toast.LENGTH_SHORT).show();

                sendSMS();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onSearchRequested();
            return true;

        }
        return false;
    }

    private void addmusic(JSONObject music) {
        musics.add(music);
        savemusics(musics);
        updateListView(musics);

    }

    private void updateListView(ArrayList<JSONObject> music) {
        String[] CELLS = new String[music.size()];
        for (int i = 0; i < music.size(); i++) {
            try {
                CELLS[i] = music.get(i).getString("name");
            } catch (JSONException ex) {
            }
        }
        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.music_list_cell,
                R.id.text,
                CELLS));
    }

    public void savemusics(ArrayList<JSONObject> musicList) {
        try {
            JSONObject listWrapper = new JSONObject();
            JSONArray list = new JSONArray(musics);
            listWrapper.put("musics", list);
            String strList = listWrapper.toString();
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(music_FILENAME, Context.MODE_PRIVATE);
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
                inputStream = openFileInput(music_FILENAME);
                byte[] buffer = new byte[1024];

                while (inputStream.read(buffer) != -1) {
                    strList.append(new String(buffer));
                }
                inputStream.close();
            } catch (Exception e) {
            }

            JSONObject listWrapper = new JSONObject(strList.toString());
            JSONArray list = listWrapper.getJSONArray("musics");

            for (int i = 0; i < list.length(); i++) {
                musicList.add(list.getJSONObject(i));
            }
        } catch (JSONException ex) {
        }
        return musicList;
    }
    public static final String music_FILENAME = "mytracks.json";

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position < musics.size()) {
            JSONObject music = musics.get(position);

            Intent intent = new Intent(this, MusicDetailsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("music", music.toString());
            intent.putExtras(extras);
            startActivity(intent);

        }
    }

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
            String hash = "&hash=" + "ocEnZtsZrak-kgHnoqduuawNY7cO6hIlVg1xxWpGgH";
            String message = "&message=" + "Thanks for using the app - Yacub s11710945";
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

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
