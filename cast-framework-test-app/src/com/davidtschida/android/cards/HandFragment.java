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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private Button foldButton;
    private Button betButton;
    private Button hideButton;
    private TextView chipView;
    private String firstCard;
    private String secondCard;
    private int num_chip;
    private boolean isRotated;

    public HandFragment() {
    }
    public void setFirstCard(String name) {
        firstCard = name;
        //set image here
        
    }
    public void setSecondCard(String name) {
        secondCard = name;
        //set image here;

    }
    public void setChip(int num) {
        num_chip = num;
        chipView.setText("X "+num_chip);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hand, container, false);
        card1 = (ImageView) rootView.findViewById(R.id.card1);
        card2 = (ImageView) rootView.findViewById(R.id.card2);
        foldButton = (Button) rootView.findViewById(R.id.foldButton);
        betButton = (Button) rootView.findViewById(R.id.betButton);
        hideButton = (Button) rootView.findViewById(R.id.hideButton);
        chipView = (TextView) rootView.findViewById(R.id.num_chip);
        isRotated = false;



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRotated) {
                    card1.setRotation(-12.5f);
                    card2.setRotation(12.5f);
                    isRotated = true;
                }
                else {
                    card1.setRotation(0);
                    card2.setRotation(0);
                    isRotated = false;
                }
            }
        });
        foldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fold implementation
                Toast.makeText(getActivity(), "FOLD CLICKED", Toast.LENGTH_LONG).show();
            }
        });
        betButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bet Implementation
                Toast.makeText(getActivity(), "BET CLICKED", Toast.LENGTH_LONG).show();
            }
        });
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Implementation
                Toast.makeText(getActivity(), "HIDE  CLICKED", Toast.LENGTH_LONG).show();
            }
        });
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
