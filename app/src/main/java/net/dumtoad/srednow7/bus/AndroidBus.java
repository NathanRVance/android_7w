package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.implementation.GameImpl;
import net.dumtoad.srednow7.ui.UI;
import net.dumtoad.srednow7.ui.UIFacade;

class AndroidBus implements Bus {

    private Game game = GameImpl.INSTANCE;
    private UI ui = new UIFacade();

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public UI getUI() {
        return ui;
    }

    @Override
    public void reset() {
        ui.reset();
        game.reset();
    }
}
