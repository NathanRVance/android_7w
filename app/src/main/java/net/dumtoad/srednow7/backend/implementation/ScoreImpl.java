package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Score;
import net.dumtoad.srednow7.bus.Bus;

class ScoreImpl implements Score {

    private Player player;
    private int militaryLosses = 0;
    private int[] militaryVictories = new int[3];

    ScoreImpl(Player player) {
        this(player, 0, new int[3]);
    }

    ScoreImpl(Player player, int militaryLosses, int[] militaryVictories) {
        this.player = player;
        this.militaryLosses = militaryLosses;
        this.militaryVictories = militaryVictories;
    }

    int[] getMilitaryVictories() {
        return militaryVictories;
    }

    @Override
    public void resolveMilitary(int era) {
        int playerMilitary = 0;
        for (Card card : player.getPlayedCards()) {
            playerMilitary += card.getProducts(player).get(Card.Resource.SHIELD);
        }
        for (Game.Direction direction : Game.Direction.values()) {
            int otherMilitary = 0;
            for (Card card : Bus.bus.getGame().getPlayerDirection(player, direction).getPlayedCards()) {
                otherMilitary += card.getProducts(player).get(Card.Resource.SHIELD);
            }
            if (otherMilitary > playerMilitary) {
                militaryLosses++;
            } else if (otherMilitary < playerMilitary) {
                militaryVictories[era]++;
            }
        }
    }

    @Override
    public int getMilitaryVps() {
        int vps = 0;
        vps -= militaryLosses;
        vps += militaryVictories[0];
        vps += militaryVictories[1] * 3;
        vps += militaryVictories[2] * 5;
        return vps;
    }

    @Override
    public int getMilitaryLosses() {
        return militaryLosses;
    }

    @Override
    public int getGoldVps() {
        return player.getGold() / 3;
    }

    @Override
    public int getWonderVps() {
        int vps = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getType() == Card.Type.STAGE) {
                vps += card.getProducts(player).get(Card.Resource.VP);
                vps += card.getSpecialVps(player);
            }
        }
        return vps;
    }

    @Override
    public int getStructureVps() {
        int vps = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getType() == Card.Type.STRUCTURE) {
                vps += card.getProducts(player).get(Card.Resource.VP);
            }
        }
        return vps;
    }

    @Override
    public int getCommercialVps() {
        int vps = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getType() == Card.Type.COMMERCIAL) {
                vps += card.getProducts(player).get(Card.Resource.VP);
                vps += card.getSpecialVps(player);
            }
        }
        return vps;
    }

    @Override
    public int getGuildVps() {
        int vps = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getType() == Card.Type.GUILD) {
                vps += card.getProducts(player).get(Card.Resource.VP);
                vps += card.getSpecialVps(player);
            }
        }
        return vps;
    }

    @Override
    public int getScienceVps() {
        int sciences[] = new int[3];
        int wilds = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getProducts(player).get(Card.Resource.COMPASS) == 1 && card.getProducts(player).get(Card.Resource.GEAR) == 1
                    && card.getProducts(player).get(Card.Resource.TABLET) == 1) {
                wilds++;
            } else if (card.getProducts(player).get(Card.Resource.COMPASS) == 1) {
                sciences[0]++;
            } else if (card.getProducts(player).get(Card.Resource.GEAR) == 1) {
                sciences[1]++;
            } else if (card.getProducts(player).get(Card.Resource.TABLET) == 1) {
                sciences[2]++;
            }
        }
        return recurseForScience(sciences, wilds);
    }

    private int recurseForScience(int sciences[], int wilds) {
        int maxVps = 0;
        if (wilds > 0) {
            for (int i = 0; i < sciences.length; i++) {
                sciences[i]++;
                int vps = recurseForScience(sciences, wilds - 1);
                sciences[i]--;
                maxVps = (vps > maxVps) ? vps : maxVps;
            }
        } else {
            int vps = 0;
            int min = sciences[0];
            for (int i : sciences) {
                vps += i * i;
                min = (i < min) ? i : min;
            }
            vps += min * 7;
            return vps;
        }
        return maxVps;
    }

    @Override
    public int getTotalVPs() {
        int vps = getMilitaryVps();
        vps += getGoldVps();
        vps += getWonderVps();
        vps += getStructureVps();
        vps += getCommercialVps();
        vps += getGuildVps();
        vps += getScienceVps();
        return vps;
    }
}
