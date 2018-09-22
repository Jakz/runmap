package com.github.jakz.runmap.fit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.jakz.runmap.fit.known.FileIdMessage;
import com.pixbits.lib.io.BinaryBuffer;
import com.pixbits.lib.io.BinaryBuffer.Mode;

public class FitFile
{
  private FitHeader header;
  
  private void parseHeaderAndSeekAfter(BinaryBuffer raf)
  {
    int headerSize = raf.readByte() & 0xFF;
    raf.position(0);
    
    byte[] header = new byte[headerSize];
    raf.read(header);
    
    this.header = new FitHeader(header);
    raf.position(headerSize);
  }
  
  public FitFile(Path path) throws IOException
  {    
    try (BinaryBuffer raf = new BinaryBuffer(path, Mode.READ, ByteOrder.LITTLE_ENDIAN))
    {      
      parseHeaderAndSeekAfter(raf);
      
      RecordHeader recordHeader = new RecordHeader(raf.readByte());
      
      while (raf.position() < raf.length() - 2)
      {      
        if (!recordHeader.isDefinitionMessage())
          throw new IllegalArgumentException("Record at offset "+raf.position()+" should be definition");
  
        DefinitionMessage definitionMessage = new DefinitionMessage(recordHeader, raf);
        System.out.println(definitionMessage);
        
        //System.out.println("Offset: "+Integer.toHexString(raf.position()));
  
        recordHeader = new RecordHeader(raf.readByte());
  
        int c = 0;
        
        while (!recordHeader.isDefinitionMessage() && c++ < 10)
        {
          DataMessage dataMessage = new DataMessage(raf, definitionMessage);
          
          if (definitionMessage.globalNumber() == 0)
          {
            FileIdMessage fileIdMessage = new FileIdMessage(dataMessage);
            System.out.println("Read message: "+c+++": "+fileIdMessage);
          }
          else
          {
            System.out.println("Read message: "+c+++": "+dataMessage);

          }
          
          recordHeader = new RecordHeader(raf.readByte());
        }
      }
    }
  }
  
  public FitHeader header() { return header; }
  
  public static void main(String[] args)
  {
    try
    {
      FitFile fit = new FitFile(Paths.get("1549532563.fit"));
      System.out.println(fit.header());
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
