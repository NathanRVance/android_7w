package net.dumtoad.android_7w.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;
import net.dumtoad.android_7w.cards.Player;
import net.dumtoad.android_7w.fragment.WonderFragment;
import net.dumtoad.android_7w.view.CardView;

public class TurnController {
    private MasterViewController mvc;
    private int playerTurn;
    private int playerViewing;
    private Mode mode;

    private enum Mode {
        wonder,
        summary,
        hand
    }

    public TurnController(MasterViewController mvc) {
        this.mvc = mvc;
        this.mode = Mode.wonder;
    }

    public TurnController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.playerTurn = savedInstanceState.getInt("playerTurn");
        this.playerViewing = savedInstanceState.getInt("playerViewing");
        this.mode = Mode.valueOf(savedInstanceState.getString("mode"));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putInt("playerTurn", playerTurn);
        outstate.putInt("playerViewing", playerViewing);
        outstate.putString("mode", mode.toString());
        return outstate;
    }

    public void startTurn(int playerNum) {
        this.playerTurn = this.playerViewing = playerNum;
        if(mvc.getPlayer(playerNum).isAI()) {
            //Do something cool!
        } else {
            WonderFragment wf = new WonderFragment();
            mvc.getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, wf, "WonderSelect")
                    .commit();
        }
    }

    //west is true, east is false
    public void go(boolean direction) {
        if(mode == Mode.hand)
            mode = Mode.wonder;
        if(direction) {
            playerViewing = (playerViewing+1) % mvc.getNumPlayers();
        } else {
            playerViewing--;
            if(playerViewing < 0) {
                playerViewing = mvc.getNumPlayers()-1;
            }
        }
        showMode();
    }

    private void showMode() {
        if(playerTurn == playerViewing) {
            setupForTurn();
        } else {
            setupForView();
        }
        switch(mode) {
            case wonder: showWonder();
                break;
            case summary: showSummary();
                break;
            case hand: showHand();
                break;
            default:
                break;
        }
    }

    public void setupForTurn() {
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
    }

    public void setupForView() {
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.GONE);
    }

    public void showWonder() {
        mode = Mode.wonder;
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(false);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);
        LinearLayout content = (LinearLayout) mvc.getActivity().findViewById(R.id.content);
        content.removeAllViews();
        Player player = mvc.getPlayer(playerViewing);
        CardCollection cc = new CardCollection();
        cc.addAll(player.getWonderStages());
        cc.addAll(player.getPlayedCards());
        cc.sort();
        for(Card card : cc) {
            CardView cv = new CardView(card, mvc.getActivity());
            content.addView(cv);
        }

        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(player.getWonder().getNameString());
    }

    public void showSummary() {
        mode = Mode.summary;
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(false);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);
    }

    public void showHand() {
        mode = Mode.hand;
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);
    }

    public void onComplete() {
        showMode();
    }

}
