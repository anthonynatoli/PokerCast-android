package com.davidtschida.android.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONObject;

/**
 * Created by Joe Koncel on 9/29/2014.
 */
public class HelpFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        return rootView;
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
