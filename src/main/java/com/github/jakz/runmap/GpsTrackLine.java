package com.github.jakz.runmap;

import java.awt.Color;
import java.util.Collection;
import java.util.stream.Collectors;

import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;
import com.pixbits.lib.ui.color.ColorUtils;

public class GpsTrackLine 
{  
  /*private GpxTrackSegment track;
  private final Map map;
  
  private Color color;
  private float opacity;
  private float weight;
  
  public GpsTrackLine(Map map)
  {
    super(map);
    this.map = map;
    
    setVisible(false);
   
    opacity = 1.0f;
    weight = 2.0f;
    
    this.addEventListener("click", new MapMouseEvent() {
      @Override public void onEvent(MouseEvent event) { onClick(event); }
    });
  }
  
  public void setAndBuild(GpxTrackSegment segment)
  {
    setSegment(segment);
    rebuild();
  }
  
  public void setSegment(GpxTrackSegment segment, Color color)
  {
    this.track = segment;
    this.color = color;
  }
  
  public void setSegment(GpxTrackSegment track)
  {
    setSegment(track, track.color());
  }
  
  public void setWeight(float weight) { this.weight = weight; }
  public void setColor(Color color) { this.color = color; }
  public void setOpacity(float opacity) { this.opacity = opacity; }
  
  private void rebuild()
  {    
    LatLng[] points = track.points().stream()
        .map(c -> new LatLng(c.coordinate().lat(), c.coordinate().lng()))
        .toArray(i -> new LatLng[i]);
      
    setPath(points);
      
    PolylineOptions options = new PolylineOptions();
    options.setGeodesic(true);
    
    options.setStrokeColor(ColorUtils.colorToHex(color));
    options.setStrokeOpacity(opacity);
    options.setStrokeWeight(weight);
    setOptions(options);
  }
  
  private void onClick(MouseEvent event)
  {
    Coordinate coordinate = new Coordinate(event.latLng().getLat(), event.latLng().getLng());
  }
  
  public void build() { rebuild(); }
  
  public Map map() { return map; }
  
  public Collection<Coordinate> coordinates() 
  { 
    return track.points().stream()
      .map(GpxWaypoint::coordinate)
      .map(c -> new Coordinate(c))
      .collect(Collectors.toList());
  }*/
}
