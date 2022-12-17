package org.oekofen.collector;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record CollectorRecord(Map<String, Object> data, Instant instant)
{

  public Set<Map.Entry<String, Object>> entrySet()
  {
    return data.entrySet();
  }

  public void put(String name, Object value)
  {
    data.put(name, value);
  }

  @Override
  public String toString()
  {
    return "Instant: " + instant + ", Data: " + data;
  }

  public Object get(String name)
  {
    return data.get(name);
  }
}
