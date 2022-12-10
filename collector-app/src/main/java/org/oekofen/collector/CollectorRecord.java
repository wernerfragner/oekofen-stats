package org.oekofen.collector;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CollectorRecord
{
  private final Map<String, Object> data;
  private final Instant instant;

  public CollectorRecord(Map<String, Object> data)
  {
    this(data, Instant.now());
  }

  public CollectorRecord()
  {
    this(new HashMap<>());
  }

  public CollectorRecord(Map<String, Object> data, Instant instant)
  {
    this.data = data;
    this.instant = instant;
  }

  public Instant getInstant()
  {
    return instant;
  }

  public Set<Map.Entry<String, Object>> entrySet()
  {
    return data.entrySet();
  }

  public void put(String name, Object value)
  {
    data.put(name, value);
  }

  public Map<String, Object> getData()
  {
    return data;
  }

  @Override
  public String toString()
  {
    return data.toString();
  }

  public Object get(String name)
  {
    return data.get(name);
  }
}
