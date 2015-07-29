package net.dumtoad.android_7w.controller;

import android.app.Activity;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Database;
import net.dumtoad.android_7w.fragment.SetupFragment;
import net.dumtoad.android_7w.fragment.WonderSelectFragment;

/**
 * Created by nathav63 on 7/28/15.
 */
public class MasterViewController {

    private Activity activity;
    private Database database;
    private String names[];
    private boolean ais[];
    private int numPlayers;

    public MasterViewController(Activity activity) {
        this.activity = activity;
    }

    public void setup() {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new SetupFragment(), "Setup")
                .commit();
    }

    public void postSetup(String names[], boolean ais[], int numPlayers) {
        this.names = names;
        this.ais = ais;
        this.numPlayers = numPlayers;
        database = new Database(numPlayers);
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new WonderSelectFragment(), "WonderSelect")
                .commit();
    }

}
