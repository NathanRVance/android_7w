package net.dumtoad.srednow7.backend.implementation;

import net.dumtoad.srednow7.backend.AI;
import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Savable;
import net.dumtoad.srednow7.backend.Score;
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

    PlayerImpl(CharSequence name, boolean isAI) {
        this.name = name;
        this.isAI = isAI;
        if (isAI) ai = new AIImpl(this);
    }

    PlayerImpl() {
    }

    @Override
    public Wonder getWonder() {
        return wonder;
    }

    @Override
    public void setWonder(Wonder wonder) {
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

    @Override
    public void setPlayedFree(boolean playedFree) {
        this.playedFree = playedFree;
    }

    @Override
    public boolean mostRecentPlayedCardGivesFreeBuild() {
        return !played.isEmpty() && played.get(played.size() - 1).providesAttribute(Card.Attribute.FreeBuild);
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
    public void addGold(int amount) {
        gold += amount;
    }

    @Override
    public void addGoldBuffer(int amount) {
        playBuffer.goldSelf += amount;
    }

    @Override
    public void setToBeBuilt(Card card, int goldEast, int goldWest) {
        playBuffer.card = card;
        addGoldBuffer(card.getSpecialGold(this));
        playBuffer.goldEast = goldEast;
        playBuffer.goldWest = goldWest;
    }

    @Override
    public void resolveBuild() {
        if (playBuffer.card != null) {
            Bus.bus.getBackend().getPlayerDirection(this, Backend.Direction.EAST).addGold(playBuffer.goldEast);
            addGold(playBuffer.goldEast * -1);

            Bus.bus.getBackend().getPlayerDirection(this, Backend.Direction.WEST).addGold(playBuffer.goldWest);
            addGold(playBuffer.goldWest * -1);

            played.add(playBuffer.card);
            addGold(playBuffer.card.getProducts().get(Card.Resource.GOLD));
            addGold(playBuffer.card.getCosts().get(Card.Resource.GOLD) * -1);
        }
        addGold(playBuffer.goldSelf);
        playBuffer.clear();
    }

    @Override
    public Serializable getContents() {
        Serializable[] contents = new Serializable[9];
        contents[0] = played.getContents();
        contents[1] = score.getContents();
        contents[2] = gold;
        contents[3] = isAI();
        if (isAI()) contents[4] = getAI().getContents();
        contents[5] = name.toString();
        contents[6] = playBuffer.getContents();
        contents[7] = justDiscarded;
        contents[8] = playedFree;
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
                card = Bus.bus.getBackend().getCardCreator().getAllCards().get((Enum) in[1]);
            }
            goldEast = (int) in[2];
            goldWest = (int) in[3];
            goldSelf = (int) in[4];
        }
    }
}
