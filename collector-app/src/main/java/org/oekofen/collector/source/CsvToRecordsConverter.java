package org.oekofen.collector.source;

import org.oekofen.collector.CollectorRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvToRecordsConverter
{

  public List<CollectorRecord> convertToRecords(String csvContent)
  {
    if (csvContent == null || csvContent.isEmpty())
    {
      return Collections.emptyList();
    }

    List<CollectorRecord> records = new ArrayList<>();
    List<String> header = null;
    for (String line : csvContent.split("\n"))
    {
      if (line.trim().isEmpty())
      {
        continue;
      }

      if (header == null)
      {
        header = toHeader(line);
      }
      else
      {
        CollectorRecord rec = toRecord(header, line);
        if (rec != null)
        {
          records.add(rec);
        }
      }
    }
    return records;
  }


  private List<String> toHeader(String line)
  {
    List<String> header = new ArrayList<>();
    for (String item : line.split(";"))
    {
      item = item.trim();
      if (!item.isBlank())
      {
        int bracketIdx = item.indexOf("[");
        if (bracketIdx > 0)
        {
          header.add(item.substring(0, bracketIdx).trim());
        }
        else
        {
          header.add(item);
        }
      }
    }
    return header;
  }

  private CollectorRecord toRecord(List<String> header, String line)
  {
    String date = null;
    String time = null;

    Map<String, Object> csvData = new HashMap<>();
    String[] values = line.split(";");
    for (int i = 0; i < header.size(); i++)
    {
      if (i < values.length && !values[i].trim().isEmpty())
      {
        if (header.get(i).equalsIgnoreCase("Datum"))
        {
          date = values[i].trim();
        }
        else if (header.get(i).equalsIgnoreCase("Zeit"))
        {
          time = values[i].trim();
        }

        csvData.put(header.get(i), tryConvertValue(values[i].trim().replace(",", ".")));
      }
    }

    if (date == null || time == null)
    {
      return null;
    }

    Instant instant = toInstant(date, time);

    Map<String, Object> csv = new HashMap<>();
    csv.put("csv", csvData);
    return new CollectorRecord(csv, instant);
  }

  private Object tryConvertValue(String value)
  {
    try
    {
      return Integer.parseInt(value);
    }
    catch (NumberFormatException nfe1)
    {
      try
      {
        return Double.parseDouble(value);
      }
      catch (NumberFormatException nfe2)
      {
        return value;
      }
    }
  }

  private Instant toInstant(String date, String time)
  {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse(date + " " + time, formatter);
    return dt.toInstant(ZoneOffset.UTC); // assume that data is in UTC
  }
}
