package org.oekofen.collector.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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


  public String collect()
  {
    LOG.info("Executing HTTP GET request: {}", buildUrl("*****"));
    return restTemplate.getForObject(buildUrl(password), String.class);
  }

  private String buildUrl(String pwd)
  {
    return "http://" + host + ":" + port + "/" + pwd + "/all";
  }
}
