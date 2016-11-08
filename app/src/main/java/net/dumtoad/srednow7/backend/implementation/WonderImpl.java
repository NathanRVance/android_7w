package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Wonder;

class WonderImpl implements Wonder {

    private Enum name;
    private Card.Resource resource;
    private CardList stages = new CardListImpl();

    private WonderImpl(Enum name, Card.Resource resource) {
        this.name = name;
        this.resource = resource;
    }

    @Override
    public Enum getEnum() {
        return name;
    }

    @Override
    public Card.Resource getResource() {
        return resource;
    }

    @Override
    public CardList getStages() {
        return stages;
    }

    public static class Builder implements Wonder.Builder {

        WonderImpl wonder;

        public Builder(Enum name, Card.Resource res) {
            wonder = new WonderImpl(name, res);
        }

        @Override
        public Wonder.Builder addStage(Card stage) {
            wonder.stages.add(stage);
            return this;
        }

        @Override
        public Wonder build() {
            return wonder;
        }
    }
}
