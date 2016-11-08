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

    /**
     * Useful info for later:
     * Because Bus doesn't get recreated, neither does this class. Which means we may be able to
     * cheat a little on screen rotations, but we'll also have to be careful on how we handle
     * saving and loading games.
     */

    public static final String FRAGMENT_TAG = "fragment";
    public static final String PLAYER_ID = "playerID";

    private Queue<Fragment> fragmentQueue = new LinkedList<>();
    private boolean viewIsInvalid = true;

    @Override
    public void displaySetup() {
        fragmentQueue.add(new SetupFragment());
        if(viewIsInvalid)
            invalidateView();
    }

    @Override
    public void displayWonderSideSelect(int playerID) {
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYER_ID, playerID);
        WonderSelectFragment frag = new WonderSelectFragment();
        frag.setArguments(bundle);
        fragmentQueue.add(frag);
        if(viewIsInvalid)
            invalidateView();
    }

    @Override
    public void displayTurn(int playerID) {
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYER_ID, playerID);
        GameFragment frag = new GameFragment();
        frag.setArguments(bundle);
        fragmentQueue.add(frag);
        if(viewIsInvalid)
            invalidateView();
    }

    @Override
    public void displayEndOfGame() {
        fragmentQueue.add(new EndFragment());
        if(viewIsInvalid)
            invalidateView();
    }

    @Override
    public void invalidateView() {
        Fragment frag = fragmentQueue.poll();
        if(frag == null) {
            viewIsInvalid = true;
        } else {
            MainActivity.getMainActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, frag, FRAGMENT_TAG)
                    .commit();
            viewIsInvalid = false;
        }
    }
}
