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

public class PlyoExerciseName {
    public static final int ALTERNATING_JUMP_LUNGE = 0;
    public static final int WEIGHTED_ALTERNATING_JUMP_LUNGE = 1;
    public static final int BARBELL_JUMP_SQUAT = 2;
    public static final int BODY_WEIGHT_JUMP_SQUAT = 3;
    public static final int WEIGHTED_JUMP_SQUAT = 4;
    public static final int CROSS_KNEE_STRIKE = 5;
    public static final int WEIGHTED_CROSS_KNEE_STRIKE = 6;
    public static final int DEPTH_JUMP = 7;
    public static final int WEIGHTED_DEPTH_JUMP = 8;
    public static final int DUMBBELL_JUMP_SQUAT = 9;
    public static final int DUMBBELL_SPLIT_JUMP = 10;
    public static final int FRONT_KNEE_STRIKE = 11;
    public static final int WEIGHTED_FRONT_KNEE_STRIKE = 12;
    public static final int HIGH_BOX_JUMP = 13;
    public static final int WEIGHTED_HIGH_BOX_JUMP = 14;
    public static final int ISOMETRIC_EXPLOSIVE_BODY_WEIGHT_JUMP_SQUAT = 15;
    public static final int WEIGHTED_ISOMETRIC_EXPLOSIVE_JUMP_SQUAT = 16;
    public static final int LATERAL_LEAP_AND_HOP = 17;
    public static final int WEIGHTED_LATERAL_LEAP_AND_HOP = 18;
    public static final int LATERAL_PLYO_SQUATS = 19;
    public static final int WEIGHTED_LATERAL_PLYO_SQUATS = 20;
    public static final int LATERAL_SLIDE = 21;
    public static final int WEIGHTED_LATERAL_SLIDE = 22;
    public static final int MEDICINE_BALL_OVERHEAD_THROWS = 23;
    public static final int MEDICINE_BALL_SIDE_THROW = 24;
    public static final int MEDICINE_BALL_SLAM = 25;
    public static final int SIDE_TO_SIDE_MEDICINE_BALL_THROWS = 26;
    public static final int SIDE_TO_SIDE_SHUFFLE_JUMP = 27;
    public static final int WEIGHTED_SIDE_TO_SIDE_SHUFFLE_JUMP = 28;
    public static final int SQUAT_JUMP_ONTO_BOX = 29;
    public static final int WEIGHTED_SQUAT_JUMP_ONTO_BOX = 30;
    public static final int SQUAT_JUMPS_IN_AND_OUT = 31;
    public static final int WEIGHTED_SQUAT_JUMPS_IN_AND_OUT = 32;
    public static final int INVALID = Fit.UINT16_INVALID;

    private static final Map<Integer, String> stringMap;

    static {
        stringMap = new HashMap<Integer, String>();
        stringMap.put(ALTERNATING_JUMP_LUNGE, "ALTERNATING_JUMP_LUNGE");
        stringMap.put(WEIGHTED_ALTERNATING_JUMP_LUNGE, "WEIGHTED_ALTERNATING_JUMP_LUNGE");
        stringMap.put(BARBELL_JUMP_SQUAT, "BARBELL_JUMP_SQUAT");
        stringMap.put(BODY_WEIGHT_JUMP_SQUAT, "BODY_WEIGHT_JUMP_SQUAT");
        stringMap.put(WEIGHTED_JUMP_SQUAT, "WEIGHTED_JUMP_SQUAT");
        stringMap.put(CROSS_KNEE_STRIKE, "CROSS_KNEE_STRIKE");
        stringMap.put(WEIGHTED_CROSS_KNEE_STRIKE, "WEIGHTED_CROSS_KNEE_STRIKE");
        stringMap.put(DEPTH_JUMP, "DEPTH_JUMP");
        stringMap.put(WEIGHTED_DEPTH_JUMP, "WEIGHTED_DEPTH_JUMP");
        stringMap.put(DUMBBELL_JUMP_SQUAT, "DUMBBELL_JUMP_SQUAT");
        stringMap.put(DUMBBELL_SPLIT_JUMP, "DUMBBELL_SPLIT_JUMP");
        stringMap.put(FRONT_KNEE_STRIKE, "FRONT_KNEE_STRIKE");
        stringMap.put(WEIGHTED_FRONT_KNEE_STRIKE, "WEIGHTED_FRONT_KNEE_STRIKE");
        stringMap.put(HIGH_BOX_JUMP, "HIGH_BOX_JUMP");
        stringMap.put(WEIGHTED_HIGH_BOX_JUMP, "WEIGHTED_HIGH_BOX_JUMP");
        stringMap.put(ISOMETRIC_EXPLOSIVE_BODY_WEIGHT_JUMP_SQUAT, "ISOMETRIC_EXPLOSIVE_BODY_WEIGHT_JUMP_SQUAT");
        stringMap.put(WEIGHTED_ISOMETRIC_EXPLOSIVE_JUMP_SQUAT, "WEIGHTED_ISOMETRIC_EXPLOSIVE_JUMP_SQUAT");
        stringMap.put(LATERAL_LEAP_AND_HOP, "LATERAL_LEAP_AND_HOP");
        stringMap.put(WEIGHTED_LATERAL_LEAP_AND_HOP, "WEIGHTED_LATERAL_LEAP_AND_HOP");
        stringMap.put(LATERAL_PLYO_SQUATS, "LATERAL_PLYO_SQUATS");
        stringMap.put(WEIGHTED_LATERAL_PLYO_SQUATS, "WEIGHTED_LATERAL_PLYO_SQUATS");
        stringMap.put(LATERAL_SLIDE, "LATERAL_SLIDE");
        stringMap.put(WEIGHTED_LATERAL_SLIDE, "WEIGHTED_LATERAL_SLIDE");
        stringMap.put(MEDICINE_BALL_OVERHEAD_THROWS, "MEDICINE_BALL_OVERHEAD_THROWS");
        stringMap.put(MEDICINE_BALL_SIDE_THROW, "MEDICINE_BALL_SIDE_THROW");
        stringMap.put(MEDICINE_BALL_SLAM, "MEDICINE_BALL_SLAM");
        stringMap.put(SIDE_TO_SIDE_MEDICINE_BALL_THROWS, "SIDE_TO_SIDE_MEDICINE_BALL_THROWS");
        stringMap.put(SIDE_TO_SIDE_SHUFFLE_JUMP, "SIDE_TO_SIDE_SHUFFLE_JUMP");
        stringMap.put(WEIGHTED_SIDE_TO_SIDE_SHUFFLE_JUMP, "WEIGHTED_SIDE_TO_SIDE_SHUFFLE_JUMP");
        stringMap.put(SQUAT_JUMP_ONTO_BOX, "SQUAT_JUMP_ONTO_BOX");
        stringMap.put(WEIGHTED_SQUAT_JUMP_ONTO_BOX, "WEIGHTED_SQUAT_JUMP_ONTO_BOX");
        stringMap.put(SQUAT_JUMPS_IN_AND_OUT, "SQUAT_JUMPS_IN_AND_OUT");
        stringMap.put(WEIGHTED_SQUAT_JUMPS_IN_AND_OUT, "WEIGHTED_SQUAT_JUMPS_IN_AND_OUT");
    }


    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value, or empty if unknown
     */
    public static String getStringFromValue( Integer value ) {
        if( stringMap.containsKey( value ) ) {
            return stringMap.get( value );
        }

        return "";
    }

    /**
     * Retrieves a value given a string representation
     * @return The value or INVALID if unkwown
     */
    public static Integer getValueFromString( String value ) {
        for( Map.Entry<Integer, String> entry : stringMap.entrySet() ) {
            if( entry.getValue().equals( value ) ) {
                return entry.getKey();
            }
        }

        return INVALID;
    }

}
