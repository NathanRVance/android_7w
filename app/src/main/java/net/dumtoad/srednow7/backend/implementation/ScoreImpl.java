package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.Score;
import net.dumtoad.srednow7.backend.implementation.variableResource.VariableResource;
import net.dumtoad.srednow7.bus.Bus;

class ScoreImpl implements Score {

    private static final Card.Resource[] sciences = new Card.Resource[]{Card.Resource.COMPASS, Card.Resource.TABLET, Card.Resource.GEAR};
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
            for (Card card : GameImpl.INSTANCE.getPlayerDirection(player, direction).getPlayedCards()) {
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
        ResQuant selected = new ResQuantImpl();
        int wilds = 0;
        ResQuant stealable = new ResQuantImpl();
        int numStolen = 0;
        for (Card card : player.getPlayedCards()) {
            if (card.getProductionStyle() == VariableResource.ResourceStyle.STOLEN_SCIENCE) {
                stealable = card.getProducts(player); // = not add because they are all the same
                numStolen++;
            } else if (card.getProducts(player).get(Card.Resource.COMPASS) == 1 && card.getProducts(player).get(Card.Resource.GEAR) == 1
                    && card.getProducts(player).get(Card.Resource.TABLET) == 1) {
                wilds++;
            } else {
                selected.addResources(card.getProducts(player));
            }
        }
        return recurseForScience(selected, wilds, stealable, numStolen);
    }

    private int recurseForScience(ResQuant selected, int wilds, ResQuant stealable, int numStolen) {
        int maxVps = 0;
        if (wilds > 0 || numStolen > 0) {
            //It isn't obvious to me how to combine the following if/else
            if(wilds > 0) {
                for (Card.Resource resource : sciences) {
                    selected.add(resource, 1);
                    int vps = recurseForScience(selected, wilds - 1, stealable, numStolen);
                    selected.add(resource, -1);
                    maxVps = (vps > maxVps) ? vps : maxVps;
                }
            } else { //Processing stolen resources
                for (Card.Resource resource : sciences) {
                    if (stealable.get(resource) > 0) {
                        selected.add(resource, 1);
                        stealable.add(resource, -1);
                        int vps = recurseForScience(selected, wilds, stealable, numStolen - 1);
                        selected.add(resource, -1);
                        stealable.add(resource, 1);
                        maxVps = (vps > maxVps) ? vps : maxVps;
                    }
                }
            }
        } else {
            int vps = 0;
            int min = Integer.MAX_VALUE;
            for (Card.Resource resource : sciences) {
                int numProduced = selected.get(resource);
                vps += numProduced * numProduced;
                min = (numProduced < min) ? numProduced : min;
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
