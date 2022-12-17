package org.oekofen.collector.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oekofen.collector.CollectorRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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
  public CollectorRecord transform(CollectorRecord rec)
  {
    if (objectTransformations == null || objectTransformations.isEmpty())
    {
      return rec;
    }

    CollectorRecord resultRec = new CollectorRecord(new HashMap<>(), rec.instant());
    for (Map.Entry<String, Object> objectEntry : rec.entrySet())
    {
      List<ObjectTransformation> objectTransformationsForSource = objectTransformations.getBySourceName(objectEntry.getKey());
      for (ObjectTransformation objectTransformation : objectTransformationsForSource)
      {
        Map<String, Object> resultObject = objectTransformation.transform(objectEntry);
        if (resultObject.size() > 0)
        {
          resultRec.put(objectTransformation.getTargetName(), resultObject);
        }
      }
    }
    return resultRec;
  }


}
