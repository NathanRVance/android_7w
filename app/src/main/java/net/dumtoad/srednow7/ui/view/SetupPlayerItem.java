package net.dumtoad.srednow7.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.dumtoad.srednow7.MainActivity;
import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.dialog.EditTextDialog;

public class SetupPlayerItem extends RelativeLayout implements EditTextDialog.EditTextDialogCallback {

    public static String EDIT_TEXT_DIALOG_TAG = "edittextdialog";
    private TextView textView;
    private String names[];
    private boolean ais[];
    private int index;

    public SetupPlayerItem(Context context) {
        super(context);
    }

    public SetupPlayerItem(Context context, String names[], boolean ais[], int index) {
        super(context);
        this.names = names;
        this.ais = ais;
        this.index = index;

        LayoutInflater inflater = MainActivity.getMainActivity().getLayoutInflater();
        inflater.inflate(R.layout.setup_player_item, this, true);

        init();
    }

    private void init() {
        textView = (TextView) findViewById(R.id.player_name);
        textView.setText(names[index]);
        textView.setOnClickListener(v -> {
            EditTextDialog df = new EditTextDialog();
            df.registerCallback(SetupPlayerItem.this);
            Bundle args = new Bundle();
            args.putString("name", names[index]);
            df.setArguments(args);
            df.show(MainActivity.getMainActivity().getFragmentManager(), EDIT_TEXT_DIALOG_TAG);
        });

        RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
        rg.removeAllViews();
        rg.setOrientation(LinearLayout.HORIZONTAL);
        final RadioButton human = new RadioButton(getContext());
        human.setOnClickListener(v -> ais[index] = !human.isChecked());
        human.setText(getContext().getString(R.string.human));
        human.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize));
        rg.addView(human);

        final RadioButton ai = new RadioButton(getContext());
        ai.setOnClickListener(v -> ais[index] = ai.isChecked());
        ai.setText(getContext().getString(R.string.ai));
        ai.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize));
        rg.addView(ai);

        if (ais[index]) {
            ai.setChecked(true);
        } else {
            human.setChecked(true);
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

    @Override
    public void onEditTextComplete(String text) {
        textView.setText(text);
        names[index] = text;
    }
}
