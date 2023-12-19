package org.oekofen.collector.transform;

import java.util.Map;

public class SimpleMapEntry implements Map.Entry<String, Object>
{

  private final String key;
  private Object value;

  public SimpleMapEntry(String key, Object value)
  {
    this.key = key;
    this.value = value;
  }

  @Override
  public String getKey()
  {
    return key;
  }

  @Override
  public Object getValue()
  {
    return value;
  }

  @Override
  public Object setValue(Object value)
  {
    Object prev = value;
    this.value = value;
    return prev;
  }
}
