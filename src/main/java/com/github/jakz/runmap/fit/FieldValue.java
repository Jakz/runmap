package com.github.jakz.runmap.fit;

public class FieldValue
{
  private final long value;
  
  private FieldValue(long value)
  {
    this.value = value;
  }
  
  public int asInt() { return (int)value; }
  public long asLong() { return value; }

  public String toString() { return Long.toString(value); }
  
  public static FieldValue of(long value)
  {
    return new FieldValue(value);
  }
}
