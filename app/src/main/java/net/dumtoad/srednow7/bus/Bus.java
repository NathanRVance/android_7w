package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.ui.UI;

import java.io.Serializable;

public interface Bus {

    Bus bus = new AndroidBus();

    Game getGame();

    UI getUI();

    void startNewGame();

    Serializable saveGame();

    void saveToMemory();

    boolean hasSave();

    void loadGame(Serializable contents);

    void loadGame();

    void deleteSave();
}
