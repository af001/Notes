package technology.xor.notes.database;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class CipherDatabaseHelper extends SQLiteOpenHelper {

    // DATABASE INFORMATION
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes.db";

    // TABLE FOR NOTES
    private static final String TABLE_NAME = "notes";

    // TABLE FOR PREFERENCES STORAGE
    private static final String TABLE_MISC = "misc";

    // TABLE FOR SITE STORAGE
    private static final String TABLE_SITE = "sites";

    // TABLE FOR PHOTOS
    private static final String TABLE_PHOTO = "photos";

    // COLUMN INFORMATION - NOTES
    private static final String NOTE_ID = "id";
    private static final String NOTE_DATE = "date";
    private static final String NOTE_LOCATION = "location";
    private static final String NOTE_SITE_ID = "site_id";
    private static final String NOTE_NOTE = "note";

    // COLUMN INFORMATION - MISC
    private static final String MISC_ID = "id";
    private static final String MISC_KEY = "key";
    private static final String MISC_VALUE = "value";

    // COLUMN INFORMATION - SITES
    private static final String SITE_ID = "id";
    private static final String SITE_NAME = "site_id";
    private static final String SITE_DATE = "date";
    private static final String SITE_LOCATION = "location";

    // COLUMN INFORMATION - PHOTOS
    private static final String PHOTO_ID = "id";
    private static final String PHOTO_NAME = "site_id";
    private static final String PHOTO_DATA = "data";

    public static final String TAG = "Notes DB";

    private final ArrayList<CipherNotes> note_list = new ArrayList<CipherNotes>();
    private final ArrayList<CipherSites> site_list = new ArrayList<CipherSites>();
    private final ArrayList<CipherPhotos> photo_list = new ArrayList<>();

    private Context context;

    public CipherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE A TABLE FOR NOTES
        String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NOTE_DATE + " TEXT," +
                NOTE_LOCATION + " TEXT," +
                NOTE_SITE_ID + " TEXT," +
                NOTE_NOTE + " TEXT" +
                ")";
        db.execSQL(CREATE_NOTE_TABLE);

        // CREATE A TABLE FOR PREFERENCES
        String CREATE_MISC_TABLE = "CREATE TABLE " + TABLE_MISC + "(" +
                MISC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MISC_KEY + " TEXT," +
                MISC_VALUE + " TEXT" +
                ")";
        db.execSQL(CREATE_MISC_TABLE);

        // CREATE A TABLE FOR SITES
        String CREATE_SITE_TABLE = "CREATE TABLE " + TABLE_SITE + "(" +
                SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SITE_NAME + " TEXT," +
                SITE_DATE + " TEXT," +
                SITE_LOCATION + " TEXT" +
                ")";
        db.execSQL(CREATE_SITE_TABLE);

        // CREATE A TABLE FOR PHOTOS
        String CREATE_PHOTO_TABLE = "CREATE TABLE " + TABLE_PHOTO + "(" +
                PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PHOTO_NAME + " TEXT," +
                PHOTO_DATA + " BLOB" +
                ")";
        db.execSQL(CREATE_PHOTO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // START FRESH
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MISC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        onCreate(db);
    }

    public void DeleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    //----> DATABASE FUNCTIONS FOR: NOTES <-------//

    public void AddNote(CipherNotes newNote) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(NOTE_DATE, newNote.GetDate());
        values.put(NOTE_LOCATION, newNote.GetLocation());
        values.put(NOTE_SITE_ID, newNote.GetSiteId());
        values.put(NOTE_NOTE, newNote.GetNote());
        // INSERT ROW
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public CipherNotes GetNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");

        Cursor cursor = db.query(TABLE_NAME, new String[] { NOTE_ID,
                        NOTE_DATE, NOTE_LOCATION, NOTE_SITE_ID, NOTE_NOTE }, NOTE_ID + "=?",
                        new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        CipherNotes cNotes = new CipherNotes(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        cursor.close();
        db.close();

        return cNotes;
    }

    public ArrayList<CipherNotes> GetNotes() {
        try {
            note_list.clear();

            String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + NOTE_SITE_ID + " IS NULL";

            SQLiteDatabase db = this.getWritableDatabase("user_pass");
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CipherNotes cNotes = new CipherNotes();
                    cNotes.SetId(Integer.parseInt(cursor.getString(0)));
                    cNotes.SetDate(cursor.getString(1));
                    cNotes.SetLocation(cursor.getString(2));
                    cNotes.SetSiteId(cursor.getString(3));
                    cNotes.SetNote(cursor.getString(4));

                    // ADD THE NOTE TO THE LIST
                    note_list.add(cNotes);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return note_list;
    }

    public ArrayList<CipherNotes> GetSiteNotes(String siteId) {
        try {
            note_list.clear();

            String[] whereArgs = new String[] { siteId };
            String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + NOTE_SITE_ID + " = ?";

            SQLiteDatabase db = this.getWritableDatabase("user_pass");
            Cursor cursor = db.rawQuery(selectQuery, whereArgs);

            if (cursor.moveToFirst()) {
                do {
                    CipherNotes cNotes = new CipherNotes();
                    cNotes.SetId(Integer.parseInt(cursor.getString(0)));
                    cNotes.SetDate(cursor.getString(1));
                    cNotes.SetLocation(cursor.getString(2));
                    cNotes.SetSiteId(cursor.getString(3));
                    cNotes.SetNote(cursor.getString(4));

                    // ADD THE NOTE TO THE LIST
                    note_list.add(cNotes);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return note_list;
    }

    public void UpdateNote(CipherNotes note) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");

        ContentValues values = new ContentValues();
        values.put(NOTE_DATE, note.GetDate());
        values.put(NOTE_LOCATION, note.GetLocation());
        values.put(NOTE_SITE_ID, note.GetSiteId());
        values.put(NOTE_NOTE, note.GetNote());
        db.update(TABLE_NAME, values, NOTE_ID + " = ?", new String[]{String.valueOf(note.GetId())});
        db.close();
    }

    public void DeleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        db.delete(TABLE_NAME, NOTE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int GetTotalNumbers() {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();

        return cursor.getCount();
    }

    //----> DATABASE FUNCTIONS FOR: PREFERENCES <-------//


    public int GetNumberPrefs() {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");
        String countQuery = "SELECT * FROM " + TABLE_MISC;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public void AddKey(CipherPrefs newPref) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(MISC_KEY, newPref.GetKey());
        values.put(MISC_VALUE, newPref.GetValue());
        // INSERT ROW
        db.insert(TABLE_MISC, null, values);
        db.close();
    }

    public CipherPrefs GetKey(String key) {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");

        Cursor cursor = db.query(TABLE_MISC, new String[]{MISC_ID, MISC_KEY, MISC_VALUE}, MISC_KEY + "= ?",
                new String[]{key}, null, null, null, null);

        CipherPrefs cPrefs = new CipherPrefs();

        if (cursor.moveToFirst()) {
            do {
                cPrefs.SetId(Integer.parseInt(cursor.getString(0)));
                cPrefs.SetKey(cursor.getString(1));
                cPrefs.SetValue(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return cPrefs;
    }

    public void DeleteKey(int id) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        db.delete(TABLE_MISC, MISC_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void UpdatePref(CipherPrefs pref) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");

        ContentValues values = new ContentValues();
        values.put(MISC_KEY, pref.GetKey());
        values.put(MISC_VALUE, pref.GetValue());
        db.update(TABLE_MISC, values, MISC_ID + " = ?", new String[]{String.valueOf(pref.GetId())});
        db.close();
    }

    //----> DATABASE FUNCTIONS FOR: SITES <-------//

    /**
     * AddSite - ADD A SITE NUMBER AND PHOTOS TO THE DATABASE
     * @param newSite
     */
    public void AddSite(CipherSites newSite) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(SITE_NAME, newSite.GetSiteId());
        values.put(SITE_DATE, newSite.GetDateCreated());
        values.put(SITE_LOCATION, newSite.GetSiteLocation());

        // INSERT ROW
        db.insert(TABLE_SITE, null, values);
        db.close();
    }

    public CipherSites GetSite(String siteId) {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");

        Cursor cursor = db.query(TABLE_SITE, new String[] { SITE_NAME }, SITE_NAME + "=?",
                new String[] { siteId }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        CipherSites cSites = new CipherSites(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        db.close();

        return cSites;
    }

    public int GetNumberSites() {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");

        String countQuery = "SELECT * FROM " + TABLE_SITE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    public ArrayList<CipherSites> GetSites() {
        try {
            site_list.clear();

            String selectQuery = "SELECT * FROM " + TABLE_SITE;

            SQLiteDatabase db = this.getWritableDatabase("user_pass");
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CipherSites cSites = new CipherSites();
                    cSites.SetId(Integer.parseInt(cursor.getString(0)));
                    cSites.SetSiteId(cursor.getString(1));
                    cSites.SetDateCreated(cursor.getString(2));
                    cSites.SetSiteLocation(cursor.getString(3));

                    // ADD THE NOTE TO THE LIST
                    site_list.add(cSites);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return site_list;
    }

    public void UpdateSite(CipherSites site) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");

        ContentValues values = new ContentValues();
        values.put(SITE_NAME, site.GetSiteId());
        values.put(SITE_DATE, site.GetDateCreated());
        values.put(SITE_LOCATION, site.GetSiteLocation());

        db.update(TABLE_SITE, values, NOTE_ID + " = ?", new String[]{String.valueOf(site.GetId())});
        db.close();
    }

    public void DeleteSite(int id) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        db.delete(TABLE_SITE, SITE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    //----> DATABASE FUNCTIONS FOR: PHOTOS <-------//

    public void AddPhoto(CipherPhotos newPhoto) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(PHOTO_NAME, newPhoto.GetSiteId());
        values.put(PHOTO_DATA, newPhoto.GetPhoto());

        // INSERT ROW
        db.insert(TABLE_PHOTO, null, values);
        db.close();
    }

    public ArrayList<CipherPhotos> GetPhotos(String siteId) {
        try {
            photo_list.clear();

            String[] whereArgs = new String[] { siteId };
            String selectQuery = "SELECT * FROM " + TABLE_PHOTO + " WHERE " + PHOTO_NAME + " = ?";

            SQLiteDatabase db = this.getWritableDatabase("user_pass");
            Cursor cursor = db.rawQuery(selectQuery, whereArgs);

            if (cursor.moveToFirst()) {
                do {
                    CipherPhotos cPhotos = new CipherPhotos();
                    cPhotos.SetId(Integer.parseInt(cursor.getString(0)));
                    cPhotos.SetSiteId(cursor.getString(1));
                    cPhotos.SetPhoto(cursor.getBlob(2));

                    // ADD THE NOTE TO THE LIST
                    photo_list.add(cPhotos);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return photo_list;
    }

    public int GetNumberPhotos() {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");
        String countQuery = "SELECT * FROM " + TABLE_PHOTO;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }
}

// TODO: Pass encrypted password to database. Store preferences in DB and encrypt with get(SIM_ID).
// TODO: Check to see if the device is rooted on boot. If so, clean app and delete the db.
// TODO: Add inflated photos for sites once clicked.
// TODO: Validate alert dialog back/cancel pressed.
