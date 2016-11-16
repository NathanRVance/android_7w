package net.dumtoad.srednow7.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.bus.Bus;

public class LoadDialog extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.load_title));
        builder.setMessage(getResources().getString(R.string.load_message));
        builder.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i)
                -> Bus.bus.loadGame());
        builder.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i)
                -> Bus.bus.deleteSave());

        return builder.create();
    }

}
