package net.dumtoad.srednow7.controller;

import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Toast;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.dialog.PassThePhone;
import net.dumtoad.srednow7.fragment.GameFragment;
import net.dumtoad.srednow7.player.Player;

public class TurnController {
    private MasterViewController mvc;
    private TradeController tradeController;
    private int playerTurn;
    private boolean playDiscard;

    public TurnController(MasterViewController mvc) {
        this.mvc = mvc;
        tradeController = new TradeController(mvc, getCurrentPlayer());
    }

    public TurnController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.playerTurn = savedInstanceState.getInt("playerTurn");
        tradeController = new TradeController(mvc, getCurrentPlayer(), savedInstanceState.getBundle("tradeController"));
        this.playDiscard = savedInstanceState.getBoolean("playDiscard");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putInt("playerTurn", playerTurn);
        outstate.putBundle("tradeController", tradeController.getInstanceState());
        outstate.putBoolean("playDiscard", playDiscard);
        return outstate;
    }

    private Player getCurrentPlayer() {
        return mvc.getPlayer(playerTurn);
    }

    public int getCurrentPlayerNum() {
        return playerTurn;
    }

    public void startTurn(int playerNum, boolean playDiscard) {
        this.playDiscard = playDiscard;
        playerTurn = playerNum;
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
            Bundle args = new Bundle();
            args.putInt("playerTurn", playerTurn);
            args.putBoolean("playDiscard", playDiscard);
            gf.setArguments(args);
            mvc.getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_layout, gf, GameFragment.GAME_FRAGMENT_TAG)
                    .commit();
        }
    }

    public TradeController getTradeController() {
        return tradeController;
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
        } else if (getCurrentPlayer().hasOneFreeBuild() || hasCoupon || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card))) {
            int cardGoldCost = (tradeController.getTotalCost() * -1) - card.getCost().get(Card.Resource.GOLD);
            if (hasCoupon) cardGoldCost = 0;
            Hand hand;
            if (playDiscard) hand = mvc.getTableController().getDiscards();
            else hand = getCurrentPlayer().getHand();
            if (getCurrentPlayer().hasOneFreeBuild() && !(hasCoupon || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card)))) {
                getCurrentPlayer().spendFreeBuild();
                getCurrentPlayer().buildCard(card, 0, 0, 0, hand);
            } else {
                getCurrentPlayer().buildCard(card, cardGoldCost, tradeController.getCurrentCost(false), tradeController.getCurrentCost(true), hand);
            }
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

}
