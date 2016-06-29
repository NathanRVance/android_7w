package net.dumtoad.android_7w.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.controller.MasterViewController;

public class AbstractDialog extends DialogFragment {
    protected MasterViewController mvc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mvc = MainActivity.getMasterViewController();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mvc = MainActivity.getMasterViewController();
    }

}
