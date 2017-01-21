package net.dumtoad.srednow7.bus;

import android.app.Fragment;
import android.os.Bundle;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.UI;
import net.dumtoad.srednow7.ui.dialog.GetResponseDialog;
import net.dumtoad.srednow7.ui.fragment.EndFragment;
import net.dumtoad.srednow7.ui.fragment.GameFragment;
import net.dumtoad.srednow7.ui.fragment.SetupFragment;
import net.dumtoad.srednow7.ui.fragment.WonderSelectFragment;

public class DisplayFactory {

    public static final String FRAGMENT_TAG = "fragment";
    public static final String PLAYER_ID = "PLAYERID";
    public static final String QUEUE_ID = "QUEUEID";
    public static final String TITLE = "TITLE";
    public static final String MESSAGE = "MESSAGE";
    public static final String VALID_RESPONSES = "VALID_RESPONSES";
    public static final String CARD_NAME = "CARD_NAME";

    public static UI.Display getDisplay(DisplayType type, int playerID) {
        FragmentDisplay ret = new FragmentDisplay();
        ret.displayType = type;
        ret.playerID = playerID;
        return ret;
    }

    public static UI.Display getDisplay(int playerID, String title, String message, String[] validResponses, String cardName) {
        DialogDisplay ret = new DialogDisplay();
        ret.playerID = playerID;
        ret.title = title;
        ret.message = message;
        ret.validResponses = validResponses;
        ret.cardName = cardName;
        return ret;
    }

    public static UI.Display getNothingDisplay() {
        return new DoNothingDisplay();
    }

    public enum DisplayType {
        Setup,
        WonderSideSelect,
        Turn,
        EndOfGame,
        Blank
    }

    private static class FragmentDisplay implements UI.Display {

        private int playerID;
        private DisplayType displayType;

        @Override
        public void show(Enum queueID) {
            Fragment frag;
            switch (displayType) {
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
                case Blank:
                    frag = new Fragment();
                    break;
                default:
                    throw new IllegalArgumentException("Fragment type " + displayType + " is undefined");
            }
            Bundle bundle = new Bundle();
            bundle.putInt(PLAYER_ID, playerID);
            bundle.putSerializable(QUEUE_ID, queueID);
            frag.setArguments(bundle);
            MainActivity.getMainActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, frag, FRAGMENT_TAG).commit();
        }
    }

    private static class DialogDisplay implements UI.Display {

        private int playerID;
        private String title;
        private String message;
        private String[] validResponses;
        private String cardName;

        @Override
        public void show(Enum queueID) {
            Bundle bundle = new Bundle();
            bundle.putInt(PLAYER_ID, playerID);
            bundle.putSerializable(QUEUE_ID, queueID);
            bundle.putString(TITLE, title);
            bundle.putString(MESSAGE, message);
            bundle.putStringArray(VALID_RESPONSES, validResponses);
            bundle.putString(CARD_NAME, cardName);
            GetResponseDialog dialog = new GetResponseDialog();
            dialog.setArguments(bundle);
            dialog.show(MainActivity.getMainActivity().getFragmentManager(), FRAGMENT_TAG);
        }
    }

    private static class DoNothingDisplay implements UI.Display {

        @Override
        public void show(Enum queueID) {

        }
    }
}
