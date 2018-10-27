package com.github.jakz.runmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import org.jxmapviewer.viewer.GeoPosition;
import org.xml.sax.SAXException;

import com.github.jakz.runmap.jxmap.MapPanel;
import com.github.jakz.runmap.jxmap.Sample2;
import com.github.jakz.runmap.ui.GlobalStatsTable;
import com.github.jakz.runmap.ui.WorkoutTable;
import com.pixbits.lib.algorithm.DouglasPeucker2D;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.io.xml.gpx.Bounds;
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
import com.pixbits.lib.ui.table.DataSource;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args )
  {
    UIUtils.setNimbusLNF();
    
    if (false)
    {
    
    try
    {
      Gpx gpx = GpxParser.parse(Paths.get("/Users/jack/Desktop/Fix.gpx"));
      GpxTrackSegment segment = gpx.tracks().get(0).segments().get(0);
      
      List<GpxWaypoint> points = segment.points();
      System.out.printf("Loaded a track with %d points\n", segment.size());
      ZonedDateTime zonedTime = points.get(0).time();
      ZonedDateTime finalTime = points.get(points.size()-1).time();
      ZonedDateTime last = zonedTime;
      
      //ZonedDateTime finalTime = zonedTime.plusMinutes(89);
      //ZonedDateTime last = points.get(points.size()-1).time();
      //int lastValid = points.size()-1;
      
      //while (true)
      //{
        //if (!last.equals(points.get(lastValid).time()))
        //  break;
        
        //--lastValid;
      //}
 
      //++lastValid;
      
      int lastValid = 0;  
      
      double totalDistance = 0.0f;
      for (int i = lastValid; i < points.size()-1; ++i)
        totalDistance += segment.distanceBetweenPoints(i);
      long totalSeconds = ChronoUnit.SECONDS.between(last, finalTime);
      
      System.out.printf("Found %d points to average on a distance %s in a time range of %f minutes\n",
          (points.size()-lastValid-1),
          String.format("%.2fkm", totalDistance),
          totalSeconds / 60.0f       
      );
      
      double distanceSum = 0.0f;
      for (int i = lastValid; i < points.size()-1; ++i)
      {
        distanceSum += segment.distanceBetweenPoints(i);
        double percent = distanceSum / totalDistance;
        int deltaSeconds = (int)(percent * totalSeconds);
        ZonedDateTime oldTime = points.get(i+1).time();
        points.get(i+1).setTime(last.plusSeconds(deltaSeconds));
        System.out.println("Adjusted timestamp from "+oldTime+" to "+points.get(i+1).time());
      }
      
      GpxParser.save(gpx, Paths.get("/Users/jack/Desktop/test2.gpx"));

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    if (true)
      return;
    
    }

    try
    {
      setup();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
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
  
  public static void setup() throws IOException, SAXException, JAXBException
  {
    FolderScanner scanner = new FolderScanner("glob:*.{gpx,fit}", null, true) ;
    Set<Path> files = scanner.scan(Paths.get("data"));
    
    System.out.printf("Found %d known files\n", files.size());
    
    List<Workout> tracks = files.stream()
      //.limit(1)
      .map(p -> { System.out.println("Parsing "+p.toString()); return p; })
      .map(StreamException.rethrowFunction(p -> { 
        if (FileUtils.pathExtension(p).equals("fit"))
          return new FitParser().parse(p);
        else
          return GpxParser.parse(p);        
      }))
      .flatMap(Gpx::stream)
      .flatMap(GpxTrack::stream)
      .map(Workout::new)
      .sorted((w1, w2) -> w2.start().compareTo(w1.start()))
      .collect(Collectors.toList());
    
    List<Coordinate> points = tracks.stream()
      .map(s -> { System.out.println(s.start()+"  Length: "+s.length()+ " Altitude sum: "+s.altitudeSum()+" delta: "+s.altitudeDifference()+" climb: "+s.climb()); return s.gpx(); })
      .flatMap(GpxTrackSegment::stream)
      .map(GpxWaypoint::coordinate)
      .collect(Collectors.toList());    
        
    System.out.printf("Loaded %d waypoints\n", points.size());

    
    //map.setCenter(new LatLng(43.780582, 11.296338));
    //map.setZoom(16.0);
    
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
    
    /*
    for (Workout track : tracks)
    {
      GpsTrackLine line = new GpsTrackLine(map);
      line.setSegment(track.gpx());
      line.setWeight(3.0f);
      line.setOpacity(0.6f);
      line.setVisible(true);
      line.setColor(java.awt.Color.RED);
      line.build();
    }
    */

    MapPanel mapPanel = new MapPanel();
    WrapperFrame<?> mapFrame = UIUtils.buildFrame(mapPanel, "Routes");
    mapFrame.exitOnClose();
    mapFrame.setVisible(true);
    
    Bounds bounds = new Bounds();
    tracks.forEach(track -> {
      List<Coordinate> pts = track.gpx().stream().map(GpxWaypoint::coordinate).collect(Collectors.toList());
      
      DouglasPeucker2D<Coordinate> simplify = new DouglasPeucker2D<>(new Coordinate[0], c -> c.lat() * 1000000, c -> c.lng()* Math.cos(c.lat()) * 1000000);
      pts = Arrays.asList(simplify.simplify(pts.toArray(new Coordinate[0]), 50, false));
      
      bounds.updateBound(pts);
      mapPanel.routePainter().add(pts, java.awt.Color.RED);
      mapPanel.heatMapPainter().addData(pts);
    });
    
    mapPanel.viewer().zoomToBestFit(
        Arrays.asList(new Coordinate[] { bounds.ne(), bounds.sw() })
          .stream()
          .map(c -> new GeoPosition(c.lat(), c.lng()))
          .collect(Collectors.toSet()
        ), 0.7);
    
    

    
    
    JPanel panel = UIUtils.buildFillPanel(new WorkoutTable(DataSource.of(tracks)), true);
    WrapperFrame<?> frame = UIUtils.buildFrame(panel, "Workouts");
    frame.exitOnClose();
    frame.setVisible(true);
    
    JPanel globalStats = UIUtils.buildFillPanel(new GlobalStatsTable(DataSource.of(tracks)), true);
    WrapperFrame<?> globalStatsFrame = UIUtils.buildFrame(globalStats, "Statistics");
    globalStatsFrame.setVisible(true);
  }
}
