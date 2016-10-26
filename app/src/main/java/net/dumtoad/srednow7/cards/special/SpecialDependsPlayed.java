package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.player.Player;

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
            for (boolean direction : new boolean[]{true, false}) {
                for (Card c : MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getPlayedCards()) {
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
