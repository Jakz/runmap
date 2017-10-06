package com.github.jakz.runmap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Function;
import java.util.stream.DoubleStream;

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
  
  private GpxTrackSegment gpx;
  private int[] heartRates;
  
  private Function<Workout, Double> caloriesCalculator;
  
  public Workout(GpxTrackSegment gpx)
  {
    if (gpx.points().size() < 2)
      throw new IllegalArgumentException("a workout requires a GpxTrackSegment with at least 2 points");

    GpxWaypoint start = gpx.points().get(0);
    GpxWaypoint end = gpx.points().get(gpx.points().size()-1);
    
    this.gpx = gpx;
    this.start = start.time().withZoneSameInstant(ZoneId.systemDefault());
    this.end = end.time().withZoneSameInstant(ZoneId.systemDefault());
    this.lapse = TimeInterval.of(start.time(), end.time());
    
    this.caloriesCalculator = w -> {
      double base = w.length()*58.0f;
      double addendum = 0.0;
      
      if (!Double.isNaN(gpx.get(0).coordinate().alt()))
      {
        double strainLength = 0.0;
        double strainAltitude = 0.0;
        
        double lastAltitude = Double.NaN;
        
        for (int i = 0; i < gpx.size(); ++i)
        {
          GpxWaypoint p = gpx.get(i);
          
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
              strainLength += gpx.distanceBetweenPoints(i-1);
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
    int j = 0;
    heartRates = new int[gpx.size()];
    
    for (GpxWaypoint p : gpx)
    {
      int hr = -1;
      
      GpxExtension root = p.extensions();
      if (root != null)
      {
        for (Element element : root.getExtensions())
        {
          if (element.getNodeName().equals("gpxtpx:TrackPointExtension"))
          {
            NodeList gpxExtensions = element.getChildNodes();
            for (int i = 0; i < gpxExtensions.getLength(); ++i)
            {
              Node child = gpxExtensions.item(i);
              if (child.getNodeName().equals("gpxtpx:hr"))
              {
                String text = child.getTextContent();
                hr = Integer.valueOf(text);
              }
            }
          }
        }
      }
      
      heartRates[j] = hr;
      ++j;
    }
    
    maxHeartRate = Integer.MIN_VALUE;
    minHeartRate = Integer.MAX_VALUE;
    averageHeartRate = 0;
    int samples = 0;
    for (int hr : heartRates)
    {
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
    DoubleStream heights = gpx.stream().mapToDouble(p -> p.coordinate().alt()).filter(p -> !Double.isNaN(p));
    
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
  
  public GpxTrackSegment gpx() { return gpx; }

  public ZonedDateTime start() { return start; }
  public ZonedDateTime end() { return end; }
  public TimeInterval lapse() { return lapse; }
  
  public double length()
  {
    if (Double.isNaN(length))
      length = gpx.totalLength();
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
    if (heartRates == null)
      cacheHeartRateStatistics();
    
    return averageHeartRate;
  }
  
  public int minHeartRate()
  {
    if (heartRates == null)
      cacheHeartRateStatistics();
    
    return minHeartRate;
  }
  
  public int maxHeartRate()
  {
    if (heartRates == null)
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
