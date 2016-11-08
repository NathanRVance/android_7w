package net.dumtoad.srednow7.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.ui.UIUtil;

public class HelpDialog extends AbstractDialog {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        String message[] = getArguments().getString(MESSAGE, "").split("\\*");
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (String string : message) {
            String parts[] = string.split("-");
            if (parts.length == 1) {
                sb.append(parts[0]);
            } else {
                UIUtil.appendSb(sb, parts[1], new ForegroundColorSpan(UIUtil.getColorId(parts[0])));
            }
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(sb)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    //Do nothing
                }).create();
    }

}
