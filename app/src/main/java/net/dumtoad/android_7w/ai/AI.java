package net.dumtoad.android_7w.ai;

import net.dumtoad.android_7w.cards.Player;

public class AI {

    private final Player player;


    public AI(Player player) {
        this.player = player;
        player.setWonderSide(true);
    }

}
