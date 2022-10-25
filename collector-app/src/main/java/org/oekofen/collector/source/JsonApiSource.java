package org.oekofen.collector.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JsonApiSource
{
  @Value("${collect.source.host}")
  private String host;
  @Value("${collect.source.port:4321}")
  private int port;
  @Value("${collect.source.password}")
  private String password;

  public String collect()
  {
    return "{ \"sensor\" : 42.123}";
  }
}
