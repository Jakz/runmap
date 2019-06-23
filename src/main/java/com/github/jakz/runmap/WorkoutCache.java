package com.github.jakz.runmap;

import java.util.HashMap;
import java.util.Map;

import com.github.jakz.runmap.data.Workout;
import com.pixbits.lib.io.xml.gpx.Bounds;
import com.pixbits.lib.ui.map.MapPanel;
import com.pixbits.lib.ui.map.PolylineElement;

public class WorkoutCache
{
  private class Data
  {
    PolylineElement gfxRoute;
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
  
  void addCachedRoute(Workout workout, PolylineElement route)
  {
    Data data = cache.compute(workout, (wk, d) -> d != null ? d : new Data());
    data.gfxRoute = route;
  }
  
  Bounds getBounds(Workout workout) { return cache.get(workout).bounds; }
  PolylineElement getRoute(Workout workout) { return cache.get(workout).gfxRoute; }
}
