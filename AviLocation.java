package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aayaffe on 09/01/2016.
 */
public class AviLocation {
    public double lat;
    public double lon;
    public double sog = 0;
    public float cog = 0;
    public double depth = 0;
    public Date lastUpdate;

    public AviLocation() {

    }
    public AviLocation(double Lat, double Lng) {
        lat = Lat;
        lon = Lng;
        lastUpdate = new Date();
    }

    public AviLocation(AviLocation initial, int dir, double mark1dis){ //by location, direction and distance        //dir is from destination toward the initial point
        lastUpdate = new Date();
        AviLocation destination = new AviLocation();
        double tc=Math.toRadians(-dir);
        double l2r = mark1dis/(3440.06479482);

        double lat1 = Math.toRadians(initial.getLat()), lng1 = Math.toRadians(initial.getLng());

        double lat2 =Math.asin(Math.sin(lat1)*Math.cos(l2r)+Math.cos(lat1)*Math.sin(l2r)*Math.cos(tc));
        double dlon=Math.atan2(Math.sin(tc)*Math.sin(l2r)*Math.cos(lat1),Math.cos(l2r)-Math.sin(lat1)*Math.sin(lat2));
        double lng2=(lng1-dlon +Math.PI)%(2*Math.PI)-Math.PI;

        destination.setLat(Math.toDegrees(lat2));
        destination.setLng(Math.toDegrees(lng2));
        lat=Math.toDegrees(lat2);
        lon=Math.toDegrees(lng2);
    }

    public AviLocation(AviLocation p1, double brng1, AviLocation p2,  double brng2){//by 2 locations and 2 directions(radials)
        lastUpdate = new Date();
        double lat1 = Math.toRadians(p1.getLat()), lon1 = Math.toRadians(p1.getLng());
        double lat2 = Math.toRadians(p2.getLat()), lon2 = Math.toRadians(p2.getLng());
        double brng13 = Math.toRadians(brng1), brng23 = Math.toRadians(brng2);
        double dLat = lat2 - lat1, dLon = lon2 - lon1;

        double dist12 = 2 * Math.asin(Math.sqrt(Math.sin(dLat / 2)
                * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2)));
        if (dist12 == 0);
        Double brngA = Math.acos((Math.sin(lat2) - Math.sin(lat1) * Math.cos(dist12)) / (Math.sin(dist12) * Math.cos(lat1)));
        if (brngA.isNaN()) brngA = 0.0;
        Double brngB = Math.acos((Math.sin(lat1) - Math.sin(lat2) * Math.cos(dist12)) / (Math.sin(dist12) * Math.cos(lat2)));
        double brng12, brng21;
        if (Math.sin(lon2 - lon1) > 0) {
            brng12 = brngA;
            brng21 = 2 * Math.PI - brngB;
        } else {
            brng12 = 2 * Math.PI - brngA;
            brng21 = brngB;
        }
        double alpha1 = (brng13 - brng12 + Math.PI) % (2 * Math.PI) - Math.PI; // angle
        double alpha2 = (brng21 - brng23 + Math.PI) % (2 * Math.PI) - Math.PI; // angle

        double alpha3 = Math.acos(-Math.cos(alpha1) * Math.cos(alpha2)
                + Math.sin(alpha1) * Math.sin(alpha2) * Math.cos(dist12));
        double dist13 = Math.atan2(
                Math.sin(dist12) * Math.sin(alpha1) * Math.sin(alpha2),
                Math.cos(alpha2) + Math.cos(alpha1) * Math.cos(alpha3));
        double lat3 = Math.asin(Math.sin(lat1) * Math.cos(dist13)
                + Math.cos(lat1) * Math.sin(dist13) * Math.cos(brng13));
        double dLon13 = Math.atan2(
                Math.sin(brng13) * Math.sin(dist13) * Math.cos(lat1),
                Math.cos(dist13) - Math.sin(lat1) * Math.sin(lat3));
        double lon3 = lon1 + dLon13;
        lon3 = (lon3 + Math.PI) % (2 * Math.PI) - Math.PI;
        lat= Math.toDegrees(lat3);
        lon= Math.toDegrees(lon3);
    }

    public AviLocation(AviLocation p1, AviLocation p2){ //Middle point
        lastUpdate = new Date();
        double lon2 =p2.getLng();
        double lon1 = p1.getLng();
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1 = Math.toRadians(p1.getLat());
        double lat2 = Math.toRadians(p2.getLat());
        lon1 = Math.toRadians(p1.getLng());
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        lat=Math.toDegrees(lat3);
        lon=Math.toDegrees(lon3);
    }


    public AviLocation(double Lat, double Lng,  float cog, double sog, double depth, Date lastUpdate) {
        lat = Lat;
        lon = Lng;
        this.sog = sog;
        this.cog = cog;
        this.depth = depth;
        this.lastUpdate = lastUpdate;

    }



    public Location toLocation(){
        return GeoUtils.createLocation(lat,lon);
    }

    public static long Age(AviLocation aviLocation) {
        if (aviLocation==null) return -1;
        if (aviLocation.lastUpdate==null) return -1;
        long diffInMs = new Date().getTime() - aviLocation.lastUpdate.getTime();
        return TimeUnit.MILLISECONDS.toSeconds(diffInMs);
    }

    /**
     * Returns the approximate distance in meters between this
     * location and the given location.  Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param dest the destination location
     * @return the approximate distance in meters, -1 if error
     */
    public float distanceTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).distanceTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }


    /**
     * Returns the approximate initial bearing in degrees East of true
     * North when traveling along the shortest path between this
     * location and the given location.  The shortest path is defined
     * using the WGS84 ellipsoid.  Locations that are (nearly)
     * antipodal may produce meaningless results.
     *
     * @param dest the destination location
     * @return the initial bearing in degrees
     */
    public float bearingTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).bearingTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AviLocation that = (AviLocation) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        if (Double.compare(that.sog, sog) != 0) return false;
        return Float.compare(that.cog, cog) == 0 && Double.compare(that.depth, depth) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sog);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (cog != +0.0f ? Float.floatToIntBits(cog) : 0);
        temp = Double.doubleToLongBits(depth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lon;
    }

    public void setLat(double Lat) {
        this.lat=Lat;
    }
    public void setLng(double Lng) {
        this.lon=Lng;
    }
}
