package technology.xor.notes.support;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.nio.charset.StandardCharsets;

import orangegangsters.lollipin.managers.LockManager;
import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.lockscreen.CustomLockPin;
import technology.xor.notes.notes.R;

public class AskUserDialog {

    private SharedPreferences mSharedPreferences;
    private LockManager mLockManager;

    public void AlertUser(final String data, final Context context, final boolean isFirstRun) {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mLockManager = new LockManager();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle("Encryption Passcode");
        // builder.setIcon(R.id.xxxx)

        final int maxLength = 20;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);

        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(75, 75, 75, 25);

        final BootstrapEditText password = new BootstrapEditText(context);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setFilters(fArray);
        password.setHint("Password");
        layout.addView(password);

        TextView tv = new TextView(context);
        tv.setTextSize(12);
        layout.addView(tv);

        final BootstrapEditText password2 = new BootstrapEditText(context);
        password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password2.setFilters(fArray);
        password2.setHint("Re-Enter Password");
        layout.addView(password2);

        if (!isFirstRun) {
            password2.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
        }

        builder.setView(layout);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!password.getText().toString().equals("")) {

                            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(context);
                            String code = data + password.getText().toString();
                            byte[] text = code.getBytes(StandardCharsets.UTF_8);

                            if (isFirstRun) {
                                if (password.getText().toString().equals(password2.getText().toString())) {

                                    mLockManager.getAppLock().setEncryptionPasscode(Base64.encodeToString(text, Base64.DEFAULT));
                                    System.out.println("New: " + Base64.encodeToString(text, Base64.DEFAULT));

                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Password Mismatch!", Toast.LENGTH_SHORT).show();
                                    password.setText("");
                                    password2.setText("");
                                    AlertUser(data, context, true);
                                }
                            } else {
                                boolean isValid = mLockManager.getAppLock().checkEncryptionPasscode(Base64.encodeToString(text, Base64.DEFAULT));
                                System.out.println("Resume: " + Base64.encodeToString(text, Base64.DEFAULT));

                                if (isValid) {
                                    dialog.dismiss();
                                } else {
                                    password.setText("");
                                    AlertUser(data, context, false);
                                    Toast.makeText(context, "Invalid Decryption Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            AlertUser(data, context, isFirstRun);
                            Toast.makeText(context, "Empty Passwords Not Allowed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (isFirstRun) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.remove(AppData.PASSWORD_PREFERENCE_KEY);
                            editor.remove(AppData.PASSWORD_SALT_PREFERENCE_KEY);
                            editor.remove(AppData.PASSWORD_SALT_KEY);
                            editor.remove(AppData.PASSWORD_KEY);
                            editor.apply();

                            // DROP THE DATABASE
                            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(context);
                            dbHelper.DeleteDatabase();
                            dbHelper.close();

                            ((Activity) context).finish();
                        } else {
                            ((Activity) context).finish();
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
}
