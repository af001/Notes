package orangegangsters.lollipin.managers;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import orangegangsters.lollipin.PinActivity;
import orangegangsters.lollipin.PinFragmentActivity;
import orangegangsters.lollipin.interfaces.LifeCycleInterface;
import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherPrefs;
import technology.xor.notes.support.AskUserDialog;
import technology.xor.notes.support.Crypto;
import technology.xor.notes.support.MakeClean;

public class AppLockImpl<T extends AppLockActivity> extends AppLock implements LifeCycleInterface {

    public static final String TAG = "AppLockImpl";

    /**
     * The {@link android.content.SharedPreferences} key used to store the password
     */
    private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";
    /**
     * The {@link android.content.SharedPreferences} key used to store the last active time
     */
    private static final String LAST_ACTIVE_MILLIS_PREFERENCE_KEY = "LAST_ACTIVE_MILLIS";
    /**
     * The {@link android.content.SharedPreferences} key used to store the timeout
     */
    private static final String TIMEOUT_MILLIS_PREFERENCE_KEY = "TIMEOUT_MILLIS_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the logo resource id
     */
    private static final String LOGO_ID_PREFERENCE_KEY = "LOGO_ID_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the forgot option
     */
    private static final String SHOW_FORGOT_PREFERENCE_KEY = "SHOW_FORGOT_PREFERENCE_KEY";
    /**
     * The {@link SharedPreferences} key used to store whether the user has backed out of the {@link AppLockActivity}
     */
    private static final String PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY = "PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY";
    /**
     * The {@link android.content.SharedPreferences} key used to store the dynamically generated password salt
     */
    private static final String PASSWORD_SALT_PREFERENCE_KEY = "PASSWORD_SALT_PREFERENCE_KEY";

    private static final String PASSWORD_KEY = "PASSKEY";

    private static final String PASSWORD_SALT_KEY = "PASSWORD_SALT_KEY";

    private static final String ASK_PASSWORD = "ASK_PASSWORD";

    /**
     * The {@link android.content.SharedPreferences} used to store the password, the last active time etc...
     */
    private SharedPreferences mSharedPreferences;
    private Context context;

    /**
     * The activity class that extends {@link orangegangsters.lollipin.managers.AppLockActivity}
     */
    private Class<T> mActivityClass;

    public AppLockImpl(Context context, Class<T> activityClass) {
        super();
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mActivityClass = activityClass;
        this.context = context;
    }

    @Override
    public void setTimeout(long timeout) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(TIMEOUT_MILLIS_PREFERENCE_KEY, timeout);
        editor.apply();
    }

    private void setSalt() {
        try {
            String salt = Crypto.saltString(Crypto.generateSalt());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PASSWORD_SALT_PREFERENCE_KEY, salt);
            editor.apply();
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    private String getSalt()  {
        return mSharedPreferences.getString(PASSWORD_SALT_PREFERENCE_KEY, null);
    }

    @Override
    public long getTimeout() {
        return mSharedPreferences.getLong(TIMEOUT_MILLIS_PREFERENCE_KEY, DEFAULT_TIMEOUT);
    }

    @Override
    public void setLogoId(int logoId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(LOGO_ID_PREFERENCE_KEY, logoId);
        editor.apply();
    }

    @Override
    public int getLogoId() {
        return mSharedPreferences.getInt(LOGO_ID_PREFERENCE_KEY, LOGO_ID_NONE);
    }

    @Override
    public void setShouldShowForgot(boolean showForgot) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(SHOW_FORGOT_PREFERENCE_KEY, showForgot);
        editor.apply();
    }

    @Override
    public boolean pinChallengeCancelled() {
        return mSharedPreferences.getBoolean(PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY, false);
    }

    @Override
    public void setPinChallengeCancelled(boolean backedOut) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PIN_CHALLENGE_CANCELLED_PREFERENCE_KEY, backedOut);
        editor.apply();
    }

    @Override
    public boolean shouldShowForgot() {
        // TODO: Set to false and change after first boot
        return mSharedPreferences.getBoolean(SHOW_FORGOT_PREFERENCE_KEY, true);
    }

    @Override
    public void enable() {
        PinActivity.setListener(this);
        PinFragmentActivity.setListener(this);
    }

    @Override
    public void disable() {
        PinActivity.clearListeners();
        PinFragmentActivity.clearListeners();
    }

    @Override
    public void disableAndRemoveConfiguration() {
        PinActivity.clearListeners();
        PinFragmentActivity.clearListeners();
        mSharedPreferences.edit().remove(PASSWORD_PREFERENCE_KEY)
                .remove(LAST_ACTIVE_MILLIS_PREFERENCE_KEY)
                .remove(TIMEOUT_MILLIS_PREFERENCE_KEY)
                .remove(LOGO_ID_PREFERENCE_KEY)
                .remove(SHOW_FORGOT_PREFERENCE_KEY)
                .apply();
    }

    @Override
    public long getLastActiveMillis() {
        return mSharedPreferences.getLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, 0);
    }

    @Override
    public void setLastActiveMillis() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, System.currentTimeMillis());
        editor.apply();
    }

    @Override
    public boolean checkPasscode(String passcode) {

        boolean isSame = false;

        String salt = getSalt();

        try {

            if (mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
                String storedPasscode = mSharedPreferences.getString(PASSWORD_PREFERENCE_KEY, "");

                Crypto.SecretKeys keys = Crypto.generateKeyFromPassword(passcode, salt);
                Crypto.CipherTextIvMac cipherTextIvMac = Crypto.getCipherTextIvMac(storedPasscode);
                String decrypted = Crypto.decryptString(cipherTextIvMac, keys);

                if (passcode.equalsIgnoreCase(decrypted)) {
                    isSame = true;
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return isSame;
    }

    @Override
    public boolean setPasscode(String passcode) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        setSalt();
        String salt = getSalt();

        if (passcode == null) {
            editor.remove(PASSWORD_PREFERENCE_KEY);
            editor.apply();
            this.disable();
        } else {
            try {
                Crypto.SecretKeys keys = Crypto.generateKeyFromPassword(passcode, salt);
                Crypto.CipherTextIvMac cipherTextIvMac = Crypto.encrypt(passcode, keys);

                editor.putString(PASSWORD_PREFERENCE_KEY, cipherTextIvMac.toString());
                editor.apply();
                this.enable();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean isPasscodeSet() {
        if (mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isIgnoredActivity(Activity activity) {
        String clazzName = activity.getClass().getName();

        // ignored activities
        if (mIgnoredActivities.contains(clazzName)) {
            Log.d(TAG, "ignore activity " + clazzName);
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldLockScreen(Activity activity) {
        Log.d(TAG, "Lockscreen called");

        // previously backed out of pin screen
        if (pinChallengeCancelled()) {
            return true;
        }

        // already unlock
        if (activity instanceof AppLockActivity) {
            AppLockActivity ala = (AppLockActivity) activity;
            if (ala.getType() == AppLock.UNLOCK_PIN) {
                Log.d(TAG, "already unlock activity");
                return false;
            }
        }

        // no pass code set
        if (!isPasscodeSet()) {
            Log.d(TAG, "lock passcode not set.");
            return false;
        }

        // no enough timeout
        long lastActiveMillis = getLastActiveMillis();
        long passedTime = System.currentTimeMillis() - lastActiveMillis;
        long timeout = getTimeout();
        if (lastActiveMillis > 0 && passedTime <= timeout) {
            Log.d(TAG, "no enough timeout " + passedTime + " for "
                    + timeout);
            return false;
        }

        return true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        setLastActiveMillis();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (isIgnoredActivity(activity)) {
            return;
        }

        MakeClean.shouldContinue = false;

        if (shouldLockScreen(activity)) {
            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(context);
            CipherPrefs cPrefs = new CipherPrefs(ASK_PASSWORD, "1");
            dbHelper.AddKey(cPrefs);
            dbHelper.close();

            Intent intent = new Intent(activity.getApplicationContext(), mActivityClass);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplication().startActivity(intent);
        }

        setLastActiveMillis();
    }

    private void setEncryptionSalt() {
        try {
            String salt = Crypto.saltString(Crypto.generateSalt());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PASSWORD_SALT_KEY, salt);
            editor.apply();
        } catch (GeneralSecurityException e) {
            Log.e("Main", "Error: " + e);
        }
    }

    private String getEncryptionSalt()  {
        return mSharedPreferences.getString(PASSWORD_SALT_KEY, null);
    }

    public boolean checkEncryptionPasscode(String passcode) {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(context);
        String salt;
        boolean isSame = false;

        salt = getEncryptionSalt();

        try {
            CipherPrefs cipherPrefs = dbHelper.GetKey(PASSWORD_KEY);

            if (cipherPrefs.GetKey() != null) {
                Crypto.SecretKeys keys = Crypto.generateKeyFromPassword(passcode, salt);
                Crypto.CipherTextIvMac cipherTextIvMac = Crypto.getCipherTextIvMac(cipherPrefs.GetValue());
                String decrypted = Crypto.decryptString(cipherTextIvMac, keys);

                if (passcode.equals(decrypted)) {
                    isSame = true;
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return isSame;
    }

    public boolean setEncryptionPasscode(String passcode) {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(context);
        setEncryptionSalt();
        String salt = getEncryptionSalt();
        boolean isSet = false;

        if (passcode == null) {
            CipherPrefs cipherPrefs = dbHelper.GetKey(PASSWORD_KEY);
            if (cipherPrefs.GetKey() != null) {
                dbHelper.DeleteKey(cipherPrefs.GetId());
            }
        } else {
            try {
                Crypto.SecretKeys keys = Crypto.generateKeyFromPassword(passcode, salt);
                Crypto.CipherTextIvMac cipherTextIvMac = Crypto.encrypt(passcode, keys);

                CipherPrefs cPrefs = new CipherPrefs(PASSWORD_KEY, cipherTextIvMac.toString());
                dbHelper.AddKey(cPrefs);
                dbHelper.close();
                isSet = true;

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return isSet;
    }
}