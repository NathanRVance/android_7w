package net.dumtoad.srednow7.ui;

public interface UI {

    void displaySetup();

    void displayWonderSideSelect(int playerID);

    void displayTurn(int playerID);

    void displayEndOfGame();

    void invalidateView();

    void reset();

}
