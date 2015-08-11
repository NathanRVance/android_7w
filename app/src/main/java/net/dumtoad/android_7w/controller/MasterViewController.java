package net.dumtoad.android_7w.controller;

import android.app.Activity;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.ai.AI;
import net.dumtoad.android_7w.cards.Database;
import net.dumtoad.android_7w.cards.Player;
import net.dumtoad.android_7w.cards.Wonder;
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

        players = new Player[numPlayers];

        for(int i = 0; i < numPlayers; i++) {
            Player player = new Player(this, ais[i], names[i]);
            players[i] = player;
            Wonder wonder = database.drawWonder();
            player.setWonder(wonder);

            if(player.isAI()) {
                player.setAI(new AI(player));
                player.getAI().setWonderSide(wonder);
            } else {
                WonderSelectFragment frag = new WonderSelectFragment();
                frag.setPlayerNum(i);
                activity.getFragmentManager().beginTransaction()
                        .replace(R.id.main_layout, frag, "WonderSelect")
                        .commit();
            }
        }
    }

    public Player getPlayer(int index) {
        if(index < players.length) {
            return players[index];
        }
        return null;
    }

}
