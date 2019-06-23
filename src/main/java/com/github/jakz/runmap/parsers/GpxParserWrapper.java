package com.github.jakz.runmap.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.github.jakz.runmap.data.WorkoutPoint;
import com.github.jakz.runmap.data.WorkoutTrack;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.xml.gpx.GpxParser;
import com.pixbits.lib.io.xml.gpx.GpxTrack;

public class GpxParserWrapper implements Parser
{
  public boolean canParse(Path path)
  {
    return FileUtils.pathExtension(path).equals("gpx");
  }
  
  @Override
  public List<WorkoutTrack> parse(Path path) throws IOException
  {
    try
    {
      return GpxParser.parse(path).stream()
          .flatMap(GpxTrack::stream)
          .map(s -> new WorkoutTrack(s.stream()
              .map(WorkoutPoint::new)
              .collect(Collectors.toList())
          )).collect(Collectors.toList());
    }
    catch (JAXBException|SAXException e)
    {
      throw new IOException(e);
    }
  }
}
