package net.dumtoad.srednow7.controller;

import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.CardCollection;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.dialog.PassThePhone;
import net.dumtoad.srednow7.fragment.GameFragment;
import net.dumtoad.srednow7.player.Player;
import net.dumtoad.srednow7.util.Util;
import net.dumtoad.srednow7.view.CardView;

public class TurnController {
    private MasterViewController mvc;
    private TradeController tradeController;
    private int playerTurn;
    private int playerViewing;
    private Mode mode;
    private boolean playDiscard;

    public TurnController(MasterViewController mvc) {
        this.mvc = mvc;
        tradeController = new TradeController(mvc, getCurrentPlayer());
        this.mode = Mode.wonder;
    }

    public TurnController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.playerTurn = savedInstanceState.getInt("playerTurn");
        tradeController = new TradeController(mvc, getCurrentPlayer(), savedInstanceState.getBundle("tradeController"));
        this.playerViewing = savedInstanceState.getInt("playerViewing");
        this.mode = Mode.valueOf(savedInstanceState.getString("mode"));
        this.playDiscard = savedInstanceState.getBoolean("playDiscard");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putInt("playerTurn", playerTurn);
        outstate.putInt("playerViewing", playerViewing);
        outstate.putString("mode", mode.toString());
        outstate.putBundle("tradeController", tradeController.getInstanceState());
        outstate.putBoolean("playDiscard", playDiscard);
        return outstate;
    }

    public Player getCurrentPlayer() {
        return mvc.getPlayer(playerTurn);
    }

    public int getCurrentPlayerNum() {
        return playerTurn;
    }

    public void startTurn(int playerNum, boolean playDiscard) {
        this.playDiscard = playDiscard;
        mode = Mode.handtrade;
        playerTurn = playerViewing = playerNum;
        tradeController = new TradeController(mvc, getCurrentPlayer());
        if (mvc.getPlayer(playerNum).isAI()) {
            mvc.getPlayer(playerNum).getAI().doTurn(playDiscard);
        } else {
            if (mvc.getTableController().getNumHumanPlayers() > 1) {
                DialogFragment df = new PassThePhone();
                Bundle args = new Bundle();
                args.putString("name", mvc.getPlayer(playerNum).getName());
                df.setArguments(args);
                df.show(mvc.getActivity().getFragmentManager(), "passthephone");
            }
            mvc.autosave(); //Autosave now, when the user is distracted, so a slight lag is less noticeable.
            GameFragment gf = new GameFragment();
            mvc.getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, gf, "WonderSelect")
                    .commit();
        }
    }

    public TradeController getTradeController() {
        return tradeController;
    }

    //west is true, east is false
    public void go(boolean direction) {
        playerViewing = getPlayerDirection(playerViewing, direction);
        ViewGroup next;
        if (Util.isTablet()) next = getTabletView();
        else next = getMode();
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        ViewGroup current = (ViewGroup) content.getChildAt(content.getChildCount() - 1);
        Util.animateTranslate(content, current, next, !direction);
    }

    private int getPlayerDirection(int start, boolean direction) {
        if (direction) {
            return (start + 1) % mvc.getNumPlayers();
        } else {
            start--;
            if (start < 0) {
                return mvc.getNumPlayers() - 1;
            }
        }
        return start;
    }

    private ScrollView getMode() {
        switch (mode) {
            case wonder:
                return getWonder();
            case summary:
                return getSummary();
            case handtrade:
                return getHand();
            default:
                return null;
        }
    }

    public void switchToView(Mode mode) {
        if (Util.isTablet()) {
            ((RelativeLayout) mvc.getActivity().findViewById(R.id.content)).addView(getTabletView());
        } else {
            this.mode = mode;
            crossfadeToView(getMode());
        }
    }

    private LinearLayout getTabletView() {
        LayoutInflater inflater = mvc.getActivity().getLayoutInflater();
        LinearLayout content = (LinearLayout) inflater.inflate(R.layout.tablet_view, (RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        ScrollView wonder = getWonder();
        ((RelativeLayout) content.findViewById(R.id.wonder_view)).addView(wonder);
        ScrollView summary = getSummary();
        ((RelativeLayout) content.findViewById(R.id.summary_view)).addView(summary);
        if (playerViewing != getPlayerDirection(playerTurn, true) && playerViewing != getPlayerDirection(playerTurn, false)
                && playerViewing != playerTurn) {
            content.removeView(content.findViewById(R.id.hand_view));
        } else {
            ScrollView hand = getHand();
            ((RelativeLayout) content.findViewById(R.id.hand_view)).addView(hand);
        }
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(false);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(false);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);
        return content;
    }

    private void crossfadeToView(View view) {
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        ScrollView current = (ScrollView) content.getChildAt(content.getChildCount() - 1);
        if(current == null) current = new ScrollView(mvc.getActivity());
        Util.animateCrossfade(content, current, view);
    }

    private void setupForTurn() {
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
        ((Button) mvc.getActivity().findViewById(R.id.hand)).setText(mvc.getActivity().getString(R.string.hand));
    }

    private void setupForView() {
        if (playerViewing == getPlayerDirection(playerTurn, true) || playerViewing == getPlayerDirection(playerTurn, false)) {
            mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
            ((Button) mvc.getActivity().findViewById(R.id.hand)).setText(mvc.getActivity().getString(R.string.trade));
        } else
            mvc.getActivity().findViewById(R.id.hand).setVisibility(View.GONE);
    }

    private ScrollView getWonder() {
        mode = Mode.wonder;
        if (playerTurn == playerViewing) {
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
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view, (RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        for (Card card : player.getWonderStages()) {
            CardView cv = new CardView(card, mvc.getActivity(), player, false, playDiscard);
            if (!player.getPlayedCards().contains(card)) {
                cv.setText(mvc.getActivity().getString(R.string.not_built, cv.getText()));
            }
            ll.addView(cv);
        }

        CardCollection cc = player.getPlayedCards();
        cc.sort();
        for (Card card : cc) {
            if (card.getType() == Card.Type.STAGE) continue;
            CardView cv = new CardView(card, mvc.getActivity(), player, false, playDiscard);
            ll.addView(cv);
        }

        return sv;
    }

    private ScrollView getSummary() {
        mode = Mode.summary;
        if (playerTurn == playerViewing) {
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
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view, (RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        TextView tv = new TextView(mvc.getActivity());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));
        ll.addView(tv);
        tv.setText(player.getSummary());

        return sv;
    }

    private ScrollView getHand() {
        if (playerViewing != getPlayerDirection(playerTurn, true) && playerViewing != getPlayerDirection(playerTurn, false)
                && playerViewing != playerTurn) {
            ScrollView ret = getSummary();
            mode = Mode.handtrade;
            return ret;
        }
        mode = Mode.handtrade;
        if (playerTurn == playerViewing) {
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
        ScrollView sv = (ScrollView) inflater.inflate(R.layout.content_view, (RelativeLayout) mvc.getActivity().findViewById(R.id.content), false);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);

        if (playerTurn == playerViewing) {

            TextView tv = new TextView(mvc.getActivity());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));
            ll.addView(tv);
            String direction = (mvc.getTableController().getPassingDirection())?
                    mvc.getActivity().getResources().getString(R.string.west)
                    : mvc.getActivity().getResources().getString(R.string.east);
            tv.setText(mvc.getActivity().getResources().getString(R.string.passing_direction, direction));

            Hand hand;
            if (playDiscard) {
                hand = mvc.getTableController().getDiscards();
            } else {
                hand = player.getHand();
            }
            for (Card card : hand) {
                CardView cv = new CardView(card, mvc.getActivity(), player, true, playDiscard);
                if (!(playDiscard || getCurrentPlayer().hasCouponFor(card) || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card)))) {
                    //Darken it slightly
                    cv.getBackground().setColorFilter(ContextCompat.getColor(mvc.getActivity(), R.color.gray), PorterDuff.Mode.MULTIPLY);
                }
                ll.addView(cv);
            }
        } else {
            if (playerViewing == getPlayerDirection(playerTurn, true))
                tradeController.trade(ll, true);
            else
                tradeController.trade(ll, false);
        }

        return sv;
    }

    public void onComplete() {
        RelativeLayout content = (RelativeLayout) mvc.getActivity().findViewById(R.id.content);
        content.removeAllViews();
        switchToView(mode);
    }

    public boolean requestDiscard(Card card) {
        getCurrentPlayer().discardCard(card);
        endTurn();
        return true;
    }

    public boolean requestWonder(Card card) {
        Card stage = getCurrentPlayer().nextWonderStage();
        if (stage == null) {
            Toast.makeText(mvc.getActivity(), "Already built all stages!", Toast.LENGTH_SHORT).show();
            System.out.println("Already built all stages!");
        } else if (tradeController.canAffordResources(stage) && tradeController.canAffordGold(stage)) {
            getCurrentPlayer().buildWonder(stage, card, (tradeController.getTotalCost() * -1) - stage.getCost().get(Card.Resource.GOLD),
                    tradeController.getCurrentCost(false), tradeController.getCurrentCost(true));
            endTurn();
            return true;
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
            System.out.println("Insufficient resources");
        }
        return false;
    }

    public boolean requestBuild(Card card) {
        boolean hasCoupon = getCurrentPlayer().hasCouponFor(card) || playDiscard;
        if (getCurrentPlayer().getPlayedCards().contains(card.getName())) {
            Toast.makeText(mvc.getActivity(), "Already built " + card.getNameString(), Toast.LENGTH_SHORT).show();
            System.out.println("Already built " + card.getNameString());
        } else if (hasCoupon && tradeController.hasTrade()) {
            if (playDiscard) {
                Toast.makeText(mvc.getActivity(), "Don't trade, you can build for free", Toast.LENGTH_SHORT).show();
                System.out.println("Don't trade, you can build for free");
            } else {
                Toast.makeText(mvc.getActivity(), "Don't trade, you have a coupon", Toast.LENGTH_SHORT).show();
                System.out.println("Don't trade, you have a coupon");
            }
        } else if (tradeController.overpaid(card)) {
            Toast.makeText(mvc.getActivity(), "Overpaid, undo some trades", Toast.LENGTH_SHORT).show();
            System.out.println("Overpaid, undo some trades");
        } else if (hasCoupon || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card))) {
            int cardGoldCost = (tradeController.getTotalCost() * -1) - card.getCost().get(Card.Resource.GOLD);
            if (hasCoupon) cardGoldCost = 0;
            Hand hand;
            if (playDiscard) hand = mvc.getTableController().getDiscards();
            else hand = getCurrentPlayer().getHand();
            getCurrentPlayer().buildCard(card, cardGoldCost, tradeController.getCurrentCost(false), tradeController.getCurrentCost(true), hand);
            endTurn();
            return true;
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
            System.out.println("Insufficient resources");
        }
        return false;
    }

    private void endTurn() {
        playDiscard = false;
        mvc.getTableController().nextPlayerStart();
    }

    public enum Mode {
        wonder,
        summary,
        handtrade
    }

}
