package net.dumtoad.srednow7.backend.implementation.Special;

import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;

public class BestAdjacentGuildVps implements SpecialValue {

    @Override
    public int getSpecialValue(Player player) {
        int max = 0;
        for (Backend.Direction direction : Backend.Direction.values()) {
            for (Card c : Bus.bus.getBackend().getPlayerDirection(player, direction).getPlayedCards()) {
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
