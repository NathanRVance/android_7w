package net.dumtoad.srednow7.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.view.EndView;

public class EndFragment extends GameFragment {

    @Override
    public ViewGroup getHandTradeView() {
        return new EndView(mvc, playerViewing);
    }

    @Override
    protected ViewGroup getMode() {
        ViewGroup ret = super.getMode();
        //Fix buttons
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
        ((Button) mvc.getActivity().findViewById(R.id.hand)).setText(R.string.score);
        return ret;
    }

}
