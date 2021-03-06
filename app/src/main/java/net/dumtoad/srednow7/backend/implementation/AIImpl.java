package net.dumtoad.srednow7.backend.implementation;

import android.support.annotation.NonNull;

import net.dumtoad.srednow7.backend.AI;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.TradeBackend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class AIImpl implements AI {

    private Player player;
    private boolean scientist = false;

    AIImpl(Player player) {
        this.player = player;
    }

    @Override
    public void selectWonderSide(int playerID) {
        Random random = new Random();
        GameImpl.INSTANCE.getSetup(playerID).setWonderSide(random.nextInt(2));
        GameImpl.INSTANCE.getSetup(playerID).finish();
    }

    @Override
    public void doTurn() {
        updateScientist();
        CardList hand = player.getHand();
        boolean playDiscard = player.isPlayDiscard();
        List<CardAction> actions = new ArrayList<>();
        for (Player.CardAction action : (playDiscard) ? new Player.CardAction[]{Player.CardAction.BUILD} : Player.CardAction.values()) {
            for (Card card : hand) {
                actions.add(calcCardAction(card, action, playDiscard));
            }
        }
        Collections.sort(actions);
        doAction(actions.get(0), actions.size());
    }

    @Override
    public int loseGold(int amountToLose) {
        return amountToLose;
    }

    private void updateScientist() {
        if (player.getWonder().getResource() == Card.Resource.CLOTH
                || player.getWonder().getResource() == Card.Resource.PAPER
                || player.getWonder().getResource() == Card.Resource.GLASS
                || player.getWonder().getName().contains("Babylon")) {
            scientist = true;
        }
    }

    private void doAction(CardAction cardAction, int actionsSize) {
        //Set the trades
        player.getTradeBackend().clear();
        for (Game.Direction direction : Game.Direction.values()) {
            for (Card.Resource res : TradeBackend.tradeable) {
                player.getTradeBackend().makeTrade(res, cardAction.trades.get(direction).get(res), direction);
            }
        }
        System.out.printf("Actions size: %d wonder: %s\n\taction: %s weight: %d\n"
                , actionsSize, player.getWonder().getName(), cardAction.action.toString(), cardAction.weight);
        //Do the action
        player.requestCardAction(cardAction.action, cardAction.card);
    }

    private CardAction calcCardAction(Card card, Player.CardAction action, boolean playDiscard) {
        CardAction cardAction = new CardAction(card, action);
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
            if (cost == -1 || cost + cardAction.card.getCosts(player).get(Card.Resource.GOLD) > player.getGold()) {
                cardAction.weight = -1;
                return;
            } else
                cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
        }

        ResQuant prod = new ResQuantImpl();
        for (Card card : player.getPlayedCards()) {
            prod.addResources(card.getProducts(player));
        }

        //Tradeable resources get 5 for what we don't have, 1 for what we do.
        for (Card.Resource res : TradeBackend.tradeable) {
            int mult = (prod.get(res) > 0) ? 1 : 5;
            cardAction.weight += cardAction.card.getProducts(player).get(res) * mult;
        }

        //Gold add 1 for every 3 gold
        cardAction.weight += cardAction.card.getProducts(player).get(Card.Resource.GOLD) / 3;

        //VPs get 1 weight each
        cardAction.weight += cardAction.card.getProducts(player).get(Card.Resource.VP);

        //Military is worth 2 for each shield for each battle it will turn the tide in, 1(ish) otherwise
        int shields = cardAction.card.getProducts(player).get(Card.Resource.SHIELD);
        if (shields > 0) {
            int myShields = prod.get(Card.Resource.SHIELD);
            for (Game.Direction direction : Game.Direction.values()) {
                int sh = 0;
                for (Card card : GameImpl.INSTANCE.getPlayerDirection(player, direction).getPlayedCards())
                    sh += card.getProducts(player).get(Card.Resource.SHIELD);
                if ((myShields < sh && myShields + shields >= sh) || (myShields == sh)) {
                    cardAction.weight += shields * 2;
                }
            }
        }

        //Science
        int mult = (scientist) ? 2 : 1;
        if (GameImpl.INSTANCE.getEra() < 2 && scientist) mult = 4;
        //If it is the one(s) we have the least of it's worth 2 * mult, otherwise it's worth mult
        int least = prod.get(Card.Resource.GEAR);
        least = (prod.get(Card.Resource.COMPASS) < least) ? prod.get(Card.Resource.COMPASS) : least;
        least = (prod.get(Card.Resource.TABLET) < least) ? prod.get(Card.Resource.TABLET) : least;
        for (Card.Resource res : new Card.Resource[]{Card.Resource.GEAR, Card.Resource.COMPASS, Card.Resource.TABLET}) {
            if (cardAction.card.getProducts(player).get(res) == 0) continue;
            if (prod.get(res) == least)
                cardAction.weight += 2 * mult;
            else cardAction.weight += mult;
        }

        //Commercial
        if (cardAction.card.providesTrade(Game.Direction.EAST, Card.TradeType.RESOURCE)
                || cardAction.card.providesTrade(Game.Direction.WEST, Card.TradeType.RESOURCE)) {
            for (Card.Resource res : new Card.Resource[]{Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY, Card.Resource.ORE}) {
                if (prod.get(res) == 0) cardAction.weight++;
            }
        } else if (cardAction.card.providesTrade(Game.Direction.EAST, Card.TradeType.INDUSTRY)
                || cardAction.card.providesTrade(Game.Direction.WEST, Card.TradeType.INDUSTRY)) {
            for (Card.Resource res : new Card.Resource[]{Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER}) {
                if (prod.get(res) == 0) cardAction.weight += 2;
            }
        }

        //Speshul card does speshul shtuffsh...
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
            cardAction.weight += GameImpl.INSTANCE.getEra() * -1 + 3;
            CardAction ca = new CardAction(nextStage, Player.CardAction.BUILD);
            calcBuild(ca, player, false);
            cardAction.weight += ca.weight;
            if (GameImpl.INSTANCE.getRound() >= 5 && nextStage.getName().contains("" + (GameImpl.INSTANCE.getEra() + 1))) {
                //If we're at the end of the era and haven't played this era's wonder stage yet
                cardAction.weight *= 2;
            }

            //Trade cost
            int cost = getTradeGoldCost(ca, player);
            cardAction.trades = ca.trades;
            if (cost == -1) {
                cardAction.weight = -1;
            } else {
                cardAction.weight -= cost / 2; //Not quite divided by 3; there's an opportunity cost to spending.
            }
        }
    }

    private void calcDiscard(CardAction cardAction) {
        cardAction.weight = 1;
    }

    private void addNextEffect(CardAction cardAction, Player player) {
        CardAction ca = new CardAction(cardAction.card, Player.CardAction.BUILD);
        Player p = GameImpl.INSTANCE.getPlayerDirection(player, GameImpl.INSTANCE.getPassingDirection());
        calcBuild(ca, p, false);
        //Perhaps we'll play a card just to spite our opponents!
        cardAction.weight += ca.weight / 3;
    }

    //Simultaneously sets cardAction's trades field to the optimal solution
    private int getTradeGoldCost(CardAction cardAction, Player player) {
        //Make a new one because we don't want to mess up another player's trades
        TradeBackend tb = new TradeBackendImpl(player);
        if (tb.canAfford(cardAction.card)) return 0;

        //Favor the player who's currently losing...
        Game.Direction favor = (GameImpl.INSTANCE.getPlayerDirection(player, Game.Direction.EAST).getScore().getTotalVPs()
                < GameImpl.INSTANCE.getPlayerDirection(player, Game.Direction.WEST).getScore().getTotalVPs()) ?
                Game.Direction.EAST : Game.Direction.WEST;
        //...and set up a direction array so that we can quickly go through them in the right order
        Game.Direction[] defaultDirectionOrder = new Game.Direction[]{favor, favor == Game.Direction.EAST ? Game.Direction.WEST : Game.Direction.EAST};

        Map<Game.Direction, ResQuant> bestTrade = new HashMap<>();
        bestTrade.put(Game.Direction.EAST, new ResQuantImpl());
        bestTrade.put(Game.Direction.WEST, new ResQuantImpl());

        for (Game.Direction direction : Game.Direction.values()) {
            for (Card.Resource res : Card.Resource.values()) {
                cardAction.trades.get(direction).put(res, 0);
            }
        }

        int cost = optimizeCostsRecurse(cardAction, tb, defaultDirectionOrder, bestTrade, 1000);

        if (cost > player.getGold()) return -1;

        cardAction.trades = bestTrade;
        return cost;
    }

    private int optimizeCostsRecurse(CardAction cardAction, TradeBackend tb, Game.Direction[] defaultDirectionOrder,
                                     Map<Game.Direction, ResQuant> bestTrade, int bestCost) {
        if (tb.canAfford(cardAction.card)) {
            if (tb.overpaid(cardAction.card)) return bestCost;
            int currentCost = tb.getGoldSpent(Game.Direction.EAST) + tb.getGoldSpent(Game.Direction.WEST);
            if (currentCost < bestCost) {
                bestCost = currentCost;
                for (Game.Direction d : cardAction.trades.keySet()) {
                    for (Card.Resource r : TradeBackend.tradeable) {
                        bestTrade.get(d).put(r, cardAction.trades.get(d).get(r));
                    }
                }
            }
            return bestCost;
        }
        for (Card.Resource res : TradeBackend.tradeable) {
            int resBought = tb.resourcesBought(Game.Direction.EAST).get(res) + tb.resourcesBought(Game.Direction.WEST).get(res);
            if (cardAction.card.getCosts(player).get(res) > resBought) { //Only go on if we haven't purchased card's cost of this already
                //The order we'll check to buy in based on price then on who's winning
                Game.Direction[] buyOrder;
                if (tb.prices(Game.Direction.EAST).get(res).equals(tb.prices(Game.Direction.WEST).get(res)))
                    buyOrder = defaultDirectionOrder;
                else if (tb.prices(Game.Direction.EAST).get(res) < tb.prices(Game.Direction.WEST).get(res))
                    buyOrder = new Game.Direction[]{Game.Direction.EAST, Game.Direction.WEST};
                else
                    buyOrder = new Game.Direction[]{Game.Direction.WEST, Game.Direction.EAST};

                for (Game.Direction direction : buyOrder) {
                    if (tb.resourcesForSale(direction).get(res) > 0 && tb.goldAvailable() >= tb.prices(direction).get(res)) {
                        cardAction.trades.get(direction).add(res, 1);
                        tb.makeTrade(res, 1, direction);
                        int cost = optimizeCostsRecurse(cardAction, tb, defaultDirectionOrder, bestTrade, bestCost);
                        if (cost < bestCost) {
                            bestCost = cost;
                        }
                        //Undo what we just did so we can try something else
                        cardAction.trades.get(direction).add(res, -1);
                        tb.makeTrade(res, -1, direction);
                    }
                }
            }
        }
        return bestCost;
    }

    private class CardAction implements Comparable<CardAction> {

        Card card;
        int weight;
        Player.CardAction action;

        private Map<Game.Direction, ResQuant> trades = new HashMap<>();

        {
            trades.put(Game.Direction.EAST, new ResQuantImpl());
            trades.put(Game.Direction.WEST, new ResQuantImpl());
        }

        CardAction(Card card, Player.CardAction action) {
            this.card = card;
            this.action = action;
        }

        @Override
        public int compareTo(@NonNull CardAction other) {
            if (weight < other.weight) return 1;
            if (weight == other.weight) return 0;
            return -1;
        }
    }

}
