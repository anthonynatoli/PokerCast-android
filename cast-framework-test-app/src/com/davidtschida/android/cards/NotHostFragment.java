package com.davidtschida.android.cards;



import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    SharedPreferences pref;
    ProgressDialog pd;

    public NotHostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_not_host, container, false);
        pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        pd = ProgressDialog.show(this.getActivity(), "Waiting", "Waiting for other players...");

        return rootView;
    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageRecieved(JSONObject json) {

        int chips = 0;
        String card1, card2;
        JSONObject content;

        try{


            content = json.getJSONObject("content");

            card1 = content.getString("card1");
            card2 = content.getString("card2");
            chips = content.getInt("chips");

            SharedPreferences.Editor edit = pref.edit();
            edit.putString("card1",card1);
            edit.putString("card2",card2);
            edit.putInt("chips", chips);
            edit.putString("hasTurn", "false");
            edit.putString("fromHelp", "false");
            edit.commit();
            //Server acknowledges it received the information

            pd.dismiss();

            HandFragment hf = new HandFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, hf);
            host.getCastmanager().setConnectedListener(hf);
            host.getCastmanager().setOnMessageRecievedListener(hf);
            //transaction.addToBackStack(null);
            transaction.commit();

        }
        catch (JSONException e){
            //Toast.makeText(getActivity(), "Server communication error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onCastDisconnected() {
        JoinFragment jf = new JoinFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, jf);
        host.getCastmanager().setConnectedListener(jf);
        host.getCastmanager().setOnMessageRecievedListener(jf);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
