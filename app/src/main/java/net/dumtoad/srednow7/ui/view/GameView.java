package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Game;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.UIUtil;

public abstract class GameView extends RelativeLayout {
    Player playerTurn;
    Player playerViewing;
    Activity activity;

    public GameView(Context context) {
        super(context);
    }

    GameView(Activity context, Player playerTurn, Player playerViewing) {
        super(context);
        this.activity = context;
        this.playerTurn = playerTurn;
        this.playerViewing = playerViewing;

        String wonderName = playerViewing.getWonder().getName().replace("_", " ");
        if (!UIUtil.isTablet()) {
            String[] parts = wonderName.split(" ");
            wonderName = parts[parts.length - 1];
        }
        ((TextView) activity.findViewById(R.id.title)).setText(wonderName);

        if (playerTurn == playerViewing) {
            activity.findViewById(R.id.hand).setVisibility(View.VISIBLE);
            ((Button) activity.findViewById(R.id.hand)).setText(activity.getString(R.string.hand));
        } else if (playerViewing == Bus.bus.getGame().getPlayerDirection(playerTurn, Game.Direction.EAST)
                || playerViewing == Bus.bus.getGame().getPlayerDirection(playerTurn, Game.Direction.WEST)) {
            activity.findViewById(R.id.hand).setVisibility(View.VISIBLE);
            ((Button) activity.findViewById(R.id.hand)).setText(activity.getString(R.string.trade));
        } else {
            activity.findViewById(R.id.hand).setVisibility(View.GONE);
        }

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup content = (ViewGroup) inflater.inflate(R.layout.content_view, this, true).findViewById(R.id.content_view);
        populateContent(content);
    }

    protected abstract void populateContent(ViewGroup content);
}
