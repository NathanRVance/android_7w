package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.implementation.specialValue.NotSoSpecial;
import net.dumtoad.srednow7.backend.implementation.specialValue.SpecialValue;
import net.dumtoad.srednow7.backend.implementation.variableResource.StaticResource;
import net.dumtoad.srednow7.backend.implementation.variableResource.VariableResource;

import java.util.ArrayList;
import java.util.List;

class CardImpl implements Card {

    private Type type;
    private Enum name;
    private List<Enum> makesFreeEnum;
    private CardList makesFree = new CardListImpl();
    private List<Enum> makesThisFreeEnum;
    private CardList makesThisFree = new CardListImpl();
    private String message;
    private VariableResource costs;
    private VariableResource products;
    private SpecialValue specialGold;
    private SpecialValue specialVps;
    private TradeType tradeType;
    private List<Game.Direction> tradeDirections;
    private List<Attribute> attributes;

    private CardImpl(Builder builder) {
        type = builder.type;
        name = builder.name;
        makesFreeEnum = builder.makesFreeEnum;
        makesThisFreeEnum = builder.makesThisFreeEnum;
        message = builder.message;
        costs = builder.costs;
        products = builder.products;
        specialGold = builder.specialGold;
        specialVps = builder.specialVps;
        tradeType = builder.tradeType;
        tradeDirections = builder.tradeDirections;
        attributes = builder.attributes;
    }

    @Override
    public int getSpecialGold(Player player) {
        return specialGold.getSpecialValue(GameImpl.INSTANCE, player);
    }

    @Override
    public boolean isSpecialGold() {
        return specialGold.isSpecial();
    }

    @Override
    public int getSpecialVps(Player player) {
        return specialVps.getSpecialValue(GameImpl.INSTANCE, player);
    }

    @Override
    public boolean isSpecialVps() {
        return specialVps.isSpecial();
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
            makesFree.add(Generate.findCardByName(e));
        }

        makesThisFree = new CardListImpl();
        for (Enum e : makesThisFreeEnum) {
            makesThisFree.add(Generate.findCardByName(e));
        }
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
    public VariableResource.ResourceStyle getProductionStyle() {
        return products.getStyle();
    }

    @Override
    public ResQuant getCosts(Player player) {
        return costs.getResources(GameImpl.INSTANCE, player);
    }


    public static class Builder implements Card.Builder {

        private Type type;
        private Enum name;
        private String message = "";
        private VariableResource costs = new StaticResource();
        private VariableResource products = new StaticResource();
        private SpecialValue specialGold = new NotSoSpecial();
        private SpecialValue specialVps = new NotSoSpecial();
        private TradeType tradeType;
        private List<Game.Direction> tradeDirections = new ArrayList<>();
        private List<Enum> makesFreeEnum = new ArrayList<>();
        private List<Enum> makesThisFreeEnum = new ArrayList<>();
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
        public Builder setCosts(VariableResource costs) {
            this.costs = costs;
            return this;
        }

        @Override
        public Builder setProducts(VariableResource products) {
            this.products = products;
            return this;
        }

        @Override
        public Builder setSpecialGold(SpecialValue specialGold) {
            this.specialGold = specialGold;
            return this;
        }

        @Override
        public Builder setSpecialVps(SpecialValue specialVps) {
            this.specialVps = specialVps;
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
        public Builder setMakesThisFree(Enum card) {
            this.makesThisFreeEnum.add(card);
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
