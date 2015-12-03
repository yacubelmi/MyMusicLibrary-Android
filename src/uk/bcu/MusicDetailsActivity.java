/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.bcu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.bcu.services.ServiceListener;
import uk.bcu.services.AbstractService;
import uk.bcu.services.MusicDetailsService;
import uk.bcu.services.ImageDownloadService;

/**
 *
 * @author Yacub
 */
public class MusicDetailsActivity extends Activity implements ServiceListener {

    private JSONObject music;
    private Thread thread;
    private Thread imageThread;
    private ImageView imgTrack;
    private TextView txtTitle;
    private TextView txtartist;
    private TextView txtDescriptionTitle;
    private TextView txtmbid;
    private TextView txtAlbum;
    private ImageView largeImg;
    private TextView txtlisteners;
    private TextView txtplaycount;

   
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setTitle("Track Detail Info");
        super.setTitleColor(Color.rgb(56, 172, 236));

        setContentView(R.layout.details);

        
        ImageView AlbumArt = (ImageView) findViewById(R.id.AlbumArt);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        // Now Set your animation
        AlbumArt.startAnimation(fadeInAnimation);
        
        txtTitle = (TextView) findViewById(R.id.AlbumTitle);
        txtartist = (TextView) findViewById(R.id.Artist);
        txtmbid = (TextView) findViewById(R.id.MbidDetails);
        txtDescriptionTitle = (TextView) findViewById(R.id.UrlDetails);
        txtAlbum = (TextView) findViewById(R.id.ReleaseDateDetails);
        txtlisteners = (TextView) findViewById(R.id.ListenersDetails);
        txtplaycount = (TextView) findViewById(R.id.PlaycountDetails);

        imgTrack = (ImageView) findViewById(R.id.AlbumArt);

        String strmusic = this.getIntent().getExtras().getString("music");
        if (strmusic != null) {
            try {
                music = new JSONObject(strmusic);
                super.setTitle(music.getString("name"));

                txtTitle.setText(music.getString("name"));
                txtartist.setText(music.getString("artist"));
                txtmbid.setText(music.getString("mbid"));

                String id = music.getString("mbid");
                MusicDetailsService service = new MusicDetailsService(id);
                service.addListener(this);
                thread = new Thread(service);
                thread.start();

                // music image
                String imagePath = this.getDir("MymusicLib",
                        Context.MODE_PRIVATE).getAbsolutePath()
                        + "/MymusicLib/" + id + ".png";
                File f = new File(imagePath);
                if (f.exists()) {
                    imgTrack.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                } else {
                    ImageDownloadService downloadService = new ImageDownloadService(
                            music.getJSONArray("image").getJSONObject(2).getString("#text"), imagePath);
                    downloadService.addListener(this);
                    imageThread = new Thread(downloadService);
                    imageThread.start();
                }
            } catch (JSONException ex) {
            }
        }
    }

    public void ServiceComplete(AbstractService service) {
        if (!service.hasError()) {
            if (service instanceof MusicDetailsService) {
                music = ((MusicDetailsService) service).getResult();

                try {

                    // JSONObject music1 = music.getJSONObject("track");
                    JSONObject album = music.getJSONObject("album");
                    JSONArray albumimg = album.getJSONArray("image");
                    JSONObject img = albumimg.getJSONObject(2);
                    //   JSONObject music2 = music1.getJSONObject("wiki");
                    String bic = album.getString("url");
                    String bic2 = album.getString("releasedate");
                    String bic3 = img.getString("#text");
                    String bic4 = album.getString("playcount");
                    String bic5 = album.getString("listeners");
                    txtDescriptionTitle.setText(bic);
                    txtAlbum.setText(bic2);
                    txtlisteners.setText(bic5);
                    txtplaycount.setText(bic4);

                } catch (JSONException ex) {
                }
            } else if (service instanceof ImageDownloadService) {
                File f = new File(((ImageDownloadService) service).getLocal());
                if (f.exists()) {
                    imgTrack.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.itmRefreash:
                Toast.makeText(MusicDetailsActivity.this, "Refreash", Toast.LENGTH_SHORT).show();
                try {
                    String id = music.getString("id");
                    MusicDetailsService service = new MusicDetailsService(id);
                    service.addListener(this);
                    thread = new Thread(service);
                    thread.start();

                    String imagePath = this.getDir("MymusicLib",
                            Context.MODE_PRIVATE).getAbsolutePath()
                            + "/MymusicLib/" + id + ".png";

                    ImageDownloadService downloadService = new ImageDownloadService(
                            music.getJSONArray("image").getJSONObject(2).getString("#text"), imagePath);
                    downloadService.addListener(this);
                    imageThread = new Thread(downloadService);
                    imageThread.start();

                } catch (JSONException ex) {
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
