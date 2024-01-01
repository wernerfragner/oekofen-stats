package org.oekofen.collector.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(prefix = "collect.source", name = "csv-api.enabled")
public class OekofenCsvApi implements CsvApi
{
  private static final Logger LOG = LogManager.getLogger();

  @Value("${collect.source.host}")
  private String host;
  @Value("${collect.source.port:4321}")
  private int port;
  @Value("${collect.source.password}")
  private String password;

  private final RestTemplate restTemplate = new RestTemplate();

  @Override
  public String getCsvContent(String endpoint)
  {
    LOG.info("Executing HTTP GET request: {}", buildUrl("*****", endpoint));
    return restTemplate.getForObject(buildUrl(password, endpoint), String.class);
  }


  private String buildUrl(String pwd, String endpoint)
  {
    return "http://" + host + ":" + port + "/" + pwd + "/" + endpoint;
  }
}
