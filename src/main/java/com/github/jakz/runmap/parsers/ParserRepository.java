package com.github.jakz.runmap.parsers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.jakz.runmap.data.WorkoutTrack;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.lang.Pair;

public class ParserRepository
{
  private final List<Parser> parsers;
  
  public ParserRepository()
  {
    parsers = List.of(
      new FitParser(),
      new GpxParserWrapper(),
      new GZipParser(1024 * 1024, this)
    );
  }
  
  public boolean canBeParsed(Path path)
  {
    return parsers.stream().anyMatch(parser -> parser.canParse(path));
  }
  
  public List<WorkoutTrack> parse(Path path)
  {
    Optional<Parser> valid = parsers.stream().filter(parser -> parser.canParse(path)).findFirst();

    if (valid.isPresent())
      System.out.println("Parsing "+path.toString()+" with "+valid.get().getClass().getName());
    else
      System.out.println("No parser found for "+path.getFileName().toString());
    
    return valid.map(StreamException.rethrowFunction(p -> p.parse(path))).orElse(List.of());
  }
}
