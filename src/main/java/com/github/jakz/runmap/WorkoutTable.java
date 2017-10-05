package com.github.jakz.runmap;

import java.time.ZonedDateTime;
import java.time.format.FormatStyle;

import javax.swing.JTable;

import com.pixbits.lib.io.xml.gpx.GpxWaypoint;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.DateTimeRenderer;
import com.pixbits.lib.ui.table.renderers.DistanceRenderer;
import com.pixbits.lib.ui.table.renderers.TimeIntervalRenderer;
import com.pixbits.lib.util.TimeInterval;

public class WorkoutTable extends JTable
{
  DataSource<Workout> data;
  TableModel<Workout> model;
  
  public WorkoutTable(DataSource<Workout> data)
  {
    this.data = data;
    this.model = new TableModel<>(this, data);

    ColumnSpec<Workout, ZonedDateTime> start = new ColumnSpec<>("Start", ZonedDateTime.class, w -> w.start());
    start.setRenderer(new DateTimeRenderer(true, true, FormatStyle.MEDIUM));
    model.addColumn(start);
    
    ColumnSpec<Workout, ZonedDateTime> end = new ColumnSpec<>("End", ZonedDateTime.class, w -> w.end());
    end.setRenderer(new DateTimeRenderer(false, true, FormatStyle.MEDIUM));
    model.addColumn(end);
    
    ColumnSpec<Workout, TimeInterval> lapse = new ColumnSpec<>("Lapse", TimeInterval.class, w -> w.lapse());
    lapse.setRenderer(new TimeIntervalRenderer(null));
    model.addColumn(lapse);
    
    ColumnSpec<Workout, Double> distance = new ColumnSpec<>("Distance", Double.class, w -> w.length());
    distance.setRenderer(new DistanceRenderer());
    model.addColumn(distance);
    
    ColumnSpec<Workout, Double> climb = new ColumnSpec<>("Climb", Double.class, w -> w.climb());
    climb.setRenderer(new DistanceRenderer(DistanceRenderer.Unit.MT));
    model.addColumn(climb);
    
    ColumnSpec<Workout, Integer> avgHR = new ColumnSpec<>("Average HR", Integer.class, w -> w.averageHeartRate());
    model.addColumn(avgHR);
    
    ColumnSpec<Workout, Integer> minHR = new ColumnSpec<>("Min HR", Integer.class, w -> w.minHeartRate());
    model.addColumn(minHR);
    
    ColumnSpec<Workout, Integer> maxHR = new ColumnSpec<>("Max HR", Integer.class, w -> w.maxHeartRate());
    model.addColumn(maxHR);
    
    this.setAutoCreateRowSorter(true);
  }
}
