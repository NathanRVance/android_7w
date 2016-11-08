package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.TradeBackend;
import net.dumtoad.srednow7.bus.Bus;

import java.io.Serializable;

class GameImpl implements Game {

    private Player player;
    private CardList hand;
    private TradeBackend tradeBackend;
    private boolean isFinished = false;

    GameImpl(Player player, CardList hand) {
        this.player = player;
        this.hand = hand;
        tradeBackend = new TradeBackendImpl(player);
    }

    @Override
    public void requestCardAction(CardAction action, Card card) throws BadActionException {
        switch(action) {
            case BUILD:
                checkCanAffordBuild(card);
                boolean hasCoupon = player.hasCouponFor(card) || isPlayDiscard();
                if (hasCoupon) {
                    player.setToBeBuilt(card, 0, 0);
                } else if (tradeBackend.canAfford(card)) {
                    player.setToBeBuilt(card, tradeBackend.getGoldSpent(Backend.Direction.EAST),
                            tradeBackend.getGoldSpent(Backend.Direction.WEST));
                } else if ((player.canPlay1Free() && ! player.playedFreeThisEra())) {
                    player.setPlayedFree(true);
                    player.setToBeBuilt(card, 0, 0);
                } else {
                    //We shouldn't get here
                    throw new BadActionException("Yell at the programmer");
                }
                break;
            case DISCARD:
                if(tradeBackend.hasTrade()) {
                    throw new BadActionException("Don't trade when discarding");
                }
                Bus.bus.getBackend().discard(card);
                player.addGoldBuffer(3);
                break;
            case WONDER:
                Card stage = player.nextWonderStage();
                if (stage == null) {
                    throw new BadActionException("Already built all stages");
                } else if (tradeBackend.canAfford(stage)) {
                    player.setToBeBuilt(stage, tradeBackend.getGoldSpent(Backend.Direction.EAST),
                            tradeBackend.getGoldSpent(Backend.Direction.WEST));
                } else {
                    throw new BadActionException("Insufficient resources");
                }
                break;
            default:
                throw new BadActionException("Yell at the programmer");
        }
        //If we haven't thrown an exception by now, the card has been played and we can remove it from the hand
        hand.remove(card);
        isFinished = true;
        Bus.bus.getBackend().finishedTurn();
    }

    @Override
    public boolean canAffordBuild(Card card) {
        try {
            checkCanAffordBuild(card);
        } catch (BadActionException e) {
            return false;
        }
        return true;
    }

    private void checkCanAffordBuild(Card card) throws BadActionException {
        boolean hasCoupon = player.hasCouponFor(card) || isPlayDiscard();
        if (player.getPlayedCards().contains(card)) {
            throw new BadActionException("Already built " + card.getEnum());
        } else if (hasCoupon && getTradeBackend().hasTrade()) {
            throw new BadActionException("Don't trade, you can build for free");
        } else if (tradeBackend.overpaid(card)) {
            throw new BadActionException("Overpaid, undo some trades");
        } else if (! (hasCoupon || tradeBackend.canAfford(card) || (player.canPlay1Free() && ! player.playedFreeThisEra()))) {
            throw new BadActionException("Insufficient resources");
        }
    }

    @Override
    public CardList getHandInTurn() {
        return hand;
    }

    @Override
    public boolean isPlayDiscard() {
        return player.mostRecentPlayedCardGivesFreeBuild();
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public TradeBackend getTradeBackend() {
        return tradeBackend;
    }

    @Override
    public boolean hasFinishedTurn() {
        return isFinished;
    }

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[2];
        contents[0] = tradeBackend.getContents();
        contents[1] = isFinished;
        return contents;
    }

    @Override
    public void restoreContents(Serializable contents) throws Exception {
        Serializable[] in = (Serializable[]) contents;
        tradeBackend.restoreContents(in[0]);
        isFinished = (boolean) in[1];
    }
}
