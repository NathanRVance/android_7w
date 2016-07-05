package net.dumtoad.android_7w.controller;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;
import net.dumtoad.android_7w.cards.Hand;
import net.dumtoad.android_7w.player.Player;
import net.dumtoad.android_7w.dialog.PassThePhone;
import net.dumtoad.android_7w.fragment.WonderFragment;
import net.dumtoad.android_7w.view.CardView;

public class TurnController {
    private MasterViewController mvc;
    private TradeController tradeController;
    private int playerTurn;
    private int playerViewing;
    private Mode mode;

    private enum Mode {
        wonder,
        summary,
        handtrade
    }

    public TurnController(MasterViewController mvc) {
        this.mvc = mvc;
        tradeController = new TradeController(mvc);
        this.mode = Mode.wonder;
    }

    public TurnController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        tradeController = new TradeController(mvc, savedInstanceState.getBundle("tradeController"));
        this.playerTurn = savedInstanceState.getInt("playerTurn");
        this.playerViewing = savedInstanceState.getInt("playerViewing");
        this.mode = Mode.valueOf(savedInstanceState.getString("mode"));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putInt("playerTurn", playerTurn);
        outstate.putInt("playerViewing", playerViewing);
        outstate.putString("mode", mode.toString());
        outstate.putBundle("tradeController", tradeController.getInstanceState());
        return outstate;
    }

    public Player getCurrentPlayer() {
        return mvc.getPlayer(playerTurn);
    }

    public void startTurn(int playerNum) {
        mode = Mode.handtrade;
        tradeController = new TradeController(mvc);
        playerTurn = playerViewing = playerNum;
        if(mvc.getPlayer(playerNum).isAI()) {
            mvc.getPlayer(playerNum).getAI().doTurn();
        } else {
            if(mvc.getTableController().getNumHumanPlayers() > 1) {
                DialogFragment df = new PassThePhone();
                Bundle args = new Bundle();
                args.putString("name", mvc.getPlayer(playerNum).getName());
                df.setArguments(args);
                df.show(mvc.getActivity().getFragmentManager(), "passthephone");
            }
            WonderFragment wf = new WonderFragment();
            mvc.getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, wf, "WonderSelect")
                    .commit();
        }
    }

    //west is true, east is false
    public void go(boolean direction) {
        playerViewing = getPlayerDirection(playerViewing, direction);
        ScrollView next = showMode();
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        ScrollView current = (ScrollView) content.getChildAt(content.getChildCount()-1);
        mvc.animateTranslate(content, current, next, ! direction);
    }

    private int getPlayerDirection(int start, boolean direction) {
        if(direction) {
            return (start+1) % mvc.getNumPlayers();
        } else {
            start--;
            if(start < 0) {
                return mvc.getNumPlayers() - 1;
            }
        }
        return start;
    }

    private ScrollView showMode() {
        switch(mode) {
            case wonder: return showWonder();
            case summary: return showSummary();
            case handtrade: return showHand();
            default: return null;
        }
    }

    public void crossfadeToView(View view) {
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        ScrollView current = (ScrollView) content.getChildAt(content.getChildCount()-1);
        mvc.animateCrossfade(content, current, view);
    }

    public void setupForTurn() {
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
        ((Button)mvc.getActivity().findViewById(R.id.hand)).setText("Hand");
    }

    public void setupForView() {
        if(playerViewing == getPlayerDirection(playerTurn, true) || playerViewing == getPlayerDirection(playerTurn, false)) {
            mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
            ((Button)mvc.getActivity().findViewById(R.id.hand)).setText("Trade");
        } else
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.GONE);
    }

    public ScrollView showWonder() {
        mode = Mode.wonder;
        if(playerTurn == playerViewing) {
            setupForTurn();
        } else {
            setupForView();
        }
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(false);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);

        Player player = mvc.getPlayer(playerViewing);
        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(player.getWonder().getNameString());

        LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view,(RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        for(Card card : player.getWonderStages()) {
            CardView cv = new CardView(card, mvc.getActivity(), false);
            if(! player.getPlayedCards().contains(card)) {
                cv.setText(cv.getText() + " (not built)");
            }
            ll.addView(cv);
        }

        CardCollection cc = player.getPlayedCards();
        cc.sort();
        for(Card card : cc) {
            if(card.getType() == Card.Type.STAGE) continue;
            CardView cv = new CardView(card, mvc.getActivity(), false);
            ll.addView(cv);
        }

        return sv;
    }

    public ScrollView showSummary() {
        mode = Mode.summary;
        if(playerTurn == playerViewing) {
            setupForTurn();
        } else {
            setupForView();
        }
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(false);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);

        Player player = mvc.getPlayer(playerViewing);
        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(player.getWonder().getNameString());

        LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view,(RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        TextView tv = new TextView(mvc.getActivity());
        ll.addView(tv);
        tv.setText(player.getSummary());

        return sv;
    }

    public ScrollView showHand() {
        if(playerViewing != getPlayerDirection(playerTurn, true) && playerViewing != getPlayerDirection(playerTurn, false)
                && playerViewing != playerTurn) {
            ScrollView ret = showSummary();
            mode = Mode.handtrade;
            return ret;
        }
        mode = Mode.handtrade;
        if(playerTurn == playerViewing) {
            setupForTurn();
        } else {
            setupForView();
        }
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);

        Player player = mvc.getPlayer(playerViewing);
        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(player.getWonder().getNameString());

        LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view,(RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        if(playerTurn == playerViewing) {
            Hand hand = player.getHand();
            for (Card card : hand) {
                CardView cv = new CardView(card, mvc.getActivity(), true);
                ll.addView(cv);
            }
        } else {
            if(playerViewing == getPlayerDirection(playerTurn, true))
                tradeController.trade(ll, true);
            else
                tradeController.trade(ll, false);
        }

        return sv;
    }

    public void onComplete() {
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        content.removeAllViews();
        content.addView(showMode());
    }

    public void requestDiscard(Card card) {
        getCurrentPlayer().discardCard(card);
        endTurn();
    }

    public void requestWonder(Card card) {
        Card stage = getCurrentPlayer().nextWonderStage();
        if(stage == null) {
            Toast.makeText(mvc.getActivity(), "Already built all stages!", Toast.LENGTH_SHORT).show();
        } else if(tradeController.canAffordResources(stage) && tradeController.canAffordGold(card)) {
            getCurrentPlayer().buildWonder(stage, card, (tradeController.getTotalCost() * -1) - card.getCost().get(Card.Resource.GOLD),
                    tradeController.getCurrentCost(false), tradeController.getCurrentCost(true));
            endTurn();
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestBuild(Card card) {
        if(tradeController.canAffordResources(card) && tradeController.canAffordGold(card)) {
            getCurrentPlayer().buildCard(card, (tradeController.getTotalCost() * -1) - card.getCost().get(Card.Resource.GOLD),
                    tradeController.getCurrentCost(false), tradeController.getCurrentCost(true));
            endTurn();
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
        }
    }

    private void endTurn() {
        mvc.getTableController().nextPlayerStart();
    }

}
