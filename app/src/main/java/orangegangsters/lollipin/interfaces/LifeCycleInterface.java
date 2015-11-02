package orangegangsters.lollipin.interfaces;

import android.app.Activity;

/**
 * Created by stoyan on 1/12/15.
 * Allows to follow the LifeCycle of the {@link orangegangsters.lollipin.PinActivity}
 * Implemented by {@link orangegangsters.lollipin.managers.AppLockImpl} in order to
 * determine when the app was launched for the last time and when to launch the
 * {@link orangegangsters.lollipin.managers.AppLockActivity}
 */
public interface LifeCycleInterface {

    /**
     * Called in {@link android.app.Activity#onResume()}
     */
    public void onActivityResumed(Activity activity);

    /**
     * Called in {@link android.app.Activity#onPause()}
     */
    public void onActivityPaused(Activity activity);
}
