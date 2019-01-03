package com.github.jakz.runmap;

import java.util.HashMap;
import java.util.Map;

import com.github.jakz.runmap.jxmap.MapPanel;
import com.github.jakz.runmap.jxmap.RoutePainter;
import com.pixbits.lib.io.xml.gpx.Bounds;

public class WorkoutCache
{
  private class Data
  {
    RoutePainter.CachedRoute gfxRoute;
    Bounds bounds;
  }
  
  private final Map<Workout, Data> cache;
  
  public WorkoutCache()
  {
    cache = new HashMap<>();
  }
  
  void addBounds(Workout workout, Bounds bounds)
  {
    Data data = cache.compute(workout, (wk, d) -> d != null ? d : new Data());
    data.bounds = bounds;
  }
  
  void addCachedRoute(Workout workout, RoutePainter.CachedRoute route)
  {
    Data data = cache.compute(workout, (wk, d) -> d != null ? d : new Data());
    data.gfxRoute = route;
  }
  
  Bounds getBounds(Workout workout) { return cache.get(workout).bounds; }
  RoutePainter.CachedRoute getRoute(Workout workout) { return cache.get(workout).gfxRoute; }
}
