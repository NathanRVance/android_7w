package net.dumtoad.srednow7.ui.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
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
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.Setup;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.LeftRightSwipe;
import net.dumtoad.srednow7.ui.UIUtil;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;

import static net.dumtoad.srednow7.ui.UIFacade.PLAYER_ID;

public class WonderSelectFragment extends Fragment implements LeftRightSwipe {

    private RadioButton buttA;
    private RadioButton buttB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.wonder_select, container, false);

        view.findViewById(R.id.help).setOnClickListener(view1 -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, getString(R.string.help_wonderselect_title));
            bundle.putString(HelpDialog.MESSAGE, getString(R.string.help_wonderselect));
            helpDialog.setArguments(bundle);
            helpDialog.show(getFragmentManager(), "helpDialog");
        });

        int playerID = getArguments().getInt(PLAYER_ID);

        final Player player = Bus.bus.getBackend().getPlayers().get(playerID);
        final Setup setup = Bus.bus.getBackend().getSetup(playerID);

        ((TextView) view.findViewById(R.id.player_name)).setText(player.getName());

        final RelativeLayout content = (RelativeLayout) view.findViewById(R.id.content);

        final TextView tv = (TextView) ((ScrollView) content.getChildAt(0)).getChildAt(0);
        tv.setText(UIUtil.getSummary(setup.getWonder()));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.textsize));

        RadioGroup rg = (RadioGroup) view.findViewById(R.id.wonder_switch);
        rg.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            boolean side = checkedId == R.id.wonder_switch_A;
            ScrollView s1 = (ScrollView) content.getChildAt(content.getChildCount() - 1);

            LayoutInflater inflater1 = LayoutInflater.from(getActivity());
            ScrollView s2 = (ScrollView) inflater1.inflate(R.layout.wonder_select_content, content, false);
            TextView tv1 = (TextView) s2.getChildAt(0);
            setup.setWonderSide(side ? 0 : 1);
            tv1.setText(UIUtil.getSummary(setup.getWonder()));
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.textsize));

            //Animate the swap
            UIUtil.animateTranslate(content, s1, s2, !side);
        });

        buttA = (RadioButton) rg.getChildAt(0);
        buttB = (RadioButton) rg.getChildAt(1);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("selectedSides")) {
                buttA.setChecked(true);
                setup.setWonderSide(0);
            } else {
                buttB.setChecked(true);
                setup.setWonderSide(1);
                tv.setText(UIUtil.getSummary(setup.getWonder()));
            }
        } else {
            buttA.setChecked(true);
            setup.setWonderSide(0);
        }

        view.findViewById(R.id.ok).setOnClickListener(v -> {
            setup.finish();
            Bus.bus.getUI().invalidateView();
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
