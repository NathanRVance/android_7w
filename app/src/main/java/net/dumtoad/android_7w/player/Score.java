package net.dumtoad.android_7w.player;

import android.os.Bundle;

import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.Special;
import net.dumtoad.android_7w.controller.MasterViewController;

public class Score {

    private Player player;
    private MasterViewController mvc;
    private int militaryLosses = 0;
    private int[] militaryVictories = new int[3];

    public Score(Player player, MasterViewController mvc) {
        this.player = player;
        this.mvc = mvc;
    }

    public Score(Player player, MasterViewController mvc, Bundle savedInstanceState) {
        this.player = player;
        this.mvc = mvc;
        militaryLosses = savedInstanceState.getInt("militaryLosses");
        militaryVictories = savedInstanceState.getIntArray("militaryVictories");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putInt("militaryLosses", militaryLosses);
        outstate.putIntArray("militaryVictories", militaryVictories);
        return outstate;
    }

    public void resolveMilitary(int era) {
        int playerMilitary = 0;
        for(Card card : player.getPlayedCards()) {
            playerMilitary += card.getProducts().get(Card.Resource.SHIELD);
        }
        for(boolean direction : new boolean[]{true, false}) {
            int otherMilitary = 0;
            for(Card card : mvc.getTableController().getPlayerDirection(direction, player).getPlayedCards()) {
                otherMilitary += card.getProducts().get(Card.Resource.SHIELD);
            }
            if(otherMilitary > playerMilitary) {
                militaryLosses++;
            } else if(otherMilitary < playerMilitary) {
                militaryVictories[era]++;
            }
        }
    }

    public int getMilitaryVps() {
        int vps = 0;
        vps -= militaryLosses;
        vps += militaryVictories[0];
        vps += militaryVictories[1] * 3;
        vps += militaryVictories[2] * 5;
        return vps;
    }

    public int getMilitaryLosses() {
        return militaryLosses;
    }

    public int getScienceVps() {
        int sciences[] = new int[3];
        int wilds = 0;
        for(Card card : player.getPlayedCards()) {
            if(card.getProducts().get(Card.Resource.COMPASS) == 1 && card.getProducts().get(Card.Resource.GEAR) == 1
                    && card.getProducts().get(Card.Resource.TABLET) == 1) {
                wilds++;
            } else if(card.getProducts().get(Card.Resource.COMPASS) == 1) {
                sciences[0]++;
            } else if(card.getProducts().get(Card.Resource.GEAR) == 1) {
                sciences[1]++;
            } else if(card.getProducts().get(Card.Resource.TABLET) == 1) {
                sciences[2]++;
            }
        }
        return recurseForScience(sciences, wilds);
    }

    private int recurseForScience(int sciences[], int wilds) {
        int maxVps = 0;
        if(wilds > 0) {
            for(int i = 0; i < sciences.length; i++) {
                sciences[i]++;
                int vps = recurseForScience(sciences, wilds - 1);
                sciences[i]--;
                maxVps = (vps > maxVps)? vps : maxVps;
            }
        } else {
            return getScienceVps(sciences);
        }
        return maxVps;
    }

    private int getScienceVps(int sciences[]) {
        int vps = 0;
        int min = sciences[0];
        for(int i : sciences) {
            vps += i * i;
            min = (i < min)? i : min;
        }
        vps += min * 7;
        return vps;
    }

    public int getVPs() {
        int vps = getMilitaryVps();
        vps += player.getGold() / 3;
        for(Card card : player.getPlayedCards()) {
            vps += card.getProducts().get(Card.Resource.VP);
            if(Special.isSpecialVps(card, player)) {
                vps += Special.getSpecialVps(card, player);
            }
        }
        vps += getScienceVps();
        return vps;
    }

}
