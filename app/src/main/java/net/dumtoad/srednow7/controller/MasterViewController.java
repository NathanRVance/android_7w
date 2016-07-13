package net.dumtoad.srednow7.controller;

import android.app.Activity;
import android.os.Bundle;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Database;
import net.dumtoad.srednow7.fragment.EndFragment;
import net.dumtoad.srednow7.fragment.SetupFragment;
import net.dumtoad.srednow7.fragment.WonderSelectFragment;
import net.dumtoad.srednow7.player.Player;
import net.dumtoad.srednow7.util.Util;

import java.util.ArrayList;

public class MasterViewController {

    private Activity activity;
    private Database database;
    private Player players[] = new Player[3];
    private TableController tc;
    private boolean hasAutosave = false;

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
        return players.length;
    }

    public void setup() {
        tc = new TableController(this);
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, new SetupFragment(), "Setup")
                .commit();
    }

    public void postSetup(String names[], boolean ais[], int numPlayers) {

        database = new Database(numPlayers);

        players = new Player[numPlayers];
        ArrayList<Integer> humanPlayerIndecies = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++) {
            players[i] = new Player(this, ais[i], names[i]);
            players[i].setWonder(database.drawWonder());

            if(! players[i].isAI()) {
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
        return players[index];
    }

    public int getPlayerNum(Player player) {
        for(int i = 0; i < players.length; i++) {
            if(player == players[i]) return i;
        }
        return -1;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void startMainGame() {
        tc.startEra();
    }

    public Database getDatabase() {
        return database;
    }

    public void onSaveInstanceState(Bundle outstate)
    {
        outstate.putInt("numPlayers", players.length);
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
        outstate.putBoolean("hasAutosave", hasAutosave);
    }

    public void onRestoreInstanceState (Bundle savedInstanceState) {
        int numPlayers = savedInstanceState.getInt("numPlayers");
        if(savedInstanceState.getBoolean("databaseCreated")) {
            database = new Database(numPlayers);
            database.onRestoreInstanceState(savedInstanceState);
            players = new Player[numPlayers];
            for(int i = 0; i < numPlayers; i++) {
                players[i] = new Player(this, savedInstanceState.getBundle("player" + i));
            }
            tc = new TableController(this, savedInstanceState.getBundle("tc"));
        }
        hasAutosave = savedInstanceState.getBoolean("hasAutosave");
    }

    public void endGame() {
        EndFragment endFrag = new EndFragment();
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, endFrag, "EndFragment")
                .commit();
        hasAutosave = false;
        autosave();
    }

    public void deleteSave() {
        hasAutosave = false;
        Bundle save = new Bundle();
        onSaveInstanceState(save);
        Util.saveBundle(save);
    }

    public void autosave() {
        hasAutosave = true;
        Bundle save = new Bundle();
        onSaveInstanceState(save);
        Util.saveBundle(save);
    }

    public boolean hasAutosave() {
        Bundle bundle = Util.retrieveBundle();
        return bundle.getBoolean("hasAutosave", false);
    }

    public boolean restore() {
        try {
            Bundle bundle = Util.retrieveBundle();
            if (bundle.getBoolean("hasAutosave", false)) {
                onRestoreInstanceState(bundle);
                tc.getTurnController().startTurn(tc.getTurnController().getCurrentPlayerNum(), false);
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false; //Whoever called us will just start a regular new game now.
        }
        return false;
    }

}
