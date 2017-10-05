package com.github.jakz.runmap;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.OptionalDouble;

import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;

public class Workout
{
  private final ZonedDateTime start, end;
  
  private double length = Double.NaN;
  private double heightDifference = Double.NaN;
  
  private GpxTrackSegment gpx;
  
  public Workout(GpxTrackSegment gpx)
  {
    if (gpx.points().size() < 2)
      throw new IllegalArgumentException("a workout requires a GpxTrackSegment with at least 2 points");

    GpxWaypoint start = gpx.points().get(0);
    GpxWaypoint end = gpx.points().get(gpx.points().size()-1);
    
    this.gpx = gpx;
    this.start = start.time();
    this.end = end.time();
  }
  
  public GpxTrackSegment gpx() { return gpx; }

  public ZonedDateTime start() { return start; }
  public ZonedDateTime end() { return end; }
  
  public double length()
  {
    if (Double.isNaN(length))
      length = gpx.totalLength();
    return length;
  }
  
  public double heightDifference()
  {
    if (Double.isNaN(heightDifference))
    {
      OptionalDouble min = gpx.stream().mapToDouble(p -> p.coordinate().alt()).min();
      OptionalDouble max = gpx.stream().mapToDouble(p -> p.coordinate().alt()).max();
      heightDifference = max.getAsDouble() - min.getAsDouble();
    }
    
    return heightDifference;
  }
}
