package net.dumtoad.srednow7.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.TurnController;

public class GameFragment extends AbstractFragment implements MainActivity.LeftRightSwipe {

    private Button west;
    private Button east;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = MainActivity.getMasterViewController();
        final View view = inflater.inflate(R.layout.game_view, container, false);

        west = (Button) view.findViewById(R.id.west);
        west.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.getTableController().getTurnController().go(true);
            }
        });

        east = (Button) view.findViewById(R.id.east);
        east.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.getTableController().getTurnController().go(false);
            }
        });

        view.findViewById(R.id.wonder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnController tc = mvc.getTableController().getTurnController();
                tc.crossfadeToView(tc.showWonder());
            }
        });

        view.findViewById(R.id.summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnController tc = mvc.getTableController().getTurnController();
                tc.crossfadeToView(tc.showSummary());
            }
        });

        view.findViewById(R.id.hand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TurnController tc = mvc.getTableController().getTurnController();
                tc.crossfadeToView(tc.showHand());
            }
        });

        MainActivity.getMainActivity().registerLeftRightSwipe(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mvc.getTableController().getTurnController().onComplete();
    }

    @Override
    public void swipeLeft() {
        east.performClick();
    }

    @Override
    public void swipeRight() {
        west.performClick();
    }
}
