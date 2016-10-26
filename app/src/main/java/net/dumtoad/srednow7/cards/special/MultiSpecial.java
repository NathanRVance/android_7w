package net.dumtoad.srednow7.cards.special;

import net.dumtoad.srednow7.player.Player;

public class MultiSpecial implements SpecialValue {

    private SpecialValue[] specialValues;

    public MultiSpecial(SpecialValue... specialValues) {
        this.specialValues = specialValues;
    }

    @Override
    public int getSpecialValue(Player player) {
        int total = 0;
        for(SpecialValue specialValue : specialValues) {
            total += specialValue.getSpecialValue(player);
        }
        return total;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
