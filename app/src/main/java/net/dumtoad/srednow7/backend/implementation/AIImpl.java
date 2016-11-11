package net.dumtoad.srednow7.backend.implementation;

import android.support.annotation.NonNull;

import net.dumtoad.srednow7.backend.AI;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.TradeBackend;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.TradeUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class AIImpl implements AI, TradeUI {

    private Player player;
    private boolean scientist = false;

    AIImpl(Player player) {
        this.player = player;
    }

    @Override
    public void selectWonderSide(int playerID) {
        Random random = new Random();
        Bus.bus.getGame().getSetup(playerID).setWonderSide(random.nextInt(2));
        Bus.bus.getGame().getSetup(playerID).finish();
    }

    @Override
    public void doTurn() {
        updateScientist();
        CardList hand = player.getHand();
        boolean playDiscard = player.isPlayDiscard();
        ArrayList<CardAction> actions = new ArrayList<>();
        for (Player.CardAction action : (playDiscard) ? new Player.CardAction[]{Player.CardAction.BUILD} : Player.CardAction.values()) {
            for (Card card : hand) {
                actions.add(calcCardAction(card, action, playDiscard));
            }
        }
        Collections.sort(actions);
        doAction(actions.get(0));
    }

    private void updateScientist() {
        if (player.getWonder().getResource() == Card.Resource.CLOTH
                || player.getWonder().getResource() == Card.Resource.PAPER
                || player.getWonder().getResource() == Card.Resource.GLASS
                || player.getWonder().getEnum() == Generate.Wonders.The_Hanging_Gardens_of_Babylon) {
            scientist = true;
        }
    }

    private void doAction(CardAction cardAction) {
        setTrades(cardAction, player.getTradeBackend());
        try {
            player.requestCardAction(cardAction.action, cardAction.card);
        } catch (Player.BadActionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error doing action weight = " + cardAction.weight);
        }
    }

    private void setTrades(CardAction cardAction, TradeBackend tb) {
        tb.clear();
        for (Card.Resource res : cardAction.tradeEast.keySet()) {
            tb.makeTrade(res, cardAction.tradeEast.get(res), Game.Direction.EAST);
        }
        for (Card.Resource res : cardAction.tradeWest.keySet()) {
            tb.makeTrade(res, cardAction.tradeWest.get(res), Game.Direction.WEST);
        }
    }

    private CardAction calcCardAction(Card card, Player.CardAction action, boolean playDiscard) {
        CardAction cardAction = new CardAction(card);
        cardAction.action = action;
        switch (action) {
            case BUILD:
                calcBuild(cardAction, player, playDiscard);
                break;
            case WONDER:
                calcWonder(cardAction);
                break;
            case DISCARD:
                calcDiscard(cardAction);
                break;
            default:
                break;
        }
        if (cardAction.weight != -1) {
            addNextEffect(cardAction, player);
        }
        return cardAction;
    }

    private void calcBuild(CardAction cardAction, Player player, boolean playDiscard) {
        if (player.getPlayedCards().contains(cardAction.card)) {
            cardAction.weight = -1;
            return;
        }

        if (!playDiscard && !player.hasCouponFor(cardAction.card)) {
            //Trade cost
            int cost = getTradeGoldCost(cardAction, player);
            if (cost == -1 || cost + cardAction.card.getCosts().get(Card.Resource.GOLD) > player.getGold()) {
                cardAction.weight = -1;
                return;
            } else
                cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
        }

        ResQuant prod = new ResQuantImpl();
        for (Card card : player.getPlayedCards()) {
            prod.addResources(card.getProducts());
        }

        //Tradeable resources get 5 for what we don't have, 1 for what we do.
        for (Card.Resource res : TradeBackend.tradeable) {
            int mult = (prod.get(res) > 0) ? 1 : 5;
            cardAction.weight += cardAction.card.getProducts().get(res) * mult;
        }

        //Gold gets 1 for every 3 gold
        int gold = cardAction.card.getProducts().get(Card.Resource.GOLD);
        gold += cardAction.card.getSpecialGold(player);
        cardAction.weight += gold / 3;

        //VPs get 1 weight each
        cardAction.weight += cardAction.card.getProducts().get(Card.Resource.VP);
        cardAction.weight += cardAction.card.getSpecialVps(player);

        //Military is worth 2 for each shield for each battle it will turn the tide in, 1(ish) otherwise
        int shields = cardAction.card.getProducts().get(Card.Resource.SHIELD);
        if (shields > 0) {
            int myShields = prod.get(Card.Resource.SHIELD);
            for (Game.Direction direction : Game.Direction.values()) {
                int sh = 0;
                for (Card card : Bus.bus.getGame().getPlayerDirection(player, direction).getPlayedCards())
                    sh += card.getProducts().get(Card.Resource.SHIELD);
                if ((myShields < sh && myShields + shields >= sh) || (myShields == sh)) {
                    cardAction.weight += shields * 2;
                } else {
                    cardAction.weight += shields / 2;
                }
            }
        }

        //Science
        int mult = (scientist) ? 2 : 1;
        if (Bus.bus.getGame().getEra() < 2 && scientist) mult = 4;
        //If it is the one(s) we have the least of it's worth 2 * mult, otherwise it's worth mult
        int least = prod.get(Card.Resource.GEAR);
        least = (prod.get(Card.Resource.COMPASS) < least) ? prod.get(Card.Resource.COMPASS) : least;
        least = (prod.get(Card.Resource.TABLET) < least) ? prod.get(Card.Resource.TABLET) : least;
        for (Card.Resource res : new Card.Resource[]{Card.Resource.GEAR, Card.Resource.COMPASS, Card.Resource.TABLET}) {
            if (cardAction.card.getProducts().get(res) == 0) continue;
            if (cardAction.card.getProducts().get(res) == least) cardAction.weight += 2 * mult;
            else cardAction.weight += mult;
        }

        //Commercial
        if (cardAction.card.providesTrade(Game.Direction.EAST, Card.TradeType.resource)
                || cardAction.card.providesTrade(Game.Direction.WEST, Card.TradeType.resource)) {
            for (Card.Resource res : new Card.Resource[]{Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY, Card.Resource.ORE}) {
                if (prod.get(res) == 0) cardAction.weight++;
            }
        } else if (cardAction.card.providesTrade(Game.Direction.EAST, Card.TradeType.industry)
                || cardAction.card.providesTrade(Game.Direction.WEST, Card.TradeType.industry)) {
            for (Card.Resource res : new Card.Resource[]{Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER}) {
                if (prod.get(res) == 0) cardAction.weight += 2;
            }
        }

        //Speshul card does speshul shtuffs...
        for (Card.Attribute attribute : Card.Attribute.values()) {
            if (cardAction.card.providesAttribute(attribute)) {
                cardAction.weight += 5;
            }
        }
    }

    private void calcWonder(CardAction cardAction) {
        Card nextStage = player.nextWonderStage();
        if (nextStage == null) {
            //We've already played all our wonder stages!
            cardAction.weight = -1;
        } else {
            cardAction.weight += Bus.bus.getGame().getEra() * -1 + 3;
            CardAction ca = new CardAction(nextStage);
            calcBuild(ca, player, false);
            cardAction.weight += ca.weight;

            //Trade cost
            int cost = getTradeGoldCost(ca, player);
            cardAction.tradeEast = ca.tradeEast;
            cardAction.tradeWest = ca.tradeWest;
            if (cost == -1 || cost + nextStage.getCosts().get(Card.Resource.GOLD) > player.getGold()) {
                cardAction.weight = -1;
            } else
                cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
        }
    }

    private void calcDiscard(CardAction cardAction) {
        cardAction.weight = 1;
    }

    private void addNextEffect(CardAction cardAction, Player player) {
        CardAction ca = new CardAction(cardAction.card);
        Player p = Bus.bus.getGame().getPlayerDirection(player, Bus.bus.getGame().getPassingDirection());
        calcBuild(ca, p, false);
        //Perhaps we'll play a card just to spite our opponents!
        cardAction.weight += ca.weight / 2;
    }

    private int getTradeGoldCost(CardAction cardAction, Player player) {
        TradeBackend tb = new TradeBackendImpl(player);
        if (tb.canAfford(cardAction.card)) return 0;
        //TODO: Finish this
        return -1;
    }

    @Override
    public void update(int goldAvailable, ResQuant resourcesForSale, ResQuant resourcesBought, ResQuant prices) {

    }

    @Override
    public Serializable getContents() {
        return null;
    }

    @Override
    public void restoreContents(Serializable contents) throws Exception {

    }

    private class CardAction implements Comparable<CardAction> {

        public Card card;
        int weight;
        Player.CardAction action;
        ResQuant tradeEast = new ResQuantImpl();
        ResQuant tradeWest = new ResQuantImpl();

        CardAction(Card card) {
            this.card = card;
        }

        @Override
        public int compareTo(@NonNull CardAction other) {
            if (weight < other.weight) return 1;
            if (weight == other.weight) return 0;
            return -1;
        }
    }

}
