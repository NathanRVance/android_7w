package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;
import net.dumtoad.srednow7.ui.UIUtil;

public class SummaryView extends GameView {

    public SummaryView(Context context) {
        super(context);
    }

    public SummaryView(Activity activity, Player playerTurn, Player playerViewing) {
        super(activity, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        activity.findViewById(R.id.wonder).setEnabled(true);
        activity.findViewById(R.id.summary).setEnabled(false);
        activity.findViewById(R.id.hand).setEnabled(true);

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help_summary_title));
            bundle.putString(HelpDialog.MESSAGE, activity.getString(R.string.help_summary));
            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });

        TextView tv = new TextView(activity);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.textsize));
        tv.setText(UIUtil.getSummary(playerViewing));
        content.addView(tv);
    }

}
