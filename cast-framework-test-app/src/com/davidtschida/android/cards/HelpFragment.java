package com.davidtschida.android.cards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe Koncel on 9/29/2014.
 */
public class HelpFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    private SharedPreferences mPrefs;
    private String player_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        mPrefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        player_id = mPrefs.getString("player_id", null);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("fromHelp","true");
        edit.commit();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    HandFragment hf = new HandFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    host.getCastmanager().setConnectedListener(hf);
                    host.getCastmanager().setOnMessageRecievedListener(hf);

                    transaction.replace(R.id.content, hf);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                } else {
                    return false;
                }
            }
        });
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
        String command;

        // Message for turn
        String turnPlayerID;

        // Message for end_hand
        String winner_id;
        String winner_name;
        int pot_value;

        try {
            JSONObject content = json.getJSONObject("content");
            command = json.getString("command");

            if(command.equals("turn")) {
                // store command message in SharedPreference
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("hasTurn","true");
                edit.commit();

                turnPlayerID = content.getString("player_id");
                if (player_id != null && player_id.equals(turnPlayerID)) {
                    //It's my turn!
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(400);
                    Toast.makeText(getActivity(), "Your turn.", Toast.LENGTH_LONG).show();
                }


            } else if(command.equals("end_hand")) {
                //End_hand message
                winner_id = content.getString("winner_id");
                winner_name = content.getString("winner_name");
                pot_value = content.getInt("pot_value");

                AlertDialog.Builder winner = new AlertDialog.Builder(getActivity());
                winner.setTitle("Winner");
                winner.setMessage("The winner is " + winner_name + ".");

                winner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //JoinFragment jf = new JoinFragment();
                        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        //transaction.replace(R.id.content, jf);
                        //host.getCastmanager().setConnectedListener(jf);
                        //host.getCastmanager().setOnMessageRecievedListener(jf);
                        //transaction.commit();
                    }
                });
                winner.show();
            } 

        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
