package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.action.Action;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;
import net.dumtoad.srednow7.backend.implementation.variableResource.StandardResource;

import java.util.ArrayList;
import java.util.List;

import static net.dumtoad.srednow7.backend.Card.TradeType.ONECHEAPER;

class CardImpl implements Card {

    private Type type;
    private String name;
    private List<String> makesFreeName;
    private CardList makesFree = new CardListImpl();
    private CardList makesThisFree = new CardListImpl();
    private String message;
    private ResourceStrategy costs;
    private ResourceStrategy products;
    private TradeType tradeType;
    private List<Game.Direction> tradeDirections;
    private List<Attribute> attributes;
    private Action action;
    private int era;
    private int[] playerCutoffs;
    private List<Generate.Expansion> expansions;
    private ResponseCallback callback;

    private CardImpl(Builder builder) {
        type = builder.type;
        name = builder.name;
        makesFreeName = builder.makesFreeName;
        message = builder.message;
        costs = builder.costs;
        products = builder.products;
        tradeType = builder.tradeType;
        tradeDirections = builder.tradeDirections;
        attributes = builder.attributes;
        action = builder.action;
        era = builder.era;
        playerCutoffs = builder.playerCutoffs;
        expansions = builder.expansions;
        callback = builder.callback;
    }

    @Override
    public int getEra() {
        return era;
    }

    int[] getPlayerCutoffs() {
        return playerCutoffs;
    }

    List<Generate.Expansion> getExpansions() {
        return expansions;
    }

    @Override
    public CardList makeThisFree() {
        return makesThisFree;
    }

    @Override
    public CardList makesFree() {
        return makesFree;
    }

    @Override
    public boolean providesAttribute(Attribute attribute) {
        return attributes.contains(attribute);
    }

    @Override
    public void performAction(Player player) {
        action.act(player);
    }

    @Override
    public int getActionPrecidence() {
        if (action == null) return -1;
        return action.getPrecidence();
    }

    @Override
    public ResponseCallback getCallback() {
        return callback;
    }

    void resolveCoupons() {
        makesFree = new CardListImpl();
        for (String s : makesFreeName) {
            CardImpl madeFree = Generate.findCardByName(s);
            makesFree.add(madeFree);
            madeFree.madeFreeBy(this);
        }
    }

    private void madeFreeBy(Card card) {
        makesThisFree.add(card);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CharSequence getMessage() {
        return message;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean providesTrade(Game.Direction direction, TradeType type) {
        return type == tradeType && tradeDirections.contains(direction);
    }

    @Override
    public boolean makesTradeCheaper(Game.Direction direction) {
        return tradeType == ONECHEAPER && tradeDirections.contains(direction);
    }

    @Override
    public ResQuant getProducts(Player player) {
        return products.getResources(GameImpl.INSTANCE, player);
    }

    @Override
    public ResQuant getProductsNotSpecial() {
        return products.getResourcesNotSpecial();
    }

    @Override
    public ResourceStrategy.ResourceStyle getProductionStyle() {
        return products.getStyle();
    }

    @Override
    public ResQuant getCosts(Player player) {
        return costs.getResources(GameImpl.INSTANCE, player);
    }

    @Override
    public boolean isSpecialIn(Resource resource) {
        return products.isSpecialIn(resource);
    }


    static class Builder {

        private Type type;
        private String name;
        private String message = "";
        private ResourceStrategy costs = new StandardResource();
        private ResourceStrategy products = new StandardResource();
        private TradeType tradeType;
        private List<Game.Direction> tradeDirections = new ArrayList<>();
        private List<String> makesFreeName = new ArrayList<>();
        private List<Attribute> attributes = new ArrayList<>();
        private Action action = null;
        private int era = -1;
        private int[] playerCutoffs = new int[0];
        private List<Generate.Expansion> expansions = new ArrayList<>();
        private ResponseCallback callback;

        Builder(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        Builder setCosts(ResourceStrategy costs) {
            this.costs = costs;
            return this;
        }

        Builder setProducts(ResourceStrategy products) {
            this.products = products;
            return this;
        }

        Builder setTradeType(TradeType tradeType) {
            this.tradeType = tradeType;
            return this;
        }

        Builder addTradeDirection(Game.Direction direction) {
            this.tradeDirections.add(direction);
            return this;
        }

        Builder setMakesFree(String card) {
            this.makesFreeName.add(card);
            return this;
        }

        Builder addAttribute(Attribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        Builder setAction(Action action) {
            this.action = action;
            return this;
        }

        Builder setEra(int era) {
            this.era = era;
            return this;
        }

        Builder setPlayerCutoffs(int[] playerCutoffs) {
            this.playerCutoffs = playerCutoffs;
            return this;
        }

        Builder addExpansion(Generate.Expansion expansion) {
            expansions.add(expansion);
            return this;
        }

        Builder setCallback(ResponseCallback callback) {
            this.callback = callback;
            return this;
        }

        CardImpl build() {
            return new CardImpl(this);
        }
    }
}
