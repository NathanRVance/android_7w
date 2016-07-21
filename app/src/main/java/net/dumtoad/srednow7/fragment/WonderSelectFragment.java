package net.dumtoad.srednow7.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.player.Player;
import net.dumtoad.srednow7.util.Util;

import java.util.ArrayList;

public class WonderSelectFragment extends AbstractFragment implements MainActivity.LeftRightSwipe {

    private RadioButton buttA;
    private RadioButton buttB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        view.findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.help_wonderselect_title));
                bundle.putString("message", getString(R.string.help_wonderselect));
                helpDialog.setArguments(bundle);
                helpDialog.show(getFragmentManager(), "helpDialog");
            }
        });

        this.mvc = MainActivity.getMasterViewController();
        final ArrayList<Integer> playerNums = getArguments().getIntegerArrayList("PlayerNums");
        if (playerNums == null || playerNums.size() == 0) {
            mvc.startMainGame();
            return view;
            //throw new RuntimeException("Can't have AI only game");
        }
        int playerNum = playerNums.get(0);
        final Player player = mvc.getPlayer(playerNum);

        ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());

        final RelativeLayout content = (RelativeLayout) view.findViewById(R.id.content);

        final TextView tv = (TextView) ((ScrollView) content.getChildAt(0)).getChildAt(0);
        tv.setText(player.getWonder().getSummary(true));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.wonder_switch);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                boolean side = checkedId == R.id.wonder_switch_A;
                player.setWonderSide(side);
                ScrollView s1 = (ScrollView) content.getChildAt(content.getChildCount() - 1);

                LayoutInflater inflater = LayoutInflater.from(mvc.getActivity());
                ScrollView s2 = (ScrollView) inflater.inflate(R.layout.wonder_select_content, content, false);
                TextView tv = (TextView) s2.getChildAt(0);
                tv.setText(player.getWonder().getSummary(side));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mvc.getActivity().getResources().getDimension(R.dimen.textsize));

                //Animate the swap
                Util.animateTranslate(content, s1, s2, ! side);
            }
        });

        buttA = (RadioButton) rg.getChildAt(0);
        buttB = (RadioButton) rg.getChildAt(1);

        if (savedInstanceState != null) {
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
                if (playerNums.isEmpty()) {
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

        MainActivity.getMainActivity().registerLeftRightSwipe(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("selectedSides", buttA.isChecked());
    }

    @Override
    public void swipeLeft() {
        buttB.performClick();
    }

    @Override
    public void swipeRight() {
        buttA.performClick();
    }
}
