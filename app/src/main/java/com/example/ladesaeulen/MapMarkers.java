package com.example.ladesaeulen;
import com.google.android.gms.maps.model.LatLng;

public class MapMarkers {
   private LatLng location;
   private String titel;
   private double distance;

   public LatLng getLocation() {
      return location;
   }

   public void setLocation(LatLng location) {
      this.location = location;
   }



   public String getTitel() {
      return titel;
   }

   public void setTitel(String titel) {
      this.titel = titel;
   }

   public double getDistance(){return distance;}

   public void setdistance(float lat1, float lon1, double lat2, double lon2)

   {
      double R = 6371; // Erdradius in km
      double dLat = (lat1 - lat2) * Math.PI / 180.0;
      double dLon = (lon1 - lon2) * Math.PI / 180.0;
      double a = Math.sin(dLat / 2.) * Math.sin(dLat / 2.)
              + Math.cos(lat1 * Math.PI / 180.0)
              * Math.cos(lat2 * Math.PI / 180.0)
              * Math.sin(dLon / 2.) * Math.sin(dLon / 2.);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1. - a));
      double d = R * c;
      distance = d;
   }


}
