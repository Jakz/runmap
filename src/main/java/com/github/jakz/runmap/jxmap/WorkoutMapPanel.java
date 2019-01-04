package com.github.jakz.runmap.jxmap;

import com.pixbits.lib.ui.map.HeatMapPainter;
import com.pixbits.lib.ui.map.MapPanel;
import com.pixbits.lib.ui.map.PolylinePainter;

public class WorkoutMapPanel extends MapPanel
{
  PolylinePainter routePainter = new PolylinePainter(this);
  HeatMapPainter heatMapPainter = new HeatMapPainter(0.0006, 0.0006, true);
  
  public WorkoutMapPanel()
  {
    super(1000, 1000);
    addPainter(routePainter);
    addPainter(heatMapPainter);
  }
  
  public PolylinePainter routePainter() { return routePainter; }
  public HeatMapPainter heatMapPainter() { return heatMapPainter; }
  
  
}