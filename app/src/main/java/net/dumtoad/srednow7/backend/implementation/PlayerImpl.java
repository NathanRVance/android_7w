package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.AI;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Savable;
import net.dumtoad.srednow7.backend.Score;
import net.dumtoad.srednow7.backend.TradeBackend;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.bus.Bus;

import java.io.Serializable;

class PlayerImpl implements Player {

    private CharSequence name;
    private boolean isAI;
    private AI ai;
    private Wonder wonder;
    private CardList played = new CardListImpl();
    private Score score = new ScoreImpl(this);
    private int gold = 3;
    private PlayBuffer playBuffer = new PlayBuffer();
    private boolean justDiscarded = false;
    private boolean playedFree = false;
    private boolean hasFinishedTurn = false;
    private TradeBackend tradeBackend;
    private CardList hand;

    PlayerImpl(CharSequence name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
        if (isAI) ai = new AIImpl(this);
        tradeBackend = new TradeBackendImpl(this);
    }

    PlayerImpl() {
    }

    @Override
    public Wonder getWonder() {
        return wonder;
    }

    void setWonder(Wonder wonder) {
        this.wonder = wonder;
    }

    @Override
    public Card nextWonderStage() {
        for (Card stage : wonder.getStages()) {
            if (!played.contains(stage)) {
                return stage;
            }
        }
        return null;
    }

    @Override
    public CharSequence getName() {
        return name;
    }

    @Override
    public CardList getPlayedCards() {
        return played;
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void requestCardAction(CardAction action, Card card) throws BadActionException {
        switch (action) {
            case BUILD:
                checkCanAffordBuild(card);
                boolean hasCoupon = hasCouponFor(card) || isPlayDiscard();
                if (hasCoupon) {
                    setToBeBuilt(card, 0, 0);
                } else if (tradeBackend.canAfford(card)) {
                    setToBeBuilt(card, tradeBackend.getGoldSpent(Game.Direction.EAST),
                            tradeBackend.getGoldSpent(Game.Direction.WEST));
                } else if ((canPlay1Free() && !playedFreeThisEra())) {
                    setPlayedFree(true);
                    setToBeBuilt(card, 0, 0);
                } else {
                    //We shouldn't get here
                    throw new BadActionException("Yell at the programmer");
                }
                break;
            case DISCARD:
                if (tradeBackend.hasTrade()) {
                    throw new BadActionException("Don't trade when discarding");
                }
                Bus.bus.getGame().discard(card);
                addGoldBuffer(3);
                break;
            case WONDER:
                Card stage = nextWonderStage();
                if (stage == null) {
                    throw new BadActionException("Already built all stages");
                } else if (tradeBackend.canAfford(stage)) {
                    setToBeBuilt(stage, tradeBackend.getGoldSpent(Game.Direction.EAST),
                            tradeBackend.getGoldSpent(Game.Direction.WEST));
                } else {
                    throw new BadActionException("Insufficient resources");
                }
                break;
            default:
                throw new BadActionException("Yell at the programmer");
        }
        //If we haven't thrown an exception by now, the card has been played and we can remove it from the hand
        hand.remove(card);
        hasFinishedTurn = true;
        Bus.bus.getGame().finishedTurn();
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

    @Override
    public CardList getHand() {
        return hand;
    }

    void setHand(CardList hand) {
        this.hand = hand;
    }

    private void checkCanAffordBuild(Card card) throws BadActionException {
        boolean hasCoupon = hasCouponFor(card) || isPlayDiscard();
        if (getPlayedCards().contains(card)) {
            throw new BadActionException("Already built " + card.getEnum());
        } else if (hasCoupon && getTradeBackend().hasTrade()) {
            throw new BadActionException("Don't trade, you can build for free");
        } else if (tradeBackend.overpaid(card)) {
            throw new BadActionException("Overpaid, undo some trades");
        } else if (!(hasCoupon || tradeBackend.canAfford(card) || (canPlay1Free() && !playedFreeThisEra()))) {
            throw new BadActionException("Insufficient resources");
        }
    }

    @Override
    public boolean hasCouponFor(Card card) {
        for (Card c2 : card.makeThisFree()) {
            if (played.contains(c2)) return true;
        }
        return false;
    }

    @Override
    public boolean canPlay7thCard() {
        for (Card card : played) {
            if (card.providesAttribute(Card.Attribute.Play7thCard))
                return true;
        }
        return false;
    }

    @Override
    public boolean canPlay1Free() {
        for (Card card : played) {
            if (card.providesAttribute(Card.Attribute.Play1Free))
                return true;
        }
        return false;
    }

    @Override
    public boolean playedFreeThisEra() {
        return playedFree;
    }

    void setPlayedFree(boolean playedFree) {
        this.playedFree = playedFree;
    }

    @Override
    public boolean isPlayDiscard() {
        return !played.isEmpty() && played.get(played.size() - 1).providesAttribute(Card.Attribute.FreeBuild);
    }

    @Override
    public TradeBackend getTradeBackend() {
        return tradeBackend;
    }

    boolean isAI() {
        return isAI;
    }

    AI getAI() {
        return ai;
    }

    @Override
    public int getGold() {
        return gold;
    }

    private void addGold(int amount) {
        gold += amount;
    }

    private void addGoldBuffer(int amount) {
        playBuffer.goldSelf += amount;
    }

    private void setToBeBuilt(Card card, int goldEast, int goldWest) {
        playBuffer.card = card;
        addGoldBuffer(card.getSpecialGold(this));
        playBuffer.goldEast = goldEast;
        playBuffer.goldWest = goldWest;
    }

    void resolveBuild() {
        if (playBuffer.card != null) {
            ((PlayerImpl) Bus.bus.getGame().getPlayerDirection(this, Game.Direction.EAST)).addGold(playBuffer.goldEast);
            addGold(playBuffer.goldEast * -1);

            ((PlayerImpl) Bus.bus.getGame().getPlayerDirection(this, Game.Direction.WEST)).addGold(playBuffer.goldWest);
            addGold(playBuffer.goldWest * -1);

            played.add(playBuffer.card);
            addGold(playBuffer.card.getProducts().get(Card.Resource.GOLD));
            addGold(playBuffer.card.getCosts().get(Card.Resource.GOLD) * -1);
        }
        addGold(playBuffer.goldSelf);
        playBuffer.clear();
        tradeBackend = new TradeBackendImpl(this);
    }

    void startTurn() {
        hasFinishedTurn = false;
    }

    boolean hasFinishedTurn() {
        return hasFinishedTurn;
    }

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[11];
        contents[0] = played.getContents();
        contents[1] = score.getContents();
        contents[2] = gold;
        contents[3] = isAI();
        if (isAI()) contents[4] = getAI().getContents();
        contents[5] = name.toString();
        contents[6] = playBuffer.getContents();
        contents[7] = justDiscarded;
        contents[8] = playedFree;
        contents[9] = hasFinishedTurn;
        contents[10] = tradeBackend.getContents();
        return contents;
    }

    @Override
    public void restoreContents(Serializable contents) throws Exception {
        Serializable[] in = (Serializable[]) contents;
        played.restoreContents(in[0]);
        score.restoreContents(in[1]);
        gold = (int) in[2];
        isAI = (boolean) in[3];
        if (isAI) {
            ai = new AIImpl(this);
            ai.restoreContents(in[4]);
        }
        name = (String) in[5];
        playBuffer.restoreContents(in[6]);
        justDiscarded = (boolean) in[7];
        playedFree = (boolean) in[8];
        hasFinishedTurn = (boolean) in[9];
        tradeBackend = new TradeBackendImpl(this);
        tradeBackend.restoreContents(in[10]);
    }

    private class PlayBuffer implements Savable {

        Card card;
        int goldEast;
        int goldWest;
        int goldSelf;

        private void clear() {
            card = null;
            goldWest = 0;
            goldWest = 0;
            goldSelf = 0;
        }

        @Override
        public Serializable getContents() {
            Serializable[] contents = new Serializable[5];
            boolean hasBuffer = card != null;
            contents[0] = hasBuffer;
            if(hasBuffer) {
                contents[1] = card.getEnum();
            }
            contents[2] = goldEast;
            contents[3] = goldWest;
            contents[4] = goldSelf;
            return contents;
        }

        @Override
        public void restoreContents(Serializable contents) throws Exception {
            Serializable[] in = (Serializable[]) contents;
            if((boolean) in[0]) {
                card = Bus.bus.getGame().getCardCreator().getAllCards().get((Enum) in[1]);
            }
            goldEast = (int) in[2];
            goldWest = (int) in[3];
            goldSelf = (int) in[4];
        }
    }
}
