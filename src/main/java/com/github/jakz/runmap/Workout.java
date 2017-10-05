package com.github.jakz.runmap;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;
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
  
  private int averageHeartRate = -1;
  private int maxHeartRate = -1;
  private int minHeartRate = -1;
  
  private GpxTrackSegment gpx;
  private int[] heartRates;
  
  public Workout(GpxTrackSegment gpx)
  {
    if (gpx.points().size() < 2)
      throw new IllegalArgumentException("a workout requires a GpxTrackSegment with at least 2 points");

    GpxWaypoint start = gpx.points().get(0);
    GpxWaypoint end = gpx.points().get(gpx.points().size()-1);
    
    this.gpx = gpx;
    this.start = start.time();
    this.end = end.time();
    this.lapse = TimeInterval.of(start.time(), end.time());
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
}
