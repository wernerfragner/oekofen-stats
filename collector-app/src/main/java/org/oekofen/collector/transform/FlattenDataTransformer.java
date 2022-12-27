package org.oekofen.collector.transform;

import org.oekofen.collector.CollectorRecord;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlattenDataTransformer implements Transformer
{
  @Override
  public CollectorRecord transform(CollectorRecord rec)
  {
    Map<String, Object> outputMap = new HashMap<>();
    addFieldsFromMap(rec.data(), outputMap, "");
    return new CollectorRecord(outputMap, rec.instant());
  }


  private static void addFieldsFromMap(Map<String, Object> inputMap, Map<String, Object> outputMap, String parentFieldName)
  {
    for (Map.Entry<String, Object> entry : inputMap.entrySet())
    {
      Object value = entry.getValue();
      if (value == null)
      {
        continue;
      }

      String fieldName = (parentFieldName.isEmpty() ? entry.getKey() : parentFieldName + "." + entry.getKey());

      if (value instanceof Map)
      {
        addFieldsFromMap((Map<String, Object>) value, outputMap, fieldName);
      }
      if (value instanceof Number nr)
      {
        outputMap.put(fieldName, nr);
      }
      if (value instanceof String str)
      {
        outputMap.put(fieldName, str);
      }
      if (value instanceof Boolean b)
      {
        outputMap.put(fieldName, b);
      }
    }
  }
}
