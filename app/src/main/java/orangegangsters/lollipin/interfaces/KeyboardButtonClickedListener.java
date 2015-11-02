package orangegangsters.lollipin.interfaces;

import orangegangsters.lollipin.enums.KeyboardButtonEnum;

/**
 * Created by stoyan and oliviergoutay on 1/13/15.
 * The {@link orangegangsters.lollipin.managers.AppLockActivity} will implement
 * this in order to receive events from {@link orangegangsters.lollipin.views.KeyboardButtonView}
 * and {@link orangegangsters.lollipin.views.KeyboardView}
 */
public interface KeyboardButtonClickedListener {

    /**
     * Receive the click of a button, just after a {@link android.view.View.OnClickListener} has fired.
     * Called before {@link #onRippleAnimationEnd()}.
     * @param keyboardButtonEnum The organized enum of the clicked button
     */
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum);

    /**
     * Receive the end of a {@link com.andexert.library.RippleView} animation using a
     * {@link com.andexert.library.RippleView} to determine the end.
     * Called after {@link #onKeyboardClick(orangegangsters.lollipin.enums.KeyboardButtonEnum)}.
     */
    public void onRippleAnimationEnd();

}
