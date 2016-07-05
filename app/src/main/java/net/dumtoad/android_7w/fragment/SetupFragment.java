package net.dumtoad.android_7w.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.view.SetupPlayerItem;

import java.util.ArrayList;

public class SetupFragment extends AbstractFragment {

    private int numPlayers = 3;
    private ArrayList<SetupPlayerItem> setupItems;
    private String[] names;
    private boolean[] ais;
    private LinearLayout playerSelectLayout;
    private Button addButton;
    private Button subtractButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.setup, container, false);
        playerSelectLayout = (LinearLayout) view.findViewById(R.id.player_select_layout);

        names = new String[7];
        ais = new boolean[7];
        ais[0] = false;
        for(int i = 1; i < 7; i++) {
            ais[i] = true;
        }
        numPlayers = 3;
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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            addSetupItem();
            addSetupItem();
            addSetupItem();
            return;
        }
        setupItems = new ArrayList<>();
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
        final int index = setupItems.size();
        String name = "Player " + (setupItems.size()+1);
        names[index] = name;

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
