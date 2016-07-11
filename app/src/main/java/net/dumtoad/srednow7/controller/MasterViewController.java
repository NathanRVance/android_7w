package net.dumtoad.srednow7.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Database;
import net.dumtoad.srednow7.fragment.EndFragment;
import net.dumtoad.srednow7.fragment.SetupFragment;
import net.dumtoad.srednow7.fragment.WonderSelectFragment;
import net.dumtoad.srednow7.player.Player;

import java.util.ArrayList;

public class MasterViewController {

    private Activity activity;
    private Database database;
    private Player players[] = new Player[3];
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
    }

    public void animateTranslate(final ViewGroup parent, final View current, final View next, boolean left) {
        //Animate the swap
        int duration = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);
        int viewWidth = parent.getWidth();

        //If there's a scrollbar, we want it invisible
        current.setVerticalScrollBarEnabled(false);

        TranslateAnimation ta;
        if(left) {
            ta = new TranslateAnimation(0, viewWidth * -1, 0, 0);
        } else {
            ta = new TranslateAnimation(0, viewWidth, 0, 0);
        }
        ta.setDuration(duration);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                parent.removeView(current);
            }
        }, ta.getDuration());
        current.startAnimation(ta);

        next.setVisibility(View.INVISIBLE);
        next.setVerticalScrollBarEnabled(false);
        parent.addView(next);
        if(left) {
            ta = new TranslateAnimation(viewWidth, 0, 0, 0);
        } else {
            ta = new TranslateAnimation(viewWidth * -1, 0, 0, 0);
        }
        ta.setDuration(duration);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                next.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(next instanceof ScrollView) {
                    next.setVerticalScrollBarEnabled(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        next.startAnimation(ta);
    }

    public void animateCrossfade(final ViewGroup parent, final View current, final View next) {
        int duration = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

        next.setAlpha(0f);
        parent.addView(next);
        next.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        current.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parent.removeView(current);
                    }
                });

    }

    public void endGame() {
        EndFragment endFrag = new EndFragment();
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, endFrag, "EndFragment")
                .commit();
    }

    public boolean isTablet()
    {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        return (metrics.widthPixels / metrics.density > 600);
    }

}
