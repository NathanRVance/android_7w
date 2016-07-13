package net.dumtoad.srednow7.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import net.dumtoad.srednow7.R;

public class WelcomeDialog extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean continu = getArguments().getBoolean("continue");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.welcome_title));
        builder.setMessage(getResources().getString(R.string.welcome_message));
        builder.setPositiveButton(getResources().getString(R.string.new_game), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mvc.deleteSave();
                mvc.setup();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.help), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing yet...
            }
        });
        if(continu) {
            builder.setNeutralButton(getResources().getString(R.string.continue_game), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(! mvc.restore()) { //In case there's errors or something...
                        mvc.setup();
                    }
                }
            });
        }

        return builder.create();
    }

}
