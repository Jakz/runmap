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

import com.github.jakz.runmap.data.Workout;
import com.github.jakz.runmap.data.WorkoutPoint;
import com.github.jakz.runmap.data.WorkoutTrack;
import com.github.jakz.runmap.jxmap.Sample2;
import com.github.jakz.runmap.jxmap.WorkoutMapPanel;
import com.github.jakz.runmap.parsers.FitParser;
import com.github.jakz.runmap.parsers.ParserRepository;
import com.github.jakz.runmap.ui.ChartPanel;
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
import com.pixbits.lib.ui.map.HeatMapPainter;
import com.pixbits.lib.ui.map.MapPanel;
import com.pixbits.lib.ui.map.PolylineElement;
import com.pixbits.lib.ui.map.PolylinePainter;
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
  
  private static final WorkoutCache cache = new WorkoutCache();
  private static final Mediator mediator = new MyMediator();
  
  private static WorkoutMapPanel mapPanel;
  
  private static WrapperFrame<ChartPanel> chartFrame;
  
  public static void setup() throws IOException, SAXException, JAXBException
  {
    ParserRepository parsers = new ParserRepository();
    FolderScanner scanner = new FolderScanner("glob:*.*", null, true);
    Set<Path> files = scanner.scan(Paths.get("data"));
    
    files = files.stream()
        .filter(parsers::canBeParsed)
        .collect(Collectors.toSet());

    System.out.printf("Found %d known files\n", files.size());
    
    List<Workout> tracks = files.stream()
      .limit(20)
      .map(p -> { System.out.println("Parsing "+p.toString()); return p; })
      .map(p -> {
        try
        {
          return parsers.parse(p);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          return new ArrayList<WorkoutTrack>();
        }
      })
      .flatMap(List::stream)
      .map(Workout::new)
      .sorted((w1, w2) -> w2.start().compareTo(w1.start()))
      .collect(Collectors.toList());
    
    List<Coordinate> points = tracks.stream()
      .map(s -> { System.out.println(s.start()+"  Length: "+s.length()+ " Altitude sum: "+s.altitudeSum()+" delta: "+s.altitudeDifference()+" climb: "+s.climb()); return s.track(); })
      .flatMap(WorkoutTrack::stream)
      .map(WorkoutPoint::coordinate)
      .collect(Collectors.toList());    
        
    System.out.printf("Loaded %d waypoints\n", points.size());

    GradientColorGenerator generator = new GradientColorGenerator(new Color(255,0,0), new Color(255,255,0));


    mapPanel = new WorkoutMapPanel();

    WrapperFrame<?> mapFrame = UIUtils.buildFrame(mapPanel, "Routes");
    mapFrame.exitOnClose();
    mapFrame.setVisible(true);
    
    Bounds totalBounds = new Bounds();
    tracks.forEach(track -> {
      List<Coordinate> pts = track.stream().map(WorkoutPoint::coordinate).collect(Collectors.toList());
      
      DouglasPeucker2D<Coordinate> simplify = new DouglasPeucker2D<>(new Coordinate[0], c -> c.lat() * 1000000, c -> c.lng()* Math.cos(c.lat()) * 1000000);
      pts = Arrays.asList(simplify.simplify(pts.toArray(new Coordinate[0]), 50, false));
      
      Bounds bounds = new Bounds(pts);
      PolylineElement route = mapPanel.routePainter().add(pts, java.awt.Color.RED);
      cache.addCachedRoute(track, route);
      cache.addBounds(track, bounds);
      totalBounds.updateBound(bounds);
      mapPanel.heatMapPainter().addData(pts);
    });
    
    mapPanel.zoomToFit(totalBounds, 0.7f);
    
    
    mapPanel.heatMapPainter().enabled = false;
    
    
    JPanel panel = UIUtils.buildFillPanel(new WorkoutTable(mediator, DataSource.of(tracks)), true);
    WrapperFrame<?> frame = UIUtils.buildFrame(panel, "Workouts");
    frame.exitOnClose();
    frame.setVisible(true);
    
    JPanel globalStats = UIUtils.buildFillPanel(new GlobalStatsTable(DataSource.of(tracks)), true);
    WrapperFrame<?> globalStatsFrame = UIUtils.buildFrame(globalStats, "Statistics");
    globalStatsFrame.setVisible(true);
    
    ChartPanel chartPanel = new ChartPanel();
    chartFrame = UIUtils.buildFrame(chartPanel, "Chart");
  }
  
  private static class MyMediator implements Mediator
  {
    Workout selection = null;
    
    @Override
    public void onWorkoutSelected(Workout workout) 
    {
      if (selection != null)
        cache.getRoute(selection).setWidth(1);
      
      selection = workout;
       
      
      chartFrame.panel().showForAltitude(workout);
      cache.getRoute(workout).setWidth(10);
      chartFrame.setVisible(true);
      
      mapPanel.zoomToFit(cache.getBounds(workout), 0.7f);
    }
    
  }
}
