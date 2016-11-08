package net.dumtoad.srednow7.ui.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.view.EndView;

public class EndFragment extends GameFragment {

    @Override
    public ViewGroup getHandTradeView() {
        return new EndView(getActivity(), playerViewing);
    }

    @Override
    protected ViewGroup getMode() {
        ViewGroup ret = super.getMode();
        //Fix buttons
        getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
        ((Button) getActivity().findViewById(R.id.hand)).setText(R.string.score);
        return ret;
    }

}
