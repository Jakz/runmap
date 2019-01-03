package com.github.jakz.runmap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.github.jakz.runmap.Workout;
import com.pixbits.lib.io.xml.gpx.Coordinate;
import com.pixbits.lib.io.xml.gpx.GpxTrackSegment;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;

public class ChartPanel extends JPanel
{
  private XYChart chart;
  private XChartPanel<?> panel;
  
  public ChartPanel()
  {
    setPreferredSize(new Dimension(400, 400));
    setMinimumSize(new Dimension(400,400));
    setLayout(new BorderLayout());
  }
  
  public void showForAltitude(Workout workout)
  {
    GpxTrackSegment gpx = workout.gpx();
    
    List<Date> timestamps = gpx.stream().map(GpxWaypoint::time).map(zdt -> Date.from(zdt.toInstant())).collect(Collectors.toList());
    List<Double> altitudes = gpx.stream().map(GpxWaypoint::coordinate).map(Coordinate::alt).collect(Collectors.toList());
    
    chart = new XYChartBuilder().width(400).height(400).title("Altitude").xAxisTitle("X").yAxisTitle("Y").build();
       
    XYSeries series = chart.addSeries("Fake Data", timestamps, altitudes);
    series.setLineColor(XChartSeriesColors.BLUE);
    series.setMarkerColor(Color.ORANGE);
    series.setMarker(SeriesMarkers.CIRCLE);
    series.setLineStyle(SeriesLines.SOLID);
    
    panel = new XChartPanel<>(chart);
    add(panel, BorderLayout.CENTER);
    
    invalidate();
  }
}
