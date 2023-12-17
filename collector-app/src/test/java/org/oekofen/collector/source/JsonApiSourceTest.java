package org.oekofen.collector.source;

import org.junit.jupiter.api.Test;
import org.oekofen.collector.CollectorRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonApiSourceTest
{
  private final JsonApiSource source = new JsonApiSource();

  @Test
  void getNewRecords_NullJsonContent()
  {
    List<CollectorRecord> records = source.getNewRecords(null, "http//host:port");
    assertEquals(0, records.size());
  }

  @Test
  void getNewRecords()
  {
    String json = """
            {
              "key1" : 41,
              "key2" : "test"
            }
            """;
    List<CollectorRecord> records = source.getNewRecords(json, "http//host:port");
    assertEquals(1, records.size());
    assertEquals(41, records.get(0).data().get("key1"));
    assertEquals("test", records.get(0).data().get("key2"));
  }
}