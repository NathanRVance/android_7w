package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.CardCollection;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.player.Player;

public class WonderView extends GameView {

    public WonderView(Context context) {
        super(context);
    }

    public WonderView(MasterViewController mvc, int playerTurn, int playerViewing) {
        super(mvc, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(false);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help_wonder_title));
                bundle.putString("message", mvc.getActivity().getString(R.string.help_wonder));
                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });

        Player player = mvc.getPlayer(playerViewing);
        for (Card card : player.getWonderStages()) {
            CardView cv = new CardView(card, mvc.getActivity(), player, false, false);
            if (!player.getPlayedCards().contains(card)) {
                cv.setText(mvc.getActivity().getString(R.string.not_built, cv.getText()));
            }
            content.addView(cv);
        }

        CardCollection cc = player.getPlayedCards();
        cc.sort();
        for (Card card : cc) {
            if (card.getType() == Card.Type.STAGE) continue;
            CardView cv = new CardView(card, mvc.getActivity(), player, false, false);
            content.addView(cv);
        }
    }

}
