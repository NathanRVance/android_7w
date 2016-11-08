package net.dumtoad.srednow7.backend;

public interface Player extends Savable {

    Wonder getWonder();

    void setWonder(Wonder wonder);

    Card nextWonderStage();

    CharSequence getName();

    CardList getPlayedCards();

    Score getScore();

    boolean hasCouponFor(Card card);

    boolean canPlay7thCard();

    boolean canPlay1Free();

    boolean playedFreeThisEra();

    void setPlayedFree(boolean playedFree);

    boolean mostRecentPlayedCardGivesFreeBuild();

    boolean isAI();

    AI getAI();

    int getGold();

    /**
     * Adds gold directly
     * @param amount of gold to add
     */
    void addGold(int amount);

    /**
     * Adds gold at end of turn
     * @param amount of gold to add
     */
    void addGoldBuffer(int amount);

    /**
     * Adds card to buffer to be played. Also handles adding special gold to the buffer
     * @param card card to be played
     * @param goldEast amount of gold to be transferred to the east
     * @param goldWest amount of gold to be transferred to the west
     */
    void setToBeBuilt(Card card, int goldEast, int goldWest);

    /**
     * Resolves building the card, giving gold to trade partners (and removing it from self),
     * adding gold from card's static production, and adding gold from the buffer
     */
    void resolveBuild();

}
