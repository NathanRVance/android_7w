package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.action.Action;
import net.dumtoad.srednow7.backend.implementation.variableResource.StandardResource;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;

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
    private List<Action> actions;
    private int era;
    private int[] playerCutoffs;
    private List<Generate.Expansion> expansions;

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
        actions = builder.actions;
        era = builder.era;
        playerCutoffs = builder.playerCutoffs;
        expansions = builder.expansions;
    }

    int getEra() {
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
    public void performActions(Player player) {
        for(Action action : actions) {
            action.act(player);
        }
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


    public static class Builder implements Card.Builder {

        private Type type;
        private String name;
        private String message = "";
        private ResourceStrategy costs = new StandardResource();
        private ResourceStrategy products = new StandardResource();
        private TradeType tradeType;
        private List<Game.Direction> tradeDirections = new ArrayList<>();
        private List<String> makesFreeName = new ArrayList<>();
        private List<Attribute> attributes = new ArrayList<>();
        private List<Action> actions = new ArrayList<>();
        private int era = -1;
        private int[] playerCutoffs = new int[0];
        private List<Generate.Expansion> expansions = new ArrayList<>();

        public Builder(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder setCosts(ResourceStrategy costs) {
            this.costs = costs;
            return this;
        }

        @Override
        public Builder setProducts(ResourceStrategy products) {
            this.products = products;
            return this;
        }

        @Override
        public Builder setTradeType(TradeType tradeType) {
            this.tradeType = tradeType;
            return this;
        }

        @Override
        public Builder addTradeDirection(Game.Direction direction) {
            this.tradeDirections.add(direction);
            return this;
        }

        @Override
        public Builder setMakesFree(String card) {
            this.makesFreeName.add(card);
            return this;
        }

        @Override
        public Builder addAttribute(Attribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        @Override
        public Card.Builder addAction(Action action) {
            actions.add(action);
            return this;
        }

        Card.Builder setEra(int era) {
            this.era = era;
            return this;
        }

        Card.Builder setPlayerCutoffs(int[] playerCutoffs) {
            this.playerCutoffs = playerCutoffs;
            return this;
        }

        Card.Builder addExpansion(Generate.Expansion expansion) {
            expansions.add(expansion);
            return this;
        }

        @Override
        public CardImpl build() {
            return new CardImpl(this);
        }
    }
}
