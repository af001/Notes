package technology.xor.notes.support;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MakeClean extends IntentService {

    public static volatile boolean shouldContinue = false;

    public MakeClean() {
        super("MakeClean");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RunStatus();
    }

    private void RunStatus() {

        long endTime = System.currentTimeMillis() + AppData.APPLICATION_TIMEOUT;

        if (shouldContinue) {
            //noinspection StatementWithEmptyBody
            while (System.currentTimeMillis() < endTime) { }

            if (shouldContinue) {
                // TODO: CHANGE TO DATABASE
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("KEYSTORE");
                editor.apply();
            }

        } else if (!shouldContinue) {
            stopSelf();
        }
    }
}
