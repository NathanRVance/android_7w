package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.implementation.GameImpl;
import net.dumtoad.srednow7.ui.UI;
import net.dumtoad.srednow7.ui.UIFacade;

import java.io.Serializable;

class AndroidBus implements Bus {

    private Game game = new GameImpl();
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
    public void startNewGame() {
        ui.reset();
        game.reset();
        game.startNewGame();
    }

    @Override
    public Serializable saveGame() {
        Serializable[] contents = new Serializable[2];
        contents[0] = game;
        contents[1] = ui;
        return contents;
    }

    @Override
    public void saveToMemory() {
        SaveUtil.saveGame(saveGame());
    }

    @Override
    public boolean hasSave() {
        return SaveUtil.hasSave();
    }

    @Override
    public void loadGame(Serializable contents) {
        Serializable[] in = (Serializable[]) contents;
        try {
            game = (Game) in[0];
            ui = (UI) in[1];
        } catch (Exception e) {
            e.printStackTrace();
            ui = new UIFacade();
            game = new GameImpl();
            game.startNewGame();
        }
    }

    @Override
    public void loadGame() {
        loadGame(SaveUtil.loadGame());
    }

    @Override
    public void deleteSave() {
        SaveUtil.deleteSave();
    }
}
