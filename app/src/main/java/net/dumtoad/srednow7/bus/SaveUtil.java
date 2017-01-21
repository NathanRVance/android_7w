package net.dumtoad.srednow7.bus;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.backend.implementation.Generate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SaveUtil {

    private static final String HAS_SAVE = "HAS_SAVE";
    private static final String SAVE = "SAVE";
    private static final String EXPANSIONS = "EXPANSIONS";
    private static final String PLAYER_NAMES = "PLAYER_NAMES";
    private static final String DELIMITER = "\0";
    private static final String ISAI = "ISAI";

    public static void saveExtensions(Set<Generate.Expansion> expansions) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        Set<String> strings = new HashSet<>();
        for(Generate.Expansion expansion : expansions) {
            strings.add(expansion.name());
        }
        editor.putStringSet(EXPANSIONS, strings);
        editor.apply();
    }

    public static Set<Generate.Expansion> getExpansions() {
        Set<String> strings = getSharedPreferences().getStringSet(EXPANSIONS, new HashSet<>());
        Set<Generate.Expansion> ret = new HashSet<>();
        for(String string : strings) {
            try {
                ret.add(Generate.Expansion.valueOf(string));
            } catch (Exception e) {
                //If I change the list of expansions, I'd rather it not crash.
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static void savePlayerNames(String[] names) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        StringBuilder sb = new StringBuilder(names[0]);
        for(int i = 1; i < names.length; i++) {
            sb.append(DELIMITER);
            sb.append(names[i]);
        }
        editor.putString(PLAYER_NAMES, sb.toString());
        editor.apply();
    }

    public static String[] getPlayerNames() {
        return getSharedPreferences().getString(PLAYER_NAMES, "").split(DELIMITER);
    }

    public static void saveIsAI(boolean[] isAI) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        StringBuilder sb = new StringBuilder(String.valueOf(isAI[0]));
        for(int i = 1; i < isAI.length; i++) {
            sb.append(DELIMITER);
            sb.append(String.valueOf(isAI[i]));
        }
        editor.putString(ISAI, sb.toString());
        editor.apply();
    }

    public static boolean[] getIsAI() {
        String[] strings = getSharedPreferences().getString(ISAI, "").split(DELIMITER);
        boolean[] isAI = new boolean[strings.length];
        for(int i = 0; i < isAI.length; i++) {
            isAI[i] = Boolean.valueOf(strings[i]);
        }
        return isAI;
    }

    static void saveGame(Serializable gameState) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(gameState);
            objectOutputStream.close();
            encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(SAVE, encoded);
        editor.putBoolean(HAS_SAVE, true);
        editor.apply();
    }

    static void deleteSave() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(HAS_SAVE, false);
        editor.apply();
    }

    static boolean hasSave() {
        return getSharedPreferences().getBoolean(HAS_SAVE, false);
    }

    static Serializable loadGame() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String s = sharedPreferences.getString(SAVE, null);
        byte[] bytes = Base64.decode(s, 0);
        Serializable object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (Serializable) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
        return object;
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.getMainActivity());
    }

}
