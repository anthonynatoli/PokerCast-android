package com.davidtschida.android.cards;



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
 *
 */
public class HostFragment extends CastFragment implements OnMessageReceivedListener, OnCastConnectedListener{

    Button startButton;
    EditText player;
    EditText chip;

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_host, container, false);
        player = (EditText)rootView.findViewById(R.id.aiPlayer);
        chip = (EditText)rootView.findViewById(R.id.chips);
        startButton = (Button) rootView.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("command", "start_hand");
                    JSONObject content = new JSONObject();
                    content.put("aiPlayer", player.getText());
                    content.put("chipsPerPlayer", chip.getText());
                    msg.put("content", content);
                    host.getCastmanager().sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        //Toast.makeText(getActivity(), "MOOO "+json.toString(4), Toast.LENGTH_LONG).show();
        boolean success = false;
        try{
            if (json.get("command").equals("hand")) {
                json = json.getJSONObject("content");
                success = json.getBoolean("success");
            }
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
