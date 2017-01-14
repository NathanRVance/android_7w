package net.dumtoad.srednow7.backend;

import java.io.Serializable;

public interface Player extends Serializable {

    Wonder getWonder();

    Card nextWonderStage();

    CharSequence getName();

    CardList getPlayedCards();

    Score getScore();

    /**
     * Plays a card
     *
     * @param action action to be performed using the card
     * @param card   card to be played
     * @return result of this request
     */
    CardActionResult requestCardAction(CardAction action, Card card);

    boolean canAffordBuild(Card card);

    /**
     * Gets the hand of the player currently in turn
     *
     * @return list of cards in player's hand, sorted by type, or discards, if applicable
     */
    CardList getHand();

    boolean hasCouponFor(Card card);

    boolean canPlay7thCard();

    boolean canPlay1Free();

    boolean isPlayDiscard();

    boolean canBuildWonderFree();

    TradeBackend getTradeBackend();

    int getGold();

    void addGoldBuffer(int amount);

    enum CardAction {
        BUILD,
        DISCARD,
        WONDER
    }

    enum CardActionResult {
        OK,
        TRADED_WHEN_DISCARDING,
        TRADED_WHEN_CAN_BUILD_FREE,
        ALREADY_BUILT_ALL_WONDER_STAGES,
        INSUFFICIENT_RESOURCES,
        ALREADY_BUILT,
        OVERPAID
    }

}
