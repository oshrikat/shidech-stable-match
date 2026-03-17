package oshrik.shidech_stable_match.utilities;

/**
 * מחלקה המייצגת נקודת ציון גיאוגרפית.
 */
public class Location 
{

    private double latitude;
    private double longitude;

    public Location(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }


    // בנאי ריק - התאמה מהבסיס נתונים מונגו לשרת
    public Location()
    {

    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public double distanceTo(Location other) 
    {
        final int EARTH_RADIUS = 6371; // רדיוס כדור הארץ בק"מ
    
        double latDistance = Math.toRadians(other.latitude - this.latitude);
    
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
    
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
       
       
        return EARTH_RADIUS * c; // מחזיר מרחק בק"מ
    }
}