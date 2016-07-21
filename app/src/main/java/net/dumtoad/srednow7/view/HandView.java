package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Card;
import net.dumtoad.srednow7.cards.Hand;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.controller.TradeController;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.fragment.GameFragment;
import net.dumtoad.srednow7.player.Player;

public class HandView extends GameView {

    public HandView(Context context) {
        super(context);
    }

    public HandView(MasterViewController mvc, int playerTurn) {
        super(mvc, playerTurn, playerTurn);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help_hand_title));
                bundle.putString("message", mvc.getActivity().getString(R.string.help_hand));
                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });

        TextView tv = new TextView(mvc.getActivity());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));
        String direction = (mvc.getTableController().getPassingDirection())?
                mvc.getActivity().getResources().getString(R.string.west)
                : mvc.getActivity().getResources().getString(R.string.east);
        tv.setText(mvc.getActivity().getResources().getString(R.string.passing_direction, direction));
        content.addView(tv);

        Player player = mvc.getPlayer(playerViewing);
        TradeController tc = player.getTradeController();
        Hand hand;
        GameFragment gameFragment = (GameFragment) mvc.getActivity().getFragmentManager().findFragmentByTag(GameFragment.GAME_FRAGMENT_TAG);
        boolean playDiscard = gameFragment.isPlayDiscard();
        if (playDiscard) {
            hand = mvc.getTableController().getDiscards();
        } else {
            hand = player.getHand();
        }
        for (Card card : hand) {
            CardView cv = new CardView(card, mvc.getActivity(), player, true, playDiscard);
            if (!(playDiscard || player.hasCouponFor(card) || (tc.canAffordResources(card) && tc.canAffordGold(card)))) {
                //Darken it slightly
                cv.getBackground().setColorFilter(ContextCompat.getColor(mvc.getActivity(), R.color.gray), PorterDuff.Mode.MULTIPLY);
            }
            content.addView(cv);
        }
    }

}
