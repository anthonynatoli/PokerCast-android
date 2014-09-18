package com.davidtschida.android.cards;

/**
 * Created by Joe Koncel on 9/17/2014.
 */
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    MediaRouteButton mrb;

    public JoinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.join_fragment, container, false);
        mrb = (MediaRouteButton) rootView.findViewById(R.id.media_route_button);
        host.getCastmanager().setMediaRouteActionProvider(mrb);
        sendButton = (Button) rootView.findViewById(R.id.join_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject o = new JSONObject("{ \"command\": \"join\" }");
                    host.getCastmanager().sendMessage(o);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        //Toast.makeText(getActivity(), "MOOO "+json.toString(4), Toast.LENGTH_LONG).show();
        boolean success, host;
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
                host = json.getBoolean("host");
            }
            catch (JSONException e){
                Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                host = false;
            }
            if (host){
                //Open the host fragment
            }
            else {
                //Open the "waiting for players fragment"
            }
            //store player ID
        }
        else {
            //Toast.makeText(getActivity(), "Connection busy, try again", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }
}
