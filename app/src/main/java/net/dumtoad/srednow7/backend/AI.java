package net.dumtoad.srednow7.backend;

public interface AI {

    void selectWonderSide(int playerID);

    void doTurn();

    /**
     * Select the amount of gold to lose. The difference between the amount selected
     * and amountToLose will be taken as debt (-1 vp each).
     * @param amountToLose maximum amount of gold to be discarded
     * @return amount selected
     */
    int loseGold(int amountToLose);

}
