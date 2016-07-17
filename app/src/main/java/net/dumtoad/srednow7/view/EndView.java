package net.dumtoad.srednow7.view;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.dialog.HelpDialog;
import net.dumtoad.srednow7.player.Player;

import java.util.ArrayList;
import java.util.Collections;

public class EndView extends GameView {

    public EndView(Context context) {
        super(context);
    }

    public EndView(MasterViewController mvc, int playerViewing) {
        super(mvc, playerViewing, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        mvc.getActivity().findViewById(R.id.wonder).setEnabled(true);
        mvc.getActivity().findViewById(R.id.summary).setEnabled(true);
        mvc.getActivity().findViewById(R.id.hand).setEnabled(false);

        mvc.getActivity().findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment helpDialog = new HelpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", mvc.getActivity().getString(R.string.help_score_title));
                bundle.putString("message", mvc.getActivity().getString(R.string.help_score));
                helpDialog.setArguments(bundle);
                helpDialog.show(mvc.getActivity().getFragmentManager(), "helpDialog");
            }
        });

        LayoutInflater inflater = mvc.getActivity().getLayoutInflater();
        inflater.inflate(R.layout.new_game_button, content, true);
        content.findViewById(R.id.new_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.setup();
            }
        });

        ArrayList<PlayerScore> scores = new ArrayList<>();
        for(Player player : mvc.getPlayers()) {
            PlayerScore ps = new PlayerScore();
            ps.player = player;
            ps.score = player.getScore().getVPs();
            scores.add(ps);
        }
        Collections.sort(scores);

        for(PlayerScore ps : scores) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(ps.player.getName())
                    .append(" (")
                    .append(ps.player.getWonder().getNameString())
                    .append(") VPs: ")
                    .append(String.valueOf(ps.score))
                    .append("\n");
            sb.append(" Military: ").append(String.valueOf(ps.player.getScore().getMilitaryVps())).append("\n");
            sb.append(" Gold: ").append(String.valueOf(ps.player.getScore().getGoldVps())).append("\n");
            sb.append(" Wonder: ").append(String.valueOf(ps.player.getScore().getWonderVps())).append("\n");
            sb.append(" Structure: ").append(String.valueOf(ps.player.getScore().getStructureVps())).append("\n");
            sb.append(" Commerce: ").append(String.valueOf(ps.player.getScore().getCommercialVps())).append("\n");
            sb.append(" Guild: ").append(String.valueOf(ps.player.getScore().getGuildVps())).append("\n");
            sb.append(" Science: ").append(String.valueOf(ps.player.getScore().getScienceVps())).append("\n");
            sb.append("\n");

            final TextView tv = new TextView(mvc.getActivity());
            tv.setText(sb);
            content.addView(tv);
            if(mvc.getPlayerNum(ps.player) == playerViewing) {
                findViewById(R.id.content_scroll_view).post(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.content_scroll_view).scrollTo(0, tv.getTop());
                    }
                });
            }
        }
    }

    private class PlayerScore implements Comparable<PlayerScore> {
        public Player player;
        public int score;

        @Override
        public int compareTo(@NonNull PlayerScore other) {
            if(score < other.score) return 1;
            if(score == other.score) return 0;
            return -1;
        }
    }

}
