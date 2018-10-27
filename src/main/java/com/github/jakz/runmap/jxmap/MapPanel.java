package com.github.jakz.runmap.jxmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class MapPanel extends JPanel
{
  JXMapViewer viewer;
  TileFactoryInfo info;
  
  private RoutePainter routePainter;
  private HeatMapPainter heatMapPainter;
  
  private final MouseListener mouseListener;
  
  public MapPanel()
  {
    viewer = new JXMapViewer();
    viewer.setPreferredSize(new Dimension(1000, 1000));
    
    info = new OSMTileFactoryInfo();
    DefaultTileFactory tileFactory = new DefaultTileFactory(info);
    viewer.setTileFactory(tileFactory);
    
    routePainter = new RoutePainter();
    
    heatMapPainter = new HeatMapPainter(0.0005, 0.0005 * 1.30);
    
    CompoundPainter<JXMapViewer> compositePainter = new CompoundPainter<>(heatMapPainter, routePainter);
    viewer.setOverlayPainter(compositePainter);

    mouseListener = new MouseListener();
    addMouseListener(mouseListener);
    addMouseWheelListener(mouseListener);
    addMouseMotionListener(mouseListener);
    
    setLayout(new BorderLayout());
    add(viewer, BorderLayout.CENTER);
  }
  
  public JXMapViewer viewer() { return viewer; }
  public RoutePainter routePainter() { return routePainter; }
  public HeatMapPainter heatMapPainter() { return heatMapPainter; }
  
  private class MouseListener extends MouseAdapter
  {
    Point2D.Float base;
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
      int dx = e.getWheelRotation();
      int z = viewer.getZoom();
      int min = info.getMinimumZoomLevel();
      int max = info.getMaximumZoomLevel();
      
      if (dx < 0)
      {
        viewer.setZoom(Math.max(min, z + dx));
        Rectangle bounds = viewer.getViewportBounds();
        viewer.setCenter(new Point2D.Float(bounds.x + e.getX(), bounds.y + e.getY()));
        routePainter.invalidate();
        heatMapPainter.invalidate();
      }
      else if (dx > 0)
      {
        viewer.setZoom(Math.min(max, z + dx));
        routePainter.invalidate();
        heatMapPainter.invalidate();
      }
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
      if (base != null)
      {
        double dx = e.getX() - base.getX();
        double dy = e.getY() - base.getY();
        
        Point2D center = viewer.getCenter();
        viewer.setCenter(new Point2D.Double(center.getX() - dx, center.getY() - dy));
      }
      
      base = new Point2D.Float(e.getX(), e.getY());
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
      base = null;
    }
  }
}