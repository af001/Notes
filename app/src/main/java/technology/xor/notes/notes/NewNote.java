package technology.xor.notes.notes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherNotes;
import technology.xor.notes.support.GPSTracker;

public class NewNote extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_note);

        Intent mIntent = getIntent();
        final boolean isSite = mIntent.getBooleanExtra("isSite", false);
        final String siteId = mIntent.getStringExtra("siteName");

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Add Note");
        // builder.setIcon(R.id.xxxx)

        final int maxLength = 200;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(75, 75, 75, 25);

        final BootstrapEditText message = new BootstrapEditText(this);
        message.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        message.setLines(4);
        message.setSingleLine(false);
        message.setGravity(Gravity.TOP);
        message.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        message.setFilters(fArray);
        message.setHint("Note...");
        layout.addView(message);

        TextView tv = new TextView(this);
        tv.setTextSize(12);
        layout.addView(tv);

        builder.setView(layout);
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!message.getText().toString().equals("")) {
                            CipherNotes cNotes;
                            if (isSite) {
                                cNotes = new CipherNotes(GetDateTime(), GetLocation(), siteId, message.getText().toString());
                            } else {
                                cNotes = new CipherNotes(GetDateTime(), GetLocation(), null, message.getText().toString());
                            }

                            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(getBaseContext());
                            dbHelper.AddNote(cNotes);
                            dbHelper.close();

                            Log.d("Add Note Dialog", "Successfully added note to the database.");
                            NewNote.this.finish();

                        } else {
                            Toast.makeText(getBaseContext(), "Empty Messages Not Allowed!", Toast.LENGTH_SHORT).show();
                            message.setText("");
                        }
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewNote.this.finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private String GetDateTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        return sdf.format(cal.getTime());
    }

    private String GetLocation() {
        GPSTracker gpsTracker = new GPSTracker(getBaseContext());
        gpsTracker.getLocation();

        return String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude());
    }
}
