package net.dumtoad.android_7w.controller;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.cards.CardCollection;
import net.dumtoad.android_7w.cards.Generate;
import net.dumtoad.android_7w.player.Player;
import net.dumtoad.android_7w.cards.ResQuant;

import java.util.HashMap;

public class TradeController {
    private MasterViewController mvc;
    private boolean west;
    private ResQuant tradeWest;
    private ResQuant tradeEast;
    private HashMap<Card.Resource, LinearLayout> westViews;
    private HashMap<Card.Resource, LinearLayout> eastViews;
    private TextView goldStatus;
    private Card.Resource[] tradeable = new Card.Resource[] {Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY,
            Card.Resource.ORE, Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER };

    public TradeController(MasterViewController mvc) {
        this.mvc = mvc;
        tradeWest = new ResQuant();
        tradeEast = new ResQuant();
        westViews = new HashMap<>();
        eastViews = new HashMap<>();
    }

    public TradeController(MasterViewController mvc, Bundle savedInstanceState) {
        this.mvc = mvc;
        tradeWest = new ResQuant(savedInstanceState.getString("tradeWest"));
        tradeEast = new ResQuant(savedInstanceState.getString("tradeEast"));
        west = savedInstanceState.getBoolean("west");
        westViews = new HashMap<>();
        eastViews = new HashMap<>();
    }

    public Bundle getInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putString("tradeWest", tradeWest.getString());
        bundle.putString("tradeEast", tradeEast.getString());
        bundle.putBoolean("west", west);
        return bundle;
    }

    public void trade(LinearLayout content, boolean west) {
        this.west = west;
        content.removeAllViews();
        if(west)
            addTradeItems(content, tradeWest, westViews);
        else
            addTradeItems(content, tradeEast, eastViews);
        updateViews();
    }

    private void addTradeItems(LinearLayout content, final ResQuant currentTrade,
                               HashMap<Card.Resource, LinearLayout> views) {
        views.clear();
        goldStatus = new TextView(mvc.getActivity());
        content.addView(goldStatus);
        LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
        for(final Card.Resource res : tradeable) {
            LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.trade_item, content, false);
            Button add = (Button) ll.findViewById(R.id.add);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentTrade.put(res, currentTrade.get(res) + 1);
                    updateViews();
                }
            });
            Button subtract = (Button) ll.findViewById(R.id.subtract);
            subtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentTrade.put(res, currentTrade.get(res) - 1);
                    updateViews();
                }
            });
            views.put(res, ll);
            content.addView(ll);
        }
    }

    public void updateViews() {
        Player player = mvc.getTableController().getTurnController().getCurrentPlayer();
        if(west)
            updateViews(westViews, tradeWest, mvc.getTableController().getPlayerDirection(true, player));
        else
            updateViews(eastViews, tradeEast, mvc.getTableController().getPlayerDirection(false, player));
    }

    private void updateViews(HashMap<Card.Resource, LinearLayout> views, ResQuant currentTrade, Player other) {
        int playerGold = mvc.getTableController().getTurnController().getCurrentPlayer().getGold();
        goldStatus.setText("Gold available: " + (playerGold - getTotalCost()));
        ResQuant numAvailable = numAvailable(other, currentTrade, false);
        for(Card.Resource res : views.keySet()) {
            LinearLayout ll = views.get(res);
            SpannableStringBuilder sb = new SpannableStringBuilder();
            Card.appendSb(sb, res.toString().toLowerCase(), new ForegroundColorSpan(Card.getColorId(res.toString())));
            sb.append(": ").append(String.valueOf(numAvailable.get(res)));
            sb.append("\nBought: ");
            if (currentTrade.get(res) == 0) {
                sb.append("0");
            } else {
                Card.appendSb(sb, String.valueOf(currentTrade.get(res)),
                        new ForegroundColorSpan(ContextCompat.getColor(mvc.getActivity(), R.color.red)));
            }
            int cost = getCost(mvc.getTableController().getTurnController().getCurrentPlayer(), west, res);
            sb.append("\nCost: ").append(String.valueOf(cost));
            ((TextView) ll.findViewById(R.id.title)).setText(sb);
            ll.findViewById(R.id.add).setEnabled(numAvailable.get(res) > 0 && getTotalCost() + cost <= playerGold);
            ll.findViewById(R.id.subtract).setEnabled(currentTrade.get(res) > 0);
        }
    }

    public int getTotalCost() {
        return getCurrentCost(true) + getCurrentCost(false);
    }

    public int getCurrentCost(boolean west) {
        int tot = 0;
        ResQuant trade = (west)? tradeWest : tradeEast;
        for(Card.Resource res : trade.keySet()) {
            tot += getCost(mvc.getTableController().getTurnController().getCurrentPlayer(), true, res)
                    * trade.get(res);
        }
        return tot;
    }

    public int getCost(Player player, boolean west, Card.Resource res) {
        boolean plainResource = true;
        if(res == Card.Resource.CLOTH || res == Card.Resource.GLASS || res == Card.Resource.PAPER)
            plainResource = false;
        CardCollection cards = player.getPlayedCards();
        if(plainResource) {
            if(player.getWonder().getName().equals(Generate.Wonders.The_Statue_of_Zeus_in_Olympia)
                    && ! player.getWonderSide() && cards.contains(Generate.WonderStages.Stage_2)) {
                return 1;
            }
            if(west && cards.contains(Generate.Era0.West_Trading_Post)) {
                return 1;
            }
            if(! west && cards.contains(Generate.Era0.East_Trading_Post)) {
                return 1;
            }
        } else {
            if(cards.contains(Generate.Era0.Marketplace)) {
                return 1;
            }
        }
        return 2;
    }

    //Status is current trade status, and is positive
    public ResQuant numAvailable(Player player, ResQuant status, boolean includeCommercial) {
        ResQuant available = new ResQuant();
        //Add the wonder resource
        available.put(player.getWonder().getResource(), 1);
        //Remove status resources
        available.addResources(status);
        //complicated are cards where you must choose which resource they produce
        CardCollection complicated = new CardCollection();
        for(Card card : player.getPlayedCards()) {
            //If we aren't including commercial, skip it now
            if(! includeCommercial && card.getType() == Card.Type.COMMERCIAL) continue;
            //We only care about cards that produce resources (commercial has already been skipped if necessary)
            if(! (card.getType() == Card.Type.RESOURCE || card.getType() == Card.Type.INDUSTRY
                    || card.getType() == Card.Type.COMMERCIAL)) continue;

            ResQuant prod = card.getProducts();

            //Count number of products card produces
            int numProducts = 0;
            for(Card.Resource res : tradeable) {
                if(prod.get(res) > 0) numProducts++;
            }
            if(numProducts == 1) { //Great, a reasonable card. Deal with it now
                for(Card.Resource res : tradeable) {
                    available.put(res, available.get(res) + prod.get(res));
                }
            } else if(numProducts > 1) { //Ugh, it's complicated. Deal with it later
                complicated.add(card);
            }
            //If it doen't produce a tradeable resource, we don't care about it.
        }

        //We're left with some cards that could be in one of several categories. I think (not sure)
        //that this is an NP-complete problem, so brute force! Find all legal combinations, and take
        //the maximum available for each resource.
        ResQuant answer = new ResQuant().addResources(available); //Makes a copy of available
        availableRecurse(complicated, available, answer);

        return answer;
    }

    private void availableRecurse(CardCollection cards, ResQuant available, ResQuant answer) {
        if(cards.size() > 0) {
            Card card = cards.remove(0);
            for(Card.Resource res : tradeable) {
                if(card.getProducts().get(res) > 0) {
                    available.put(res, available.get(res) + 1);
                    availableRecurse(cards, available, answer);
                    available.put(res, available.get(res) - 1);
                }
            }
        } else {
            if(available.allZeroOrAbove()) {
                for(Card.Resource res : tradeable) {
                    if(available.get(res) > answer.get(res)) {
                        answer.put(res, available.get(res));
                    }
                }
            }
        }
    }

    public boolean canAffordResources(Card card) {
        ResQuant status = new ResQuant().subtractResources(card.getCost());
        status.put(Card.Resource.GOLD, 0); //Handle gold elsewhere
        status.addResources(tradeEast);
        status.addResources(tradeWest);
        return numAvailable(mvc.getTableController().getTurnController().getCurrentPlayer(), status, true).allZeroOrAbove();
    }

    public boolean canAffordGold(Card card) {
        int playerGold = mvc.getTableController().getTurnController().getCurrentPlayer().getGold();
        return playerGold >= card.getCost().get(Card.Resource.GOLD) + getTotalCost();
    }

}