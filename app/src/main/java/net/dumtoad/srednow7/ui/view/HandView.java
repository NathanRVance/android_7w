package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;

public class HandView extends GameView {

    public HandView(Context context) {
        super(context);
    }

    public HandView(Activity activity, Player playerTurn) {
        super(activity, playerTurn, playerTurn);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        activity.findViewById(R.id.wonder).setEnabled(true);
        activity.findViewById(R.id.summary).setEnabled(true);
        activity.findViewById(R.id.hand).setEnabled(false);

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help_hand_title));
            bundle.putString(HelpDialog.MESSAGE, activity.getString(R.string.help_hand));
            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });

        TextView tv = new TextView(activity);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.textsize));
        String direction = (Bus.bus.getGame().getPassingDirection() == Game.Direction.WEST) ?
                activity.getResources().getString(R.string.west)
                : activity.getResources().getString(R.string.east);
        tv.setText(activity.getResources().getString(R.string.passing_direction, direction));
        content.addView(tv);

        Player player = playerViewing;
        for (Card card : player.getHand()) {
            boolean isPlayDiscard = player.isPlayDiscard();
            CardView cv = new CardView(card, activity, player, true, isPlayDiscard, getQueueID());
            if (!(isPlayDiscard || player.hasCouponFor(card) || player.canAffordBuild(card))) {
                //Darken it slightly
                cv.getBackground().setColorFilter(ContextCompat.getColor(activity, R.color.gray), PorterDuff.Mode.MULTIPLY);
            }
            content.addView(cv);
        }
    }

}
