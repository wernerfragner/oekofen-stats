package org.oekofen.collector.transform;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectTransformations
{
  @JsonProperty(required = true)
  private List<ObjectTransformation> objects;

  public List<ObjectTransformation> getObjects()
  {
    return objects;
  }

  public void setObjects(List<ObjectTransformation> objects)
  {
    this.objects = objects;
  }

  @JsonIgnore
  public List<ObjectTransformation> getBySourceName(String sourceName)
  {
    if (objects == null)
    {
      return Collections.emptyList();
    }

    List<ObjectTransformation> results = new ArrayList<>();
    for (ObjectTransformation mapping : objects)
    {
      if (mapping.matchesSourceName(sourceName))
      {
        ObjectTransformation result = new ObjectTransformation();
        result.setSourceName(sourceName);
        result.setTargetName(mapping.getTargetNameBySourceName(sourceName));
        result.setFields(mapping.getFields());
        results.add(result);
      }
    }
    return results;
  }

  public boolean isEmpty()
  {
    return objects == null || objects.isEmpty();
  }
}
