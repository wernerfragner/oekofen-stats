package org.oekofen.collector.transform;

import java.util.Map;

public interface Transformer
{
  Map<String, Object> transform(Map<String, Object> data);
}
