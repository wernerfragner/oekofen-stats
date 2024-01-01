package org.oekofen.collector.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.oekofen.collector.CollectorRecord;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.oekofen.collector.source.Utils.loadResourceAsString;

class CsvApiSourceTest
{

  private final MockCsvApi csvApi = new MockCsvApi();
  private final CsvApiSource source = new CsvApiSource(csvApi);

  @BeforeEach
  void setUp()
  {
    source.setInterval(1);
    source.setWaitTimeMillis(0L);
  }

  @Test
  void collect_noCsvDataAvailable()
  {
    csvApi.registerCsvContent("log0", null);
    csvApi.registerCsvContent("log1", null);
    csvApi.registerCsvContent("log2", null);
    csvApi.registerCsvContent("log3", null);

    var records = source.collect();
    assertEquals(0, records.size());
  }

  @Test
  void collect_noCsvDataAvailableWithInfoPage()
  {
    String noContent = """
            Oekofen JSON Interface   V4.00b   http://www.oekofen.at
                        
            usage:
            http://touch_ip:JSON_port/password/command
            """;

    csvApi.registerCsvContent("log0", noContent);
    csvApi.registerCsvContent("log1", noContent);
    csvApi.registerCsvContent("log2", noContent);
    csvApi.registerCsvContent("log3", noContent);

    var records = source.collect();
    assertEquals(0, records.size());
  }

  @Test
  void collect_csvDataAvailable()
  {
    String csvContent = """
            Datum ;Zeit ;AT [ýC]
            09.12.2022;00:03:13;-0,6
            """;

    csvApi.registerCsvContent("log0", null);
    csvApi.registerCsvContent("log1", csvContent);
    csvApi.registerCsvContent("log2", null);
    csvApi.registerCsvContent("log3", null);

    // ACT: collect data

    var records = source.collect();
    assertEquals(1, records.size());
    assertEquals("2022-12-09T00:03:13Z", records.get(0).instant().toString());

    var csv = (Map<String, Object>) records.get(0).get("csv");
    assertEquals("09.12.2022", csv.get("Datum"));
    assertEquals("00:03:13", csv.get("Zeit"));
    assertEquals(-0.6, csv.get("AT"));

    // ACT: collect data again - with no changes

    records = source.collect();
    assertEquals(0, records.size());

    // ACT: collect data again - with changes

    String newCsvContent = """
            Datum ;Zeit ;AT [ýC]
            10.12.2022;00:02:14;-1,2
            """;
    csvApi.registerCsvContent("log1", newCsvContent);

    records = source.collect();
    assertEquals(1, records.size());
    assertEquals("2022-12-10T00:02:14Z", records.get(0).instant().toString());

    csv = (Map<String, Object>) records.get(0).get("csv");
    assertEquals("10.12.2022", csv.get("Datum"));
    assertEquals("00:02:14", csv.get("Zeit"));
    assertEquals(-1.2, csv.get("AT"));
  }

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