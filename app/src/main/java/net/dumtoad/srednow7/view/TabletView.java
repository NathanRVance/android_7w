package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.controller.TableController;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.fragment.GameFragment;
import net.dumtoad.srednow7.player.Player;

public class TabletView extends GameView {

    public TabletView(Context context) {
        super(context);
    }

    public TabletView(MasterViewController mvc, int playerTurn, int playerViewing) {
        super(mvc, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        final TableController tc = mvc.getTableController();

        removeAllViews();
        LayoutInflater inflater = mvc.getActivity().getLayoutInflater();
        inflater.inflate(R.layout.tablet_view, this, true);

        ((ViewGroup) findViewById(R.id.wonder_view)).addView(new WonderView(mvc, playerTurn, playerViewing));
        ((ViewGroup) findViewById(R.id.summary_view)).addView(new SummaryView(mvc, playerTurn, playerViewing));
        GameFragment gameFragment = (GameFragment) mvc.getActivity().getFragmentManager().findFragmentByTag(GameFragment.GAME_FRAGMENT_TAG);
        View handView = gameFragment.getHandTradeView();
        if(handView instanceof SummaryView) {
            //We already have one of those...
            findViewById(R.id.hand_view).setVisibility(GONE);
        } else {
            ((ViewGroup) findViewById(R.id.hand_view)).addView(handView);
        }

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help));


                StringBuilder sb = new StringBuilder();
                sb.append(mvc.getActivity().getString(R.string.help_wonder_title)).append("\n");
                sb.append(mvc.getActivity().getString(R.string.help_wonder)).append("\n\n");

                sb.append(mvc.getActivity().getString(R.string.help_summary_title)).append("\n");
                sb.append(mvc.getActivity().getString(R.string.help_summary)).append("\n\n");

                if (playerViewing == playerTurn) {
                    sb.append(mvc.getActivity().getString(R.string.help_hand_title)).append("\n");
                    sb.append(mvc.getActivity().getString(R.string.help_hand));
                } else if (playerViewing == tc.getPlayerDirection(playerTurn, true) || playerViewing == tc.getPlayerDirection(playerTurn, false)) {
                    sb.append(mvc.getActivity().getString(R.string.help_trade_title)).append("\n");
                    sb.append(mvc.getActivity().getString(R.string.help_trade));
                }

                bundle.putString("message", sb.toString());

                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });


        //These two things must happen now because they've been overwritten
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(false);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(false);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);

        Player player = mvc.getPlayer(playerViewing);
        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(player.getWonder().getNameString());
    }

}
