package net.dumtoad.srednow7.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.UIUtil;

import static net.dumtoad.srednow7.bus.DisplayFactory.QUEUE_ID;

public class CardView extends Button {

    private Enum queueID;

    public CardView(Context context) {
        super(context);
    }

    public CardView(Card card, Context context, Player player, boolean buildable, boolean freePlay, Enum queueID) {
        super(context);
        this.queueID = queueID;
        setText(UIUtil.formatName(card.getName(), card.getType()));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.textsize));
        if (buildable) build(card, player, freePlay);
        else view(card, player);
    }

    private void requestAction(Player player, Player.CardAction action, Card card) {
        Player.CardActionResult result = player.requestCardAction(action, card);
        if (result == Player.CardActionResult.OK) {
            Bus.bus.getUI().invalidateView(queueID);
        } else {
            String error;
            switch (result) {
                case TRADED_WHEN_DISCARDING:
                    error = "Don't trade when discarding";
                    break;
                case TRADED_WHEN_CAN_BUILD_FREE:
                    error = "Don't trade, you can build for free";
                    break;
                case ALREADY_BUILT_ALL_WONDER_STAGES:
                    error = "Already built all stages";
                    break;
                case INSUFFICIENT_RESOURCES:
                    error = "Insufficient resources";
                    break;
                case ALREADY_BUILT:
                    error = "Already built " + UIUtil.formatName(card.getName(), card.getType());
                    break;
                case OVERPAID:
                    error = "Overpaid, undo some trades";
                    break;
                default:
                    error = "Yell at the programmer";
            }
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void build(final Card card, final Player player, final boolean freePlay) {
        setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(UIUtil.formatName(card.getName(), card.getType()))
                .setMessage(UIUtil.getSummary(card, player, true, true))
                .setNegativeButton("OK", (dialog, which) -> {
                    //Do nothing
                })
                .setPositiveButton("Actions...", (dialog, which) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle("Select action");
                    if (freePlay) {
                        builder.setItems(new CharSequence[]
                                        {"Cancel", "Build Card"},
                                (dialog12, which12) -> {
                                    switch (which12) {
                                        case 0:
                                            //Do nothing
                                            break;
                                        case 1:
                                            requestAction(player, Player.CardAction.BUILD, card);
                                            break;
                                    }
                                });
                    } else {
                        builder.setItems(new CharSequence[]
                                        {"Cancel", "Discard", "Build Wonder", "Build Card"},
                                (dialog1, which1) -> {
                                    switch (which1) {
                                        case 0:
                                            //Do nothing
                                            break;
                                        case 1:
                                            requestAction(player, Player.CardAction.DISCARD, card);
                                            break;
                                        case 2:
                                            requestAction(player, Player.CardAction.WONDER, card);
                                            break;
                                        case 3:
                                            requestAction(player, Player.CardAction.BUILD, card);
                                            break;
                                    }
                                });
                    }
                    builder.create().show();
                })
                .create().show());
    }

    private void view(final Card card, final Player player) {
        setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(UIUtil.formatName(card.getName(), card.getType()))
                .setMessage(UIUtil.getSummary(card, player, true, false))
                .setPositiveButton("Ok", (dialog, which) -> {
                    //Do nothing
                }).show());
    }


}
