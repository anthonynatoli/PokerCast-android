package com.davidtschida.android.cards;

/**
 * Created by Joe Koncel on 9/17/2014.
 */
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.MediaRouteButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe Koncel on 9/17/2014.
 */
public class JoinFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {
    Button sendButton;
    EditText playerName;
    SharedPreferences mPrefs;
    Boolean connected;
    //MediaRouteButton mrb;

    public JoinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.join_fragment, container, false);
        //mrb = (MediaRouteButton) rootView.findViewById(R.id.media_route_button);
        //host.getCastmanager().setMediaRouteActionProvider(mrb);
        connected = false;
        playerName = (EditText) rootView.findViewById(R.id.player_name);
        sendButton = (Button) rootView.findViewById(R.id.join_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), playerName.getText(), Toast.LENGTH_LONG).show();
                if (connected == false){
                    Toast.makeText(getActivity(), "Please first connect to a Chromecast", Toast.LENGTH_LONG).show();
                    return;
                }
                if (playerName.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject o = new JSONObject();
                    o.put("command", "join");
                    o.put("name", playerName.getText().toString());
                    host.getCastmanager().sendMessage(o);

                    //Uncomment this to test
                    /*HandFragment hf = new HandFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, hf);
                    //transaction.addToBackStack(null);
                    transaction.commit();*/

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(getResources().getColor(R.color.green_pressed), PorterDuff.Mode.DARKEN);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });

        mPrefs = getActivity().getSharedPreferences("data", 0);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        //Toast.makeText(getActivity(), "MOOO "+json.toString(4), Toast.LENGTH_LONG).show();
        boolean success, gameHost;
        String player_id;

        try{
            success = json.getBoolean("success");
        }
        catch (JSONException e){
            Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            success = false;
        }
        if (success){
            try {
                gameHost = json.getBoolean("host");
                player_id = json.getString("player_id");

                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("player_id", player_id);
                editor.commit();
            }
            catch (JSONException e){
                Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                gameHost = false;
            }
            if (gameHost){
                //Open the host fragment
                HostFragment hf = new HostFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, hf);
                //transaction.addToBackStack(null);
                transaction.commit();
            }
            else {
                //Open the not host fragment
                NotHostFragment nhf = new NotHostFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, nhf);
                //transaction.addToBackStack(null);
                transaction.commit();
            }
            //store player ID
        }
        else {
            Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
        connected = true;
    }
}
