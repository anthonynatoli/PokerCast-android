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
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private Button helpButton;
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

    private String hasTurn;
    private String fromHelp;

    private SharedPreferences mPrefs;
    public HandFragment() {
    }
    public void setFirstCard(String name) {
        Log.e("Setting card", "first");
        firstCard = name;
        //set image here
        Resources res = getResources();
        int resID = res.getIdentifier(firstCard, "drawable", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        card1.setImageDrawable(drawable);

    }
    public void setSecondCard(String name) {
        Log.e("Setting card", "second");
        secondCard = name;
        //set image here
        Resources res = getResources();
        int resID = res.getIdentifier(secondCard, "drawable", getActivity().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        card2.setImageDrawable(drawable);

    }
    public void setChip(int num) {
        Log.e("Setting Chip", "SETTING");
        num_chip = num;
        chipView.setText("X "+num_chip);
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt("chips", num);
        edit.commit();
    }
    public void disableButtons() {
        foldButton.setEnabled(false);
        betButton.setEnabled(false);
        turnBox.setEnabled(false);
        turnBox.setVisibility(View.INVISIBLE);
        foldButton.setBackgroundResource(R.drawable.grey_shape);
        betButton.setBackgroundResource(R.drawable.grey_shape);
        foldButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        betButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        betButton.getBackground().setAlpha(170);
        foldButton.getBackground().setAlpha(170);
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
        helpButton = (Button) rootView.findViewById(R.id.help_button);
        isRotated = false;
        isHidden = false;
        player_id = null;
        last_bet = 0;

        mPrefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPrefs.edit();

        // check if it has "turn" message
        hasTurn = mPrefs.getString("hasTurn", null);
        fromHelp = mPrefs.getString("fromHelp", null);

        //Toast.makeText(getActivity(), "OnCreate", Toast.LENGTH_LONG).show();

        if(fromHelp.equals("false")) {
            try {
                JSONObject msg = new JSONObject();
                msg.put("command", "hand_received");
                host.getCastmanager().sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //get Player_id from sharedPreference

        player_id = mPrefs.getString("player_id", null);
        if (mPrefs.getString("card1", null) != null) {
            Log.e("Card1", mPrefs.getString("card1", null));
            setFirstCard(mPrefs.getString("card1", null));
        }
        if (mPrefs.getString("card2", null) != null) {
            setSecondCard(mPrefs.getString("card2", null));
            Log.e("Card2", mPrefs.getString("card2", null));
        }
        setChip(mPrefs.getInt("chips", 0));
        Log.e("chip",mPrefs.getInt("chips", 0)+"");
        //Disable buttons unless it's my turn
        disableButtons();

        if(fromHelp.equals("false")) {
            disableButtons();
        }
        else {
            if(hasTurn.equals("true")) {
                enableButtons();
                edit.putString("hasTurn","false");
                edit.commit();
            }
            edit.putString("fromHelp","false");
            edit.commit();
        }

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
                            error.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
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
                                    try {
                                        JSONObject msg = new JSONObject();
                                        msg.put("command","my_turn");
                                        JSONObject content = new JSONObject();
                                        content.put("bet", Integer.parseInt(currentBet));
                                        msg.put("content", content);
                                        host.getCastmanager().sendMessage(msg);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    setChip(num_chip-Integer.parseInt(currentBet));
                                    disableButtons();
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
            }
        });
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation myRotation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotator);
                card1.startAnimation(myRotation);
                /*//Hide Implementation
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
                }*/
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpFragment hf = new HelpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, hf);
                host.getCastmanager().setConnectedListener(hf);
                host.getCastmanager().setOnMessageRecievedListener(hf);

                transaction.addToBackStack("hand");
                transaction.commit();
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
        int chips = 0;
        JSONObject content;

        try{
            content = json.getJSONObject("content");
            command = json.getString("command");
            //Turn message
            if(command.equals("turn")) {
                this.last_bet = content.getInt("last_bet");
                turnPlayerID = content.getString("player_id");
                if (player_id != null && player_id.equals(turnPlayerID)) {
                    //It's my turn!
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(400);
// need to be fixed ***********************************************************************************************************
                    /*
                    SharedPreferences.Editor edit = mPrefs.edit();
                    edit.putString("hasTurn", "true");
                    edit.commit();
                    */
// need to be fixed ***********************************************************************************************************

                    enableButtons();
                }
            }
            else if(command.equals("end_hand")) {
                //End_hand message
                winner_id = content.getString("winner_id");
                winner_name = content.getString("winner_name");
                pot_value = content.getInt("pot_value");

                AlertDialog.Builder winner = new AlertDialog.Builder(getActivity());
                winner.setTitle("Winner");
                winner.setMessage("The winner is "+winner_name+".");
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
            else if(command.equals("hand")) {
                content = json.getJSONObject("content");
                String card1 = content.getString("card1");
                String card2 = content.getString("card2");
                chips = content.getInt("chips");

                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("card1", card1);
                edit.putString("card2", card2);
                edit.putInt("chips", chips);
                edit.putString("hasTurn", "false");
                edit.putString("fromHelp", "false");
                edit.commit();

                setFirstCard(content.getString("card1"));
                setSecondCard(content.getString("card2"));
                setChip(content.getInt("chips"));

                try {
                    JSONObject msg = new JSONObject();
                    msg.put("command", "hand_received");
                    host.getCastmanager().sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
