package com.davidtschida.android.cards;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidtschida.android.cast.framework.OnCastConnectedListener;
import com.davidtschida.android.cast.framework.OnMessageReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Inhwan Lee on 9/17/2014.
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
    private boolean isHidden;

    private String player_id;
    private Button turnBox;

    private EditText betText;

    private int last_bet;

    private SharedPreferences mPrefs;
    public HandFragment() {
    }
    public void setFirstCard(String name) {
        firstCard = name;
        //set image here
        Resources res = getResources();
        int resID = res.getIdentifier(firstCard, "drawable", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        card1.setImageDrawable(drawable);

    }
    public void setSecondCard(String name) {
        secondCard = name;
        //set image here
        Resources res = getResources();
        int resID = res.getIdentifier(secondCard, "drawable", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        card2.setImageDrawable(drawable);

    }
    public void setChip(int num) {
        num_chip = num;
        chipView.setText("X "+num_chip);

    }
    public void disableButtons() {
        foldButton.setEnabled(false);
        betButton.setEnabled(false);
        hideButton.setEnabled(false);
        turnBox.setEnabled(false);
        turnBox.setVisibility(View.INVISIBLE);
        foldButton.setBackgroundResource(R.drawable.grey_shape);
        betButton.setBackgroundResource(R.drawable.grey_shape);
        hideButton.setBackgroundResource(R.drawable.grey_shape);
        foldButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        betButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        hideButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        betButton.getBackground().setAlpha(170);
        foldButton.getBackground().setAlpha(170);
        hideButton.getBackground().setAlpha(170);
    }
    public void enableButtons() {
        turnBox.setVisibility(View.VISIBLE);

        betButton.setEnabled(true);
        foldButton.setEnabled(true);
        hideButton.setEnabled(true);

        betButton.setBackgroundResource(R.drawable.blue_shape);
        foldButton.setBackgroundResource(R.drawable.red_shape);
        hideButton.setBackgroundResource(R.drawable.navy_shape);
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
        turnBox = (Button) rootView.findViewById(R.id.turnBox);
        isRotated = false;
        isHidden = false;
        player_id = null;
        last_bet = 0;

        //get Player_id from sharedPreference

        mPrefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        player_id = mPrefs.getString("player_id", null);
        if(mPrefs.getString("card1", null) != null)
            setFirstCard(mPrefs.getString("card1", null));
        else {
            //Log.e("Error", "Something is wrong with firstcard");
        }
        if(mPrefs.getString("card2", null) != null)
            setFirstCard(mPrefs.getString("card2", null));
        else {
            //Log.e("Error", "Something is wrong with Second card");
        }
        setChip(mPrefs.getInt("chips", 0));

        //Disable buttons unless it's my turn
        disableButtons();




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
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("command","my_turn");
                    JSONObject content = new JSONObject();
                    content.put("bet", -1);
                    msg.put("content",content);
                    host.getCastmanager().sendMessage(msg);

                } catch(JSONException e) {
                    e.printStackTrace();
                }
                disableButtons();
            }
        });
        betButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bet Implementation

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                LayoutInflater li = LayoutInflater.from(getActivity());
                View dialogView = li.inflate(R.layout.bet_edittext, null);

                alert.setTitle("BET");
                alert.setMessage("Choose your Bet!");
                alert.setView(dialogView);

                // Set an EditText view to get user input
                betText = (EditText) dialogView.findViewById(R.id.bet_text_for_dialog);
                betText.setText(last_bet+"");
                betText.setSelection(betText.getText().length()); // cursor at the end

                //soft keyboard shows up
                betText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(betText, 0);
                    }
                }, 50);

                betText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String currentBet = betText.getText().toString();

                        //hide keyboard
                        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(betText.getWindowToken(), 0);

                        if(Integer.parseInt(currentBet) < last_bet) {
                            // Ask confirmation for the bet amount
                            AlertDialog.Builder error = new AlertDialog.Builder(getActivity());
                            error.setTitle("BET");
                            error.setMessage("You should bet more than the last bet.");
                            error.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            error.show();
                        }
                        else {
                            // Ask confirmation for the bet amount
                            AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                            confirmation.setTitle("BET");
                            confirmation.setMessage("Are you sure you want to bet " + currentBet + "?");
                            confirmation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(getActivity(), "Betting " + currentBet, Toast.LENGTH_SHORT).show();
                                    try {
                                        JSONObject msg = new JSONObject();
                                        msg.put("command","my_turn");
                                        JSONObject content = new JSONObject();
                                        content.put("bet", Integer.parseInt(currentBet));
                                        msg.put("content",content);
                                        host.getCastmanager().sendMessage(msg);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            confirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });
                            confirmation.show();
                        }

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
                disableButtons();
            }
        });
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Implementation
                if(!isHidden) {
                    hideButton.setText("UNDO");
                    card1.setVisibility(View.INVISIBLE);
                    card2.setVisibility(View.INVISIBLE);
                    isHidden = true;
                }
                else {
                    hideButton.setText("HIDE");
                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                    isHidden = false;
                }
            }
        });
    }

    @Override
    public void onMessageRecieved(JSONObject json) {
        String command;

        //Message for turn
        String turnPlayerID;

        //Message for end_hand
        String winner_id;
        String winner_name;
        int pot_value;




        try{
            JSONObject content = json.getJSONObject("content");
            //Log.e("message", "received");
            command = json.getString("command");
            //Turn message
            if(command.equals("turn")) {
                //Log.e("Turn", "Received");
                this.last_bet = content.getInt("last_bet");
                turnPlayerID = content.getString("player_id");
                //Log.e("Player", player_id);
                //Log.e("turn Player", turnPlayerID);
                if (player_id != null && player_id.equals(turnPlayerID)) {
                    //It's my turn!
                    enableButtons();
                }
            }
            else if(command.equals("end_hand")) {
                //End_hand message
                winner_id = content.getString("winner_id");
                winner_name = content.getString("winner_name");
                pot_value = content.getInt("pot_value");

                //Log.e("Winner", winner_name);
                //Log.e("Pot Value", pot_value+"");
            }


        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCastConnected() {
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
    }
}
