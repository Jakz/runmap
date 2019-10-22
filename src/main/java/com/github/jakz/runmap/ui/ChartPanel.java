package com.github.jakz.runmap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.github.jakz.runmap.data.Workout;
import com.github.jakz.runmap.data.WorkoutPoint;
import com.github.jakz.runmap.data.WorkoutTrack;
import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;

public class ChartPanel extends JPanel
{
  private enum Mode
  {
    ALTITUDE("Altitude"),
    HEART_RATE("Heart Rate")
    
    ;
    
    public final String caption;
    
    private Mode(String caption) { this.caption = caption; } 
    
    @Override public String toString() { return caption; }
  }
  
  private XYChart chart;
  private XChartPanel<?> panel;
  private JComboBox<Mode> modeComboBox;
  
  private Workout workout;
  
  
  public ChartPanel()
  {
    modeComboBox = new JComboBox<>(Mode.values());
    
    modeComboBox.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        Mode mode = modeComboBox.getItemAt(modeComboBox.getSelectedIndex());
        
        if (mode == Mode.ALTITUDE)
          showForAltitude(workout);
        else if (mode == Mode.HEART_RATE)
          showForHeartRate(workout);
      }
    });
    
    JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    header.add(modeComboBox);
    
    
    setPreferredSize(new Dimension(400, 400));
    setMinimumSize(new Dimension(400,400));
    setLayout(new BorderLayout());
    
    add(header, BorderLayout.NORTH);
  }
  
  public void showForAltitude(Workout workout)
  {
    this.workout = workout;
    WorkoutTrack track = workout.track();
    List<Date> timestamps = track.stream().map(WorkoutPoint::time).map(zdt -> Date.from(zdt.toInstant())).collect(Collectors.toList());
    List<Double> altitudes = track.stream().map(WorkoutPoint::coordinate).map(Coordinate::alt).collect(Collectors.toList());

    
    chart = new XYChartBuilder().width(400).height(400).title("Altitude").xAxisTitle("time").yAxisTitle("meters").build();   
    
    XYSeries series = chart.addSeries("Altitude", timestamps, altitudes);
    series.setLineColor(XChartSeriesColors.GREEN);
    //series.setMarkerColor(Color.ORANGE);
    series.setMarker(SeriesMarkers.NONE);
    series.setLineStyle(SeriesLines.SOLID);
    
    if (panel != null)
      remove(panel);
    panel = new XChartPanel<>(chart);
       
    add(panel, BorderLayout.CENTER);
    
    
    repaint();
  }
  
  public void showForHeartRate(Workout workout)
  {
    this.workout = workout;
    WorkoutTrack track = workout.track();   
    List<Date> timestamps = track.stream().map(WorkoutPoint::time).map(zdt -> Date.from(zdt.toInstant())).collect(Collectors.toList());
    List<Integer> heartRates = track.stream().map(WorkoutPoint::heartRate).collect(Collectors.toList());
    
    chart = new XYChartBuilder().width(400).height(400).title("Heart Rate").xAxisTitle("time").yAxisTitle("beats/min").build();   
    
    XYSeries series = chart.addSeries("Heart Rate", timestamps, heartRates);
    series.setLineColor(XChartSeriesColors.RED);
    //series.setMarkerColor(Color.ORANGE);
    series.setMarker(SeriesMarkers.NONE);
    series.setLineStyle(SeriesLines.SOLID);

    if (panel != null)
      remove(panel);
    panel = new XChartPanel<>(chart);
       
    add(panel, BorderLayout.CENTER);
    
    repaint();
  }
}
