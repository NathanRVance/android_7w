package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.TradeBackend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class TradeBackendImpl implements TradeBackend {

    private static final long serialVersionUID = 8687384948278605407L;
    private transient Player player;
    private int gold;
    private Map<Game.Direction, ResQuant> trades;
    //Cache prices so that they only have to be calculated once
    private Map<Game.Direction, ResQuant> prices;

    TradeBackendImpl(Player player) {
        this.player = player;
        gold = 3;
        clearAllButGold();
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void makeTrade(Card.Resource resource, int amount, Game.Direction direction) {
        gold -= amount * getPrice(resource, direction);
        if (gold < 0) throw new RuntimeException("Gold less than 0");
        int currentTrade = trades.get(direction).get(resource);
        trades.get(direction).put(resource, currentTrade + amount);
    }

    @Override
    public int goldAvailable() {
        return gold;
    }

    @Override
    public ResQuant resourcesForSale(Game.Direction direction) {
        return numAvailable(GameImpl.INSTANCE.getPlayerDirection(player, direction), trades.get(direction), false);
    }

    @Override
    public ResQuant resourcesBought(Game.Direction direction) {
        return trades.get(direction);
    }

    @Override
    public ResQuant prices(Game.Direction direction) {
        if (prices == null) {
            prices = new HashMap<>();
            for (Game.Direction d : Game.Direction.values()) {
                prices.put(d, new ResQuantImpl());
                for (Card.Resource resource : tradeable) {
                    prices.get(d).put(resource, getPrice(resource, d));
                }
            }
        }
        return prices.get(direction);
    }

    private int getPrice(Card.Resource resource, Game.Direction direction) {
        Card.TradeType type =
                (resource == Card.Resource.CLOTH || resource == Card.Resource.GLASS || resource == Card.Resource.PAPER) ?
                        Card.TradeType.INDUSTRY : Card.TradeType.RESOURCE;

        int cost = 2;
        for (Card card : player.getPlayedCards()) {
            if (card.providesTrade(direction, type))
                cost = 1;
        }
        for (Card card : player.getPlayedCards()) {
            if (card.makesTradeCheaper(direction))
                cost--;
        }
        if(cost < 0) cost = 0;
        return cost;
    }

    @Override
    public ResQuant getLeftoverResources(Card card) {
        ResQuant cost = card.getCosts(player);
        cost.put(Card.Resource.GOLD, 0); //Deal with gold elsewhere
        for (ResQuant trade : trades.values()) {
            cost.subtractResources(trade);
        }
        return numAvailable(player, cost, true);
    }

    @Override
    public boolean hasTrade() {
        boolean hasTrade = false;
        for (ResQuant trade : trades.values()) {
            hasTrade |= !(trade.allZeroOrBelow() && trade.allZeroOrAbove());
        }
        return hasTrade;
    }

    @Override
    public boolean overpaid(Card card) {
        ResQuant leftovers = getLeftoverResources(card);
        for (ResQuant trade : trades.values()) {
            for (Card.Resource resource : tradeable) {
                if (leftovers.get(resource) > 0 && trade.get(resource) > 0)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canAfford(Card card) {
        return getLeftoverResources(card).allZeroOrAbove() && gold >= card.getCosts(player).get(Card.Resource.GOLD);
    }

    @Override
    public int getGoldSpent(Game.Direction direction) {
        int amount = 0;
        for (Card.Resource res : tradeable) {
            amount += trades.get(direction).get(res) * getPrice(res, direction);
        }
        return amount;
    }

    @Override
    public void clear() {
        gold = player.getGold();
        clearAllButGold();
    }

    private void clearAllButGold() {
        trades = new HashMap<>();
        trades.put(Game.Direction.WEST, new ResQuantImpl());
        trades.put(Game.Direction.EAST, new ResQuantImpl());

        prices = null;
    }

    //Status is current trade status, and is negative towards player.
    private ResQuant numAvailable(Player player, ResQuant status, boolean includeNonTradeable) {
        ResQuant available = new ResQuantImpl();
        //Add the wonder RESOURCE
        available.put(player.getWonder().getResource(), 1);
        //Subtract the status resources
        available.subtractResources(status);
        //complicated are cards where you must choose which RESOURCE they produce
        Stack<Card> complicated = new Stack<>();
        for (Card card : player.getPlayedCards()) {
            //If we aren't including non-tradeables (everything but RESOURCE and INDUSTRY) skip them now
            if (!includeNonTradeable && card.getType() != Card.Type.RESOURCE && card.getType() != Card.Type.INDUSTRY)
                continue;

            ResQuant prod = card.getProducts(player);

            //Count number of products card produces
            int numProducts = 0;
            for (Card.Resource res : tradeable) {
                if (prod.get(res) > 0) numProducts++;
            }
            if (numProducts == 1) { //Great, a reasonable card. Deal with it now
                for (Card.Resource res : tradeable) {
                    available.add(res, prod.get(res));
                }
            } else if (numProducts > 1) { //Ugh, it's complicated. Deal with it later
                complicated.push(card);
            }
        }

        //We're left with some cards that could produce one of several resources. I think (not sure)
        //that this is an NP-complete problem, so brute force! Find all legal combinations, and take
        //the maximum available for each RESOURCE.
        ResQuant answer = new ResQuantImpl().addResources(available); //Makes a copy of available
        availableRecurse(complicated, available, answer);

        return answer;
    }

    private void availableRecurse(Stack<Card> cards, ResQuant available, ResQuant answer) {
        if (cards.empty()) {
            if (available.allZeroOrAbove()) { //Found a legal solution
                for (Card.Resource res : tradeable) {
                    if (available.get(res) > answer.get(res)) {
                        answer.put(res, available.get(res));
                    }
                }
            }
        } else {
            Card card = cards.pop();
            for (Card.Resource res : tradeable) {
                if (card.getProducts(player).get(res) > 0) {
                    available.add(res, 1);
                    availableRecurse(cards, available, answer);
                    available.add(res, -1);
                }
            }
            cards.push(card);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
    }
}
