package net.dumtoad.srednow7.backend;

public interface AI extends Savable {

    void selectWonderSide(int playerID);

    void doTurn();

}
