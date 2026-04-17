package oshrik.shidech_stable_match.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.utilities.Location;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



@Service
public class LocationApiService 
{

    // כתובת הבקשה - שים לב ל-format=json ולאישור השפה בעברית
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&accept-language=he&q=";

    public LocationApiService()
    {

    }

    public double calcDistance(Location location1,Location location2)
    {
            return location1.distanceTo(location2);
    }
    
    public Location getRealLocationFromCityName(String cityName)
    {
        // הגנה למקרה שהמשתמש לא הזין עיר
        if (cityName == null || cityName.trim().isEmpty()) {
            return null;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Location resultLocation = null;

        try {
            // צריך לקודד את שם העיר  
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
            
            Request request = new Request.Builder()
                    .url(NOMINATIM_URL + encodedCity)
                    .header("User-Agent", "ShidechApp-StudentProject") // חוק הנימוס של OpenStreetMap!
                    .build();

            // ביצוע הבקשה
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                
                // ה-API מחזיר מערך של אובייקטים, אנו ממירים אותו ל-JSONArray
                JSONArray jsonArray = new JSONArray(responseBody);
                
                // אם חזרה לפחות תוצאה אחת
                if (jsonArray.length() > 0) {
                    JSONObject firstResult = jsonArray.getJSONObject(0);
                    
                    // ה-API מחזיר את המספרים בתור מחרוזות (String), לכן נמיר ל-Double
                    double lat = Double.parseDouble(firstResult.getString("lat"));
                    double lon = Double.parseDouble(firstResult.getString("lon"));
                    
                    resultLocation = new Location(lat, lon);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching location for city: " + cityName);
            e.printStackTrace();
        }

        return resultLocation; // יחזיר את המיקום, או null במקרה של שגיאה/עיר לא קיימת
    
    }


}
