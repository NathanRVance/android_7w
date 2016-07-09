package net.dumtoad.android_7w.fragment;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.R;
import net.dumtoad.android_7w.player.Player;

import java.util.ArrayList;
import java.util.Collections;

public class EndFragment extends AbstractFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mvc = MainActivity.getMasterViewController();
        final View view = inflater.inflate(R.layout.end_view, container, false);

        TextView tv = (TextView) view.findViewById(R.id.results);

        ArrayList<PlayerScore> scores = new ArrayList<>();
        for(Player player : mvc.getPlayers()) {
            PlayerScore ps = new PlayerScore();
            ps.player = player;
            ps.score = player.getScore().getVPs();
            scores.add(ps);
        }
        Collections.sort(scores);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        for(PlayerScore ps : scores) {
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
        }

        tv.setText(sb);

        view.findViewById(R.id.new_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvc.setup();
            }
        });

        return view;
    }

    private class PlayerScore implements Comparable<PlayerScore> {
        public Player player;
        public int score;

        @Override
        public int compareTo(PlayerScore other) {
            if(score < other.score) return 1;
            if(score == other.score) return 0;
            return -1;
        }
    }

}
