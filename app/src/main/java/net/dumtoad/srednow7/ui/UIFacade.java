package net.dumtoad.srednow7.ui;

import android.app.Fragment;
import android.os.Bundle;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.fragment.EndFragment;
import net.dumtoad.srednow7.ui.fragment.GameFragment;
import net.dumtoad.srednow7.ui.fragment.SetupFragment;
import net.dumtoad.srednow7.ui.fragment.WonderSelectFragment;

import java.util.LinkedList;
import java.util.Queue;

public class UIFacade implements UI {

    public static final String FRAGMENT_TAG = "fragment";
    public static final String PLAYER_ID = "playerID";

    private Queue<Fragment> fragmentQueue = new LinkedList<>();
    private boolean needsNewView = true;

    @Override
    public void displaySetup() {
        System.out.println("###### DISPLAYING SETUP ######");
        fragmentQueue.add(new SetupFragment());
        if (needsNewView)
            invalidateView();
    }

    @Override
    public void displayWonderSideSelect(int playerID) {
        System.out.println("###### DISPLAYING WONDER SIDE SELECT ######");
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYER_ID, playerID);
        WonderSelectFragment frag = new WonderSelectFragment();
        frag.setArguments(bundle);
        fragmentQueue.add(frag);
        if (needsNewView)
            invalidateView();
    }

    @Override
    public void displayTurn(int playerID) {
        System.out.println("###### DISPLAYING TURN ######");
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYER_ID, playerID);
        GameFragment frag = new GameFragment();
        frag.setArguments(bundle);
        fragmentQueue.add(frag);
        if (needsNewView)
            invalidateView();
    }

    @Override
    public void displayEndOfGame() {
        System.out.println("###### DISPLAYING END GAME ######");
        fragmentQueue.add(new EndFragment());
        if (needsNewView)
            invalidateView();
    }

    @Override
    public void invalidateView() {
        if(fragmentQueue.isEmpty()) {
            needsNewView = true;
        } else {
            MainActivity.getMainActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, fragmentQueue.remove(), FRAGMENT_TAG)
                    .commit();
            needsNewView = false;
        }
    }

    @Override
    public void reset() {
        fragmentQueue.clear();
        needsNewView = true;
    }
}
