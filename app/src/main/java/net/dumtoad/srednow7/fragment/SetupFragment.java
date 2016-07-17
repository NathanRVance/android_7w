package net.dumtoad.srednow7.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.dialog.EditTextDialog;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.view.SetupPlayerItem;

import java.util.ArrayList;

public class SetupFragment extends AbstractFragment {

    private int numPlayers = 3;
    private ArrayList<SetupPlayerItem> setupItems;
    private String[] names = new String[]{"Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6", "Player 7"};
    private boolean[] ais = new boolean[]{false, true, true, true, true, true, true};
    private LinearLayout playerSelectLayout;
    private Button addButton;
    private Button subtractButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Start out by dismissing any previously active dialogs
        Fragment frag = getFragmentManager().findFragmentByTag(SetupPlayerItem.EDIT_TEXT_DIALOG_TAG);
        if(frag != null) {
            ((EditTextDialog) frag).dismiss();
        }

        final View view = inflater.inflate(R.layout.setup, container, false);
        playerSelectLayout = (LinearLayout) view.findViewById(R.id.player_select_layout);

        view.findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.help_setup_title));
                bundle.putString("message", getString(R.string.help_setup));
                helpDialog.setArguments(bundle);
                helpDialog.show(getFragmentManager(), "helpDialog");
            }
        });

        setupItems = new ArrayList<>();

        addButton = (Button) view.findViewById(R.id.add_button);
        subtractButton = (Button) view.findViewById(R.id.subtract_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numPlayers++;
                addSetupItem();
                subtractButton.setEnabled(true);
                if (numPlayers == 7)
                    addButton.setEnabled(false);
            }
        });

        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numPlayers--;
                removeSetupItem();
                addButton.setEnabled(true);
                if (numPlayers == 3)
                    subtractButton.setEnabled(false);
            }
        });
        subtractButton.setEnabled(false);

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.postSetup(names, ais, numPlayers);
            }
        });

        if(savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            addSetupItem();
            addSetupItem();
            addSetupItem();
        }

        return view;
    }

    private void restoreState(Bundle savedInstanceState) {
        names = savedInstanceState.getStringArray("names");
        ais = savedInstanceState.getBooleanArray("ais");
        numPlayers = savedInstanceState.getInt("numPlayers");

        addButton.setEnabled(numPlayers < 7);
        subtractButton.setEnabled(numPlayers > 3);

        for(int i = 0; i < numPlayers; i++) {
            addSetupItem();
        }

        this.mvc = MainActivity.getMasterViewController();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("names", names);
        outState.putBooleanArray("ais", ais);
        outState.putInt("numPlayers", numPlayers);
    }

    private void addSetupItem() {
        SetupPlayerItem item = new SetupPlayerItem(getActivity(), names, ais, setupItems.size());
        setupItems.add(item);

        //Animate the addition!
        int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        item.setAlpha(0f);
        playerSelectLayout.addView(item); //Added, but invisible!
        item.animate().alpha(1f).setDuration(duration).setListener(null);
    }

    private void removeSetupItem() {
        final SetupPlayerItem item = setupItems.remove(setupItems.size() - 1);

        //Animate the deletion!
        int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        item.animate().alpha(0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                playerSelectLayout.removeView(item);
            }
        });
    }

}
