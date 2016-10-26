package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.player.Player;

public class BestAdjacentGuildVps implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        int max = 0;
        for (boolean direction : new boolean[]{true, false}) {
            for (Card c : MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getPlayedCards()) {
                if (c.getType() == Card.Type.GUILD) {
                    int vps = c.getProducts().get(Card.Resource.VP) + c.getSpecialVps(player);
                    max = (vps > max) ? vps : max;
                }
            }
        }
        return max;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
