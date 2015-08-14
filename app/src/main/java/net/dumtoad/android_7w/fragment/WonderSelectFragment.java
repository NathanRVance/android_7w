package net.dumtoad.android_7w.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Player;

/**
 * Created by nathav63 on 7/28/15.
 */
public class WonderSelectFragment extends AbstractFragment {

    public void setPlayerNum(int playerNum) {
        Bundle bundle = new Bundle();
        bundle.putInt("PlayerNum", playerNum);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int playerNum = getArguments().getInt("PlayerNum");
        Player player = mvc.getPlayer(playerNum);

        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        return view;
    }
}
