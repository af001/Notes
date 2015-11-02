package technology.xor.notes.sites;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherSites;
import technology.xor.notes.notes.R;
import technology.xor.notes.support.GPSTracker;

public class NewSite extends Activity {

    private final String TAG = "NewSite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_site);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Add Site");
        // builder.setIcon(R.id.xxxx)

        final int maxLength = 16;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(75, 75, 75, 25);

        final BootstrapEditText message = new BootstrapEditText(this);
        message.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        message.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-"));
        message.setSingleLine(true);
        message.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        message.setFilters(fArray);
        message.setHint("Site Identifier...");
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

                            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(getBaseContext());
                            CipherSites cSites = new CipherSites(message.getText().toString(), GetDateTime(), GetLocation());
                            dbHelper.AddSite(cSites);
                            dbHelper.close();

                            Log.d(TAG, "Successfully added site to the database.");

                            NewSite.this.finish();

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
                        NewSite.this.finish();
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
        GPSTracker gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        return String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude());
    }
}
