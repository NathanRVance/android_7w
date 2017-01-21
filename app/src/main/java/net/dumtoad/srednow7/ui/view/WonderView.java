package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;

public class WonderView extends GameView {

    public WonderView(Context context) {
        super(context);
    }

    public WonderView(Activity activity, Player playerTurn, Player playerViewing) {
        super(activity, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        activity.findViewById(R.id.wonder).setEnabled(false);
        activity.findViewById(R.id.summary).setEnabled(true);
        activity.findViewById(R.id.hand).setEnabled(true);

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help_wonder_title));
            bundle.putString(HelpDialog.MESSAGE, activity.getString(R.string.help_wonder));
            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });

        for (Card card : playerViewing.getWonder().getStages()) {
            CardView cv = new CardView(card, activity, playerViewing, false, false, getQueueID());
            if (!playerViewing.getPlayedCards().contains(card)) {
                cv.setText(activity.getString(R.string.not_built, cv.getText()));
            }
            content.addView(cv);
        }

        for (Card card : playerViewing.getPlayedCards()) {
            if (card.getType() == Card.Type.STAGE) continue;
            CardView cv = new CardView(card, activity, playerViewing, false, false, getQueueID());
            content.addView(cv);
        }
    }

}
