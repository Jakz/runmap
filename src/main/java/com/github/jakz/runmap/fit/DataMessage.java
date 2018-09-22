package com.github.jakz.runmap.fit;

import java.util.List;
import java.util.stream.Collectors;

import com.pixbits.lib.io.BinaryBuffer;

public class DataMessage
{
  List<FieldValue> values;
  
  public DataMessage(BinaryBuffer buffer, DefinitionMessage definition)
  {    
    values = definition.fields().stream()
      .map(t -> t.read(buffer))
      .collect(Collectors.toList());
  }
  
  public String toString()
  {
    String values = this.values.stream().map(Object::toString).collect(Collectors.joining(", "));
    return String.format("data("+values+")");
  }
  
  public FieldValue value(int i)
  {
    return values.get(i);
  }
}
