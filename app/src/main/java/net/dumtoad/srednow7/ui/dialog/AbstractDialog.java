package net.dumtoad.srednow7.ui.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import net.dumtoad.srednow7.R;

public class AbstractDialog extends DialogFragment {

    EditText prepareEditTextForOnelineStrings(final EditText et) {
        et.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        et.setSingleLine();
        et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    enter(et.getText().toString());
                    return true; // consume.
                }
            }
            return false;
        });

        return makeEditTextClearable(et, getActivity());
    }

    void enter(String text) {
        //do nothing by default
    }

    public EditText makeEditTextClearable(final EditText et, Activity activity) {
        final Drawable X = ContextCompat.getDrawable(activity, R.drawable.ic_clear).mutate();
        X.setColorFilter(ContextCompat.getColor(getActivity(), R.color.gray), PorterDuff.Mode.SRC_IN);
        X.setBounds(0, 0, X.getIntrinsicWidth(), X.getIntrinsicHeight());
        et.setCompoundDrawables(et.getCompoundDrawables()[0], et.getCompoundDrawables()[1], X, et.getCompoundDrawables()[3]);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                determineClearButton(et);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et.setOnTouchListener((v, event) -> {
            if (et.getCompoundDrawables()[2] != null) {
                if (event.getX() > (et.getWidth() - et.getPaddingRight() - X.getIntrinsicWidth())) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        et.setText("");
                    }
                    return true;
                }
            }
            return false;
        });
        determineClearButton(et);
        return et;
    }

    private void determineClearButton(EditText et) {
        if (et.getText().toString().equals(""))
            et.getCompoundDrawables()[2].setAlpha(0); //make clear button transparent
        else
            et.getCompoundDrawables()[2].setAlpha(255); //make clear button visible
    }

}
