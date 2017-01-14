package net.dumtoad.srednow7.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.implementation.Generate;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.dialog.EditTextDialog;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;
import net.dumtoad.srednow7.ui.view.SetupPlayerItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SetupFragment extends Fragment {

    private int numPlayers = 3;
    private ArrayList<SetupPlayerItem> setupItems;
    private String[] names = new String[]{"Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6", "Player 7"};
    private boolean[] ais = new boolean[]{false, true, true, true, true, true, true};
    private Set<Generate.Expansion> expansions = new HashSet<>();
    private LinearLayout playerSelectLayout;
    private LinearLayout optionsLayout;
    private Button addButton;
    private Button subtractButton;
    private TextView title;
    private Button players;
    private Button options;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Start out by dismissing any previously active dialogs
        Fragment frag = getFragmentManager().findFragmentByTag(SetupPlayerItem.EDIT_TEXT_DIALOG_TAG);
        if (frag != null) {
            ((EditTextDialog) frag).dismiss();
        }

        final View view = inflater.inflate(R.layout.setup, container, false);
        playerSelectLayout = (LinearLayout) view.findViewById(R.id.player_select_layout);
        optionsLayout = new LinearLayout(getActivity());
        optionsLayout.setLayoutParams(playerSelectLayout.getLayoutParams());

        view.findViewById(R.id.help).setOnClickListener(view1 -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, getString(R.string.help_setup_title));
            bundle.putString(HelpDialog.MESSAGE, getString(R.string.help_setup));
            helpDialog.setArguments(bundle);
            helpDialog.show(getFragmentManager(), "helpDialog");
        });

        setupItems = new ArrayList<>();

        addButton = (Button) view.findViewById(R.id.add_button);
        subtractButton = (Button) view.findViewById(R.id.subtract_button);
        title = (TextView) view.findViewById(R.id.title);
        players = (Button) view.findViewById(R.id.players);
        options = (Button) view.findViewById(R.id.options);

        addButton.setOnClickListener(v -> {
            numPlayers++;
            addSetupItem();
            subtractButton.setEnabled(true);
            if (numPlayers == 7)
                addButton.setEnabled(false);
        });

        subtractButton.setOnClickListener(v -> {
            numPlayers--;
            removeSetupItem();
            addButton.setEnabled(true);
            if (numPlayers == 3)
                subtractButton.setEnabled(false);
        });
        subtractButton.setEnabled(false);

        players.setOnClickListener(v -> {
            options.setEnabled(true);
            players.setEnabled(false);
            title.setText(players.getText());
            ViewGroup vg = (ViewGroup) view.findViewById(R.id.scrollView);
            vg.removeView(optionsLayout);
            vg.addView(playerSelectLayout);
        });
        players.setEnabled(false);

        options.setOnClickListener(v -> {
            players.setEnabled(true);
            options.setEnabled(false);
            title.setText(options.getText());
            ViewGroup vg = (ViewGroup) view.findViewById(R.id.scrollView);
            vg.removeView(playerSelectLayout);
            vg.addView(optionsLayout);
        });

        view.findViewById(R.id.ok).setOnClickListener(v -> {
            String[] playerNames = new String[numPlayers];
            System.arraycopy(names, 0, playerNames, 0, numPlayers);
            boolean[] isAi = new boolean[numPlayers];
            System.arraycopy(ais, 0, isAi, 0, numPlayers);
            Bus.bus.getGame().initialize(playerNames, isAi, expansions);
            Bus.bus.getUI().invalidateView();
        });

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            addSetupItem();
            addSetupItem();
            addSetupItem();
        }

        for(Generate.Expansion expansion : Generate.Expansion.values()) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(expansion.toString());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize));
            if(expansions.contains(expansion)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setOnClickListener(v -> {
                if(checkBox.isChecked())
                    expansions.add(expansion);
                else
                    expansions.remove(expansion);
            });
            optionsLayout.addView(checkBox);
        }

        return view;
    }

    private void restoreState(Bundle savedInstanceState) {
        names = savedInstanceState.getStringArray("names");
        ais = savedInstanceState.getBooleanArray("ais");
        numPlayers = savedInstanceState.getInt("numPlayers");

        addButton.setEnabled(numPlayers < 7);
        subtractButton.setEnabled(numPlayers > 3);

        for (int i = 0; i < numPlayers; i++) {
            addSetupItem();
        }

        for(String exp : savedInstanceState.getStringArrayList("expansions")) {
            expansions.add(Generate.Expansion.valueOf(exp));
        }

        if(savedInstanceState.getBoolean("isOptions")) {
            options.performClick();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("names", names);
        outState.putBooleanArray("ais", ais);
        outState.putInt("numPlayers", numPlayers);
        outState.putBoolean("isOptions", ! options.isEnabled());
        ArrayList<String> exps = new ArrayList<>();
        for(Generate.Expansion expansion : expansions) {
            exps.add(expansion.toString());
        }
        outState.putStringArrayList("expansions", exps);
    }

    private void addSetupItem() {
        SetupPlayerItem item = new SetupPlayerItem(getActivity(), names, ais, setupItems.size());
        setupItems.add(item);

        //Animate the addition
        int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        item.setAlpha(0f);
        playerSelectLayout.addView(item); //Added, but invisible
        item.animate().alpha(1f).setDuration(duration).setListener(null);
    }

    private void removeSetupItem() {
        final SetupPlayerItem item = setupItems.remove(setupItems.size() - 1);

        //Animate the deletion
        int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        item.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                playerSelectLayout.removeView(item);
            }
        });
    }

}
