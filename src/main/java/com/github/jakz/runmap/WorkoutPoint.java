package com.github.jakz.runmap;

import java.time.ZonedDateTime;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.io.xml.gpx.GpxExtension;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;

public class WorkoutPoint
{
  private ZonedDateTime timestamp;
  private Coordinate coordinate;
  private int heartRate;
  
  public WorkoutPoint(ZonedDateTime timestamp, Coordinate coordinate, int heartRate)
  {
    this.timestamp = timestamp;
    this.coordinate = coordinate;
    this.heartRate = heartRate;
  }
  
  public WorkoutPoint(ZonedDateTime timestamp, Coordinate coordinate)
  {
    this(timestamp, coordinate, -1);
  }
  
  public WorkoutPoint(GpxWaypoint gpx)
  {
    this(gpx.time(), gpx.coordinate());
    
    GpxExtension root = gpx.extensions();
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
              heartRate = Integer.valueOf(text);
            }
          }
        }
      }
    }
  }
  
  public ZonedDateTime time() { return timestamp; }
  public Coordinate coordinate() { return coordinate; }
  public int heartRate() { return heartRate; }
}
