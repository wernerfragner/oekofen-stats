package org.oekofen.collector.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.oekofen.collector.CollectorRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "collect.source", name = "json-api.enabled")
public class JsonApiSource implements CollectorSource
{
  private static final Logger LOG = LogManager.getLogger();

  @Value("${collect.source.host}")
  private String host;
  @Value("${collect.source.port:4321}")
  private int port;
  @Value("${collect.source.password}")
  private String password;

  private final RestTemplate restTemplate = new RestTemplate();


  public List<CollectorRecord> collect()
  {
    // fetch data

    String url = buildUrl("*****");
    LOG.info("Executing HTTP GET request: {}", url);
    String json = restTemplate.getForObject(buildUrl(password), String.class);

    // convert received data

    return getNewRecords(json, url);
  }

  @NotNull
  List<CollectorRecord> getNewRecords(String json, String url)
  {
    if (json == null || json.isBlank())
    {
      LOG.warn("JSON endpoint returned an empty result {}.", url);
      return Collections.emptyList();
    }
    return Collections.singletonList(new CollectorRecord(convertToMap(json), Instant.now()));
  }

  private Map<String, Object> convertToMap(String json)
  {
    ObjectMapper mapper = new ObjectMapper();
    try
    {
      return mapper.readValue(json, Map.class);
    }
    catch (JsonProcessingException e)
    {
      LOG.atError().withThrowable(e).log("Cannot convert input JSON to Map: {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  private String buildUrl(String pwd)
  {
    return "http://" + host + ":" + port + "/" + pwd + "/all";
  }
}
