package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.variableResource.StandardResource;
import net.dumtoad.srednow7.backend.implementation.variableResource.ResourceStrategy;

import java.util.ArrayList;
import java.util.List;

class CardImpl implements Card {

    private Type type;
    private Enum name;
    private List<Enum> makesFreeEnum;
    private CardList makesFree = new CardListImpl();
    private CardList makesThisFree = new CardListImpl();
    private String message;
    private ResourceStrategy costs;
    private ResourceStrategy products;
    private TradeType tradeType;
    private List<Game.Direction> tradeDirections;
    private List<Attribute> attributes;

    private CardImpl(Builder builder) {
        type = builder.type;
        name = builder.name;
        makesFreeEnum = builder.makesFreeEnum;
        message = builder.message;
        costs = builder.costs;
        products = builder.products;
        tradeType = builder.tradeType;
        tradeDirections = builder.tradeDirections;
        attributes = builder.attributes;
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

    void resolveCoupons() {
        makesFree = new CardListImpl();
        for (Enum e : makesFreeEnum) {
            CardImpl madeFree = Generate.findCardByName(e);
            makesFree.add(madeFree);
            madeFree.madeFreeBy(this);
        }
    }

    private void madeFreeBy(Card card) {
        makesThisFree.add(card);
    }

    @Override
    public Enum getEnum() {
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
        private Enum name;
        private String message = "";
        private ResourceStrategy costs = new StandardResource();
        private ResourceStrategy products = new StandardResource();
        private TradeType tradeType;
        private List<Game.Direction> tradeDirections = new ArrayList<>();
        private List<Enum> makesFreeEnum = new ArrayList<>();
        private List<Attribute> attributes = new ArrayList<>();

        public Builder(Type type, Enum name) {
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
        public Builder setMakesFree(Enum card) {
            this.makesFreeEnum.add(card);
            return this;
        }

        @Override
        public Builder addAttribute(Attribute attribute) {
            this.attributes.add(attribute);
            return this;
        }

        @Override
        public CardImpl build() {
            return new CardImpl(this);
        }
    }
}
