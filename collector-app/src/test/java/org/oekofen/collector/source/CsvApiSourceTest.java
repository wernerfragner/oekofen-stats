package org.oekofen.collector.source;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.oekofen.collector.CollectorRecord;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.oekofen.collector.source.Utils.loadResourceAsString;

class CsvApiSourceTest
{

  private final CsvApiSource source = new CsvApiSource();

  @Test
  void getNewRecords_NullCsvContent() throws IOException
  {
    List<CollectorRecord> records = source.getNewRecords("log0", null);
    assertEquals(0, records.size());
  }

  @Test
  void getNewRecords() throws IOException
  {
    String csvContent = loadResourceAsString("csv-api-example.csv");

    List<CollectorRecord> records = source.getNewRecords("log0", csvContent);
    assertEquals(5, records.size());

    // try to get records with same CSV content again, no new records should be returned
    records = source.getNewRecords("log0", csvContent);
    assertEquals(0, records.size());

    // get records for other endpoint
    List<CollectorRecord> records1 = source.getNewRecords("log1", csvContent);
    assertEquals(5, records1.size());

    // try to get records with same CSV content again, no new records should be returned
    records = source.getNewRecords("log1", csvContent);
    assertEquals(0, records.size());
  }

  @ParameterizedTest()
  @ValueSource(ints = {1, 2, 3, 4, 5, 10, 11, 15})
  void shouldRequest_defaultInterval(int interval)
  {
    // arrange
    source.setInterval(interval);

    // act+assert
    assertTrue(source.shouldRequest());
    for (int i = 1; i < interval; i++)
    {
      assertFalse(source.shouldRequest());
    }
    assertTrue(source.shouldRequest());
  }
}