package com.github.jakz.runmap.ui;

import java.util.function.Supplier;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.github.jakz.runmap.data.Workout;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.util.TimeInterval;

public class GlobalStatsTable extends JTable
{

  
  private class Model extends AbstractTableModel
  {
    @Override
    public int getRowCount()
    {
      return titles.length;
    }

    @Override
    public int getColumnCount()
    {
      return 2;
    }
    
    @Override 
    public String getColumnName(int c)
    {
      return c == 0 ? "Name" : "Value";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValueAt(int row, int col)
    {
      if (col == 0)
        return titles[row];
      else
        return ((Supplier<String>)lambdas[row]).get();
    }
  }
  
  private class Data
  {
    double totalDistance;
    double longestDistance;
    TimeInterval totalTime;
  }
  
  private Model model;
  private DataSource<Workout> workouts;
  private Data data;
  
  public GlobalStatsTable(DataSource<Workout> workouts)
  {
    this.setModel(new Model());
    this.workouts = workouts;
    this.data = new Data();
    computeStats();
  }
  
  private void computeStats()
  {
    data.totalDistance = workouts.stream().mapToDouble(Workout::length).sum();
    data.longestDistance = workouts.stream().mapToDouble(Workout::length).max().getAsDouble();
    data.totalTime = workouts.stream().map(Workout::lapse).reduce(TimeInterval.zero(), TimeInterval::sum);
  }
  
  private final String[] titles = new String[] {
      "Amount",
      "Total Distance",
      "Longest Distance",
      "Total Time"
    };
    
    private final Supplier<?>[] lambdas = new Supplier<?>[] {
      () -> workouts.size(),
      () -> String.format("%2.2f km", data.totalDistance),
      () -> String.format("%2.2f km", data.longestDistance),
      () -> String.format("%d days, %d hours, %d minutes", data.totalTime.days(), data.totalTime.hours(), data.totalTime.minutes()),

    };
}
