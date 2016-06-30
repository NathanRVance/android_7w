package net.dumtoad.android_7w.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SetupPlayerItem extends LinearLayout {

    TextView textView;
    RadioButton buttA;
    RadioButton buttB;
    String names[];
    boolean ais[];
    int index;

    public SetupPlayerItem(Context context) {
        super(context);
    }

    public SetupPlayerItem(Context context, String names[], boolean ais[], int index) {
        super(context);
        this.names = names;
        this.ais = ais;
        this.index = index;
        setGravity(Gravity.CENTER_VERTICAL);
        init();
    }

    private void init() {
        textView = new TextView(getContext());
        textView.setText(names[index]);
        RadioGroup radioGroup = new RadioGroup(getContext());

        radioGroup.setOrientation(HORIZONTAL);

        buttA = new RadioButton(getContext());
        buttA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ais[index] = ! buttA.isChecked();
            }
        });
        buttA.setText("human");
        radioGroup.addView(buttA);

        buttB = new RadioButton(getContext());
        buttB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ais[index] = buttB.isChecked();
            }
        });
        buttB.setText("ai");
        radioGroup.addView(buttB);

        addView(textView);
        addView(radioGroup);

        if(ais[index]) {
            buttB.setChecked(true);
        } else {
            buttA.setChecked(true);
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

}
