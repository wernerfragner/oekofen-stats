package org.oekofen.collector.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectTransformation
{
  @JsonProperty
  private String sourceName;
  @JsonProperty
  private String targetName;
  @JsonProperty
  private List<String> sourceNames;
  @JsonProperty
  private List<String> targetNames;
  @JsonProperty
  private List<FieldTransformation> fields;

  public String getSourceName()
  {
    return sourceName;
  }

  public void setSourceName(String sourceName)
  {
    this.sourceName = sourceName;
  }

  public String getTargetName()
  {
    return targetName;
  }

  public void setTargetName(String targetName)
  {
    this.targetName = targetName;
  }

  public List<String> getSourceNames()
  {
    return sourceNames;
  }

  public void setSourceNames(List<String> sourceNames)
  {
    this.sourceNames = sourceNames;
  }

  public List<String> getTargetNames()
  {
    return targetNames;
  }

  public void setTargetNames(List<String> targetNames)
  {
    this.targetNames = targetNames;
  }

  @JsonIgnore
  public List<String> getTargetNamesList()
  {
    if (targetNames != null)
    {
      if (targetName != null)
      {
        targetNames.add(targetName);
      }
      return targetNames;
    }
    if (targetName != null)
    {
      return List.of(targetName);
    }
    // no targets configured, use source names as targets
    return getSourceNamesList();
  }

  @JsonIgnore
  public List<String> getSourceNamesList()
  {
    if (sourceNames != null)
    {
      if (sourceName != null)
      {
        sourceNames.add(sourceName);
      }
      return sourceNames;
    }
    if (sourceName != null)
    {
      return List.of(sourceName);
    }
    return Collections.emptyList();
  }

  public List<FieldTransformation> getFields()
  {
    return fields;
  }

  public void setFields(List<FieldTransformation> fields)
  {
    this.fields = fields;
  }

  private String prepareName(String sourceName)
  {
    return sourceName.trim();
  }

  @JsonIgnore
  public FieldTransformation getBySourceName(String sourceName)
  {
    if (fields == null)
    {
      return null;
    }

    sourceName = prepareName(sourceName);
    for (FieldTransformation mapping : fields)
    {
      if (sourceName.equalsIgnoreCase(mapping.getSourceName()))
      {
        return mapping;
      }
    }
    return null;
  }


  public boolean matchesSourceName(String sourceName)
  {
    sourceName = prepareName(sourceName);
    for (String sName : getSourceNamesList())
    {
      if (prepareName(sName).equalsIgnoreCase(sourceName))
      {
        return true;
      }
    }
    return false;
  }

  @JsonIgnore
  public String getTargetNameBySourceName(String sourceName)
  {
    sourceName = prepareName(sourceName);
    List<String> allSourceNames = getSourceNamesList();
    for (int i = 0; i < allSourceNames.size(); i++)
    {
      String sName = allSourceNames.get(i);
      if (prepareName(sName).equalsIgnoreCase(sourceName))
      {
        return prepareName(getTargetNamesList().get(i));
      }
    }
    return null;
  }

  public Map<String, Object> transform(Map.Entry<String, Object> objectEntry)
  {
    Map<String, Object> resultObject = new HashMap<>();
    if (objectEntry.getValue() instanceof Map)
    {
      Map<String, Object> fieldsMap = (Map<String, Object>) objectEntry.getValue();
      for (Map.Entry<String, Object> fieldEntry : fieldsMap.entrySet())
      {
        FieldTransformation fieldTransformation = this.getBySourceName(fieldEntry.getKey());
        if (fieldTransformation != null)
        {
          Object value = fieldTransformation.transformValue(fieldEntry.getValue());
          resultObject.put(fieldTransformation.getTargetName(), value);
        }
        // else: no mapping, ignore field
      }
    }
    return resultObject;
  }


}
