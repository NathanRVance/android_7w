package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.ui.UI;

public interface Bus {

    Bus bus = new AndroidBus();

    Game getGame(Player player);

    Backend getBackend();

    UI getUI();
}
