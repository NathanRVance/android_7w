package net.dumtoad.android_7w.controller;

import android.app.Activity;
import android.os.Bundle;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.ai.AI;
import net.dumtoad.android_7w.cards.Database;
import net.dumtoad.android_7w.cards.Player;
import net.dumtoad.android_7w.fragment.SetupFragment;
import net.dumtoad.android_7w.fragment.WonderSelectFragment;

import java.util.ArrayList;

public class MasterViewController {

    private Activity activity;
    private Database database;
    private Player players[];
    private int numPlayers;
    private TableController tc;

    public MasterViewController(Activity activity) {
        this.activity = activity;
        tc = new TableController(this);
    }

    public TableController getTableController() {
        return tc;
    }

    public Activity getActivity() {
        return activity;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setup() {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new SetupFragment(), "Setup")
                .commit();
    }

    public void postSetup(String names[], boolean ais[], int numPlayers) {
        this.numPlayers = numPlayers;

        database = new Database(numPlayers);

        players = new Player[numPlayers];
        ArrayList<Integer> humanPlayerIndecies = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++) {
            players[i] = new Player(this, ais[i], names[i]);
            players[i].setWonder(database.drawWonder());

            if(players[i].isAI()) {
                players[i].setAI(new AI(players[i]));
            } else {
                humanPlayerIndecies.add(i);
            }
        }
        WonderSelectFragment frag = new WonderSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("PlayerNums", humanPlayerIndecies);
        frag.setArguments(bundle);
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, frag, "WonderSelect")
                .commit();
    }

    public Player getPlayer(int index) {
        if(index < players.length) {
            return players[index];
        }
        return null;
    }

    public void startMainGame() {
        tc.startEra();
    }

    public Database getDatabase() {
        return database;
    }

    public void onSaveInstanceState(Bundle outstate)
    {
        outstate.putInt("numPlayers", numPlayers);
        if(database != null) {
            outstate.putBoolean("databaseCreated", true);
            outstate.putBundle("database", database.getInstanceState());
            for(int i = 0; i < players.length; i++) {
                outstate.putBundle("player" + i, players[i].getInstanceState());
            }
            outstate.putBundle("tc", tc.getInstanceState());
        }
        else {
            outstate.putBoolean("databaseCreated", false);
        }
    }

    public void onRestoreInstanceState (Bundle savedInstanceState) {
        numPlayers = savedInstanceState.getInt("numPlayers");
        if(savedInstanceState.getBoolean("databaseCreated")) {
            database = new Database(numPlayers);
            database.onRestoreInstanceState(savedInstanceState);
            players = new Player[numPlayers];
            for(int i = 0; i < numPlayers; i++) {
                players[i] = new Player(this, savedInstanceState.getBundle("player" + i));
            }
            tc = new TableController(this, savedInstanceState.getBundle("tc"));
        }
    }

}
