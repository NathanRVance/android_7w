package net.dumtoad.srednow7.ui;

import android.app.Fragment;
import android.os.Bundle;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.fragment.EndFragment;
import net.dumtoad.srednow7.ui.fragment.GameFragment;
import net.dumtoad.srednow7.ui.fragment.SetupFragment;
import net.dumtoad.srednow7.ui.fragment.WonderSelectFragment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class UIFacade implements UI {

    public static final String FRAGMENT_TAG = "fragment";
    public static final String PLAYER_ID = "playerID";
    private static final long serialVersionUID = -2416669026990337029L;

    private Queue<Display> displayQueue = new LinkedList<>();
    private Display currentDisplay;
    private boolean needsNewView = true;

    @Override
    public void display(DisplayType type, int playerID) {
        Display display = new Display();
        display.type = type;
        display.playerID = playerID;
        displayQueue.add(display);
        if(needsNewView)
            invalidateView();
    }

    private Fragment getFragment(Display displayObject) {
        Fragment frag;
        switch (displayObject.type) {
            case Setup:
                frag = new SetupFragment();
                break;
            case WonderSideSelect:
                frag = new WonderSelectFragment();
                break;
            case Turn:
                frag = new GameFragment();
                break;
            case EndOfGame:
                frag = new EndFragment();
                break;
            default:
                throw new IllegalArgumentException("Fragment type " + displayObject.type + " is undefined");
        }
        Bundle bundle = new Bundle();
        bundle.putInt(PLAYER_ID, displayObject.playerID);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void invalidateView() {
        if (displayQueue.isEmpty()) {
            needsNewView = true;
            //Throw up a blank fragment
            MainActivity.getMainActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, new Fragment(), FRAGMENT_TAG)
                    .commit();
        } else {
            currentDisplay = displayQueue.remove();
            MainActivity.getMainActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, getFragment(currentDisplay), FRAGMENT_TAG)
                    .commit();
            needsNewView = false;
        }
    }

    @Override
    public void reset() {
        displayQueue.clear();
        currentDisplay = null;
        needsNewView = true;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if(currentDisplay != null) {
            displayQueue.add(currentDisplay);
            invalidateView();
        }
    }

    private static class Display implements Serializable {

        private static final long serialVersionUID = 4767541851689518389L;
        DisplayType type;
        int playerID;

        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
        }
    }
}
