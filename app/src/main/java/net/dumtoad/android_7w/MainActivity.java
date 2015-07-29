package net.dumtoad.android_7w;

import android.app.Activity;
import android.os.Bundle;

import net.dumtoad.android_7w.cards.Database;
import net.dumtoad.android_7w.controller.MasterViewController;


public class MainActivity extends Activity {

    private MasterViewController mvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mvc = new MasterViewController(this);

        if(savedInstanceState == null)
            initialInit();
    }

    private void initialInit() {
        mvc.setup();
    }

    public MasterViewController getMasterViewController() {
        return mvc;
    }


}
