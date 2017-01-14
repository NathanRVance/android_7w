package net.dumtoad.srednow7.ui.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.UIFacade;
import net.dumtoad.srednow7.ui.dialog.HelpDialog;
import net.dumtoad.srednow7.ui.fragment.GameFragment;
import net.dumtoad.srednow7.ui.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EndView extends GameView {

    public EndView(Context context) {
        super(context);
    }

    public EndView(Activity context, Player playerViewing) {
        super(context, playerViewing, playerViewing);
    }

    @Override
    protected void populateContent(ViewGroup content) {
        activity.findViewById(R.id.wonder).setEnabled(true);
        activity.findViewById(R.id.summary).setEnabled(true);
        activity.findViewById(R.id.hand).setEnabled(false);

        activity.findViewById(R.id.help).setOnClickListener(view -> {
            DialogFragment helpDialog = new HelpDialog();
            Bundle bundle = new Bundle();
            bundle.putString(HelpDialog.TITLE, activity.getString(R.string.help_score_title));
            bundle.putString(HelpDialog.MESSAGE, activity.getString(R.string.help_score));
            helpDialog.setArguments(bundle);
            helpDialog.show(activity.getFragmentManager(), "helpDialog");
        });

        LayoutInflater inflater = activity.getLayoutInflater();
        inflater.inflate(R.layout.new_game_button, content, true);
        content.findViewById(R.id.new_game).setOnClickListener(v -> Bus.bus.startNewGame());

        List<PlayerScore> scores = new ArrayList<>();
        for (Player player : Bus.bus.getGame().getPlayers()) {
            PlayerScore ps = new PlayerScore();
            ps.player = player;
            ps.score = player.getScore().getTotalVPs();
            scores.add(ps);
        }
        Collections.sort(scores);

        final GameFragment gameFragment = (GameFragment) activity.getFragmentManager().findFragmentByTag(UIFacade.FRAGMENT_TAG);

        for (final PlayerScore ps : scores) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append(ps.player.getName())
                    .append(" (")
                    .append(UIUtil.formatName(ps.player.getWonder().getName(), Card.Type.STAGE))
                    .append(") VPs: ")
                    .append(String.valueOf(ps.score))
                    .append("\n");
            UIUtil.appendSb(sb, " Military", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.MILITARY.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getMilitaryVps())).append("\n");
            UIUtil.appendSb(sb, " Gold", new ForegroundColorSpan(UIUtil.getColorId(Card.Resource.GOLD.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getGoldVps())).append("\n");
            UIUtil.appendSb(sb, " Wonder", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.STAGE.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getWonderVps())).append("\n");
            UIUtil.appendSb(sb, " Structure", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.STRUCTURE.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getStructureVps())).append("\n");
            UIUtil.appendSb(sb, " Commerce", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.COMMERCIAL.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getCommercialVps())).append("\n");
            UIUtil.appendSb(sb, " Guild", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.GUILD.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getGuildVps())).append("\n");
            UIUtil.appendSb(sb, " Science", new ForegroundColorSpan(UIUtil.getColorId(Card.Type.SCIENCE.toString())));
            sb.append(": ").append(String.valueOf(ps.player.getScore().getScienceVps())).append("\n");
            sb.append("\n");

            final TextView tv = new TextView(activity);
            tv.setText(sb);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.textsize));
            tv.setOnClickListener(view -> {
                if (ps.player != gameFragment.getPlayerViewing())
                    gameFragment.goTo(ps.player);
            });
            content.addView(tv);
            if (ps.player == playerViewing) {
                findViewById(R.id.content_scroll_view).post(() -> findViewById(R.id.content_scroll_view).scrollTo(0, tv.getTop()));
            }
        }
    }

    private class PlayerScore implements Comparable<PlayerScore> {
        public Player player;
        int score;

        @Override
        public int compareTo(@NonNull PlayerScore other) {
            if (score < other.score) return 1;
            if (score == other.score) return 0;
            return -1;
        }
    }

}
