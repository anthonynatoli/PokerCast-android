package com.davidtschida.android.cards;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
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
 * A simple {@link Fragment} subclass.
 */
public class HostFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener {

    Button startButton;
    EditText player;
    EditText chip;
    SharedPreferences pref;

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_host, container, false);
        player = (EditText) rootView.findViewById(R.id.aiPlayer);
        chip = (EditText) rootView.findViewById(R.id.chips);
        startButton = (Button) rootView.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (player.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Enter the number of AI players", Toast.LENGTH_LONG).show();
                        // filter null input for # players
                    } else if (chip.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Enter the amount of chips", Toast.LENGTH_LONG).show();
                        // filter null input for chips
                    } else {
                        int p = Integer.parseInt(player.getText().toString());
                        int c = Integer.parseInt(chip.getText().toString());
                        // filter the wrong input from user.
                        if(p < 0 || p > 21) {
                            // 1) 1 <= # of AI player <= 21
                            Toast.makeText(getActivity(), "Please enter the valid input(# of AI players).", Toast.LENGTH_LONG).show();
                        } else if(c <= 0) {
                            // 2) chips cannot be negative
                            Toast.makeText(getActivity(), "Please enter the valid input(Amount of chips). ", Toast.LENGTH_LONG).show();
                        } else {

                            JSONObject msg = new JSONObject();
                            msg.put("command","start_hand");
                            JSONObject content = new JSONObject();
                            content.put("aiPlayer", p);
                            content.put("chipsPerPlayer", c);
                            host.getCastmanager().sendMessage(msg);
                            ProgressDialog.show(getActivity(), "Loading", "Waiting for players...");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
        }
    }

    );

    return rootView;
}


    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        //Toast.makeText(getActivity(), "MOOO "+json.toString(4), Toast.LENGTH_LONG).show();

        int chips;
        String card1, card2;
        JSONObject content;

        try {
            pref = getActivity().getSharedPreferences("data", 0);
            content = json.getJSONObject("content");
            chips = content.getInt("chips");
            card1 = content.getString("card1");
            card2 = content.getString("card2");

            SharedPreferences.Editor edit = pref.edit();
            edit.putString("card1", card1);
            edit.putString("card2", card2);
            edit.putInt("chips", chips);
            edit.commit();
            //Server acknowledges it received the information

            HandFragment hf = new HandFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, hf);
            //transaction.addToBackStack(null);
            transaction.commit();

        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
