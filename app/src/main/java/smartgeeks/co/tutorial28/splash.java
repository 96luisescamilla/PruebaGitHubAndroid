package smartgeeks.co.tutorial28;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nadxieli on 26/04/2018.
 */

public class splash extends AppCompatActivity {

    private  static final long SPLASH_TIME=4000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(splash.this, MainActivity.class);

                startActivity(mainIntent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task,SPLASH_TIME);
    }

}
