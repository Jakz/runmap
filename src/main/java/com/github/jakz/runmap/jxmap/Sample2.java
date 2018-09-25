package com.github.jakz.runmap.jxmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

/**
 * A simple sample application that shows
 * a OSM map of Europe containing a route with waypoints
 * @author Martin Steiger
 */
public class Sample2
{
  public static void main(List<GeoPosition> points)
  {
    MapPanel panel = new MapPanel();
    // Display the viewer in a JFrame
    JFrame frame = new JFrame("JXMapviewer2 Example 2");
    frame.getContentPane().add(panel);
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    // Create a track from the geo-positions
    
    // Set the focus
    panel.viewer.zoomToBestFit(new HashSet<GeoPosition>(points), 0.7);

    // Create waypoints from the geo-positions
    /*Set<Waypoint> waypoints = new HashSet<Waypoint>(Arrays.asList(
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

    CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);*/
  }
}