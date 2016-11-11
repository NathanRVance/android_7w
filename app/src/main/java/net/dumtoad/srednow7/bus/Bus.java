package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.ui.UI;

public interface Bus {

    Bus bus = new AndroidBus();

    Game getGame();

    UI getUI();

    void reset();
}
