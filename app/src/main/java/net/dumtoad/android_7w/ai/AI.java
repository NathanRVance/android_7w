package net.dumtoad.android_7w.ai;

import net.dumtoad.android_7w.cards.Player;
import net.dumtoad.android_7w.cards.Wonder;

/**
 * Created by nathav63 on 8/11/15.
 */
public class AI {

    private final Player player;

    public AI(Player player) {
        this.player = player;
    }

    public void setWonderSide(Wonder wonder) {
        wonder.setSide(true);
    }

}
