package net.dumtoad.srednow7.bus;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.implementation.GameImpl;
import net.dumtoad.srednow7.ui.UI;
import net.dumtoad.srednow7.ui.UIFacade;

import java.io.Serializable;

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
    public void startNewGame() {
        ui.reset();
        game.reset();
        game.startNewGame();
    }

    @Override
    public Serializable saveGame() {
        Serializable[] contents = new Serializable[2];
        contents[0] = game.getContents();
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
        ui = (UI) in[1];
        try {
            game.restoreContents(in[0]);
        } catch (Exception e) {
            ui.reset();
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
