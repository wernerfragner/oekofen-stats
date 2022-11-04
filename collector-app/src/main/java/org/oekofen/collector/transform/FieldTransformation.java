package org.oekofen.collector.transform;

import com.fasterxml.jackson.annotation.JsonProperty;


public class FieldTransformation
{
  @JsonProperty(required = true)
  private String sourceName;
  @JsonProperty(required = true)
  private String targetName;
  @JsonProperty
  private Float divideValueBy;
  @JsonProperty
  private Boolean intToBool;

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

  public Float getDivideValueBy()
  {
    return divideValueBy;
  }

  public void setDivideValueBy(Float divideValueBy)
  {
    this.divideValueBy = divideValueBy;
  }

  public Boolean getIntToBool()
  {
    return intToBool;
  }

  public void setIntToBool(Boolean intToBool)
  {
    this.intToBool = intToBool;
  }
}
