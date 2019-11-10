package com.intouristing.utils;

import com.intouristing.model.entity.UserPosition;

public class PositionUtils {

    private final static double AVERAGE_RADIUS_OF_EARTH_METERS = 6371000;

    public static long calculateDistance(double lat1, double lng1,
                                         double lat2, double lng2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(lng1 - lng2);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (Math.round(AVERAGE_RADIUS_OF_EARTH_METERS * c));
    }

    public static long calculateDistance(UserPosition userPos1, UserPosition userPos2) {
        return calculateDistance(
                userPos1.getLatitude(), userPos1.getLongitude(),
                userPos2.getLatitude(), userPos2.getLongitude()
        );
    }

}
