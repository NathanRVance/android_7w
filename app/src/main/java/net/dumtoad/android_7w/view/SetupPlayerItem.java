package net.dumtoad.android_7w.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nathav63 on 7/28/15.
 */
public class SetupPlayerItem extends LinearLayout {

    TextView textView;
    CheckBox checkBox;
    String names[];
    boolean ais[];
    int index;

    public SetupPlayerItem(Context context) {
        super(context);
    }

    public SetupPlayerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SetupPlayerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SetupPlayerItem(Context context, String names[], boolean ais[], int index) {
        super(context);
        this.names = names;
        this.ais = ais;
        this.index = index;
        init();
    }

    private void init() {
        textView = new TextView(getContext());
        textView.setText(names[index]);
        checkBox = new CheckBox(getContext());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ais[index] = ((CheckBox) v).isChecked();
            }
        });
        checkBox.setChecked(ais[index]);
        addView(textView);
        addView(checkBox);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

}
