package com.github.jakz.runmap.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;

import javax.swing.JTable;

import com.github.jakz.runmap.Mediator;
import com.github.jakz.runmap.data.Workout;
import com.pixbits.lib.io.xml.gpx.GpxWaypoint;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.DateTimeRenderer;
import com.pixbits.lib.ui.table.renderers.DistanceRenderer;
import com.pixbits.lib.ui.table.renderers.LambdaLabelTableRenderer;
import com.pixbits.lib.ui.table.renderers.TimeIntervalRenderer;
import com.pixbits.lib.util.TimeInterval;

public class WorkoutTable extends JTable
{
  private Mediator mediator;
  
  DataSource<Workout> data;
  TableModel<Workout> model;
  
  public WorkoutTable(Mediator mediator, DataSource<Workout> data)
  {
    this.mediator = mediator;
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
    
    ColumnSpec<Workout, Double> speed = new ColumnSpec<>("Speed", Double.class, w -> w.speed());
    speed.setRenderer(new LambdaLabelTableRenderer<>((v,l) -> l.setText(String.format("%2.2f km/h", v))));
    model.addColumn(speed);
    
    ColumnSpec<Workout, TimeInterval> pace = new ColumnSpec<>("Pace", TimeInterval.class, w -> w.paceAsTime());
    pace.setRenderer(new TimeIntervalRenderer(null));
    model.addColumn(pace);
    
    ColumnSpec<Workout, Double> climb = new ColumnSpec<>("Climb", Double.class, w -> w.climb());
    climb.setRenderer(new DistanceRenderer(DistanceRenderer.Unit.MT));
    model.addColumn(climb);
    
    ColumnSpec<Workout, Integer> avgHR = new ColumnSpec<>("Average HR", Integer.class, w -> w.averageHeartRate());
    model.addColumn(avgHR);
    
    ColumnSpec<Workout, Integer> minHR = new ColumnSpec<>("Min HR", Integer.class, w -> w.minHeartRate());
    model.addColumn(minHR);
    
    ColumnSpec<Workout, Integer> maxHR = new ColumnSpec<>("Max HR", Integer.class, w -> w.maxHeartRate());
    model.addColumn(maxHR);
    
    ColumnSpec<Workout, Integer> calories = new ColumnSpec<>("Kcal", Integer.class, w -> (int)w.calories());
    model.addColumn(calories);
    
    this.setAutoCreateRowSorter(true);
    
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent mouseEvent) 
      {
          JTable table = (JTable)mouseEvent.getSource();
          Point point = mouseEvent.getPoint();
          int row = table.rowAtPoint(point);
          if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) 
          {
            int rrow = table.convertRowIndexToModel(row);
            mediator.onWorkoutSelected(data.get(rrow));
          }
      }
    });
  }
}
