package com.example.ladesaeulen;
import java.util.ArrayList;

/**
 * Wrapper for data
 */

public class MarkerManager {

    /**
     * Main container for marker (static to get data from all activities / fragments).
     */
    private static ArrayList<MapMarkers> marker_storage;
    private static ArrayList<MapMarkers> nearby_storage= new ArrayList<>();



    /**
     * Setter for container.
     *
     * @param marker_list of marker objects
     */
    public static void setMarker_list(ArrayList<MapMarkers> marker_list) {
        marker_storage = marker_list;
    }

    /**
     * Getter for marker container.
     *
     * @return arrayList with marker objects
     */
    public static ArrayList<MapMarkers> getMarker_list() {
        return marker_storage;
    }

    public static ArrayList<MapMarkers> getnearby_list()
    {

        return nearby_storage;
    }
    /**
     * Function that heps Setting nearby stations from our current location to desired distance
     * using the radius distances that is passed in parameter
     *  @param radius used to determine the distance
     */
    public static void setNearby_storage(double radius) {
        for (int i = 0;i<marker_storage.size();i++)
        {
            if (marker_storage.get(i).getDistance()<=radius&&marker_storage.get(i)!=null)
            {
                nearby_storage.add(marker_storage.get(i));

            }
        }
    }
}
