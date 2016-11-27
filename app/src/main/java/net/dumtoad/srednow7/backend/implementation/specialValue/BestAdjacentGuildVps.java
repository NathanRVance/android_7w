package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;

public class BestAdjacentGuildVps implements SpecialValue {

    @Override
    public int getSpecialValue(Game game, Player player) {
        int max = 0;
        for (Game.Direction direction : Game.Direction.values()) {
            for (Card c : game.getPlayerDirection(player, direction).getPlayedCards()) {
                if (c.getType() == Card.Type.GUILD) {
                    int vps = c.getProducts(player).get(Card.Resource.VP) + c.getSpecialVps(player);
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
