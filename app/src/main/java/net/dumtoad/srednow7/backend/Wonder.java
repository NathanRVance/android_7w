package net.dumtoad.srednow7.backend;

public interface Wonder {

    String getName();

    Card.Resource getResource();

    CardList getStages();

    interface Builder {
        Builder addStage(Card stage);

        Wonder build();
    }

}
