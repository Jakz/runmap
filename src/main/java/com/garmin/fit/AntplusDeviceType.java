////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Garmin Canada Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2018 Garmin Canada Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 20.74Release
// Tag = production/akw/20.74.01-0-ge94e71c
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;

import java.util.HashMap;
import java.util.Map;

public class AntplusDeviceType {
    public static final short ANTFS = 1;
    public static final short BIKE_POWER = 11;
    public static final short ENVIRONMENT_SENSOR_LEGACY = 12;
    public static final short MULTI_SPORT_SPEED_DISTANCE = 15;
    public static final short CONTROL = 16;
    public static final short FITNESS_EQUIPMENT = 17;
    public static final short BLOOD_PRESSURE = 18;
    public static final short GEOCACHE_NODE = 19;
    public static final short LIGHT_ELECTRIC_VEHICLE = 20;
    public static final short ENV_SENSOR = 25;
    public static final short RACQUET = 26;
    public static final short CONTROL_HUB = 27;
    public static final short MUSCLE_OXYGEN = 31;
    public static final short BIKE_LIGHT_MAIN = 35;
    public static final short BIKE_LIGHT_SHARED = 36;
    public static final short EXD = 38;
    public static final short BIKE_RADAR = 40;
    public static final short WEIGHT_SCALE = 119;
    public static final short HEART_RATE = 120;
    public static final short BIKE_SPEED_CADENCE = 121;
    public static final short BIKE_CADENCE = 122;
    public static final short BIKE_SPEED = 123;
    public static final short STRIDE_SPEED_DISTANCE = 124;
    public static final short INVALID = Fit.UINT8_INVALID;

    private static final Map<Short, String> stringMap;

    static {
        stringMap = new HashMap<Short, String>();
        stringMap.put(ANTFS, "ANTFS");
        stringMap.put(BIKE_POWER, "BIKE_POWER");
        stringMap.put(ENVIRONMENT_SENSOR_LEGACY, "ENVIRONMENT_SENSOR_LEGACY");
        stringMap.put(MULTI_SPORT_SPEED_DISTANCE, "MULTI_SPORT_SPEED_DISTANCE");
        stringMap.put(CONTROL, "CONTROL");
        stringMap.put(FITNESS_EQUIPMENT, "FITNESS_EQUIPMENT");
        stringMap.put(BLOOD_PRESSURE, "BLOOD_PRESSURE");
        stringMap.put(GEOCACHE_NODE, "GEOCACHE_NODE");
        stringMap.put(LIGHT_ELECTRIC_VEHICLE, "LIGHT_ELECTRIC_VEHICLE");
        stringMap.put(ENV_SENSOR, "ENV_SENSOR");
        stringMap.put(RACQUET, "RACQUET");
        stringMap.put(CONTROL_HUB, "CONTROL_HUB");
        stringMap.put(MUSCLE_OXYGEN, "MUSCLE_OXYGEN");
        stringMap.put(BIKE_LIGHT_MAIN, "BIKE_LIGHT_MAIN");
        stringMap.put(BIKE_LIGHT_SHARED, "BIKE_LIGHT_SHARED");
        stringMap.put(EXD, "EXD");
        stringMap.put(BIKE_RADAR, "BIKE_RADAR");
        stringMap.put(WEIGHT_SCALE, "WEIGHT_SCALE");
        stringMap.put(HEART_RATE, "HEART_RATE");
        stringMap.put(BIKE_SPEED_CADENCE, "BIKE_SPEED_CADENCE");
        stringMap.put(BIKE_CADENCE, "BIKE_CADENCE");
        stringMap.put(BIKE_SPEED, "BIKE_SPEED");
        stringMap.put(STRIDE_SPEED_DISTANCE, "STRIDE_SPEED_DISTANCE");
    }


    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value, or empty if unknown
     */
    public static String getStringFromValue( Short value ) {
        if( stringMap.containsKey( value ) ) {
            return stringMap.get( value );
        }

        return "";
    }

    /**
     * Retrieves a value given a string representation
     * @return The value or INVALID if unkwown
     */
    public static Short getValueFromString( String value ) {
        for( Map.Entry<Short, String> entry : stringMap.entrySet() ) {
            if( entry.getValue().equals( value ) ) {
                return entry.getKey();
            }
        }

        return INVALID;
    }

}
