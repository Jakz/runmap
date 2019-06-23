package com.github.jakz.runmap.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.github.jakz.runmap.data.WorkoutTrack;

public interface Parser
{
  boolean canParse(Path path);
  List<WorkoutTrack> parse(Path path) throws IOException;
}
