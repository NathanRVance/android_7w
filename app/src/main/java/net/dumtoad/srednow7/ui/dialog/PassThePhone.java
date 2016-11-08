package net.dumtoad.srednow7.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

public class PassThePhone extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String name = getArguments().getString("name");
        return new AlertDialog.Builder(getActivity())
                .setTitle("Pass to player " + name)
                .setPositiveButton("Ready!", (dialog, which) -> {
                    //Do nothing
                }).create();

    }

}
