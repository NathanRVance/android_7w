package net.dumtoad.android_7w.controller;

import android.os.Bundle;
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
import net.dumtoad.android_7w.cards.Player;
import net.dumtoad.android_7w.cards.ResQuant;

import java.util.HashMap;
import java.util.Iterator;

public class TradeController {
    private MasterViewController mvc;
    private boolean west;
    private ResQuant tradeWest;
    private ResQuant tradeEast;
    private HashMap<Card.Resource, LinearLayout> westViews;
    private HashMap<Card.Resource, LinearLayout> eastViews;
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
        ResQuant numAvailable = numAvailable(other, currentTrade);
        for(Card.Resource res : views.keySet()) {
            LinearLayout ll = views.get(res);
            SpannableStringBuilder sb = new SpannableStringBuilder();
            Card.appendSb(sb, res.toString().toLowerCase(), new ForegroundColorSpan(Card.getColorId(res.toString())));
            sb.append(": ").append(String.valueOf(numAvailable.get(res)));
            sb.append("\nBought: ").append(String.valueOf(currentTrade.get(res)));
            sb.append("\nCost: ").append(String.valueOf(
                    getCost(mvc.getTableController().getTurnController().getCurrentPlayer(), west, res)));
            ((TextView) ll.findViewById(R.id.title)).setText(sb);
            ll.findViewById(R.id.add).setEnabled(numAvailable.get(res) > 0);
            ll.findViewById(R.id.subtract).setEnabled(currentTrade.get(res) > 0);
        }
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
    public ResQuant numAvailable(Player player, ResQuant status) {
        ResQuant available = new ResQuant();
        available.put(player.getWonder().getResource(), 1);
        available.subtractResources(status);
        CardCollection complicated = new CardCollection();
        for(Card card : player.getPlayedCards()) {
            if(! (card.getType() == Card.Type.RESOURCE || card.getType() == Card.Type.INDUSTRY)) continue;
            ResQuant prod = card.getProducts();
            int numProducts = 0;
            for(Card.Resource res : tradeable) {
                if(prod.get(res) > 0) numProducts++;
            }
            if(numProducts == 1) {
                for(Card.Resource res : tradeable) {
                    available.put(res, available.get(res) + prod.get(res));
                }
            } else if(numProducts > 1) {
                complicated.add(card);
            }
        }
        for(Card.Resource res : tradeable) {
            if(available.get(res) < 0) {
                available.put(res, 0);
                int numRes = 0;
                for(Card card : complicated) {
                    numRes += card.getProducts().get(res);
                }
                if(numRes == available.get(res)) {
                    Iterator<Card> it = complicated.iterator();
                    while(it.hasNext()) {
                        if(it.next().getProducts().get(res) > 0)
                            it.remove();
                    }
                } else if(numRes < available.get(res)) {
                    throw new RuntimeException("Inextricable situation!");
                }
            }
        }
        for(Card card : complicated) {
            for(Card.Resource res : tradeable) {
                available.put(res, available.get(res) + card.getProducts().get(res));
            }
        }

        return available;
    }

}
