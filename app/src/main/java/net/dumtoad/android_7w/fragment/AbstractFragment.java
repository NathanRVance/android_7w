package net.dumtoad.android_7w.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import net.dumtoad.android_7w.MainActivity;
import net.dumtoad.android_7w.controller.MasterViewController;

public class AbstractFragment extends Fragment {

    protected MasterViewController mvc;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mvc = ((MainActivity) activity).getMasterViewController();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mvc = ((MainActivity) getActivity()).getMasterViewController();
    }
}
