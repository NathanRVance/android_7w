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

public class PlayerImpl implements Player {

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
    private TradeBackendImpl tradeBackend;
    private CardList hand;

    PlayerImpl(CharSequence name, boolean isAI) {
        this.name = name.toString();
        this.isAI = isAI;
        if (isAI) ai = new AIImpl(this);
        tradeBackend = new TradeBackendImpl(this);
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
    public CardActionResult requestCardAction(CardAction action, Card card) {
        switch (action) {
            case BUILD:
                CardActionResult result = checkCanAffordBuild(card);
                if(result != CardActionResult.OK) return result;
                boolean hasCoupon = hasCouponFor(card) || isPlayDiscard();
                if (hasCoupon) {
                    setToBeBuilt(card, 0, 0);
                } else if (tradeBackend.canAfford(card)) {
                    setToBeBuilt(card, tradeBackend.getGoldSpent(Game.Direction.EAST),
                            tradeBackend.getGoldSpent(Game.Direction.WEST));
                } else if ((hasAttribute(Card.Attribute.PLAY_1_FREE) && !playedFree)) {
                    setPlayedFree(true);
                    setToBeBuilt(card, 0, 0);
                } else {
                    //We shouldn't get here
                    throw new RuntimeException("This shouldn't be reached");
                }
                break;
            case DISCARD:
                if (tradeBackend.hasTrade()) {
                    return CardActionResult.TRADED_WHEN_DISCARDING;
                }
                GameImpl.INSTANCE.discard(card);
                addGoldBuffer(3);
                break;
            case WONDER:
                Card stage = nextWonderStage();
                if (stage == null) {
                    return CardActionResult.ALREADY_BUILT_ALL_WONDER_STAGES;
                } else if (tradeBackend.canAfford(stage) || hasAttribute(Card.Attribute.FREE_WONDER)) {
                    if(hasAttribute(Card.Attribute.FREE_WONDER) && tradeBackend.hasTrade()) {
                        return CardActionResult.TRADED_WHEN_CAN_BUILD_FREE;
                    }
                    setToBeBuilt(stage, tradeBackend.getGoldSpent(Game.Direction.EAST),
                            tradeBackend.getGoldSpent(Game.Direction.WEST));
                } else {
                    return CardActionResult.INSUFFICIENT_RESOURCES;
                }
                break;
            default:
                throw new RuntimeException("This shouldn't be reached");
        }
        //If we haven't thrown an exception by now, the card has been played and we can remove it from the hand
        hand.remove(card);
        GameImpl.INSTANCE.finishedTurn(this);
        return CardActionResult.OK;
    }

    @Override
    public boolean canAffordBuild(Card card) {
        return checkCanAffordBuild(card) == CardActionResult.OK;
    }

    private CardActionResult checkCanAffordBuild(Card card) {
        boolean hasCoupon = hasCouponFor(card) || isPlayDiscard();
        if (getPlayedCards().contains(card)) {
            return CardActionResult.ALREADY_BUILT;
        } else if (hasCoupon && tradeBackend.hasTrade()) {
            return CardActionResult.TRADED_WHEN_CAN_BUILD_FREE;
        } else if (tradeBackend.overpaid(card)) {
            return CardActionResult.OVERPAID;
        } else if (!(hasCoupon || tradeBackend.canAfford(card) || (hasAttribute(Card.Attribute.PLAY_1_FREE) && !playedFree))) {
            return CardActionResult.INSUFFICIENT_RESOURCES;
        }
        return CardActionResult.OK;
    }

    @Override
    public CardList getHand() {
        return hand;
    }

    void setHand(CardList hand) {
        this.hand = hand;
    }

    @Override
    public boolean hasCouponFor(Card card) {
        for (Card c2 : card.makeThisFree()) {
            if (played.contains(c2)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAttribute(Card.Attribute attribute) {
        for(int era = 0; era < 3; era++) {
            if(hasAttribute(attribute, era))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasAttribute(Card.Attribute attribute, int era) {
        for (Card card : played) {
            if (card.getEra() == era && card.providesAttribute(attribute))
                return true;
        }
        return false;
    }

    void setPlayedFree(boolean playedFree) {
        this.playedFree = playedFree;
    }

    @Override
    public boolean isPlayDiscard() {
        return !played.isEmpty() && played.get(played.size() - 1).providesAttribute(Card.Attribute.FREE_BUILD);
    }

    @Override
    public TradeBackend getTradeBackend() {
        return tradeBackend;
    }

    @Override
    public boolean isAI() {
        return isAI;
    }

    @Override
    public AI getAI() {
        return ai;
    }

    @Override
    public int getGold() {
        return gold;
    }

    @Override
    public void incurDebt(int amount) {
        score.incurDebt(amount);
    }

    public void addGold(int amount) {
        gold += amount;
    }

    @Override
    public void addGoldBuffer(int amount) {
        playBuffer.goldSelf += amount;
    }

    private void setToBeBuilt(Card card, int goldEast, int goldWest) {
        playBuffer.card = card;
        addGoldBuffer(card.getProducts(this).get(Card.Resource.GOLD));
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
            addGold(playBuffer.card.getCosts(this).get(Card.Resource.GOLD) * -1);
        }
        addGold(playBuffer.goldSelf);
        //playBuffer.clear();
        tradeBackend.clear();
    }

    int getActionPrecidence() {
        if(playBuffer.card == null) {
            return -1;
        }
        return playBuffer.card.getActionPrecidence();
    }

    void resolveAction() {
        if(playBuffer.card != null) {
            playBuffer.card.performAction(this);
        }
        playBuffer.clear();
    }

    void startTurn() {
        hasFinishedTurn = false;
    }

    void finishTurn() {
        hasFinishedTurn = true;
    }

    boolean hasFinishedTurn() {
        return hasFinishedTurn;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(score.getMilitaryLosses());
        s.writeObject(score.getMilitaryVictories());
        s.writeInt(score.getDebt());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        ai = new AIImpl(this);
        score = new ScoreImpl(this, s.readInt(), (int[]) s.readObject());
        score.incurDebt(s.readInt());
        tradeBackend.setPlayer(this);
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
            String name = (card == null) ? null : card.getName();
            s.writeObject(name);
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            card = Generate.getAllCards().get((String) s.readObject());
        }
    }
}
