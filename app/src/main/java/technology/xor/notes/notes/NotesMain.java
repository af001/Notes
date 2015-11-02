package technology.xor.notes.notes;

import java.util.Locale;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import net.sqlcipher.database.SQLiteDatabase;

import orangegangsters.lollipin.PinActivity;
import orangegangsters.lollipin.managers.AppLock;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherPrefs;
import technology.xor.notes.lockscreen.CustomLockPin;
import technology.xor.notes.sites.SiteView;
import technology.xor.notes.support.AppData;
import technology.xor.notes.support.AskUserDialog;
import technology.xor.notes.support.MakeClean;

public class NotesMain extends PinActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        InitializeSQLCipher();

        CheckFirstRun();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /* The {@link ViewPager} that will host the section contents.
        */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
    }

    private void CheckFirstRun() {

        if (!mSharedPreferences.contains(AppData.PASSWORD_PREFERENCE_KEY)) {
            Intent intent = new Intent(NotesMain.this, CustomLockPin.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, AppData.REQUEST_CODE_ENABLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!IsServiceRunning(MakeClean.class)) {
            Intent sIntent = new Intent(this, MakeClean.class);
            MakeClean.shouldContinue = true;
            startService(sIntent);
        }
    }

    private boolean IsServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
        CipherPrefs cPrefs = dbHelper.GetKey(AppData.ASK_PASSWORD);

        if (cPrefs.GetValue() != null) {
            if (cPrefs.GetValue().equals("1")) {

                MakeClean.shouldContinue = false;

                CipherPrefs cPrefsPin = dbHelper.GetKey(AppData.TEMP_PIN);
                if (cPrefsPin.GetValue() != null) {
                    cPrefs.SetValue("0");
                    cPrefs.SetId(cPrefs.GetId());
                    cPrefs.SetKey(AppData.ASK_PASSWORD);
                    dbHelper.UpdatePref(cPrefs);

                    String tmpPin = cPrefsPin.GetValue();
                    AskUserDialog uDialog = new AskUserDialog();
                    uDialog.AlertUser(tmpPin, this, false);
                    dbHelper.DeleteKey(cPrefsPin.GetId());
                    dbHelper.close();
                }
            }
        }

        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!mSharedPreferences.contains(AppData.PASSWORD_KEY)) {

            // REMOVE LOCK SCREEN PREFERENCES
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(AppData.PASSWORD_PREFERENCE_KEY);
            editor.remove(AppData.PASSWORD_SALT_PREFERENCE_KEY);
            editor.apply();

            // DROP THE DATABASE
            CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
            dbHelper.DeleteDatabase();
            dbHelper.close();

            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppData.REQUEST_CODE_ENABLE && resultCode == RESULT_OK) {
            AskUserDialog uDialog = new AskUserDialog();
            uDialog.AlertUser(data.getStringExtra("pin"), this, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return NoteView.newInstance();
                case 1:
                    return SiteView.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_notes).toUpperCase(l);
                case 1:
                    return getString(R.string.title_sites).toUpperCase(l);
            }
            return null;
        }
    }
}
