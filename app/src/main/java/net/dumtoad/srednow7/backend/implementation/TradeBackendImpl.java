package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.TradeBackend;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.TradeUI;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class TradeBackendImpl implements TradeBackend {

    private Player player;
    private int gold;
    private Map<Game.Direction, ResQuant> trades;

    TradeBackendImpl(Player player) {
        this.player = player;
        clear();
    }

    @Override
    public void makeTrade(Card.Resource resource, int amount, Game.Direction direction) {
        gold -= amount * getPrice(resource, direction);
        if (gold < 0) throw new RuntimeException("Gold less than 0");
        int currentTrade = trades.get(direction).get(resource);
        trades.get(direction).put(resource, currentTrade + amount);
    }

    @Override
    public void refresh(TradeUI tradeUI, Game.Direction direction) {
        ResQuant numAvailable = numAvailable(Bus.bus.getGame().getPlayerDirection(player, direction), trades.get(direction), false);
        ResQuant prices = new ResQuantImpl();
        for (Card.Resource resource : tradeable) {
            prices.put(resource, getPrice(resource, direction));
        }
        tradeUI.update(gold, numAvailable, trades.get(direction), prices);
    }

    @Override
    public ResQuant getLeftoverResources(Card card) {
        ResQuant cost = card.getCosts();
        cost.put(Card.Resource.GOLD, 0); //Deal with gold elsewhere
        for(ResQuant trade : trades.values()) {
            cost.subtractResources(trade);
        }
        return numAvailable(player, cost, true);
    }

    @Override
    public boolean hasTrade() {
        boolean hasTrade = false;
        for(ResQuant trade : trades.values()) {
            hasTrade |= ! (trade.allZeroOrBelow() && trade.allZeroOrAbove());
        }
        return hasTrade;
    }

    @Override
    public boolean overpaid(Card card) {
        ResQuant leftovers = getLeftoverResources(card);
        for(ResQuant trade : trades.values()) {
            for(Card.Resource resource : tradeable) {
                if(leftovers.get(resource) > 0 && trade.get(resource) > 0)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canAfford(Card card) {
        return getLeftoverResources(card).allZeroOrAbove() && gold >= card.getCosts().get(Card.Resource.GOLD);
    }

    @Override
    public int getGoldSpent(Game.Direction direction) {
        int amount = 0;
        for(Card.Resource res : tradeable) {
            amount += trades.get(direction).get(res) * getPrice(res, direction);
        }
        return amount;
    }

    @Override
    public void clear() {
        gold = player.getGold();
        trades = new HashMap<>();
        trades.put(Game.Direction.WEST, new ResQuantImpl());
        trades.put(Game.Direction.EAST, new ResQuantImpl());
    }

    private int getPrice(Card.Resource resource, Game.Direction direction) {
        Card.TradeType type =
                (resource == Card.Resource.CLOTH || resource == Card.Resource.GLASS || resource == Card.Resource.PAPER) ?
                        Card.TradeType.industry : Card.TradeType.resource;

        for (Card card : player.getPlayedCards()) {
            if (card.providesTrade(direction, type)) return 1;
        }
        return 2;
    }

    //Status is current trade status, and is negative towards player.
    private ResQuant numAvailable(Player player, ResQuant status, boolean includeNonTradeable) {
        ResQuant available = new ResQuantImpl();
        //Add the wonder resource
        available.put(player.getWonder().getResource(), 1);
        //Subtract the status resources
        available.subtractResources(status);
        //complicated are cards where you must choose which resource they produce
        Stack<Card> complicated = new Stack<>();
        for (Card card : player.getPlayedCards()) {
            //If we aren't including non-tradeables (everything but resource and industry) skip them now
            if (!includeNonTradeable && card.getType() != Card.Type.RESOURCE && card.getType() != Card.Type.INDUSTRY)
                continue;

            ResQuant prod = card.getProducts();

            //Count number of products card produces
            int numProducts = 0;
            for (Card.Resource res : tradeable) {
                if (prod.get(res) > 0) numProducts++;
            }
            if (numProducts == 1) { //Great, a reasonable card. Deal with it now
                for (Card.Resource res : tradeable) {
                    available.put(res, available.get(res) + prod.get(res));
                }
            } else if (numProducts > 1) { //Ugh, it's complicated. Deal with it later
                complicated.push(card);
            }
        }

        //We're left with some cards that could produce one of several resources. I think (not sure)
        //that this is an NP-complete problem, so brute force! Find all legal combinations, and take
        //the maximum available for each resource.
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
                if (card.getProducts().get(res) > 0) {
                    available.put(res, available.get(res) + 1);
                    availableRecurse(cards, available, answer);
                    available.put(res, available.get(res) - 1);
                }
            }
            cards.push(card);
        }
    }

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[2];
        contents[0] = trades.get(Game.Direction.EAST).getContents();
        contents[1] = trades.get(Game.Direction.WEST).getContents();
        return contents;
    }

    @Override
    public void restoreContents(Serializable contents) throws Exception {
        Serializable[] s = (Serializable[]) contents;
        trades.put(Game.Direction.EAST, new ResQuantImpl());
        trades.get(Game.Direction.EAST).restoreContents(s[0]);

        trades.put(Game.Direction.WEST, new ResQuantImpl());
        trades.get(Game.Direction.WEST).restoreContents(s[1]);

        gold = player.getGold();
        for (Game.Direction direction : trades.keySet()) {
            for (Card.Resource resource : tradeable) {
                gold -= getPrice(resource, direction) * trades.get(direction).get(resource);
            }
        }
    }
}
