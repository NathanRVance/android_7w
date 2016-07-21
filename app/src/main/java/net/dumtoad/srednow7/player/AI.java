package net.dumtoad.srednow7.player;

import android.os.Bundle;
import android.support.annotation.NonNull;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.Generate;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.cards.ResQuant;
import net.dumtoad.srednow7.cards.Special;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.controller.TradeController;
import net.dumtoad.srednow7.controller.TurnController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AI {

    private Player player;
    private MasterViewController mvc;
    private boolean scientist = false;

    public enum Action {
        build,
        wonder,
        discard
    }

    public AI(Player player) {
        this.player = player;
        player.setWonderSide(new Random().nextBoolean());
        mvc = MainActivity.getMasterViewController();
    }

    public AI(Player player, Bundle bundle) {
        this.player = player;
        mvc = MainActivity.getMasterViewController();
        scientist = bundle.getBoolean("scientist");
    }

    public Bundle getInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("scientist", scientist);
        return bundle;
    }

    public void updateScientist() {
        if(player.getWonder().getResource() == Card.Resource.CLOTH
                || player.getWonder().getResource() == Card.Resource.PAPER
                || player.getWonder().getResource() == Card.Resource.GLASS
                || player.getWonder().getName() == Generate.Wonders.The_Hanging_Gardens_of_Babylon) {
            scientist = true;
        }
    }

    public void doTurn(boolean playDiscard, TurnController turnController) {
        Hand hand;
        if(playDiscard) {
            hand = mvc.getTableController().getDiscards();
            if(hand.size() == 0) return;
        } else {
            hand = player.getHand();
        }
        ArrayList<CardAction> actions = new ArrayList<>();
        for(Action action : (playDiscard)? new Action[]{Action.build} : Action.values()) {
            for(Card card : hand) {
                actions.add(calcCardAction(card, action, playDiscard));
            }
        }
        Collections.sort(actions);
        doAction(actions.get(0), turnController);
    }

    private void doAction(CardAction cardAction, TurnController turnController) {
        turnController.getTradeController().setTrades(cardAction.tradeEast, cardAction.tradeWest);
        switch(cardAction.action) {
            case build:
                if(! turnController.requestBuild(cardAction.card)) {
                    throw new RuntimeException("Error building card " + cardAction.card.getNameString() + cardAction.weight);
                }
                break;
            case wonder:
                if(! turnController.requestWonder(cardAction.card)) {
                    throw new RuntimeException("Error building wonder " + cardAction.weight);
                }
                break;
            case discard:
                if(! turnController.requestDiscard(cardAction.card)) {
                    throw new RuntimeException("Error discarding card");
                }
                break;
        }
    }

    private CardAction calcCardAction(Card card, Action action, boolean playDiscard) {
        CardAction cardAction = new CardAction(card);
        cardAction.action = action;
        switch(action) {
            case build: calcBuild(cardAction, player, playDiscard);
                break;
            case wonder: calcWonder(cardAction);
                break;
            case discard: calcDiscard(cardAction);
                break;
            default:
                break;
        }
        if(cardAction.weight != -1) {
            addNextEffect(cardAction, player);
        }
        return cardAction;
    }

    private void calcBuild(CardAction cardAction, Player player, boolean playDiscard) {
        if(player.getPlayedCards().contains(cardAction.card.getName())) {
            cardAction.weight = -1;
            return;
        }

        if(! playDiscard && ! player.hasCouponFor(cardAction.card)) {
            //Trade cost
            int cost = getTradeGoldCost(cardAction, player);
            if (cost == -1 || cost + cardAction.card.getCost().get(Card.Resource.GOLD) > player.getGold()) {
                cardAction.weight = -1;
                return;
            } else cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
        }

        ResQuant prod = player.getRawProduction();

        //Tradeable resources get 5 for what we don't have, 1 for what we do.
        for(Card.Resource res : TradeController.tradeable) {
            int mult = (prod.get(res) > 0)? 1 : 5;
            cardAction.weight += cardAction.card.getProducts().get(res) * mult;
        }

        //Gold gets 1 for every 3 gold
        int gold = cardAction.card.getProducts().get(Card.Resource.GOLD);
        if(Special.isSpecialGold(cardAction.card, player)) {
            gold += Special.getSpecialGold(cardAction.card, player);
        }
        cardAction.weight += gold / 3;

        //VPs get 1 weight each
        cardAction.weight += cardAction.card.getProducts().get(Card.Resource.VP);
        if(Special.isSpecialVps(cardAction.card, player)) {
            cardAction.weight += Special.getSpecialVps(cardAction.card, player);
        }

        //Military is worth 2 for each shield for each battle it will turn the tide in, 1(ish) otherwise
        int shields = cardAction.card.getProducts().get(Card.Resource.SHIELD);
        if(shields > 0) {
            int myShields = prod.get(Card.Resource.SHIELD);
            for(boolean direction : new boolean[]{true, false}) {
                int sh = mvc.getTableController().getPlayerDirection(player, direction).getRawProduction().get(Card.Resource.SHIELD);
                if((myShields < sh && myShields + shields >= sh) || (myShields == sh)) {
                    cardAction.weight += shields * 2;
                } else {
                    cardAction.weight += shields / 2;
                }
            }
        }

        //Science
        int mult = (scientist)? 2 : 1;
        if(mvc.getTableController().getEra() < 2 && scientist) mult = 4;
        //If it is the one(s) we have the least of it's worth 2 * mult, otherwise it's worth mult
        int least = prod.get(Card.Resource.GEAR);
        least = (prod.get(Card.Resource.COMPASS) < least)? prod.get(Card.Resource.COMPASS) : least;
        least = (prod.get(Card.Resource.TABLET) < least)? prod.get(Card.Resource.TABLET) : least;
        for(Card.Resource res : new Card.Resource[]{Card.Resource.GEAR, Card.Resource.COMPASS, Card.Resource.TABLET}) {
            if(cardAction.card.getProducts().get(res) == 0) continue;
            if(cardAction.card.getProducts().get(res) == least) cardAction.weight += 2 * mult;
            else cardAction.weight += mult;
        }

        //Commercial
        if(Special.getTradeType(cardAction.card, player) == Special.TradeType.resource) {
            for(Card.Resource res : new Card.Resource[]{Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY, Card.Resource.ORE}) {
                if(prod.get(res) == 0) cardAction.weight++;
            }
        } else if(Special.getTradeType(cardAction.card, player) == Special.TradeType.industry) {
            for(Card.Resource res : new Card.Resource[]{Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER}) {
                if(prod.get(res) == 0) cardAction.weight += 2;
            }
        }

        //Speshul card does speshul stuffs...
        if(Special.isSpecialAction(cardAction.card, player)) {
            cardAction.weight += 5;
        }
    }

    private void calcWonder(CardAction cardAction) {
        Card nextStage = player.nextWonderStage();
        if(nextStage == null) {
            //We've already played all our wonder stages!
            cardAction.weight = -1;
        } else {
            cardAction.weight += mvc.getTableController().getEra() * -1 + 3;
            CardAction ca = new CardAction(nextStage);
            calcBuild(ca, player, false);
            cardAction.weight += ca.weight;

            //Trade cost
            int cost = getTradeGoldCost(ca, player);
            cardAction.tradeEast = ca.tradeEast;
            cardAction.tradeWest = ca.tradeWest;
            if(cost == -1 || cost + nextStage.getCost().get(Card.Resource.GOLD) > player.getGold()) {
                cardAction.weight = -1;
            } else cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
        }
    }

    private void calcDiscard(CardAction cardAction) {
        cardAction.weight = 1;
    }

    private void addNextEffect(CardAction cardAction, Player player) {
        CardAction ca = new CardAction(cardAction.card);
        Player p = mvc.getTableController().getPlayerDirection(player, mvc.getTableController().getPassingDirection());
        calcBuild(ca, p, false);
        //Perhaps we'll play a card just to spite our opponents!
        cardAction.weight += ca.weight / 2;
    }

    private int getTradeGoldCost(CardAction cardAction, Player player) {
        TradeController tc = new TradeController(mvc, player);
        ResQuant status = tc.numAvailable(player, new ResQuant().subtractResources(cardAction.card.getCost()), true);
        status.put(Card.Resource.GOLD, 0);
        if(status.allZeroOrAbove()) return 0;
        while(makeOneTrade(tc, cardAction, status, player)) {
            tc.setTrades(cardAction.tradeEast, cardAction.tradeWest);
            status = tc.getResAvailableAfterTrade(cardAction.card);
            if(status.allZeroOrAbove()) {
                if(! tc.canAffordResources(cardAction.card) || ! tc.canAffordGold(cardAction.card)) return -1;
                return tc.getTotalCost();
            }
        }

        return -1;
    }

    private boolean makeOneTrade(TradeController tc, CardAction cardAction, ResQuant resAvail, Player player) {
        ResQuant availableEast = tc.numAvailable(mvc.getTableController().getPlayerDirection(player, false),
                new ResQuant().subtractResources(cardAction.tradeEast), false);
        ResQuant availableWest = tc.numAvailable(mvc.getTableController().getPlayerDirection(player, true),
                new ResQuant().subtractResources(cardAction.tradeWest), false);
        ArrayList<Card.Resource> resources = new ArrayList<>();

        for(Card.Resource res : TradeController.tradeable) {
            if(resAvail.get(res) >= 0) continue;
            if((tc.getCost(player, false, res) < 2 && availableEast.get(res) > 0)
                || (tc.getCost(player, true, res) < 2 && availableWest.get(res) > 0)) {
                resources.add(0, res);
            } else if(availableEast.get(res) > 0 || availableWest.get(res) > 0) {
                resources.add(res);
            } else {
                return false;
            }
        }

        Card.Resource res = resources.get(0);
        boolean tradeDir = new Random().nextBoolean();
        for(int i = 0; i < 2; i++) {
            ResQuant availableDir = (tradeDir)? availableWest : availableEast;
            ResQuant tradeDirection = (tradeDir)? cardAction.tradeWest : cardAction.tradeEast;
            if(tc.getCost(player, false, res) < 2 && availableDir.get(res) > 0) {
                tradeDirection.put(res, tradeDirection.get(res) + 1);
                return true;
            }
            tradeDir = ! tradeDir;
        }
        for(int i = 0; i < 2; i++) {
            ResQuant availableDir = (tradeDir)? availableWest : availableEast;
            ResQuant tradeDirection = (tradeDir)? cardAction.tradeWest : cardAction.tradeEast;
            if(availableDir.get(res) > 0) {
                tradeDirection.put(res, tradeDirection.get(res) + 1);
                return true;
            }
            tradeDir = ! tradeDir;
        }

        return false;
    }

    private class CardAction implements Comparable<CardAction> {

        public Card card;
        public int weight;
        public Action action;
        public ResQuant tradeEast = new ResQuant();
        public ResQuant tradeWest = new ResQuant();

        public CardAction(Card card) {
            this.card = card;
        }

        @Override
        public int compareTo(@NonNull CardAction other) {
            if(weight < other.weight) return 1;
            if(weight == other.weight) return 0;
            return -1;
        }
    }

}
