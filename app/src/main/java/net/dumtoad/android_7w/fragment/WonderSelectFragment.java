package net.dumtoad.android_7w.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.cards.Player;

public class WonderSelectFragment extends AbstractFragment {

    private RadioButton buttA;

    public void setPlayerNum(int playerNum) {
        Bundle bundle = new Bundle();
        bundle.putInt("PlayerNum", playerNum);
        setArguments(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = ((MainActivity) getActivity()).getMasterViewController();
        int playerNum = getArguments().getInt("PlayerNum");
        final Player player = mvc.getPlayer(playerNum);

        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        final TextView tv = (TextView) view.findViewById(R.id.text);
        tv.setText(player.getWonder().getSummary(true));

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.wonder_switch);
        buttA = (RadioButton) rg.getChildAt(0);
        RadioButton buttB = (RadioButton) rg.getChildAt(1);
        buttA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if(rb.isChecked()) {
                    tv.setText(player.getWonder().getSummary(true));
                }
            }
        });

       buttB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if(rb.isChecked()) {
                    tv.setText(player.getWonder().getSummary(false));
                }
            }
        });

        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("selectedSides")) {
                buttA.setChecked(true);
            } else {
                buttB.setChecked(true);
                tv.setText(player.getWonder().getSummary(false));
            }
        } else {
            buttA.setChecked(true);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("selectedSides", buttA.isChecked());
    }
}
