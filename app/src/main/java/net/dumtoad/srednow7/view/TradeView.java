package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.dialog.HelpDialog;

public class TradeView extends GameView {

    public TradeView(Context context) {
        super(context);
    }

    public TradeView(MasterViewController mvc, int playerTurn, int playerViewing) {
        super(mvc, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help_trade_title));
                bundle.putString("message", mvc.getActivity().getString(R.string.help_trade));
                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });

        if (playerViewing == mvc.getTableController().getPlayerDirection(playerTurn, true))
            mvc.getTableController().getTurnController().getTradeController().trade(content, true);
        else
            mvc.getTableController().getTurnController().getTradeController().trade(content, false);
    }

}
