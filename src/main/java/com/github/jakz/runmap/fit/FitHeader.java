package com.github.jakz.runmap.fit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.pixbits.lib.io.BinaryBuffer;

public class FitHeader
{
  private BinaryBuffer data;
  
  public FitHeader(byte[] bytes)
  {
    data = new BinaryBuffer(bytes, ByteOrder.LITTLE_ENDIAN);
  }
  
  public int headerSize() { return data.readU8(0); }
  public int protocolVersion() { return data.readU8(1); }
  public int profileVersion() { return data.readU16(2); }
  public int dataSize() { return data.readU32(4); }
  public char[] magic() { 
    char[] magic = new char[4];
    return data.readAsBytes(magic, 8);
  }
  public int crc() { return data.readU16(12); }

  public String toString() {
    return String.format("header(bytes: %d, version: %d, profile: %d, contentSize: %d, '%s')", headerSize(), protocolVersion(), profileVersion(), dataSize(), new String(magic()));
  }
}
