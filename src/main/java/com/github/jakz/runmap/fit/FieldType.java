package com.github.jakz.runmap.fit;

import java.util.Arrays;

public enum FieldType
{
  ENUM("enum", 0, false, 0xff, 1),
  
  SINT8("sint8", 1, false, 0x7f, 1),
  UINT8("uint8", 2, false, 0xff, 1),
  
  SINT16("sint16", 3, true, 0x7fff, 2),
  UINT16("uint16", 4, true, 0xffff, 2),
  
  SINT32("sint32", 5, true, 0x7fffffff, 4),
  UINT32("uint32", 6, true, 0xffffffff, 4),
  
  UINT32Z("uint32z", 12, true, 0x00000000, 4),

  ;

  private FieldType(String name, int ident, boolean endian, int invalid, int length)
  {
    this.name = name;
    this.ident = ident;
    this.hasEndianness = endian;
    this.length = length;
    this.invalidValue = invalid;
  }
      
  public final int ident;
  public final int length;
  public final boolean hasEndianness;
  public final String name;
  public final int invalidValue;
  
  public static FieldType forIdent(int ident)
  {
    return Arrays.stream(values())
        .filter(t -> t.ident == ident)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown field type for base type "+ident));
  }
}