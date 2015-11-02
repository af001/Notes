package technology.xor.notes.support;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherPhotos;
import technology.xor.notes.notes.R;

public class CameraView extends AppCompatActivity {

    private static final String TAG = "CameraView";
    private ArrayList<CipherPhotos> cPhotos;
    private String siteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gridview);

        Intent mIntent = getIntent();
        siteId = mIntent.getStringExtra("site_id");

        ShowPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cPhotos.clear();
        ShowPhotos();
    }

    private void ShowPhotos() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        ImageAdapter mImageAdapter = new ImageAdapter(this);
        gridView.setAdapter(mImageAdapter);

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
        cPhotos = dbHelper.GetPhotos(siteId);

        if (!cPhotos.isEmpty()) {
            for (int ix = 0; ix < cPhotos.size(); ix++) {
                mImageAdapter.add(cPhotos.get(ix).GetPhoto());
            }
        }
    }

    private byte[] CompressBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            Log.e(TAG, "Error compressing image.");
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppData.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");
            if (bmp != null) {
                CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(this);
                CipherPhotos cPhotos = new CipherPhotos(siteId, CompressBitmap(bmp));
                dbHelper.AddPhoto(cPhotos);
                dbHelper.close();
            }
        }
    }

    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, AppData.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: CHANGE MENU BUTTONS

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.addNewImgBtn:
                TakePictureIntent();
                return true;
            case R.id.deleteAllImgBtn:
                Toast.makeText(this.getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cameraview, menu);
        return true;
    }
}
