package org.oekofen.collector.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigBasedTransformer implements Transformer
{
  private static final Logger LOG = LogManager.getLogger();
  private ObjectTransformations objectTransformations;

  public ConfigBasedTransformer(@Value("${collect.transform.config-file-path}") String configFilePath) throws IOException
  {
    if (configFilePath != null)
    {
      Path path = Path.of(configFilePath);
      String str = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      objectTransformations = new ObjectMapper().readValue(str, ObjectTransformations.class);
      if (objectTransformations.isEmpty())
      {
        LOG.warn("Given transformation config from '{}' does not contain any transformation objects.", configFilePath);
      }
      else
      {
        LOG.info("Successfully loaded transformation config from '{}'", configFilePath);
      }
    }
    else
    {
      LOG.info("No mapping file path given in property 'collect.transform.config-file-path'. No mapping transformation is performed.");
    }
  }

  @Override
  public Map<String, Object> transform(Map<String, Object> data)
  {
    if (objectTransformations == null || objectTransformations.isEmpty())
    {
      return data;
    }

    Map<String, Object> resultObjects = new HashMap<>();
    for (Map.Entry<String, Object> objectEntry : data.entrySet())
    {
      ObjectTransformation objectTransformation = objectTransformations.getBySourceName(objectEntry.getKey());
      if (objectTransformation != null && objectEntry.getValue() instanceof Map)
      {
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> fieldsMap = (Map<String, Object>) objectEntry.getValue();
        for (Map.Entry<String, Object> fieldEntry : fieldsMap.entrySet())
        {
          FieldTransformation fieldTransformation = objectTransformation.getBySourceName(fieldEntry.getKey());
          if (fieldTransformation != null)
          {
            Object value = transformValue(fieldTransformation, fieldEntry.getValue());
            resultObject.put(fieldTransformation.getTargetName(), value);
          }
          // else: no mapping, ignore field
        }

        if (resultObject.size() > 0)
        {
          resultObjects.put(objectTransformation.getTargetName(), resultObject);
        }
      }
    }
    return resultObjects;
  }

  private Object transformValue(FieldTransformation mapping, Object value)
  {
    if (mapping.getDivideValueBy() != null && value instanceof Number nrValue)
    {
      return nrValue.floatValue() / mapping.getDivideValueBy();
    }
    if (mapping.getIntToBool() != null && value instanceof Number nrValue)
    {
      return (nrValue.intValue() == 1 ? Boolean.TRUE : Boolean.FALSE);
    }
    return value;
  }
}
