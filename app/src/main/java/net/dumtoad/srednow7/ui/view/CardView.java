package net.dumtoad.srednow7.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.UIUtil;

public class CardView extends Button {

    public CardView(Context context) {
        super(context);
    }

    public CardView(Card card, Context context, Player player, boolean buildable, boolean freePlay) {
        super(context);
        setText(UIUtil.formatEnum(card.getEnum(), card.getType()));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.textsize));
        if (buildable) build(card, player, freePlay);
        else view(card, player);
    }

    private void requestAction(Player player, Game.CardAction action, Card card) {
        boolean succeeded = true;
        try {
            Bus.bus.getGame(player).requestCardAction(action, card);
        } catch (Game.BadActionException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            succeeded = false;
        }
        if(succeeded)
            Bus.bus.getUI().invalidateView();
    }

    private void build(final Card card, final Player player, final boolean freePlay) {
        setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle(UIUtil.formatEnum(card.getEnum(), card.getType()))
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
                                            requestAction(player, Game.CardAction.BUILD, card);
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
                                            requestAction(player, Game.CardAction.DISCARD, card);
                                            break;
                                        case 2:
                                            requestAction(player, Game.CardAction.WONDER, card);
                                            break;
                                        case 3:
                                            requestAction(player, Game.CardAction.BUILD, card);
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
                .setTitle(UIUtil.formatEnum(card.getEnum(), card.getType()))
                .setMessage(UIUtil.getSummary(card, player, true, false))
                .setPositiveButton("Ok", (dialog, which) -> {
                    //Do nothing
                }).show());
    }


}
