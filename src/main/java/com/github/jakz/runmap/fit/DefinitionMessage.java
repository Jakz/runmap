package com.github.jakz.runmap.fit;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pixbits.lib.io.BinaryBuffer;

public class DefinitionMessage
{
  private final RecordHeader header;
  
  private final ByteOrder order;
  private final int globalMessageNumber;
  private final int fieldCount;
  
  private final List<FieldDefinition> fields;
  private final int dataMessageSizeInBytes;
  
  public DefinitionMessage(RecordHeader header, BinaryBuffer source)
  {
    this.header = header;
    
    /* skip reserved */
    source.readByte();
    
    order = source.readByte() == 0x01 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    source.setByteOrder(order);
    
    globalMessageNumber = source.readU16();
    
    fieldCount = source.readU8();
    
    fields = new ArrayList<>();
    
    for (int i = 0; i < fieldCount; ++i)
    {
      byte[] typeSpec = new byte[3];
      source.read(typeSpec);
      FieldDefinition field = new FieldDefinition(typeSpec);
      fields.add(field);
    }
    
    dataMessageSizeInBytes = fields.stream().map(FieldDefinition::sizeInBytes).mapToInt(i -> i).sum();
  }
  
  public List<FieldDefinition> fields() { return fields; }
  public int dataMessageSizeInBytes() { return dataMessageSizeInBytes; }
  public int globalNumber() { return globalMessageNumber; }
  
  public String toString()
  {   
    StringBuilder str = new StringBuilder();
    str.append(String.format("definition(%s, globalNumber: %d, fieldCount: %d, recordSize: %d)\n", header.toString(), globalMessageNumber, fieldCount, dataMessageSizeInBytes));
    str.append(fields.stream().map(Object::toString).collect(Collectors.joining(",\n  ", "  " , "\n")));
    return str.toString();
  }
}
