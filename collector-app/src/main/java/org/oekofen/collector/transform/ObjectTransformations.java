package org.oekofen.collector.transform;

import java.util.List;

public class ObjectTransformations
{
  private List<ObjectTransformation> objects;

  public List<ObjectTransformation> getObjects()
  {
    return objects;
  }

  public void setObjects(List<ObjectTransformation> objects)
  {
    this.objects = objects;
  }

  public ObjectTransformation getBySourceName(String sourceName)
  {
    for (ObjectTransformation mapping : objects)
    {
      if (mapping.matchesSourceName(sourceName))
      {
        ObjectTransformation result = new ObjectTransformation();
        result.setSourceName(sourceName);
        result.setTargetName(mapping.getTargetNameBySourceName(sourceName));
        result.setFields(mapping.getFields());
        return result;
      }
    }
    return null;
  }
}
