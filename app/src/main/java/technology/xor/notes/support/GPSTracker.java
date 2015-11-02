package technology.xor.notes.support;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

    // THE MINIMUM DISTANCE TO CHANGE UPDATES - IN METERS
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 METERS

    // THE MINIMUM TIME BETWEEN UPDATES IN MILLISECONDS
    private static final long MIN_TIME_BW_UPDATES = 2000 * 60; // 2 MINUTE

    // FLAG FOR GPS STATUS
    boolean isGPSEnabled = false;

    // FLAG FOR NETWORK STATUS
    boolean isNetworkEnabled = false;

    // FLAG FOR GPS STATUS
    boolean canGetLocation = false;

    private Context mContext;
    private Location location;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;

    /**
     * CONSTRUCTOR
     * @param context
     */
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public GPSTracker() { }

    /**
     * METHOD TO CHECK THE STATUS OF LOCATION SERVICES
     * @return
     */
    public Location getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("GPS", "Check complete.");
            }
        } else {

            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // GET GPS STATUS
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // GET NETWORK STATUS
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGPSEnabled || isNetworkEnabled) {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // IF GPS IS ENABLED GET THE LAT/LONG USING GPS SERVICES
                    if (isGPSEnabled) {
                        if (location != null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return location;
    }

    /**
     * METHOD TO STOP RECEIVING GPS UPDATES - SAVES BATTERY!!
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(GPSTracker.this);
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                locationManager.removeUpdates(GPSTracker.this);
            }
        }
    }

    /**
     * METHOD TO GET THE LATITUDE OF THE PHONE
     * @return
     */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * METHOD TO GET THE LONGITUDE OF THE PHONE
     * @return
     */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * METHOD TO CHECK IF THE GPS OR NETWORK PROVIDE ARE ENABLED FOR GETTING LOCATION DATA
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
