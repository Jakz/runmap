package com.github.jakz.runmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.io.xml.gpx.Gpx;
import com.pixbits.lib.io.xml.gpx.GpxParser;
import com.pixbits.lib.io.xml.gpx.GpxTrack;
import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.color.Color;
import com.pixbits.lib.ui.color.GradientColorGenerator;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.Rectangle;
import com.teamdev.jxmaps.RectangleOptions;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args )
  {
    UIUtils.setNimbusLNF();
    
    Consumer<Map> callback = map -> {
      try
      {
        App.setup(map);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    };
    
    MapPanel panel = new MapPanel(callback);

    WrapperFrame<?> frame = UIUtils.buildFrame(panel, "Map");
    frame.exitOnClose();
    frame.setVisible(true);
  }
  
  
  public static class Zone
  {
    private int x, y;
    
    public Zone(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    
    public int hashCode() { return Long.hashCode(x << 32 | y); }
    public boolean equals(Object o) { return (o instanceof Zone) && ((Zone)o).x == x && ((Zone)o).y == y; }
  }
  
  public static void setup(Map map) throws IOException, SAXException, JAXBException
  {
    FolderScanner scanner = new FolderScanner("glob:*.gpx", null, true);
    Set<Path> files = scanner.scan(Paths.get("data"));
    
    System.out.printf("Found %d gpx files\n", files.size());
    
    List<GpxTrackSegment> tracks = files.stream()
      .map(p -> { System.out.println("Parsing "+p.toString()); return p; })
      .map(StreamException.rethrowFunction(GpxParser::parse))
      .flatMap(Gpx::stream)
      .flatMap(GpxTrack::stream)
      .collect(Collectors.toList());
    
    List<Coordinate> points = tracks.stream()
      .map(s -> { System.out.println("Total length: "+s.totalLength()); return s; })
      .flatMap(GpxTrackSegment::stream)
      .map(GpxWaypoint::coordinate)
      .collect(Collectors.toList());    
        
    System.out.printf("Loaded %d waypoints\n", points.size());

    final double zoneWidth = 0.0002;// 0.0002;// 0.0005;
    final double zoneHeight = zoneWidth * 0.76;
    java.util.Map<Zone, Integer> heatMap = new HashMap<>();
    
    if ((1 / zoneWidth)*180.0 > Integer.MAX_VALUE || (1 / zoneHeight)*180.0 > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Zone is too small to be calculated");
    
    for (Coordinate p : points)
    {
      int zoneX = (int)(p.lng() / zoneWidth);
      int zoneY = (int)(p.lat() / zoneHeight);
      
      Zone zone = new Zone(zoneX, zoneY);
      heatMap.compute(zone, (z, i) -> i == null ? 1 : i + 1);
    }

    System.out.printf("Computed %d zones\n", heatMap.size());
    float max = heatMap.values().stream().mapToInt(i -> i).max().getAsInt()+1;
    
    map.setCenter(new LatLng(43.780582, 11.296338));
    map.setZoom(16.0);
    
    GradientColorGenerator generator = new GradientColorGenerator(new Color(255,0,0), new Color(255,255,0));
    
    /*for (java.util.Map.Entry<Zone, Integer> entry : heatMap.entrySet())
    {
      Zone zone = entry.getKey();
      
      Rectangle rectangle = new Rectangle(map);
      RectangleOptions roptions = new RectangleOptions();
      roptions.setStrokeWeight(0.0);
      roptions.setFillColor(generator.getColor(entry.getValue()/max).toCSS());
      roptions.setFillOpacity(0.6);
      rectangle.setOptions(roptions);
      
      double baseX = zone.x()*zoneWidth, baseY = zone.y()*zoneHeight;
      rectangle.setBounds(new LatLngBounds(new LatLng(baseY, baseX), new LatLng(baseY + zoneHeight, baseX + zoneWidth)));
    }*/
    
    for (GpxTrackSegment track : tracks)
    {
      GpsTrackLine line = new GpsTrackLine(map);
      line.setSegment(track);
      line.setWeight(2.0f);
      line.setOpacity(0.6f);
      line.setVisible(true);
    }
    

  }
}
