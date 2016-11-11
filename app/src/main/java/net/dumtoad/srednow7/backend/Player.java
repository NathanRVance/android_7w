package net.dumtoad.srednow7.backend;

public interface Player extends Savable {

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
     * @throws BadActionException if card can't be played. Details in exception message
     */
    void requestCardAction(CardAction action, Card card) throws BadActionException;

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

    boolean playedFreeThisEra();

    boolean isPlayDiscard();

    TradeBackend getTradeBackend();

    int getGold();

    enum CardAction {
        BUILD,
        DISCARD,
        WONDER
    }

    class BadActionException extends Exception {
        public BadActionException(String s) {
            super(s);
        }
    }

}
