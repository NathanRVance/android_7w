package net.dumtoad.android_7w.cards;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import net.dumtoad.android_7w.ai.AI;
import net.dumtoad.android_7w.controller.MasterViewController;
import net.dumtoad.android_7w.controller.TableController;

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

    public Player(MasterViewController mvc, boolean isAI, String name) {
        this.mvc = mvc;
        playedCards = new CardCollection();
        this.isAI = isAI;
        this.name = name;
        hand = new Hand();
        gold = 3;
    }

    public Player(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        this.isAI = savedInstanceState.getBoolean("isAI");
        this.name = savedInstanceState.getString("name");
        this.hand = new Hand(mvc.getDatabase().getAllCards(), savedInstanceState.getString("handtrade"));
        this.playedCards = new CardCollection(mvc.getDatabase().getAllCards(), savedInstanceState.getString("playedCards"));
        String wonderName = savedInstanceState.getString("wonder");
        for(Wonder wonder : Generate.getWonders()) {
            if(wonder.getName().toString().equals(wonderName)) {
                this.wonder = wonder;
                break;
            }
        }
        this.wonderSide = savedInstanceState.getBoolean("wonderSide");
        this.gold = savedInstanceState.getInt("gold");
    }

    public Bundle getInstanceState() {
        Bundle outstate = new Bundle();
        outstate.putBoolean("isAI", isAI);
        outstate.putString("name", name);
        outstate.putString("handtrade", hand.getOrder());
        outstate.putString("playedCards", playedCards.getOrder());
        outstate.putString("wonder", wonder.getName().toString());
        outstate.putBoolean("wonderSide", wonderSide);
        outstate.putInt("gold", gold);
        return outstate;
    }

    public void setWonder(Wonder wonder) {
        this.wonder = wonder;
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

    public void setAI(AI ai) {
        this.ai = ai;
    }

    public AI getAI() {
        return ai;
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

    public String getNation() {
        return getWonder().getNameString();
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

    public ResQuant getProduction(boolean full) {
        ResQuant resources = new ResQuant();
        for(Card.Resource product : Card.Resource.values()) {
            resources.put(product, 0);
        }
        resources.put(getWonder().getResource(), resources.get(getWonder().getResource()) + 1);
        for(Card card : playedCards) {
            if(! full && card.getType() == Card.Type.COMMERCIAL) continue;
            ResQuant products = card.getProducts();
            for(Card.Resource product : Card.Resource.values()) {
                if(product == Card.Resource.GOLD) continue;
                resources.put(product, resources.get(product) + products.get(product));
            }
        }
        resources.put(Card.Resource.GOLD, gold);
        return resources;
    }

    public SpannableStringBuilder getSummary() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("Owned:\n");
        appendSbPlayer(sb, this, true);
        sb.append("\nWest:\n");
        appendSbPlayer(sb, mvc.getTableController().getPlayerDirection(true, this), false);
        sb.append("\nEast:\n");
        appendSbPlayer(sb, mvc.getTableController().getPlayerDirection(false, this), false);
        return sb;
    }

    private void appendSbPlayer(SpannableStringBuilder sb, Player player, boolean full) {
        ResQuant production = player.getProduction(full);
        for(Card.Resource product : Card.Resource.values()) {
            sb.append(" ");
            ForegroundColorSpan fcs = new ForegroundColorSpan(Card.getColorId(product.toString()));
            Card.appendSb(sb, product.toString().toLowerCase(), fcs);
            sb.append(": ");
            sb.append(production.get(product).toString());
            sb.append("\n");
        }
    }

    public boolean canAfford(Card card, ResQuant purchased) {
        ResQuant cost = card.getCost();
        cost.subtractResources(purchased);
        cost.subtractResources(getProduction(true));
        return cost.allZeroOrBelow();
    }

    public void buildCard(Card card, int goldHere, int goldEast, int goldWest) {
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

    public void finishTurn() {
        turnBuffer.resolve();
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

        public void resolve() {
            if(card != null)
                playedCards.add(card);
            addGold(goldHere);
            mvc.getTableController().getPlayerDirection(false, Player.this).addGold(goldEast);
            mvc.getTableController().getPlayerDirection(true, Player.this).addGold(goldWest);
        }
    }

}
