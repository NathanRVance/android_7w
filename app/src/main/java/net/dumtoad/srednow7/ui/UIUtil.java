package net.dumtoad.srednow7.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.backend.Card;
import net.dumtoad.srednow7.backend.CardList;
import net.dumtoad.srednow7.backend.Player;
import net.dumtoad.srednow7.backend.ResQuant;
import net.dumtoad.srednow7.backend.Wonder;
import net.dumtoad.srednow7.backend.implementation.CardListImpl;
import net.dumtoad.srednow7.backend.implementation.ResQuantImpl;

import java.util.List;

public class UIUtil {

    private UIUtil() {
    }

    public static CharSequence getSummary(Player player) {
        Card.Resource[] ored = new Card.Resource[] {Card.Resource.WOOD, Card.Resource.STONE, Card.Resource.CLAY,
                Card.Resource.ORE, Card.Resource.CLOTH, Card.Resource.GLASS, Card.Resource.PAPER, Card.Resource.COMPASS,
                Card.Resource.GEAR, Card.Resource.TABLET};

        SpannableStringBuilder sb = new SpannableStringBuilder();
        ResQuant production = new ResQuantImpl();
        production.put(player.getWonder().getResource(), 1);
        CardList complicated = new CardListImpl();
        for(Card card : player.getPlayedCards()) {
            int numRes = 0;
            for(Card.Resource res : ored) {
                if(card.getProducts(player).get(res) > 0) numRes++;
            }
            if(numRes > 1) {
                complicated.add(card);
            } else {
                production.addResources(card.getProducts(player));
            }
        }

        ForegroundColorSpan fcs = new ForegroundColorSpan(getColorId(Card.Resource.GOLD.toString()));
        appendSb(sb, "Gold", fcs);
        sb.append(": ").append(String.valueOf(player.getGold())).append("\n");

        if(complicated.size() > 0) {
            sb.append("Dynamic products:");
            for(Card card : complicated) {
                sb.append("\n ");
                for(Card.Resource product : ored) {
                    if(card.getProducts(player).get(product) == 1) {
                        fcs = new ForegroundColorSpan(getColorId(product.toString()));
                        appendSb(sb, product.toString().toLowerCase(), fcs);
                        sb.append(" or ");
                    }
                }
                //Remove the last " or "
                sb.delete(sb.length()-4, sb.length());
            }
            sb.append("\n");
        }

        sb.append("Static products:\n");
        for(Card.Resource product : Card.Resource.values()) {
            if(product == Card.Resource.GOLD) continue;
            sb.append(" ");
            fcs = new ForegroundColorSpan(getColorId(product.toString()));
            appendSb(sb, product.toString().toLowerCase(), fcs);
            sb.append(": ");
            sb.append(production.get(product).toString());
            sb.append("\n");
        }

        sb.append("Net military points: ").append(String.valueOf(player.getScore().getMilitaryVps()));

        return sb;
    }

    public static CharSequence getSummary(Wonder wonder) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(formatEnum(wonder.getEnum(), Card.Type.STAGE));
        sb.append('\n');
        sb.append("produces: ");
        ForegroundColorSpan fcs = new ForegroundColorSpan(getColorId(wonder.getResource().toString()));
        appendSb(sb, wonder.getResource().toString().toLowerCase(), fcs);
        sb.append('\n');
        List<Card> stages = wonder.getStages();
        int i = 1;
        for (Card card : stages) {
            sb.append("\nStage ").append(String.valueOf(i++));
            sb.append("\n");
            sb.append(getSummary(card, null, false, false));
            sb.append("\n--------\n");
        }

        return sb;
    }

    public static CharSequence getSummary(Card card, Player player, boolean printSpecialVps, boolean printSpecialGold) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        ForegroundColorSpan fcs;
        int numNonZero;

        numNonZero = 0;
        for (Integer i : card.getCosts(player).values()) {
            if (!i.equals(0))
                numNonZero++;
        }
        if (numNonZero != 0) {
            sb.append("Costs:\n");
            for (Card.Resource resource : card.getCosts(player).keySet()) {
                if (card.getCosts(player).get(resource).equals(0))
                    continue;
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(resource.toString()));
                appendSb(sb, resource.toString().toLowerCase(), fcs);
                sb.append(": ");
                sb.append(card.getCosts(player).get(resource).toString());
                sb.append("\n");
            }
        }

        numNonZero = 0;
        for (Integer i : card.getProducts(player).values()) {
            if (!i.equals(0))
                numNonZero++;
        }
        if (numNonZero != 0) {
            sb.append("Produces:\n");
            int i = 1;
            for (Card.Resource product : card.getProducts(player).keySet()) {
                if (card.getProducts(player).get(product).equals(0))
                    continue;
                sb.append(" ");
                fcs = new ForegroundColorSpan(getColorId(product.toString()));
                appendSb(sb, product.toString().toLowerCase(), fcs);
                sb.append(": ");
                sb.append(card.getProducts(player).get(product).toString());
                if (i < numNonZero && isBaseResource(product.toString())) {
                    sb.append("\tor");
                }
                sb.append("\n");
                i++;
            }
        }

        if (card.getMessage().length() != 0) {
            sb.append(card.getMessage());
            sb.append('\n');
        }

        if (printSpecialVps && card.isSpecialVps()) {
            sb.append("Currently produces:\n");
            sb.append(" ");
            fcs = new ForegroundColorSpan(getColorId(Card.Resource.VP.toString()));
            appendSb(sb, Card.Resource.VP.toString().toLowerCase(), fcs);
            sb.append(": ");
            sb.append(String.valueOf(card.getSpecialVps(player)));
            sb.append("\n");
        }

        if (printSpecialGold && card.isSpecialGold()) {
            if (!card.isSpecialVps())
                sb.append("Currently produces:\n");
            sb.append(" ");
            fcs = new ForegroundColorSpan(getColorId(Card.Resource.GOLD.toString()));
            appendSb(sb, Card.Resource.GOLD.toString().toLowerCase(), fcs);
            sb.append(": ");
            sb.append(String.valueOf(card.getSpecialGold(player)));
            sb.append("\n");
        }

        if (!card.makeThisFree().isEmpty()) {
            sb.append("Free if owned:\n");
            for (Card c : card.makeThisFree()) {
                sb.append(" ");
                sb.append(formatEnum(c.getEnum(), c.getType()));
                sb.append("\n");
            }
        }

        if (!card.makesFree().isEmpty()) {
            sb.append("Makes free:\n");
            for (Card c : card.makesFree()) {
                sb.append(" ");
                sb.append(formatEnum(c.getEnum(), c.getType()));
                sb.append("\n");
            }
        }

        return sb;
    }

    private static boolean isBaseResource(String name) {
        switch (name.toLowerCase()) {
            case "wood":
            case "stone":
            case "clay":
            case "ore":
            case "cloth":
            case "glass":
            case "paper":
                return true;
            default:
                return false;
        }
    }

    public static CharSequence formatEnum(Enum name, Card.Type type) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        ForegroundColorSpan fcs = new ForegroundColorSpan(getColorId(type.toString()));
        appendSb(sb, name.name().replace("_", " "), fcs);
        return sb;
    }

    public static boolean isTablet() {
        DisplayMetrics metrics = MainActivity.getMainActivity().getResources().getDisplayMetrics();
        return (metrics.widthPixels / metrics.density > 600);
    }

    public static void animateTranslate(final ViewGroup parent, final View current, final View next, boolean left) {
        //Animate the swap
        int duration = MainActivity.getMainActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);
        int viewWidth = parent.getWidth();

        //If there's a scrollbar, we want it invisible
        current.setVerticalScrollBarEnabled(false);

        TranslateAnimation ta;
        if (left) {
            ta = new TranslateAnimation(0, viewWidth * -1, 0, 0);
        } else {
            ta = new TranslateAnimation(0, viewWidth, 0, 0);
        }
        ta.setDuration(duration);
        new Handler().postDelayed(() -> parent.removeView(current), ta.getDuration());
        current.startAnimation(ta);

        next.setVisibility(View.INVISIBLE);
        next.setVerticalScrollBarEnabled(false);
        parent.addView(next);
        if (left) {
            ta = new TranslateAnimation(viewWidth, 0, 0, 0);
        } else {
            ta = new TranslateAnimation(viewWidth * -1, 0, 0, 0);
        }
        ta.setDuration(duration);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                next.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (next instanceof ScrollView) {
                    next.setVerticalScrollBarEnabled(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        next.startAnimation(ta);
    }

    public static void animateCrossfade(final ViewGroup parent, final View current, final View next) {
        int duration = MainActivity.getMainActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

        next.setAlpha(0f);
        parent.addView(next);
        next.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);

        current.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parent.removeView(current);
                    }
                });

    }

    public static void appendSb(SpannableStringBuilder sb, String text, CharacterStyle style) {
        sb.append(text);
        sb.setSpan(style, sb.length() - text.length(), sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    public static int getColorId(String id) {
        Context context = MainActivity.getMainActivity();
        return ContextCompat.getColor(context, context.getResources().getIdentifier(id, "color", context.getPackageName()));
    }
}
