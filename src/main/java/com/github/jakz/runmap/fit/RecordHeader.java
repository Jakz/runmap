package com.github.jakz.runmap.fit;

public class RecordHeader
{
  private final byte b;
  
  public RecordHeader(byte b)
  {
    this.b = b;
  }
  
  public boolean isCompressedTimestamp() { return (b & 0x80) != 0; }
  public boolean isNormal() { return !isCompressedTimestamp(); }
  
  public boolean isDefinitionMessage() { return (b & 0x40) != 0; }
  public boolean isDataMessage() { return !isDefinitionMessage(); }
  
  public int messageType() { return (b & 0x20) >> 5; }
  public int localMessageType() { return (b & 0x0F); }
  
  public boolean hasDeveloperData() { return isDefinitionMessage() && (b & 0x20) != 0; }
  
  public String toString() {
    if (isDefinitionMessage())
      return String.format("header(devInfo: %s, localType: %d)", hasDeveloperData() ? "y" : "n", localMessageType());
    else
      return String.format("[normal: %s, type: %s, localType: %d]", isNormal() ? "y" : "n", isDefinitionMessage() ? "definition" : "data", localMessageType());
  }
}
