package net.dumtoad.srednow7.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import net.dumtoad.srednow7.R;

public class EditTextDialog extends AbstractDialog {
    private EditTextDialogCallback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Player Name");
        builder.setMessage("Enter player name:");
        final EditText input = new EditText(getActivity());
        prepareEditTextForOnelineStrings(input);

        String currentName = getArguments().getString("name");
        input.setText(currentName);

        builder.setView(input);
        builder.setPositiveButton(R.string.ok, (dialog, id) -> enter(input.getText().toString()));
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            //nothing
        });

        return builder.create();
    }

    @Override
    protected void enter(final String text) {
        if (callback != null) {
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
