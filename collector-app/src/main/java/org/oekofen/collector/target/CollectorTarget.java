package org.oekofen.collector.target;

import java.util.Map;

public interface CollectorTarget
{
  void accept(Map<String, Object> data);
}
