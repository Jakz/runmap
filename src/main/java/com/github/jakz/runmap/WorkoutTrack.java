package com.github.jakz.runmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class WorkoutTrack implements Iterable<WorkoutPoint>
{
  private List<WorkoutPoint> points;
  
  private double[] distanceCache;
  private double totalLength = Double.NaN;
  
  public WorkoutTrack(List<WorkoutPoint> points)
  {
    this.points = points;
  }
  
  public WorkoutTrack()
  {
    points = new ArrayList<>();
  }

  public double distanceBetweenPoints(int index)
  {
    if (distanceCache == null)
    {
      distanceCache = new double[points.size()];
      Arrays.fill(distanceCache, Double.NaN);
    }
    
    if (Double.isNaN(distanceCache[index]))
      distanceCache[index] = points.get(index).coordinate().distance(points.get(index+1).coordinate());
        
    return distanceCache[index];
  }
  
  public double totalLength()
  {
    if (!Double.isNaN(totalLength))
      return totalLength;
    
    totalLength = 0.0;
    for (int i = 0; i < points.size() - 1; ++i)
      totalLength += distanceBetweenPoints(i);
    
    return totalLength;
  }
  
  public WorkoutPoint get(int index) { return points.get(index); }
  public int size() { return points.size(); }
  
  public Stream<WorkoutPoint> stream() { return points.stream(); }
  public Iterator<WorkoutPoint> iterator() { return points.iterator(); }
}
