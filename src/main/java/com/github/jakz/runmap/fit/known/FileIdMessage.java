package com.github.jakz.runmap.fit.known;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;

import com.github.jakz.runmap.fit.DataMessage;

public class FileIdMessage
{
  public enum FileIdType
  {
    ACTIVITY(4, "activity")
    
    ;
    
    private FileIdType(int ident, String name)
    {
      this.ident = ident;
      this.name = name;
    }
    
    public final int ident;
    public final String name;
    
    public static FileIdType forIdent(int ident)
    {
      return Arrays.stream(values())
        .filter(t -> t.ident == ident)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown file_id type: "+ident));
    }
  };
  
  private final DataMessage message;
  
  public FileIdMessage(DataMessage message)
  {
    this.message = message;
  }
  
  public String toString()
  { 
    Instant base = LocalDateTime.of(1989, Month.DECEMBER, 31, 0, 0, 0).toInstant(OffsetDateTime.now().getOffset());
    
    DateTimeFormatter formatter =
        DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                         .withLocale( Locale.ITALY )
                         .withZone( ZoneId.systemDefault() );
    
    return String.format("file_id(%s, %s)", 
        FileIdType.forIdent(message.value(0).asInt()).name,
        formatter.format(base.plus(message.value(4).asLong(), ChronoUnit.SECONDS))  
    ); 
  }
  
  
}
