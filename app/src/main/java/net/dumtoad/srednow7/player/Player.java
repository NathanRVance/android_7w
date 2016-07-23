package net.dumtoad.srednow7.player;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.CardCollection;
import net.dumtoad.srednow7.cards.Generate;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.cards.ResQuant;
import net.dumtoad.srednow7.cards.Special;
import net.dumtoad.srednow7.cards.Wonder;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.controller.TradeController;
import net.dumtoad.srednow7.controller.TurnController;

import java.util.ArrayList;

public class Player {

    private Hand hand;
    private CardCollection playedCards;
    private MasterViewController mvc;
    private final String name;
    private Wonder wonder;
    private boolean wonderSide;
    private final boolean isAI;
    private AI ai;
    private int gold;
    private TurnBuffer turnBuffer;
    private Score score;
    private boolean hasOneFreeBuild;
    private boolean playDiscard;
    private TurnController tc;

    public Player(MasterViewController mvc, boolean isAI, String name) {
        this.mvc = mvc;
        playedCards = new CardCollection();
        this.isAI = isAI;
        ai = new AI(this);
        this.name = name;
        hand = new Hand();
        gold = 3;
        score = new Score(this, mvc);
        hasOneFreeBuild = false;
        this.tc = new TurnController(mvc, this);
    }

    public Player(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.isAI = savedInstanceState.getBoolean("isAI");
        ai = new AI(this, savedInstanceState.getBundle("ai"));
        this.name = savedInstanceState.getString("name");
        this.hand = new Hand(mvc.getDatabase().getAllCards(), savedInstanceState.getString("handtrade"));
        String wonderName = savedInstanceState.getString("wonder");
        for(Wonder wonder : Generate.getWonders()) {
            if(wonder.getName().toString().equals(wonderName)) {
                this.wonder = wonder;
                break;
            }
        }
        this.wonderSide = savedInstanceState.getBoolean("wonderSide");
        CardCollection cc = mvc.getDatabase().getAllCards();
        cc.addAll(wonder.getStages(wonderSide));
        this.playedCards = new CardCollection(cc, savedInstanceState.getString("playedCards"));
        this.gold = savedInstanceState.getInt("gold");
        this.score = new Score(this, mvc, savedInstanceState.getBundle("score"));
        if(savedInstanceState.getBundle("turnBuffer") != null) {
            turnBuffer = new TurnBuffer(savedInstanceState.getBundle("turnBuffer"));
        }
        this.playDiscard = savedInstanceState.getBoolean("playDiscard");
        this.hasOneFreeBuild = savedInstanceState.getBoolean("hasOneFreeBuild");
        tc = new TurnController(mvc, this, savedInstanceState.getBundle("tc"));
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putBoolean("isAI", isAI);
        outstate.putBundle("ai", ai.getInstanceState());
        outstate.putString("name", name);
        outstate.putString("handtrade", hand.getOrder());
        outstate.putString("playedCards", playedCards.getOrder());
        outstate.putString("wonder", wonder.getName().toString());
        outstate.putBoolean("wonderSide", wonderSide);
        outstate.putInt("gold", gold);
        outstate.putBundle("score", score.getInstanceState());
        if(turnBuffer != null) {
            outstate.putBundle("turnBuffer", turnBuffer.getInstanceState());
        }
        outstate.putBoolean("playDiscard", playDiscard);
        outstate.putBoolean("hasOneFreeBuild", hasOneFreeBuild);
        outstate.putBundle("tc", tc.getInstanceState());
        return outstate;
    }

    public void startTurn(boolean playDiscard) {
        if(playDiscard && mvc.getTableController().getDiscards().isEmpty() || !playDiscard && hand.isEmpty()) {
            endTurn();
            return;
        }
        this.playDiscard = playDiscard;
        if(isAI) {
            ai.doTurn(playDiscard, tc);
        } else {
            mvc.autosave(); //Autosave at beginning of each player's turn
            tc.startTurn(playDiscard);
        }
    }

    public void endTurn() {
        mvc.getTableController().iFinishedMyTurn(this);
    }

    public boolean isPlayingDiscard() {
        return playDiscard;
    }

    public TradeController getTradeController() {
        return tc.getTradeController();
    }

    public TurnController getTurnController() {
        return tc;
    }

    public void setWonder(Wonder wonder) {
        this.wonder = wonder;
        ai.updateScientist();
    }

    public boolean hasOneFreeBuild() {
        return hasOneFreeBuild;
    }

    public void refreshFreeBuild() {
        if(getPlayedCards().contains(Generate.WonderStages.Stage_2) && getWonder().getName() == Generate.Wonders.The_Statue_of_Zeus_in_Olympia
                && getWonderSide()) {
            hasOneFreeBuild = true;
        }
    }

    public void spendFreeBuild() {
        hasOneFreeBuild = false;
    }

    public Wonder getWonder() {
        return wonder;
    }

    public boolean getWonderSide() {
        return wonderSide;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setWonderSide(boolean wonderSide) {
        this.wonderSide = wonderSide;
    }

    public ArrayList<Card> getWonderStages() {
        return wonder.getStages(wonderSide);
    }

    public String getName() {
        return name;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public CardCollection getPlayedCards() {
        return playedCards;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public int getGold() {
        return gold;
    }

    public Score getScore() {
        return score;
    }

    public Card nextWonderStage() {
        for (Card stage : wonder.getStages(wonderSide)) {
            if (!playedCards.contains(stage)) {
                return stage;
            }
        }
        return null;
    }

    public ResQuant getRawProduction() {
        ResQuant ret = new ResQuant();
        for(Card card : playedCards) {
            ret.addResources(card.getProducts());
        }
        ret.put(Card.Resource.GOLD, gold);
        return ret;
    }

    private Card.Resource[] ored = new Card.Resource[] {Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY,
            Card.Resource.ORE, Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER, Card.Resource.COMPASS,
            Card.Resource.GEAR, Card.Resource.TABLET};

    public SpannableStringBuilder getSummary() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        ResQuant production = new ResQuant();
        production.put(wonder.getResource(), 1);
        CardCollection complicated = new CardCollection();
        for(Card card : playedCards) {
            int numRes = 0;
            for(Card.Resource res : ored) {
                if(card.getProducts().get(res) > 0) numRes++;
            }
            if(numRes > 1) {
                complicated.add(card);
            } else {
                production.addResources(card.getProducts());
            }
        }

        ForegroundColorSpan fcs = new ForegroundColorSpan(Card.getColorId(Card.Resource.GOLD.toString()));
        Card.appendSb(sb, "Gold", fcs);
        sb.append(": ").append(String.valueOf(gold)).append("\n");

        if(complicated.size() > 0) {
            sb.append("Dynamic products:");
            for(Card card : complicated) {
                sb.append("\n ");
                for(Card.Resource product : ored) {
                    if(card.getProducts().get(product) == 1) {
                        fcs = new ForegroundColorSpan(Card.getColorId(product.toString()));
                        Card.appendSb(sb, product.toString().toLowerCase(), fcs);
                        sb.append(" or ");
                    }
                }
                //Remove the last " or "
                sb.delete(sb.length()-4, sb.length());
            }
            sb.append("\n");
        }

        sb.append("Static products:\n");
        for(Card.Resource product : Card.Resource.values()) {
            if(product == Card.Resource.GOLD) continue;
            sb.append(" ");
            fcs = new ForegroundColorSpan(Card.getColorId(product.toString()));
            Card.appendSb(sb, product.toString().toLowerCase(), fcs);
            sb.append(": ");
            sb.append(production.get(product).toString());
            sb.append("\n");
        }

        sb.append("Net military points: ").append(String.valueOf(score.getMilitaryVps()));

        return sb;
    }

    public boolean hasCouponFor(Card card) {
        for(Card c : playedCards) {
            if(c.isCouponFor(card)) return true;
        }
        return false;
    }

    public void buildCard(Card card, int goldHere, int goldEast, int goldWest, Hand hand) {
        if(!hand.remove(card)) {
            throw new RuntimeException("Could not build card I don't have!");
        }
        turnBuffer = new TurnBuffer(card, goldHere, goldEast, goldWest);
    }

    public void discardCard(Card card) {
        if(!hand.remove(card)) {
            throw new RuntimeException("Could not discard card I don't have!");
        }
        mvc.getTableController().discard(card);
        turnBuffer = new TurnBuffer(null, 3, 0, 0);
    }

    public void buildWonder(Card stage, Card card, int goldHere, int goldEast, int goldWest) {
        if(!hand.remove(card)) {
            throw new RuntimeException("Could not build card I don't have!");
        }
        turnBuffer = new TurnBuffer(stage, goldHere, goldEast, goldWest);
    }

    public void finishTurn() {
        if(turnBuffer != null)
            turnBuffer.resolve();
    }

    public boolean specialAction() {
        return turnBuffer != null && turnBuffer.resolveSpecialAction();
    }

    public Card getBufferCard() {
        if(turnBuffer != null) {
            return turnBuffer.card;
        }
        return null;
    }

    public void flush() {
        turnBuffer = null;
    }

    private class TurnBuffer {
        Card card;
        int goldHere;
        int goldEast;
        int goldWest;

        public TurnBuffer(Card card, int goldHere, int goldEast, int goldWest) {
            this.card = card;
            this.goldHere = goldHere;
            this.goldEast = goldEast;
            this.goldWest = goldWest;
        }

        public TurnBuffer(Bundle savedInstanceState) {
            String cardName = savedInstanceState.getString("card");
            if(cardName != null) {
                for (Card card : mvc.getDatabase().getAllCards()) {
                    if (card.getName().toString().equals(cardName)) {
                        this.card = card;
                        break;
                    }
                }
            }
            goldHere = savedInstanceState.getInt("goldHere");
            goldEast = savedInstanceState.getInt("goldEast");
            goldWest = savedInstanceState.getInt("goldWest");
        }

        public Bundle getInstanceState() {
            Bundle bundle = new Bundle();
            if(card != null) {
                bundle.putString("card", card.getName().toString());
            }
            bundle.putInt("goldHere", goldHere);
            bundle.putInt("goldEast", goldEast);
            bundle.putInt("goldWest", goldWest);
            return bundle;
        }

        public void resolve() {
            if(card != null) {
                playedCards.add(card);
                playedCards.sort();
                addGold(card.getProducts().get(Card.Resource.GOLD));
                if(Special.isSpecialGold(card, Player.this)) {
                    addGold(Special.getSpecialGold(card, Player.this));
                }
            }
            addGold(goldHere);
            mvc.getTableController().getPlayerDirection(Player.this, false).addGold(goldEast);
            mvc.getTableController().getPlayerDirection(Player.this, true).addGold(goldWest);
        }

        public boolean resolveSpecialAction() {
            Card c = card;
            card = null;
            return c != null && Special.specialAction(c, Player.this);
        }
    }

}
