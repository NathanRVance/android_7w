package net.dumtoad.srednow7.backend.implementation.action;

import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.implementation.GameImpl;
import net.dumtoad.srednow7.backend.implementation.PlayerImpl;

public class LoseGold implements Action {

    private int amount;

    public LoseGold(int amount) {
        this.amount = amount;
    }

    @Override
    public void act(Player player) {
        for (PlayerImpl p : GameImpl.INSTANCE.getPlayers()) {
            if (p == player) continue;
            //if (p)
        }
    }
}
