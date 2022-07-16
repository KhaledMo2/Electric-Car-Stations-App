package com.example.ladesaeulen;


/**
 * ChargingStation container
 */

public class ChargingStation {

    /* General information */
    private int id;
    private String operator;
    private String street;

    public void setNumber(String number) {
        this.number = number;
    }

    private String number;
    private String additional;
    private String postal_code;
    private String location;
    private String state;
    private String area;
    private double lat;
    private double lon;
    private String installation_date;
    private double conn_power;
    private int distance;


    private boolean isDefekt=false;


    private String chargingType;
    private String module_type;
    private int number_of_connections;

    public void setModule_type(String module_type) {
        this.module_type = module_type;
    }




    /* Usage | maintenance  */
    private boolean is_used;

    /* Getters */
    /**
     * Get the stations id.
     * @return id as int
     * To set ids use: {@link #setId(int)}
     */
    public int getId() {
        return id;
    }

    /**
     * Get the stations operator.
     * @return operator as string
     */
    public final String getOperator() {
        return operator;
    }
    public void setOperator(String operator)
    {
        this.operator=operator;
    }

    /**
     * Get the stations street.
     * @return street as string
     */
    public final String getStreet() {
        return street;
    }

    /**
     * Get the stations number of street.
     * @return number as string
     */
    public final String getNumber() {
        return number;
    }

    /**
     * Get the stations additional.
     * @return additional as string
     */
    public final String getAdditional() {
        return additional;
    }

    /**
     * Get the stations postal code.
     * @return postal code as int
     */
    public final String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code)
    {
        this.postal_code=postal_code;
    }

    /**
     * Get the stations location.
     * @return location as string
     */
    public final String getLocation() {
        return location;
    }
    public void setLocation(String location)
    {
        this.location=location;
    }

    /**
     * Get the stations state.
     * @return state as string
     */
    public final String getState() {
        return state;
    }

    /**
     * Get the stations area.
     * @return area as string
     */
    public final String getArea() {
        return area;
    }
    public void setArea(String area)
    {
        this.area=area;
    }


    /**
     * Get the stations lat position.
     * @return lat as double
     */
    public final double getLat() {
        return lat;
    }

    /**
     * Get the stations lon position.
     * @return lon as double
     */
    public final double getLon() {
        return lon;
    }

    /**
     * Get the stations installation date.
     * @return installation date as date format
     */
    public final String getInstallation_date() {
        return installation_date;
    }

    /**
     * Get the stations connection power.
     * @return connection power as double
     */
    public final double getConn_power() {
        return conn_power;
    }

    /**
     * Get the stations module type.
     * @return module type as enum
     */
    public final String getModule_type() {
        if (module_type.equals("Normalladeeinrichtung"))
        {
            return "NC";
        }
        else
        {
            return "FC";

        }



    }

    /**
     * Get the stations amount of connections.
     * @return amount of connection as int
     */
    public final int getNumber_of_connections() {
        return number_of_connections;
    }


    /**
     * Get the stations usability.
     * @return boolean for usage
     */
    public boolean isIs_used() {
        return is_used;
    }

    /**
     * Give the station an id.
     * @param id as int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter for usage.
     * @param is_used as boolean
     */
    public void setIs_used(boolean is_used) {
        this.is_used = is_used;
    }

    public String getChargingType() { return chargingType; }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    public double getDistance(){return distance;}

    /**
     * Function that calculate the distance between 2 locations
     * using the lat and long of the 2 Locations to determine the distance between them
     */

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
        distance = (int) d;
    }

    public boolean getisDefekt() {
        return isDefekt;
    }

    public void setisDefekt(boolean defekt) {
        isDefekt = defekt;
    }



}
