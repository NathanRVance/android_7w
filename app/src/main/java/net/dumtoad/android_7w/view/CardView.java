package net.dumtoad.android_7w.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import net.dumtoad.android_7w.cards.Card;

public class CardView extends Button {

    public CardView(final Card card, final Context context) {
        super(context);
        setText(card.getNameString());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(card.getNameString())
                        .setMessage(card.getSummary())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        }).show();
            }
        });
    }
}
