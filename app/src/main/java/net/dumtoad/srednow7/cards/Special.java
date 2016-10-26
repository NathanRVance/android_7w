package net.dumtoad.srednow7.cards;

import net.dumtoad.srednow7.player.Player;

public class Special {

    private static boolean isPlayDiscard(Card card, Player player) {
        if (card.getType() == Card.Type.STAGE && player.getWonder().getName() == Generate.Wonders.The_Mausoleum_of_Halicarnassus) {
            if (card.getName() == Generate.WonderStages.Stage_2 ||
                    !player.getWonderSide() && (card.getName() == Generate.WonderStages.Stage_1 || card.getName() == Generate.WonderStages.Stage_3)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOneFreeCard(Card card, Player player) {
        return (card.getType() == Card.Type.STAGE && player.getWonder().getName() == Generate.Wonders.The_Statue_of_Zeus_in_Olympia
                && player.getWonderSide() && card.getName() == Generate.WonderStages.Stage_2);
    }

    private static boolean isPlay7thCard(Card card, Player player) {
        return (player.getWonder().getName() == Generate.Wonders.The_Hanging_Gardens_of_Babylon
                && !player.getWonderSide()
                && card.getName() == Generate.WonderStages.Stage_2);
    }

    public static boolean isSpecialAction(Card card, Player player) {
        if (isPlayDiscard(card, player)) {
            return true;
        } else if (isOneFreeCard(card, player)) {
            return true;
        } else if (isPlay7thCard(card, player)) {
            return true;
        }
        return false;
    }

    public static boolean specialAction(Card card, Player player) {
        if (isPlayDiscard(card, player)) {
            player.startTurn(true);
            return true;
        } else if (isOneFreeCard(card, player)) {
            player.refreshFreeBuild();
            //Returning true would interrupt the turn, which we don't want.
        }
        return false;
    }

}
