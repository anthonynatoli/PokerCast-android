package com.davidtschida.android.cards;



import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class NotHostFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    public NotHostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_not_host, container, false);
        ProgressDialog.show(this.getActivity(), "Loading", "Waiting for players...");

        return rootView;
    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        boolean success, host;
        try{
            success = json.getBoolean("success");
            //Server acknowledges it received the information
            if (success) {
                HandFragment hf = new HandFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, hf);
                //transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        catch (JSONException e){
            Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            success = false;
        }
    }
}
