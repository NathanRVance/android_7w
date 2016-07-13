package net.dumtoad.srednow7;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import net.dumtoad.srednow7.controller.MasterViewController;
import net.dumtoad.srednow7.dialog.WelcomeDialog;


public class MainActivity extends Activity {

    private static MainActivity mainActivity;
    private MasterViewController mvc;
    private GestureDetector gestureDetector;
    private LeftRightSwipe lrs;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static MasterViewController getMasterViewController() {
        return getMainActivity().mvc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        mvc = new MasterViewController(this);
        gestureDetector = new GestureDetector(this, new GestureListener());

        if (savedInstanceState != null)
            mvc.onRestoreInstanceState(savedInstanceState);
        else  {
            DialogFragment welcomeDialog = new WelcomeDialog();
            Bundle args = new Bundle();
            args.putBoolean("continue", mvc.hasAutosave());
            welcomeDialog.setArguments(args);
            welcomeDialog.show(getFragmentManager(), "welcomeDialog");
            mvc.setup(); //So there's something in the background if the user exits the dialog
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mvc.onSaveInstanceState(outState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    private void onSwipeRight() {
        if (lrs != null) {
            lrs.swipeRight();
        }
    }

    private void onSwipeLeft() {
        if (lrs != null) {
            lrs.swipeLeft();
        }
    }

    public void registerLeftRightSwipe(LeftRightSwipe lrs) {
        this.lrs = lrs;
    }

    public interface LeftRightSwipe {
        void swipeLeft();

        void swipeRight();
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } /*else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }*/

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }


}
