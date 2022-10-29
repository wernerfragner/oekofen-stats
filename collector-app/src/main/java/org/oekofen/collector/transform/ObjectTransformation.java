package org.oekofen.collector.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ObjectTransformation
{
  private String sourceName;
  private String targetName;
  private List<String> sourceNames;
  private List<String> targetNames;
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
      return Arrays.asList(targetName);
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
      return Arrays.asList(sourceName);
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
        return true;
    }
    return false;
  }

  public String getTargetNameBySourceName(String sourceName)
  {
    List<String> allSourceNames = getSourceNamesList();
    for (int i = 0; i < allSourceNames.size(); i++ )
    {
      if (allSourceNames.get(i).equalsIgnoreCase(sourceName))
        return getTargetNamesList().get(i);
    }
    return null;
  }
}
