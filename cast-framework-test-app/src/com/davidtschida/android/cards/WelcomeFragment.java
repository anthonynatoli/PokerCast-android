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
 * Created by david on 7/27/14.
 */
public class WelcomeFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    EditText send;
    Button sendButton;
    TextView receive;

    public WelcomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome, container, false);
        send = (EditText) rootView.findViewById(R.id.send);
        sendButton = (Button) rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject o = new JSONObject(send.getText().toString());
                    host.getCastmanager().sendMessage(o);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        receive = (TextView) rootView.findViewById(R.id.receive);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        try {
            Toast.makeText(getActivity(), "MOOO "+json.toString(4), Toast.LENGTH_LONG).show();
            receive.setText(json.toString(4));
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }
}
