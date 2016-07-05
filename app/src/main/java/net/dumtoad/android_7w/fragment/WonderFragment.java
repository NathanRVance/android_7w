package net.dumtoad.android_7w.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.controller.TurnController;

public class WonderFragment extends AbstractFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = MainActivity.getMasterViewController();
        final View view = inflater.inflate(R.layout.wonder_view, container, false);

        view.findViewById(R.id.west).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.getTableController().getTurnController().go(true);
            }
        });

        view.findViewById(R.id.east).setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mvc.getTableController().getTurnController().onComplete();
    }

}
