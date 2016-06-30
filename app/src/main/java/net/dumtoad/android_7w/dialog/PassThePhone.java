package net.dumtoad.android_7w.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class PassThePhone extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String name = getArguments().getString("name");
        return new AlertDialog.Builder(getActivity())
                .setTitle("Pass to player " + name)
                .setPositiveButton("Ready!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                }).create();

    }

}
