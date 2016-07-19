package net.dumtoad.srednow7.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.util.Util;
import net.dumtoad.srednow7.view.HandView;
import net.dumtoad.srednow7.view.SummaryView;
import net.dumtoad.srednow7.view.TabletView;
import net.dumtoad.srednow7.view.TradeView;
import net.dumtoad.srednow7.view.WonderView;

public class GameFragment extends AbstractFragment implements MainActivity.LeftRightSwipe {

    public static final String GAME_FRAGMENT_TAG = "gameFragmentTag";

    protected int playerViewing;
    private Button west;
    private Button east;
    private Mode mode;
    private int playerTurn;
    private boolean playDiscard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) savedInstanceState = new Bundle(); //Avoid null pointers
        playerTurn = getArguments().getInt("playerTurn");
        playerViewing = savedInstanceState.getInt("playerViewing", playerTurn);
        playDiscard = getArguments().getBoolean("playDiscard");
        mode = Mode.valueOf(savedInstanceState.getString("mode", "handtrade"));

        this.mvc = MainActivity.getMasterViewController();
        final View view = inflater.inflate(R.layout.game_view, container, false);

        west = (Button) view.findViewById(R.id.west);
        west.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(true);
            }
        });

        east = (Button) view.findViewById(R.id.east);
        east.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(false);
            }
        });

        view.findViewById(R.id.wonder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToView(Mode.wonder);
            }
        });

        view.findViewById(R.id.summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToView(Mode.summary);
            }
        });

        view.findViewById(R.id.hand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToView(Mode.handtrade);
            }
        });

        MainActivity.getMainActivity().registerLeftRightSwipe(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putInt("playerViewing", playerViewing);
        outstate.putString("mode", mode.toString());
    }

    public boolean isPlayDiscard() {
        return playDiscard;
    }

    public void switchToView(Mode mode) {
        this.mode = mode;
        if (Util.isTablet()) {
            ((RelativeLayout) mvc.getActivity().findViewById(R.id.content))
                    .addView(new TabletView(mvc, playerTurn, playerViewing));
        } else {
            this.mode = mode;
            RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
            ViewGroup current = (ViewGroup) content.getChildAt(content.getChildCount() - 1);
            if (current == null) current = new RelativeLayout(mvc.getActivity());
            Util.animateCrossfade(content, current, getMode());
        }
    }

    protected ViewGroup getMode() {
        switch (mode) {
            case wonder:
                return new WonderView(mvc, playerTurn, playerViewing);
            case summary:
                return new SummaryView(mvc, playerTurn, playerViewing);
            case handtrade:
                return getHandTradeView();
            default:
                return null;
        }
    }

    public ViewGroup getHandTradeView() {
        if (playerViewing != mvc.getTableController().getPlayerDirection(playerTurn, true)
                && playerViewing != mvc.getTableController().getPlayerDirection(playerTurn, false)
                && playerViewing != playerTurn) {
            return new SummaryView(mvc, playerTurn, playerViewing);
        }

        if (playerTurn == playerViewing) {
            return new HandView(mvc, playerTurn);
        } else {
            return new TradeView(mvc, playerTurn, playerViewing);
        }
    }

    //west is true, east is false
    public void go(boolean direction) {
        goTo(mvc.getTableController().getPlayerDirection(playerViewing, direction));
    }

    public void goTo(int playerNum) {
        int distanceWest;
        int distanceEast;
        if(playerNum > playerViewing) {
            distanceWest = playerNum - playerViewing;
            distanceEast = mvc.getNumPlayers() - distanceWest;
        } else {
            distanceEast = playerViewing - playerNum;
            distanceWest = mvc.getNumPlayers() - distanceEast;
        }
        boolean direction = (distanceWest < distanceEast);
        playerViewing = playerNum;
        ViewGroup next;
        if (Util.isTablet())
            next = new TabletView(mvc, playerTurn, playerViewing);
        else next = getMode();
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        ViewGroup current = (ViewGroup) content.getChildAt(content.getChildCount() - 1);
        Util.animateTranslate(content, current, next, !direction);
    }

    public int getPlayerViewing() {
        return playerViewing;
    }

    @Override
    public void onResume() {
        super.onResume();
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        content.removeAllViews();
        switchToView(mode);
    }

    @Override
    public void swipeLeft() {
        east.performClick();
    }

    @Override
    public void swipeRight() {
        west.performClick();
    }

    public enum Mode {
        wonder,
        summary,
        handtrade
    }
}
