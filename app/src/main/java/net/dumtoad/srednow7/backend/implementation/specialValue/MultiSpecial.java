package net.dumtoad.srednow7.backend.implementation.specialValue;

import net.dumtoad.srednow7.backend.Player;

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
