package com.github.jakz.runmap.jxmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.color.ColorGenerator;
import com.pixbits.lib.ui.color.PastelColorGenerator;
import com.pixbits.lib.ui.color.PleasantColorGenerator;

import org.jxmapviewer.painter.Painter;

/**
 * Paints a route
 * @author Martin Steiger
 */
public class RoutePainter implements Painter<JXMapViewer>
{
  private class CachedRoute
  {
    List<Coordinate> track;
    Color color;
    Path2D pointCache;
    
    CachedRoute(List<Coordinate> track, Color color)
    {
      this.track = track;
      this.color = color;
    }
  }
  
  private boolean antiAlias = true;

  private final List<CachedRoute> tracks;


  public RoutePainter()
  {
    this.tracks = new ArrayList<>();
  }

  ColorGenerator generator = new PleasantColorGenerator();
  public void add(List<Coordinate> points, Color color)
  {
    tracks.add(new CachedRoute(points, generator.getColor()));
  }

  @Override
  public void paint(Graphics2D g, JXMapViewer map, int w, int h)
  {
    g = (Graphics2D) g.create();

    // convert from viewport to world bitmap
    Rectangle rect = map.getViewportBounds();
    g.translate(-rect.x, -rect.y);

    if (antiAlias)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    drawRoute(g, map);

    g.dispose();
  }
  
  public void generatePolyLine(Graphics2D g, JXMapViewer map, CachedRoute track)
  {
    track.pointCache = new Path2D.Double();
    
    boolean first = true;
    for (int i = 0; i < track.track.size(); ++i)
    {
      Coordinate point = track.track.get(i);
      GeoPosition gpoint = new GeoPosition(point.lat(), point.lng());
      Point2D pt = map.getTileFactory().geoToPixel(gpoint, map.getZoom());
      
      if (first)
        track.pointCache.moveTo(pt.getX(), pt.getY());
      else
        track.pointCache.lineTo(pt.getX(), pt.getY());
      
      first = false;
    }
  }

  private void drawPolyLine(Graphics2D g, JXMapViewer map, CachedRoute track)
  {
    if (track.pointCache == null)
      generatePolyLine(g, map,track);
    
    g.draw(track.pointCache);
    
    /*int[][] cache = track.pointCache;
    for (int i = 0; i < cache[0].length-1; ++i)
    {
      System.out.printf("%d,%d -- %d %d\n", cache[0][i], cache[1][i], cache[0][i+1], cache[1][i+1]);
      g.drawLine(cache[0][i], cache[1][i], cache[0][i+1], cache[1][i+1]);
    }*/
    
    /*int lastX = 0;
    int lastY = 0;
    boolean first = true;

    for (int i = 0; i < track.track.size(); ++i)
    {
      // convert geo-coordinate to world bitmap pixel
      Coordinate point = track.track.get(i);
      GeoPosition gp = new GeoPosition(point.lat(), point.lng());
      Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

      if (first)
      {
        first = false;
      }
      else
      {
        g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
      }

      lastX = (int) pt.getX();
      lastY = (int) pt.getY();
    }*/
  }
  
  public void invalidate()
  {
    tracks.forEach(track -> track.pointCache = null);
  }

  /**
   * @param g the graphics object
   * @param map the map
   */
  private void drawRoute(Graphics2D g, JXMapViewer map)
  {
    for (CachedRoute track : tracks)
    {
      Color color = new Color(track.color.getRed(), track.color.getGreen(), track.color.getBlue(), 180);

      g.setColor(color);
      g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      drawPolyLine(g, map, track);

    }
  }
}