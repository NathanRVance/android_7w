package net.dumtoad.srednow7.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.LeftRightSwipe;
import net.dumtoad.srednow7.ui.UIUtil;
import net.dumtoad.srednow7.ui.view.HandView;
import net.dumtoad.srednow7.ui.view.SummaryView;
import net.dumtoad.srednow7.ui.view.TabletView;
import net.dumtoad.srednow7.ui.view.TradeView;
import net.dumtoad.srednow7.ui.view.WonderView;

import static net.dumtoad.srednow7.ui.UIFacade.PLAYER_ID;

public class GameFragment extends Fragment implements LeftRightSwipe {

    private Player playerViewing;
    private Player playerTurn;
    private Button west;
    private Button east;
    private Mode mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) savedInstanceState = new Bundle(); //Avoid null pointers
        int pt = getArguments().getInt(PLAYER_ID);
        playerTurn = Bus.bus.getBackend().getPlayers().get(pt);
        playerViewing = Bus.bus.getBackend().getPlayers().get(savedInstanceState.getInt("playerViewing", pt));
        mode = Mode.valueOf(savedInstanceState.getString("mode", "handtrade"));

        final View view = inflater.inflate(R.layout.game_view, container, false);

        west = (Button) view.findViewById(R.id.west);
        west.setOnClickListener(v -> go(Backend.Direction.WEST));

        east = (Button) view.findViewById(R.id.east);
        east.setOnClickListener(v -> go(Backend.Direction.EAST));

        view.findViewById(R.id.wonder).setOnClickListener(v -> switchToView(Mode.wonder));

        view.findViewById(R.id.summary).setOnClickListener(v -> switchToView(Mode.summary));

        view.findViewById(R.id.hand).setOnClickListener(v -> switchToView(Mode.handtrade));

        MainActivity.getMainActivity().registerLeftRightSwipe(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putInt("playerViewing", Bus.bus.getBackend().getPlayers().indexOf(playerViewing));
        outstate.putString("mode", mode.toString());
    }

    private void switchToView(Mode mode) {
        this.mode = mode;
        if (UIUtil.isTablet()) {
            ((RelativeLayout) getActivity().findViewById(R.id.content))
                    .addView(new TabletView(getActivity(), playerTurn, playerViewing));
        } else {
            this.mode = mode;
            RelativeLayout content = (RelativeLayout) getActivity().findViewById(R.id.content);
            ViewGroup current = (ViewGroup) content.getChildAt(content.getChildCount() - 1);
            if (current == null) current = new RelativeLayout(getActivity());
            UIUtil.animateCrossfade(content, current, getMode());
        }
    }

    ViewGroup getMode() {
        switch (mode) {
            case wonder:
                return new WonderView(getActivity(), playerTurn, playerViewing);
            case summary:
                return new SummaryView(getActivity(), playerTurn, playerViewing);
            case handtrade:
                return getHandTradeView();
            default:
                return null;
        }
    }

    public ViewGroup getHandTradeView() {
        if (playerViewing != Bus.bus.getBackend().getPlayerDirection(playerTurn, Backend.Direction.WEST)
                && playerViewing != Bus.bus.getBackend().getPlayerDirection(playerTurn, Backend.Direction.EAST)
                && playerViewing != playerTurn) {
            return new SummaryView(getActivity(), playerTurn, playerViewing);
        }

        if (playerTurn == playerViewing) {
            return new HandView(getActivity(), playerTurn);
        } else {
            return new TradeView(getActivity(), playerTurn, playerViewing);
        }
    }

    private void go(Backend.Direction direction) {
        goTo(Bus.bus.getBackend().getPlayerDirection(playerViewing, direction));
    }

    public void goTo(Player player) {
        int distanceWest;
        int distanceEast;
        //Player indices are sorted west to east
        int playerNum = Bus.bus.getBackend().getPlayers().indexOf(player);
        int viewingNum = Bus.bus.getBackend().getPlayers().indexOf(playerViewing);
        int numPlayers = Bus.bus.getBackend().getPlayers().size();
        if (playerNum > viewingNum) {
            distanceEast = playerNum - viewingNum;
            distanceWest = numPlayers - distanceEast;
        } else {
            distanceWest = viewingNum - playerNum;
            distanceEast = numPlayers - distanceWest;
        }
        boolean direction = (distanceWest < distanceEast);
        playerViewing = player;
        ViewGroup next;
        if (UIUtil.isTablet())
            next = new TabletView(getActivity(), playerTurn, playerViewing);
        else next = getMode();
        RelativeLayout content = (RelativeLayout) getActivity().findViewById(R.id.content);
        ViewGroup current = (ViewGroup) content.getChildAt(content.getChildCount() - 1);
        UIUtil.animateTranslate(content, current, next, !direction);
    }

    public Player getPlayerViewing() {
        return playerViewing;
    }

    @Override
    public void onResume() {
        super.onResume();
        RelativeLayout content = (RelativeLayout) getActivity().findViewById(R.id.content);
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
