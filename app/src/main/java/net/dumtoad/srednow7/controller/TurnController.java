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
    private Player player;
    private TradeController tradeController;

    public TurnController(MasterViewController mvc, Player player) {
        this.mvc = mvc;
        this.player = player;
        tradeController = new TradeController(mvc, player);
    }

    public TurnController(MasterViewController mvc, Player player, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.player = player;
        tradeController = new TradeController(mvc, player, savedInstanceState.getBundle("tradeController"));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putBundle("tradeController", tradeController.getInstanceState());
        return outstate;
    }

    public void startTurn(boolean playDiscard) {
        tradeController = new TradeController(mvc, player);
        if (mvc.getTableController().getNumHumanPlayers() > 1) {
            DialogFragment df = new PassThePhone();
            Bundle args = new Bundle();
            args.putString("name", player.getName());
            df.setArguments(args);
            df.show(mvc.getActivity().getFragmentManager(), "passthephone");
        }
        GameFragment gf = new GameFragment();
        Bundle args = new Bundle();
        args.putInt("playerTurn", mvc.getPlayerNum(player));
        args.putBoolean("playDiscard", playDiscard);
        gf.setArguments(args);
        mvc.getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, gf, GameFragment.GAME_FRAGMENT_TAG)
                .commit();
    }

    public TradeController getTradeController() {
        return tradeController;
    }

    public boolean requestDiscard(Card card) {
        player.discardCard(card);
        player.endTurn();
        return true;
    }

    public boolean requestWonder(Card card) {
        Card stage = player.nextWonderStage();
        if (stage == null) {
            Toast.makeText(mvc.getActivity(), "Already built all stages!", Toast.LENGTH_SHORT).show();
            System.out.println("Already built all stages!");
        } else if (tradeController.canAffordResources(stage) && tradeController.canAffordGold(stage)) {
            player.buildWonder(stage, card, (tradeController.getTotalCost() * -1) - stage.getCost().get(Card.Resource.GOLD),
                    tradeController.getCurrentCost(false), tradeController.getCurrentCost(true));
            player.endTurn();
            return true;
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
            System.out.println("Insufficient resources");
        }
        return false;
    }

    public boolean requestBuild(Card card) {
        boolean hasCoupon = player.hasCouponFor(card) || player.isPlayingDiscard();
        if (player.getPlayedCards().contains(card.getName())) {
            Toast.makeText(mvc.getActivity(), "Already built " + card.getNameString(), Toast.LENGTH_SHORT).show();
            System.out.println("Already built " + card.getNameString());
        } else if (hasCoupon && tradeController.hasTrade()) {
            if (player.isPlayingDiscard()) {
                Toast.makeText(mvc.getActivity(), "Don't trade, you can build for free", Toast.LENGTH_SHORT).show();
                System.out.println("Don't trade, you can build for free");
            } else {
                Toast.makeText(mvc.getActivity(), "Don't trade, you have a coupon", Toast.LENGTH_SHORT).show();
                System.out.println("Don't trade, you have a coupon");
            }
        } else if (tradeController.overpaid(card)) {
            Toast.makeText(mvc.getActivity(), "Overpaid, undo some trades", Toast.LENGTH_SHORT).show();
            System.out.println("Overpaid, undo some trades");
        } else if (player.hasOneFreeBuild() || hasCoupon || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card))) {
            int cardGoldCost = (tradeController.getTotalCost() * -1) - card.getCost().get(Card.Resource.GOLD);
            if (hasCoupon) cardGoldCost = 0;
            Hand hand;
            if (player.isPlayingDiscard()) hand = mvc.getTableController().getDiscards();
            else hand = player.getHand();
            if (player.hasOneFreeBuild() && !(hasCoupon || (tradeController.canAffordResources(card) && tradeController.canAffordGold(card)))) {
                player.spendFreeBuild();
                player.buildCard(card, 0, 0, 0, hand);
            } else {
                player.buildCard(card, cardGoldCost, tradeController.getCurrentCost(false), tradeController.getCurrentCost(true), hand);
            }
            player.endTurn();
            return true;
        } else {
            Toast.makeText(mvc.getActivity(), "Insufficient resources", Toast.LENGTH_SHORT).show();
            System.out.println("Insufficient resources");
        }
        return false;
    }

}
