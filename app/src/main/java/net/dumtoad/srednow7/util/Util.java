package net.dumtoad.srednow7.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
import android.widget.Toast;

import net.dumtoad.srednow7.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class Util {

    private static final String SEP = "&"; //Seperator
    private static final String STRING = "string";
    private static final String STRINGARRY = "stringarry";
    private static final String INT = "int";
    private static final String INTARRY = "intarry";
    private static final String BOOLEAN = "bool";
    private static final String BOOLEANARRY = "boolarry";

    /**
     * Names within sharedprefs will be bundle1&bundle2&...&bundleN&varname&vartype
     */
    public static void saveBundle(Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getMainActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        saveRecurse(editor, bundle, "");
        editor.commit();
    }

    private static void saveRecurse(SharedPreferences.Editor editor, Bundle bundle, String name) {
        for (String key : bundle.keySet()) {
            Object o = bundle.get(key);
            if(o instanceof String) {
                editor.putString(name + key + SEP + STRING, (String) o);
            } else if(o instanceof String[]) {
                StringBuilder sb = new StringBuilder();
                for(String st : (String[]) o) {
                    sb.append(st).append(SEP);
                }
                editor.putString(name + key + SEP + STRINGARRY, sb.toString());
            } else if(o instanceof Integer) {
                editor.putInt(name + key + SEP + INT, (Integer) o);
            } else if(o instanceof int[]) {
                StringBuilder sb = new StringBuilder();
                for(int i : (int[]) o) {
                    sb.append(i).append(SEP);
                }
                editor.putString(name + key + SEP + INTARRY, sb.toString());
            } else if(o instanceof Boolean) {
                editor.putBoolean(name + key + SEP + BOOLEAN, (Boolean) o);
            } else if(o instanceof boolean[]) {
                StringBuilder sb = new StringBuilder();
                for(boolean b : (boolean[]) o) {
                    sb.append(b).append(SEP);
                }
                editor.putString(name + key + SEP + BOOLEANARRY, sb.toString());
            } else if(o instanceof Bundle) {
                saveRecurse(editor, (Bundle) o, name + key + SEP);
            } else {
                throw new RuntimeException("Something went wrong with key " + key);
            }
        }
    }

    public static Bundle retrieveBundle() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getMainActivity());
        Map<String, ?> all = preferences.getAll();
        return retrieveRecurse(all);
    }

    private static Bundle retrieveRecurse(Map<String, ?> data) {
        Bundle bundle = new Bundle();
        Map<String, Map<String, Object>> submaps = new HashMap<>();
        for(String name : data.keySet()) {
            String[] parts = name.split(SEP);
            if(parts.length == 2) {
                switch(parts[1]) {
                    case STRING:
                        bundle.putString(parts[0], (String) data.get(name));
                        break;
                    case STRINGARRY:
                        bundle.putStringArray(parts[0], ((String) data.get(name)).split(SEP));
                        break;
                    case INT:
                        bundle.putInt(parts[0], (Integer) data.get(name));
                        break;
                    case INTARRY:
                        String strarry[] = ((String) data.get(name)).split(SEP);
                        int intarry[] = new int[strarry.length];
                        for(int i = 0; i < strarry.length; i++) {
                            intarry[i] = Integer.parseInt(strarry[i]);
                        }
                        bundle.putIntArray(parts[0], intarry);
                        break;
                    case BOOLEAN:
                        bundle.putBoolean(parts[0], (Boolean) data.get(name));
                        break;
                    case BOOLEANARRY:
                        strarry = ((String) data.get(name)).split(SEP);
                        boolean boolarry[] = new boolean[strarry.length];
                        for(int i = 0; i < strarry.length; i++) {
                            boolarry[i] = Boolean.parseBoolean(strarry[i]);
                        }
                        bundle.putBooleanArray(parts[0], boolarry);
                        break;
                    default:
                        throw new RuntimeException("Something went wrong retrieving " + name);
                }
            } else {
                if(! submaps.keySet().contains(parts[0])) {
                    submaps.put(parts[0], new HashMap<String, Object>());
                }
                String subname = name.substring(parts[0].length()+1, name.length());
                submaps.get(parts[0]).put(subname, data.get(name));
            }
        }
        for(String name : submaps.keySet()) {
            String[] parts = name.split(SEP);
            bundle.putBundle(parts[0], retrieveRecurse(submaps.get(name)));
        }
        return bundle;
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
        if(left) {
            ta = new TranslateAnimation(0, viewWidth * -1, 0, 0);
        } else {
            ta = new TranslateAnimation(0, viewWidth, 0, 0);
        }
        ta.setDuration(duration);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                parent.removeView(current);
            }
        }, ta.getDuration());
        current.startAnimation(ta);

        next.setVisibility(View.INVISIBLE);
        next.setVerticalScrollBarEnabled(false);
        parent.addView(next);
        if(left) {
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
                if(next instanceof ScrollView) {
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

}
