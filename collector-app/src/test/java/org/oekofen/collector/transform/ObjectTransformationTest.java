package org.oekofen.collector.transform;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectTransformationTest
{

  private final ObjectTransformation transformation = new ObjectTransformation();

  @Test
  void transform_SourceNameWithBlank()
  {
    var brField = new FieldTransformation();
    brField.setSourceName("BR ");
    brField.setTargetName("On");
    brField.setIntToBool(true);

    transformation.setSourceName("csv");
    transformation.setTargetName("Boiler");
    transformation.setFields(List.of(brField));

    var resultMap = transformation.transform(new SimpleMapEntry("csv", Map.of("BR ", 1)));
    assertEquals(true, resultMap.get("On"));
  }
}