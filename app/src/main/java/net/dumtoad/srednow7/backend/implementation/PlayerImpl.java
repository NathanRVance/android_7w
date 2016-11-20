package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.AI;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Score;
import net.dumtoad.srednow7.backend.TradeBackend;
import net.dumtoad.srednow7.backend.Wonder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class PlayerImpl implements Player {

    private static final long serialVersionUID = 7686145239817565666L;
    private String name;
    private boolean isAI;
    private transient AI ai;
    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private transient Wonder wonder;
    private CardList played = new CardListImpl();
    private transient ScoreImpl score = new ScoreImpl(this);
    private int gold = 3;
    private PlayBuffer playBuffer = new PlayBuffer();
    private boolean playedFree = false;
    private boolean hasFinishedTurn = false;
    private TradeBackend tradeBackend;
    private CardList hand;

    PlayerImpl(CharSequence name, boolean isAI, int ID) {
        this.name = name.toString();
        this.isAI = isAI;
        if (isAI) ai = new AIImpl(this);
        tradeBackend = new TradeBackendImpl(ID);
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
                } else if ((canPlay1Free() && !playedFree)) {
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
                GameImpl.INSTANCE.discard(card);
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
        GameImpl.INSTANCE.finishedTurn();
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
        } else if (hasCoupon && tradeBackend.hasTrade()) {
            throw new BadActionException("Don't trade, you can build for free");
        } else if (tradeBackend.overpaid(card)) {
            throw new BadActionException("Overpaid, undo some trades");
        } else if (!(hasCoupon || tradeBackend.canAfford(card) || (canPlay1Free() && !playedFree))) {
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
            GameImpl.INSTANCE.getPlayerDirection(this, Game.Direction.EAST).addGold(playBuffer.goldEast);
            addGold(playBuffer.goldEast * -1);

            GameImpl.INSTANCE.getPlayerDirection(this, Game.Direction.WEST).addGold(playBuffer.goldWest);
            addGold(playBuffer.goldWest * -1);

            played.add(playBuffer.card);
            addGold(playBuffer.card.getProducts().get(Card.Resource.GOLD));
            addGold(playBuffer.card.getCosts().get(Card.Resource.GOLD) * -1);
        }
        addGold(playBuffer.goldSelf);
        playBuffer.clear();
        tradeBackend.clear();
    }

    void startTurn() {
        hasFinishedTurn = false;
    }

    boolean hasFinishedTurn() {
        return hasFinishedTurn;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(score.getMilitaryLosses());
        s.writeObject(score.getMilitaryVictories());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        ai = new AIImpl(this);
        score = new ScoreImpl(this, s.readInt(), (int[]) s.readObject());
    }

    private static class PlayBuffer implements Serializable {

        private static final long serialVersionUID = 3309052048986578694L;
        transient Card card;
        int goldEast;
        int goldWest;
        int goldSelf;

        private void clear() {
            card = null;
            goldWest = 0;
            goldWest = 0;
            goldSelf = 0;
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            Enum name = (card == null) ? null : card.getEnum();
            s.writeObject(name);
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            card = Generate.getAllCards().get((Enum) s.readObject());
        }
    }
}
