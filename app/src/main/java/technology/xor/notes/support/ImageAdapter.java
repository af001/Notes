package technology.xor.notes.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<byte[]> itemList = new ArrayList<byte[]>();

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(325, 325));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 325, 325);

        imageView.setImageBitmap(bm);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return imageView;
    }

    public Bitmap decodeSampledBitmapFromUri(byte[] photo, int reqWidth, int reqHeight) {

        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();

        // CALCULATE inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // DECODE BITMAP WITH inSampleSize SET
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeByteArray(photo, 0, photo.length, options);

        return bm;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // RAW HEIGHT AND WIDTH OF IMAGE
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

    public void add(byte[] img){ itemList.add(img); }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int arg0) { return null; }

    @Override
    public long getItemId(int position) { return 0; }
}