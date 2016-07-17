package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.dialog.HelpDialog;

public class SummaryView extends GameView {

    public SummaryView(Context context) {
        super(context);
    }

    public SummaryView(MasterViewController mvc, int playerTurn, int playerViewing) {
        super(mvc, playerTurn, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(false);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(true);

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help_summary_title));
                bundle.putString("message", mvc.getActivity().getString(R.string.help_summary));
                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });

        TextView tv = new TextView(mvc.getActivity());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));
        tv.setText(mvc.getPlayer(playerViewing).getSummary());
        content.addView(tv);
    }

}
