package net.dumtoad.android_7w;

import android.app.Activity;
import android.os.Bundle;

import net.dumtoad.android_7w.controller.MasterViewController;


public class MainActivity extends Activity {

    private MasterViewController mvc;
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        mvc = new MasterViewController(this);

        if(savedInstanceState == null)
            mvc.setup();
        else
            mvc.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mvc.onSaveInstanceState(outState);
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public MasterViewController getMasterViewController() {
        return mvc;
    }


}
