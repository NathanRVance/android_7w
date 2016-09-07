package net.dumtoad.srednow7.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.controller.TableController;
import net.dumtoad.srednow7.player.Player;
import net.dumtoad.srednow7.util.Util;

public abstract class GameView extends RelativeLayout {
    protected MasterViewController mvc;
    protected int playerTurn;
    protected int playerViewing;

    public GameView(Context context) {
        super(context);
    }

    public GameView(MasterViewController mvc, int playerTurn, int playerViewing) {
        super(mvc.getActivity());
        this.mvc = mvc;
        this.playerTurn = playerTurn;
        this.playerViewing = playerViewing;

        Player player = mvc.getPlayer(playerViewing);
        String wonderName = player.getWonder().getNameString();
        if(! Util.isTablet()) {
            String[] parts = wonderName.split(" ");
            wonderName = parts[parts.length-1];
        }
        ((TextView) mvc.getActivity().findViewById(R.id.title)).setText(wonderName);

        if(playerTurn == playerViewing) {
            setupForTurn();
        } else {
            setupForView();
        }

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater = mvc.getActivity().getLayoutInflater();
        ViewGroup content = (ViewGroup) inflater.inflate(R.layout.content_view, this, true).findViewById(R.id.content_view);
        populateContent(content);
    }

    protected abstract void populateContent(ViewGroup content);

    protected void setupForTurn() {
        mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
        ((Button) mvc.getActivity().findViewById(R.id.hand)).setText(mvc.getActivity().getString(R.string.hand));
    }

    protected void setupForView() {
        TableController tc = mvc.getTableController();
        if (playerViewing == tc.getPlayerDirection(playerTurn, true) || playerViewing == tc.getPlayerDirection(playerTurn, false)) {
            mvc.getActivity().findViewById(R.id.hand).setVisibility(View.VISIBLE);
            ((Button) mvc.getActivity().findViewById(R.id.hand)).setText(mvc.getActivity().getString(R.string.trade));
        } else
            mvc.getActivity().findViewById(R.id.hand).setVisibility(View.GONE);
    }
}
