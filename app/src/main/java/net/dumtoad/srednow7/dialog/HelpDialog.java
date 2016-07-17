package net.dumtoad.srednow7.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import net.dumtoad.srednow7.R;
import net.dumtoad.srednow7.cards.Card;

public class HelpDialog extends AbstractDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message[] = getArguments().getString("message").split("\\*");
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for(String string : message) {
            String parts[] = string.split("-");
            if(parts.length == 1) {
                sb.append(parts[0]);
            } else {
                Card.appendSb(sb, parts[1], new ForegroundColorSpan(Card.getColorId(parts[0])));
            }
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(sb)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                }).create();
    }

}
