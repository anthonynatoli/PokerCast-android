package com.davidtschida.android.cards;

/**
 * Created by Joe Koncel on 9/17/2014.
 */
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe Koncel on 9/17/2014.
 */
public class HandFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {
    private ImageView card1;
    private ImageView card2;

    public HandFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hand, container, false);
        card1 = (ImageView) rootView.findViewById(R.id.card1);
        card2 = (ImageView) rootView.findViewById(R.id.card2);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        card1.setRotation(-12.5f);
        card2.setRotation(12.5f);

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
