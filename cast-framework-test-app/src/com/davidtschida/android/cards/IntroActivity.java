package com.davidtschida.android.cards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.davidtschida.android.cards.R;
import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONObject;

public class IntroActivity extends Activity implements OnMessageReceivedListener, OnCastConnectedListener {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full page logo screen - remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);

        // Delay for 1 sec
        handler = new Handler();
        handler.postDelayed(irun, 1200);
    }

    Runnable irun = new Runnable() {
        public void run() {
            Intent i = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(i);
            finish();

            // Screen fade in & out effect
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro, menu);
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(irun);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCastConnected() {

    }

    @Override
    public void onCastDisconnected() {

    }

    @Override
    public void onMessageRecieved(JSONObject json) {

    }
}
