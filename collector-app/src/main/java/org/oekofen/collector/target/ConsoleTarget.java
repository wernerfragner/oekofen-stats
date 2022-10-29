package org.oekofen.collector.target;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsoleTarget implements CollectorTarget
{
  private static final Logger LOG = LogManager.getLogger();

  @Override
  public void accept(Map<String, Object> data)
  {
    LOG.info("Collected JSON: {}", data);
  }
}
