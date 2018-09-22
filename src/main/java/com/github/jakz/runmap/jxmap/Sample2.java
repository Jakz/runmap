package com.github.jakz.runmap.jxmap;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import com.pixbits.lib.lang.Point;


/**
 * A simple sample application that shows
 * a OSM map of Europe containing a route with waypoints
 * @author Martin Steiger
 */
public class Sample2
{
  /**
   * @param args the program args (ignored)
   */
  
  static class MapPanel extends JPanel
  {
    JXMapViewer viewer;
    TileFactoryInfo info;
    
    private final MouseListener mouseListener;
    
    public MapPanel()
    {
      viewer = new JXMapViewer();
      
      info = new OSMTileFactoryInfo();
      DefaultTileFactory tileFactory = new DefaultTileFactory(info);
      viewer.setTileFactory(tileFactory);
      
      mouseListener = new MouseListener();
      addMouseListener(mouseListener);
      addMouseWheelListener(mouseListener);
      addMouseMotionListener(mouseListener);
      
      setLayout(new BorderLayout());
      add(viewer, BorderLayout.CENTER);
    }
    
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
          viewer.setZoom(Math.max(min, z + dx));
        else if (dx > 0)
          viewer.setZoom(Math.min(max, z + dx));     
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
  
  
  public static void main(String[] args)
  {
    MapPanel panel = new MapPanel();
    // Display the viewer in a JFrame
    JFrame frame = new JFrame("JXMapviewer2 Example 2");
    frame.getContentPane().add(panel);
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);





    GeoPosition frankfurt = new GeoPosition(50,  7, 0, 8, 41, 0);
    GeoPosition wiesbaden = new GeoPosition(50,  5, 0, 8, 14, 0);
    GeoPosition mainz     = new GeoPosition(50,  0, 0, 8, 16, 0);
    GeoPosition darmstadt = new GeoPosition(49, 52, 0, 8, 39, 0);
    GeoPosition offenbach = new GeoPosition(50,  6, 0, 8, 46, 0);

    // Create a track from the geo-positions
    List<GeoPosition> track = Arrays.asList(frankfurt, wiesbaden, mainz, darmstadt, offenbach);
    RoutePainter routePainter = new RoutePainter(track);

    // Set the focus
    panel.viewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

    // Create waypoints from the geo-positions
    Set<Waypoint> waypoints = new HashSet<Waypoint>(Arrays.asList(
        new DefaultWaypoint(frankfurt),
        new DefaultWaypoint(wiesbaden),
        new DefaultWaypoint(mainz),
        new DefaultWaypoint(darmstadt),
        new DefaultWaypoint(offenbach)));

    // Create a waypoint painter that takes all the waypoints
    WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
    waypointPainter.setWaypoints(waypoints);

    // Create a compound painter that uses both the route-painter and the waypoint-painter
    List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
    painters.add(routePainter);
    painters.add(waypointPainter);

    CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
    panel.viewer.setOverlayPainter(painter);
  }
}