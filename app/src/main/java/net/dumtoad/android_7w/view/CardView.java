package net.dumtoad.android_7w.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import net.dumtoad.android_7w.cards.Card;

public class CardView extends Button {

    public CardView(Context context) {
        super(context);
    }

    public CardView(Card card, Context context, boolean buildable) {
        super(context);
        setText(card.getNameString());
        if(buildable) build(card);
        else view(card);
    }

    private void build(final Card card) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(card.getNameString())
                        .setMessage(card.getSummary())
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        })
                        .setPositiveButton("Build", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }

    private void view(final Card card) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
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
