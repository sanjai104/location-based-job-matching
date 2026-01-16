package com.parttime.service;

import org.springframework.stereotype.Service;

@Service
public class LocationService {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Check if a point is within a given radius from a center point
     * @param centerLat Latitude of center point
     * @param centerLon Longitude of center point
     * @param pointLat Latitude of point to check
     * @param pointLon Longitude of point to check
     * @param radiusKm Radius in kilometers
     * @return true if point is within radius
     */
    public boolean isWithinRadius(double centerLat, double centerLon, 
                                  double pointLat, double pointLon, double radiusKm) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }
}


