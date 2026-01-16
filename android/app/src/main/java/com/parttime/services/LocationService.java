package com.parttime.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location  .FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService {
    
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private LocationCallback callback;
    
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
        void onLocationError(String error);
    }
    
    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }
    
    public void getCurrentLocation(LocationCallback callback) {
        this.callback = callback;
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED 
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            if (callback != null) {
                callback.onLocationError("Location permission not granted");
            }
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            if (callback != null) {
                                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
                            }
                        } else {
                            if (callback != null) {
                                callback.onLocationError("Location not available");
                            }
                        }
                    }
                });
    }
}


