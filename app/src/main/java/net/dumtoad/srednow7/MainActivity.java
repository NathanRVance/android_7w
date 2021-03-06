package net.dumtoad.srednow7;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

import net.dumtoad.srednow7.bus.Bus;
import net.dumtoad.srednow7.ui.LeftRightSwipe;
import net.dumtoad.srednow7.ui.dialog.LoadDialog;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity {

    private static MainActivity mainActivity;
    private GestureDetector gestureDetector;
    private LeftRightSwipe lrs;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mainActivity = this;
        setContentView(R.layout.activity_main);
        gestureDetector = new GestureDetector(this, new GestureListener());

        if (savedInstanceState != null) {
            try {
                Bus.bus.loadGame(savedInstanceState.getSerializable("game"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (Bus.bus.hasSave()) {
                DialogFragment loadDialog = new LoadDialog();
                loadDialog.show(getFragmentManager(), "loadDialog");
            }
            Bus.bus.startNewGame(); //So that there's something meaningful in the background if the user exits the dialog
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("game", Bus.bus.saveGame());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    public void registerLeftRightSwipe(LeftRightSwipe lrs) {
        this.lrs = lrs;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(lrs == null) return false;
            boolean result = false;
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        lrs.swipeRight();
                    } else {
                        lrs.swipeLeft();
                    }
                    result = true;
                }
            }
            return result;
        }
    }

}
