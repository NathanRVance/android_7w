package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Backend;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.TradeUI;
import net.dumtoad.srednow7.ui.UIUtil;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;

import java.util.HashMap;

import static net.dumtoad.srednow7.backend.TradeBackend.tradeable;

public class TradeView extends GameView implements TradeUI {

    private HashMap<Card.Resource, LinearLayout> views;
    private TextView goldStatus;

    public TradeView(Context context) {
        super(context);
    }

    public TradeView(Activity activity, Player playerTurn, Player playerViewing) {
        super(activity, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        activity.findViewById(R.id.wonder).setEnabled(true);
        activity.findViewById(R.id.summary).setEnabled(true);
        activity.findViewById(R.id.hand).setEnabled(false);

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help_trade_title));
            bundle.putString(HelpDialog.MESSAGE, activity.getString(R.string.help_trade));
            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });

        Backend.Direction directionViewing = (playerViewing == Bus.bus.getBackend().getPlayerDirection(playerTurn, Backend.Direction.EAST)) ?
                Backend.Direction.EAST : Backend.Direction.WEST;

        views = new HashMap<>();
        goldStatus = new TextView(activity);
        goldStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.textsize));
        content.addView(goldStatus);
        LayoutInflater inflater = LayoutInflater.from(activity);
        for (final Card.Resource res : tradeable) {
            LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.trade_item, content, false);
            Button add = (Button) ll.findViewById(R.id.add);
            add.setOnClickListener(v -> {
                Bus.bus.getGame(playerTurn).getTradeBackend().makeTrade(res, 1, directionViewing);
                Bus.bus.getGame(playerTurn).getTradeBackend().refresh(TradeView.this, directionViewing);
            });
            Button subtract = (Button) ll.findViewById(R.id.subtract);
            subtract.setOnClickListener(v -> {
                Bus.bus.getGame(playerTurn).getTradeBackend().makeTrade(res, -1, directionViewing);
                Bus.bus.getGame(playerTurn).getTradeBackend().refresh(TradeView.this, directionViewing);
            });
            views.put(res, ll);
            content.addView(ll);
        }

        Bus.bus.getGame(playerTurn).getTradeBackend().refresh(this, directionViewing);
    }

    @Override
    public void update(int goldAvailable, ResQuant resourcesForSale, ResQuant resourcesBought, ResQuant prices) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        UIUtil.appendSb(sb, "Gold", new ForegroundColorSpan(UIUtil.getColorId(Card.Resource.GOLD.toString())));
        sb.append(" available: ").append(String.valueOf(goldAvailable));
        goldStatus.setText(sb);
        for (Card.Resource res : views.keySet()) {
            LinearLayout ll = views.get(res);
            sb = new SpannableStringBuilder();
            UIUtil.appendSb(sb, res.toString().toLowerCase(), new ForegroundColorSpan(UIUtil.getColorId(res.toString())));
            sb.append(": ").append(String.valueOf(resourcesForSale.get(res)));
            sb.append("\nBought: ");
            if (resourcesBought.get(res) == 0) {
                sb.append("0");
            } else {
                UIUtil.appendSb(sb, String.valueOf(resourcesBought.get(res)),
                        new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.red)));
            }
            int cost = prices.get(res);
            sb.append("\nCost: ").append(String.valueOf(cost));
            ((TextView) ll.findViewById(R.id.title)).setText(sb);
            ll.findViewById(R.id.add).setEnabled(resourcesForSale.get(res) > 0 && cost <= goldAvailable);
            ll.findViewById(R.id.subtract).setEnabled(resourcesBought.get(res) > 0);
        }
    }
}
