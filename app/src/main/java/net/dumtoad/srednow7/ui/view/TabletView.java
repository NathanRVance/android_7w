package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.UIFacade;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;
import net.dumtoad.srednow7.ui.fragment.GameFragment;

public class TabletView extends GameView {

    public TabletView(Context context) {
        super(context);
    }

    public TabletView(Activity activity, Player playerTurn, Player playerViewing) {
        super(activity, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        removeAllViews();
        LayoutInflater inflater = activity.getLayoutInflater();
        inflater.inflate(R.layout.tablet_view, this, true);

        ((ViewGroup) findViewById(R.id.wonder_view)).addView(new WonderView(activity, playerTurn, playerViewing));
        ((ViewGroup) findViewById(R.id.summary_view)).addView(new SummaryView(activity, playerTurn, playerViewing));
        GameFragment gameFragment = (GameFragment) activity.getFragmentManager().findFragmentByTag(UIFacade.FRAGMENT_TAG);
        View handView = gameFragment.getHandTradeView();
        if (handView instanceof SummaryView) {
            //We already have one of those
            findViewById(R.id.hand_view).setVisibility(GONE);
        } else {
            ((ViewGroup) findViewById(R.id.hand_view)).addView(handView);
        }

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help));


            StringBuilder sb = new StringBuilder();
            sb.append(activity.getString(R.string.help_wonder_title)).append("\n");
            sb.append(activity.getString(R.string.help_wonder)).append("\n\n");

            sb.append(activity.getString(R.string.help_summary_title)).append("\n");
            sb.append(activity.getString(R.string.help_summary)).append("\n\n");

            if (playerViewing == playerTurn) {
                sb.append(activity.getString(R.string.help_hand_title)).append("\n");
                sb.append(activity.getString(R.string.help_hand));
            } else if (playerViewing == Bus.bus.getGame().getPlayerDirection(playerTurn, Game.Direction.WEST)
                    || playerViewing == Bus.bus.getGame().getPlayerDirection(playerTurn, Game.Direction.EAST)) {
                sb.append(activity.getString(R.string.help_trade_title)).append("\n");
                sb.append(activity.getString(R.string.help_trade));
            }

            bundle.putString(HelpDialog.MESSAGE, sb.toString());

            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });


        //These two things must happen now because they've been overwritten
        activity.findViewById(R.id.wonder).setEnabled(false);
        activity.findViewById(R.id.summary).setEnabled(false);
        activity.findViewById(R.id.hand).setEnabled(false);

        Player player = playerViewing;
        ((TextView) activity.findViewById(R.id.title)).setText(player.getWonder().getName().replace("_", " "));
    }

}
