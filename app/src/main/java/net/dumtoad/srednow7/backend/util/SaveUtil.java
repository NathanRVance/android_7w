package net.dumtoad.srednow7.backend.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import net.dumtoad.srednow7.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SaveUtil {

    private static final String HAS_SAVE = "HAS_SAVE";

    public static void saveGame(Serializable gameState) {
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
        editor.clear();
        editor.putString("Save", encoded);
        editor.putBoolean(HAS_SAVE, true);
        editor.commit();
    }

    public static void deleteSave() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.putBoolean(HAS_SAVE, false);
        editor.commit();
    }

    public static boolean hasSave() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean(HAS_SAVE, false);
    }

    public static Serializable loadGame() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String s = sharedPreferences.getString("Save", null);
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
