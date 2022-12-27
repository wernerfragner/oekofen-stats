package org.oekofen.collector.source;

import org.junit.jupiter.api.Test;
import org.oekofen.collector.CollectorRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.oekofen.collector.source.Utils.loadResourceAsString;

class CsvToRecordsConverterTest
{
  private final CsvToRecordsConverter converter = new CsvToRecordsConverter();

  @Test
  void convertToRecords() throws IOException
  {
    String csvContent = loadResourceAsString("csv-api-example.csv");
    List<CollectorRecord> records = converter.convertToRecords(csvContent);
    assertEquals(5, records.size());
    CollectorRecord firstRecord = records.get(0);
    assertEquals(Instant.parse("2022-12-09T00:03:13+01:00"), firstRecord.instant());
    Map<String, Object> firstRecData = (Map<String, Object>) firstRecord.get("csv");
    assertNotNull(firstRecData);
    assertEquals("09.12.2022", firstRecData.get("Datum"));
    assertEquals("00:03:13", firstRecData.get("Zeit"));
    assertEquals(0, firstRecData.get("HK1 Pumpe"));
    assertEquals(26.4, firstRecData.get("HK2 VL Ist"));
    assertEquals(60.9, firstRecData.get("KT Ist"));
    assertEquals(8.0, firstRecData.get("KT Soll"));
  }
}