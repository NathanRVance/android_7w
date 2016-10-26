package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.player.Player;

public interface SpecialValue {

    int getSpecialValue(Player player);

    boolean isSpecial();

}
