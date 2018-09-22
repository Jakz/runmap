package com.github.jakz.runmap.fit;

import com.pixbits.lib.io.BinaryBuffer;

public class FieldDefinition
{
  public final int fieldDefinitionNumber;
  public final int sizeInBytes;
  public final FieldType type;
  
  
  public FieldDefinition(byte[] bytes)
  {
    this.fieldDefinitionNumber = bytes[0] & 0xFF;
    this.sizeInBytes = bytes[1] & 0xFF;
    this.type = FieldType.forIdent(bytes[2] & 0x0F);
  }
  
  public FieldValue read(BinaryBuffer buffer)
  {
    switch (type)
    {
      case ENUM: return FieldValue.of(buffer.readU8());
      
      case UINT8: return FieldValue.of(buffer.readU8());
      case SINT8: return FieldValue.of(buffer.readByte());
      
      case UINT16: 
        return FieldValue.of(buffer.readU16());
      case SINT16: 
        return FieldValue.of(buffer.readS16());
      
      case UINT32: return FieldValue.of(buffer.readU32());
      case SINT32: return FieldValue.of(buffer.readU32());
      
      case UINT32Z: return FieldValue.of(buffer.readU32());
      
      default: throw new IllegalArgumentException("Can't deserialize FieldValue");
    }
  }
  
  public int sizeInBytes() { return sizeInBytes; }
  
  public String toString() { return String.format("field(%d, %d, %s)", fieldDefinitionNumber, sizeInBytes, type); }
}
