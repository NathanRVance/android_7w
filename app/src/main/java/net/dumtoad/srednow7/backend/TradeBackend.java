package net.dumtoad.srednow7.backend;

public interface TradeBackend extends Savable {

    Card.Resource[] tradeable = new Card.Resource[]{Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY,
            Card.Resource.ORE, Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER};

    void makeTrade(Card.Resource resource, int amount, Game.Direction direction);

    int goldAvailable();

    ResQuant resourcesForSale(Game.Direction direction);

    ResQuant resourcesBought(Game.Direction direction);

    ResQuant prices(Game.Direction direction);

    /**
     * Gets the number of each resource leftover after building card, factoring in current trades
     *
     * @param card card to be built
     * @return the resources left over
     */
    ResQuant getLeftoverResources(Card card);

    boolean hasTrade();

    boolean overpaid(Card card);

    /**
     * Can afford both resources and gold costs (factors in current trades)
     *
     * @param card to check if we can afford
     * @return boolean indicating if we can afford it
     */
    boolean canAfford(Card card);

    int getGoldSpent(Game.Direction direction);

    void clear();

}
