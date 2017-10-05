package com.github.jakz.runmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.LatLngBounds;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.MapViewOptions;
import com.teamdev.jxmaps.Rectangle;
import com.teamdev.jxmaps.RectangleOptions;
import com.teamdev.jxmaps.swing.MapView;

public class MapPanel extends JPanel
{
  public final MapView view;
  public Map map; // TODO: should be private
  private boolean ready;
  
  private Consumer<Map> callback = m -> {};
  
  public MapPanel()
  {
    this(m -> {});
  }
  
  public MapPanel(Consumer<Map> callback) 
  {   
    this.callback = callback;
    ready = false;
    MapViewOptions viewOptions = new MapViewOptions();
    viewOptions.setApiKey("AIzaSyDZgDPjmu4dI-izRMQFzTX5Qpa2aWFirgo");
    
    view = new MapView(viewOptions);
    view.setOnMapReadyHandler(status -> {
      // Check if the map is loaded correctly
      if (status == MapStatus.MAP_STATUS_OK) 
      {
        map = view.getMap();
        
        MapTypeControlOptions controlOptions = new MapTypeControlOptions();
        controlOptions.setPosition(ControlPosition.TOP_RIGHT);
        
        MapOptions options = new MapOptions();
        options.setMapTypeControlOptions(controlOptions);        
        map.setOptions(options);

        callback.accept(map);       
        ready = true;    
      }
    });
    
    setPreferredSize(new Dimension(800,800));
    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
  }
  
  public void setCallback(Consumer<Map> callback)
  {
    this.callback = callback;
  }
}