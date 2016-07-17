package net.dumtoad.srednow7.cards;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.player.Player;

public class Special {

    public static boolean isSpecialGold(Card card, Player player) {
        return getSpecialGold(card, player) != -1;
    }

    public static int getSpecialGold(Card card, Player player) {
        if(card.getName() == Generate.Era1.Vineyard) {
            return valueHelper(Card.Type.RESOURCE, 1, player, true, true);
        } else if(card.getName() == Generate.Era1.Bazaar) {
            return valueHelper(Card.Type.INDUSTRY, 2, player, true, true);
        } else if(card.getName() == Generate.Era2.Haven) {
            return valueHelper(Card.Type.RESOURCE, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Lighthouse) {
            return valueHelper(Card.Type.COMMERCIAL, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Chamber_Of_Commerce) {
            return valueHelper(Card.Type.INDUSTRY, 2, player, false, true);
        } else if(card.getName() == Generate.Era2.Arena) {
            return valueHelper(Card.Type.STAGE, 3, player, false, true);
        }
        return -1;
    }

    public static boolean isSpecialVps(Card card, Player player) {
        return getSpecialVps(card, player) != -1;
    }

    public static int getSpecialVps(Card card, Player player) {
        if(card.getName() == Generate.Era2.Haven) {
            return valueHelper(Card.Type.RESOURCE, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Lighthouse) {
            return valueHelper(Card.Type.COMMERCIAL, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Chamber_Of_Commerce) {
            return valueHelper(Card.Type.INDUSTRY, 2, player, false, true);
        } else if(card.getName() == Generate.Era2.Arena) {
            return valueHelper(Card.Type.STAGE, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Workers_Guild) {
            return valueHelper(Card.Type.RESOURCE, 1, player, true, false);
        } else if(card.getName() == Generate.Era2.Craftmens_Guild) {
            return valueHelper(Card.Type.INDUSTRY, 2, player, true, false);
        } else if(card.getName() == Generate.Era2.Traders_Guild) {
            return valueHelper(Card.Type.COMMERCIAL, 1, player, true, false);
        } else if(card.getName() == Generate.Era2.Philosophers_Guild) {
            return valueHelper(Card.Type.SCIENCE, 1, player, true, false);
        } else if(card.getName() == Generate.Era2.Spy_Guild) {
            return valueHelper(Card.Type.MILITARY, 1, player, true, false);
        } else if(card.getName() == Generate.Era2.Strategy_Guild) {
            int ret = 0;
            for (boolean direction : new boolean[]{true, false}) {
                ret += MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getScore().getMilitaryLosses();
            }
            return ret;
        } else if(card.getName() == Generate.Era2.Shipowners_Guild) {
            return valueHelper(Card.Type.RESOURCE, 1, player, false, true)
                    + valueHelper(Card.Type.INDUSTRY, 1, player, false, true)
                    + valueHelper(Card.Type.GUILD, 1, player, false, true);
        } else if(card.getName() == Generate.Era2.Magistrates_Guild) {
            return valueHelper(Card.Type.STRUCTURE, 1, player, true, false);
        } else if(card.getName() == Generate.Era2.Builders_Guild) {
            return valueHelper(Card.Type.STAGE, 1, player, true, true);
        } else if(player.getWonder().getName().equals(Generate.Wonders.The_Statue_of_Zeus_in_Olympia)
                && ! player.getWonderSide() && card.getName() == Generate.WonderStages.Stage_3) {
            int max = 0;
            for(boolean direction : new boolean[]{true, false}) {
                for(Card c : MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getPlayedCards()) {
                    if(c.getType() == Card.Type.GUILD) {
                        int vps = c.getProducts().get(Card.Resource.VP) + getSpecialVps(c, player);
                        max = (vps > max)? vps : max;
                    }
                }
            }
            return max;
        }
        return -1;
    }

    private static int valueHelper(Card.Type type, int amount, Player player, boolean includeAdjacent, boolean includeOwn) {
        int ret = 0;
        if(includeAdjacent) {
            for (boolean direction : new boolean[]{true, false}) {
                for (Card c : MainActivity.getMasterViewController().getTableController().getPlayerDirection(player, direction).getPlayedCards()) {
                    if (c.getType() == type) {
                        ret += amount;
                    }
                }
            }
        }
        if(includeOwn) {
            for(Card c : player.getPlayedCards()) {
                if(c.getType() == type) {
                    ret += amount;
                }
            }
        }
        return ret;
    }

    public static boolean specialAction(Card card, Player player) {
        if(card.getType() == Card.Type.STAGE && player.getWonder().getName() == Generate.Wonders.The_Mausoleum_of_Halicarnassus) {
            if(card.getName() == Generate.WonderStages.Stage_2 ||
                    !player.getWonderSide() && (card.getName() == Generate.WonderStages.Stage_1 || card.getName() == Generate.WonderStages.Stage_3)) {
                int playerNum = MainActivity.getMasterViewController().getPlayerNum(player);
                MainActivity.getMasterViewController().getTableController().getTurnController().startTurn(playerNum, true);
                return true;
            }
        }
        else if(card.getType() == Card.Type.STAGE && player.getWonder().getName() == Generate.Wonders.The_Statue_of_Zeus_in_Olympia
                && player.getWonderSide() && card.getName() == Generate.WonderStages.Stage_2) {
            player.refreshFreeBuild();
            //Returning true would interrupt the turn, which we don't want.
        }
        return false;
    }

    public enum TradeType {
        resource,
        industry,
        none
    }

    public static TradeType getTradeType(Card card, Player player) {
        if((player.getWonder().getName().equals(Generate.Wonders.The_Statue_of_Zeus_in_Olympia)
                && ! player.getWonderSide() && card.getName() == Generate.WonderStages.Stage_1)
                || card.getName() == Generate.Era0.West_Trading_Post
                || card.getName() == Generate.Era0.East_Trading_Post)
            return TradeType.resource;
        else if(card.getName() == Generate.Era0.Marketplace)
            return TradeType.industry;
        else return TradeType.none;
    }

}
