package com.davidtschida.android.cards;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by david on 7/27/14.
 */
public class CastFragment extends Fragment {

    CastmanagerHost host;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof CastmanagerHost) {
            host = (CastmanagerHost) activity;
        } else throw new ClassCastException("Activity must implement CastmanagerHost!");
    }

    protected CastmanagerHost getCastManagerHost() {
        return host;
    }
}