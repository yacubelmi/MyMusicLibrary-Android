/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.bcu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 *
 * @author Yacub
 */
public class SplashScreen extends Activity {

    private static final int TIME = 4 * 1000;// 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this,
                        MainActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, TIME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, TIME);

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}
