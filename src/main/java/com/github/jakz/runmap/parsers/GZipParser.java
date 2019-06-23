package com.github.jakz.runmap.parsers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.github.jakz.runmap.data.WorkoutTrack;
import com.pixbits.lib.io.FileUtils;

public class GZipParser implements Parser
{
  private final int bufferSize;
  private final ParserRepository repository;
  
  GZipParser(int bufferSize, ParserRepository repository)
  {
    this.bufferSize = bufferSize;
    this.repository = repository;
  }
  
  @Override
  public boolean canParse(Path path)
  {
    if (FileUtils.pathExtension(path).equals("gz"))
    {
      String withoutExtension = FileUtils.fileNameWithoutExtension(path);
      return repository.canBeParsed(Paths.get(withoutExtension));
    }
    
    return false;
  }

  @Override
  public List<WorkoutTrack> parse(Path path) throws IOException
  {
    String withoutGZExtension = FileUtils.fileNameWithoutExtension(path);
    String extension = FileUtils.pathExtension(Paths.get(withoutGZExtension));
    
    Path temp = Files.createTempFile("", "." + extension);
   
    try (InputStream is = Files.newInputStream(path))
    {
      try (OutputStream os = Files.newOutputStream(temp))
      {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        GZIPInputStream gis = new GZIPInputStream(is, 64 * 1024);

        
        byte[] buffer = new byte[bufferSize];
        int len;
        
        while ((len = gis.read(buffer)) != -1)
        {
          bos.write(buffer, 0, len);
        }
        
        bos.flush();
      }
    }
    
    List<WorkoutTrack> tracks = repository.parse(temp);
    Files.delete(temp);
    return tracks;
  }

}
