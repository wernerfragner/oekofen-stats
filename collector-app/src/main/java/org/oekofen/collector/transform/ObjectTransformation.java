package org.oekofen.collector.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

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

  @JsonIgnore
  public FieldTransformation getBySourceName(String sourceName)
  {
    if (fields == null)
      return null;

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
    for (String sName : getSourceNamesList())
    {
      if (sName.equalsIgnoreCase(sourceName))
      {
        return true;
      }
    }
    return false;
  }

  @JsonIgnore
  public String getTargetNameBySourceName(String sourceName)
  {
    List<String> allSourceNames = getSourceNamesList();
    for (int i = 0; i < allSourceNames.size(); i++)
    {
      if (allSourceNames.get(i).equalsIgnoreCase(sourceName))
      {
        return getTargetNamesList().get(i);
      }
    }
    return null;
  }
}
