package net.dumtoad.srednow7.backend;

import net.dumtoad.srednow7.ui.TradeUI;

public interface TradeBackend extends Savable {

    void makeTrade(Card.Resource resource, int amount, Backend.Direction direction);

    void refresh(TradeUI tradeUI, Backend.Direction direction);

    /**
     * Gets the number of each resource leftover after building card, factoring in current trades
     * @param card card to be built
     * @return the resources left over
     */
    ResQuant getLeftoverResources(Card card);

    boolean hasTrade();

    boolean overpaid(Card card);

    /**
     * Can afford both resources and gold costs (factors in current trades)
     * @param card to check if we can afford
     * @return boolean indicating if we can afford it
     */
    boolean canAfford(Card card);

    int getGoldSpent(Backend.Direction direction);

    void clear();

    Card.Resource[] tradeable = new Card.Resource[]{Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY,
            Card.Resource.ORE, Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER};

}
