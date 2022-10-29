package org.oekofen.collector.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class JsonApiSource
{
  private static final Logger LOG = LogManager.getLogger();

  @Value("${collect.source.host}")
  private String host;
  @Value("${collect.source.port:4321}")
  private int port;
  @Value("${collect.source.password}")
  private String password;

  private RestTemplate restTemplate = new RestTemplate();


  public Map<String, Object> collect()
  {
    LOG.info("Executing HTTP GET request: {}", buildUrl("*****"));
    String json = restTemplate.getForObject(buildUrl(password), String.class);
    return convertToMap(json);
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
