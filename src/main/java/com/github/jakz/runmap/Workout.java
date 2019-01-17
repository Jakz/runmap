package com.github.jakz.runmap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pixbits.lib.io.xml.gpx.GpxExtension;
import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;
import com.pixbits.lib.lang.AtomicDouble;
import com.pixbits.lib.util.TimeInterval;

public class Workout
{
  private final ZonedDateTime start, end;
  private final TimeInterval lapse;
  
  private double length = Double.NaN;
  
  private double minAltitude = Double.NaN;
  private double maxAltitude = Double.NaN;
  private double altitudeDifference = Double.NaN;
  private double altitudeSum = Double.NaN;
  private double climb = Double.NaN;
  
  private double calories = Double.NaN;
  
  private int averageHeartRate = -1;
  private int maxHeartRate = -1;
  private int minHeartRate = -1;
  
  private WorkoutTrack track;
  
  private Function<Workout, Double> caloriesCalculator;
  
  public Workout(WorkoutTrack track)
  {
    if (track.size() < 2)
      throw new IllegalArgumentException("a workout requires a GpxTrackSegment with at least 2 points");

    WorkoutPoint start = track.get(0);
    WorkoutPoint end = track.get(track.size()-1);
    
    this.track = track;
    this.start = start.time().withZoneSameInstant(ZoneId.systemDefault());
    this.end = end.time().withZoneSameInstant(ZoneId.systemDefault());
    this.lapse = TimeInterval.of(start.time(), end.time());
    
    this.caloriesCalculator = w -> {
      double base = w.length()*58.0f;
      double addendum = 0.0;
      
      if (!Double.isNaN(track.get(0).coordinate().alt()))
      {
        double strainLength = 0.0;
        double strainAltitude = 0.0;
        
        double lastAltitude = Double.NaN;
        
        for (int i = 0; i < track.size(); ++i)
        {
          WorkoutPoint p = track.get(i);
          
          if (Double.isNaN(lastAltitude))
          {
            strainLength = 0.0;
            strainAltitude = 0.0;
            lastAltitude = p.coordinate().alt();
          }
          else
          {
            double alt = p.coordinate().alt();
            
            if (alt > lastAltitude)
            {
              strainLength += track.distanceBetweenPoints(i-1);
              strainAltitude += alt - lastAltitude;
              lastAltitude = alt;
            }
            else
            {
              if (strainAltitude > 5.0)
              {
                double slope = strainAltitude / (strainLength * 1000);
                addendum += (strainLength * 58.0) * (slope * 8);
              }
              
              lastAltitude = Double.NaN;
            }
          }
        }
      }
 
      return base + addendum;
    };
  }
  
  public void cacheHeartRateStatistics()
  {
    maxHeartRate = Integer.MIN_VALUE;
    minHeartRate = Integer.MAX_VALUE;
    averageHeartRate = 0;
    int samples = 0;
    for (WorkoutPoint point : track)
    {
      int hr = point.heartRate();
      
      if (hr >= 0)
      {
        maxHeartRate = Math.max(maxHeartRate, hr);
        minHeartRate = Math.min(minHeartRate, hr);
        averageHeartRate += hr;
        ++samples;
      }
    }
    
    if (samples > 0)
      averageHeartRate /= samples;
    else
    {
      maxHeartRate = 0;
      minHeartRate = 0;
    }
  }
  
  private void cacheAltitudeStatistics()
  {
    DoubleStream heights = track.stream().mapToDouble(p -> p.coordinate().alt()).filter(p -> !Double.isNaN(p));
    
    minAltitude = Double.MAX_VALUE;
    maxAltitude = Double.MIN_VALUE;
    
    AtomicDouble previous = new AtomicDouble(Double.NaN);
    altitudeSum = 0.0;
    climb = 0.0;
    
    heights.forEach(a -> {
      minAltitude = Math.min(minAltitude, a);
      maxAltitude = Math.min(maxAltitude, a);
      
      if (!Double.isNaN(previous.get()))
      {
        double p = previous.get();
        altitudeSum += Math.abs(p -  a);
        
        if (a - p > 0.0)
          climb += a - p;
      }
      
      previous.set(a);   
    });
    
    altitudeDifference = maxAltitude - minAltitude;
  }
  
  public Stream<WorkoutPoint> stream() { return track.stream(); }
  public WorkoutTrack track() { return track; }

  public ZonedDateTime start() { return start; }
  public ZonedDateTime end() { return end; }
  public TimeInterval lapse() { return lapse; }
  
  public double length()
  {
    if (Double.isNaN(length))
      length = track.totalLength();
    return length;
  }
  
  public double speed()
  {
    double minutes = lapse.asMinutes();
    return length() / (minutes / 60);
  }
  
  public double pace()
  {
    double minutes = lapse.asMinutes();
    return minutes / length();
    //TODO: this is in centesimals but it shouldn't
  }
  
  public TimeInterval paceAsTime()
  {
    double pace = pace();
    return TimeInterval.of(0, 0, 0, 0, (int)pace, (int)((pace - (int)pace)*60));
  }
  
  public double minAltitude()
  {
    if (Double.isNaN(minAltitude))
      cacheAltitudeStatistics();
    
    return minAltitude;
  }
  
  public double maxAltitude()
  {
    if (Double.isNaN(maxAltitude))
      cacheAltitudeStatistics();
    
    return maxAltitude;
  }
  
  public double altitudeDifference()
  {
    if (Double.isNaN(altitudeDifference))
      cacheAltitudeStatistics();
    
    return altitudeDifference;
  }
  
  public double altitudeSum()
  {
    if (Double.isNaN(altitudeSum))
      cacheAltitudeStatistics();
    
    return altitudeSum;
  }
  
  public double climb()
  {
    if (Double.isNaN(climb))
      cacheAltitudeStatistics();
    
    return climb;
  }
  
  public int averageHeartRate()
  {
    cacheHeartRateStatistics();
    
    return averageHeartRate;
  }
  
  public int minHeartRate()
  {
    if (maxHeartRate == -1)
      cacheHeartRateStatistics();
    
    return minHeartRate;
  }
  
  public int maxHeartRate()
  {
    if (maxHeartRate == -1)
      cacheHeartRateStatistics();
    
    return maxHeartRate;
  }
  
  public double calories()
  {
    if (Double.isNaN(calories))
      calories = caloriesCalculator.apply(this);
    return calories;
  }
}
