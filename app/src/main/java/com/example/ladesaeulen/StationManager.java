package com.example.ladesaeulen;

import java.util.ArrayList;

/**
 * Wrapper for data
 *
 */

public class StationManager {

    /**
     * Main container for stations (static to get data from all activities / fragments).
     */
    private static ArrayList<ChargingStation> station_storage;
    private static ArrayList<ChargingStation> favorit_station_storage =new ArrayList<>();
    private static ArrayList<ChargingStation> nearby_station_storage= new ArrayList<>();


    /**
     * Setter for container.
     *
     * @param station_list of station objects
     */
    public static void setStation_list(ArrayList<ChargingStation> station_list) {
        station_storage = station_list;
    }

    /**
     * Getter for station container.
     *
     * @return arrayList with station objects
     */
    public static ArrayList<ChargingStation> getStation_list() {
        return station_storage;
    }

    /**
      * Function that helps Setting our favorite stations
     *  @param favorit_station_storage used to adding favorite stations to StationManager
     */


    public static void setFavorit_station_storage(ArrayList<ChargingStation> favorit_station_storage) {
        StationManager.favorit_station_storage = favorit_station_storage;
    }

    /**
     * Function that helps getting our favorite stations
     */
    public static ArrayList<ChargingStation> getFavorit_station_storage() {
        return favorit_station_storage;
    }

    /**
     * Function that helps getting nearby stations from our current location
     */

    public static ArrayList<ChargingStation> getNearby_station_storage() {

        return nearby_station_storage;
    }

    /**
     * Function that helps Setting nearby stations from our current location to desired distance
     * using the radius distances that is passed in parameter
     *  @param radius used to determine the distance
     */

    public static void setNearby_station_storage(double radius) {
        for (int i = 0;i<station_storage.size();i++)
        {
            if (station_storage.get(i).getDistance()<=radius&&station_storage.get(i)!=null)
            {
                nearby_station_storage.add(station_storage.get(i));

            }
        }
    }
}
