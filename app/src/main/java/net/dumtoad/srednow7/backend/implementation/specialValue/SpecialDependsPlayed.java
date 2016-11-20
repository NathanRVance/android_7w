package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;

public class SpecialDependsPlayed implements SpecialValue {

    private Card.Type type;
    private int amount;
    private boolean includeAdjacent;
    private boolean includeOwn;

    public SpecialDependsPlayed(Card.Type type, int amount, boolean includeAdjacent, boolean includeOwn) {
        this.type = type;
        this.amount = amount;
        this.includeAdjacent = includeAdjacent;
        this.includeOwn = includeOwn;
    }

    @Override
    public int getSpecialValue(Player player) {
        int ret = 0;
        if (includeAdjacent) {
            for (Game.Direction direction : Game.Direction.values()) {
                for (Card c : Bus.bus.getGame().getPlayerDirection(player, direction).getPlayedCards()) {
                    if (c.getType() == type) {
                        ret += amount;
                    }
                }
            }
        }
        if (includeOwn) {
            for (Card c : player.getPlayedCards()) {
                if (c.getType() == type) {
                    ret += amount;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
