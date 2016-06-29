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

import java.util.ArrayList;

public class WonderSelectFragment extends AbstractFragment {

    private RadioButton buttA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = MainActivity.getMasterViewController();
        final ArrayList<Integer> playerNums = getArguments().getIntegerArrayList("PlayerNums");
        int playerNum = playerNums.remove(0);
        final Player player = mvc.getPlayer(playerNum);

        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());

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
                    player.setWonderSide(true);
                }
            }
        });

       buttB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if(rb.isChecked()) {
                    tv.setText(player.getWonder().getSummary(false));
                    player.setWonderSide(false);
                }
            }
        });

        if(savedInstanceState != null) {
            if (savedInstanceState.getBoolean("selectedSides")) {
                buttA.setChecked(true);
                player.setWonderSide(true);
            } else {
                buttB.setChecked(true);
                tv.setText(player.getWonder().getSummary(false));
                player.setWonderSide(false);
            }
        } else {
            buttA.setChecked(true);
            player.setWonderSide(true);
        }

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerNums.isEmpty()) {
                    mvc.startMainGame();
                } else {
                    WonderSelectFragment frag = new WonderSelectFragment();
                    Bundle bundle = new Bundle();
                    bundle.putIntegerArrayList("PlayerNums", playerNums);
                    frag.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.main_layout, frag, "WonderSelect")
                            .commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("selectedSides", buttA.isChecked());
    }
}
