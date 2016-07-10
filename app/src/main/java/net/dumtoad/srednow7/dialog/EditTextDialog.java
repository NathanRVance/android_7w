package net.dumtoad.srednow7.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;

public class EditTextDialog extends AbstractDialog {
    EditTextDialogCallback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mvc = MainActivity.getMasterViewController();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Player Name");
        builder.setMessage("Enter player name:");
        final EditText input = new EditText(getActivity());
        prepareEditTextForOnelineStrings(input);

        String currentName = getArguments().getString("name");
        input.setText(currentName);

        builder.setView(input);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enter(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });

        return builder.create();
    }

    @Override
    protected void enter(final String text) {
        if(callback != null) {
            callback.onEditTextComplete(text);
        }
        dismiss();
    }

    public void registerCallback(EditTextDialogCallback callback) {
        this.callback = callback;
    }

    public interface EditTextDialogCallback {
        void onEditTextComplete(String text);
    }

}
