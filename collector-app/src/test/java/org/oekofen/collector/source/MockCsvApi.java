package org.oekofen.collector.source;

import java.util.HashMap;
import java.util.Map;

public class MockCsvApi implements CsvApi
{
  private final Map<String, String> data = new HashMap<>();

  void registerCsvContent(String endPointIdentifier, String csvContent)
  {
    data.put(endPointIdentifier, csvContent);
  }

  @Override
  public String getCsvContent(String endPointIdentifier)
  {
    return data.get(endPointIdentifier);
  }
}
