package net.dumtoad.android_7w.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.player.Player;

import java.util.ArrayList;

public class WonderSelectFragment extends AbstractFragment {

    private RadioButton buttA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = MainActivity.getMasterViewController();
        final ArrayList<Integer> playerNums = getArguments().getIntegerArrayList("PlayerNums");
        if(playerNums == null || playerNums.isEmpty()) {
            throw new RuntimeException("Can't have AI only game");
        }
        int playerNum = playerNums.get(0);
        final Player player = mvc.getPlayer(playerNum);

        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());

        final RelativeLayout content = (RelativeLayout) view.findViewById(R.id.content);

        final TextView tv = (TextView) ((ScrollView) content.getChildAt(0)).getChildAt(0);
        tv.setText(player.getWonder().getSummary(true));

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.wonder_switch);
        buttA = (RadioButton) rg.getChildAt(0);
        RadioButton buttB = (RadioButton) rg.getChildAt(1);
        buttA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if(rb.isChecked()) {
                    player.setWonderSide(true);
                    ScrollView s1 = (ScrollView) content.getChildAt(content.getChildCount()-1);

                    LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
                    ScrollView s2 = (ScrollView) inflater.inflate(R.layout.wonder_select_content, content, false);
                    TextView tv = (TextView) s2.getChildAt(0);
                    tv.setText(player.getWonder().getSummary(true));

                    //Animate the swap
                    mvc.animateTranslate(content, s1, s2, false);
                }
            }
        });

       buttB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if(rb.isChecked()) {
                    player.setWonderSide(false);
                    ScrollView s1 = (ScrollView) content.getChildAt(content.getChildCount()-1);

                    LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
                    ScrollView s2 = (ScrollView) inflater.inflate(R.layout.wonder_select_content, content, false);
                    TextView tv = (TextView) s2.getChildAt(0);
                    tv.setText(player.getWonder().getSummary(false));

                    //Animate the swap
                    mvc.animateTranslate(content, s1, s2, true);
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
                playerNums.remove(0);
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
