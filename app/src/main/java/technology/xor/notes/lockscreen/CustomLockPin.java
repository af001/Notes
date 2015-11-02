package technology.xor.notes.lockscreen;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import orangegangsters.lollipin.managers.AppLockActivity;
import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.notes.R;
import technology.xor.notes.support.AppData;

public class CustomLockPin extends AppLockActivity {

    @Override
    public void showForgotDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Pincode Reset");
        builder.setMessage("Press 'OK' to reset your pincode. Once pressed, you will be allowed to create a new pin.");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteData();
                    }
                });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onPinFailure(int ix) {
        if (ix == AppData.DROP_DB) {
            DeleteData();
        }
    }

    @Override
    public void onPinSuccess(int ix) { }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }

    protected void DeleteData() {
        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
        dbHelper.DeleteDatabase();
        dbHelper.close();

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(AppData.PASSWORD_KEY);
        editor.remove(AppData.PASSWORD_SALT_KEY);
        editor.remove(AppData.PASSWORD_PREFERENCE_KEY);
        editor.remove(AppData.PASSWORD_SALT_PREFERENCE_KEY);
        editor.apply();

        finish();
    }
}
