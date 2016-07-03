package net.dumtoad.android_7w.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.cards.Card;
import net.dumtoad.android_7w.controller.TurnController;

public class CardView extends Button {

    public CardView(Context context) {
        super(context);
    }

    public CardView(Card card, Context context, boolean buildable) {
        super(context);
        setText(card.getNameString());
        if (buildable) build(card);
        else view(card);
    }

    private void build(final Card card) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final TurnController tc = MainActivity.getMasterViewController().getTableController().getTurnController();
                new AlertDialog.Builder(getContext())
                        .setTitle(card.getNameString())
                        .setMessage(card.getSummary())
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        })
                        .setPositiveButton("Actions...", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Select action")
                                        .setItems(new CharSequence[]
                                                        {"Cancel", "Discard", "Build Wonder", "Build Card"},
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {
                                                            case 0:
                                                                //Do nothing
                                                                break;
                                                            case 1:
                                                                tc.requestDiscard(card);
                                                                break;
                                                            case 2:
                                                                tc.requestWonder(card);
                                                                break;
                                                            case 3:
                                                                tc.requestBuild(card);
                                                                break;
                                                        }
                                                    }
                                                })
                                        .create().show();
                            }
                        })
                        .create().show();
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
