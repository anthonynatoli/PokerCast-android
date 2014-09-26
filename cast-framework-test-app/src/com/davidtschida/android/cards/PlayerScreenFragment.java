package com.davidtschida.android.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe Koncel on 9/15/2014.
 */
public class PlayerScreenFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {
    //EditText send;
    Button foldButton, checkButton, betButton;
    TextView receive;

    public PlayerScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_screen, container, false);
        //send = (EditText) rootView.findViewById(R.id.send);
        foldButton = (Button) rootView.findViewById(R.id.foldButton);
        foldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageOnClick("{ 'command':'fold' }");
            }
        });
        checkButton = (Button) rootView.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageOnClick("{ 'command':'check' }");
            }
        });
        betButton = (Button) rootView.findViewById(R.id.betButton);
        betButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageOnClick("{ 'command':'bet' }");
            }
        });
        receive = (TextView) rootView.findViewById(R.id.receive);
        return rootView;
    }

    public void sendMessageOnClick(String jsonToSend) {
        try {
            JSONObject o = new JSONObject(jsonToSend);
            host.getCastmanager().sendMessage(o);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        try {
            Toast.makeText(getActivity(), "MOOO " + json.toString(4), Toast.LENGTH_LONG).show();
            receive.setText(json.toString(4));
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCastDisconnected() {

    }

}
