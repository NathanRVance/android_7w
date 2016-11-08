package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.implementation.BackendImpl;
import net.dumtoad.srednow7.ui.UI;
import net.dumtoad.srednow7.ui.UIFacade;

class AndroidBus implements Bus {

    private Backend backend = BackendImpl.INSTANCE;
    private UI ui = new UIFacade();

    @Override
    public Game getGame(Player player) {
        return backend.getGame(player);
    }

    @Override
    public Backend getBackend() {
        return backend;
    }

    @Override
    public UI getUI() {
        return ui;
    }
}
