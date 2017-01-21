package net.dumtoad.srednow7.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.backend.implementation.Generate;
import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.bus.DisplayFactory;

import static net.dumtoad.srednow7.bus.DisplayFactory.CARD_NAME;
import static net.dumtoad.srednow7.bus.DisplayFactory.MESSAGE;
import static net.dumtoad.srednow7.bus.DisplayFactory.TITLE;
import static net.dumtoad.srednow7.bus.DisplayFactory.VALID_RESPONSES;

public class GetResponseDialog extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(TITLE));
        builder.setMessage(getArguments().getString(MESSAGE));

        String[] validResponses = getArguments().getStringArray(VALID_RESPONSES);
        NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(0);
        picker.setMaxValue(validResponses.length - 1);
        picker.setDisplayedValues(validResponses);
        picker.setValue(validResponses.length);
        builder.setView(picker);

        builder.setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
            Generate.getAllCards().get(getArguments().getString(CARD_NAME)).getCallback()
                    .callback(getArguments().getInt(DisplayFactory.PLAYER_ID), validResponses[picker.getValue()]);
            Bus.bus.getUI().invalidateView((Enum) getArguments().getSerializable(DisplayFactory.QUEUE_ID));
        });

        return builder.create();
    }

}
