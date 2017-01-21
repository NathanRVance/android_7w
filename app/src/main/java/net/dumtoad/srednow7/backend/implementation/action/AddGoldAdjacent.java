package net.dumtoad.srednow7.backend.implementation.action;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.implementation.GameImpl;

public class AddGoldAdjacent implements Action {

    private int amount;

    public AddGoldAdjacent(int amount) {
        this.amount = amount;
    }

    @Override
    public void act(Player player) {
        for(Game.Direction direction : Game.Direction.values()) {
            GameImpl.INSTANCE.getPlayerDirection(player, direction).addGold(amount);
        }
    }

    @Override
    public int getPrecidence() {
        return 0;
    }
}
