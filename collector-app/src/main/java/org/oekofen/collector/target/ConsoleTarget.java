package org.oekofen.collector.target;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ConsoleTarget implements CollectorTarget
{
  private static final Logger LOG = LogManager.getLogger();

  @Override
  public void accept(String collectedJson)
  {
    LOG.info("Collected JSON: {}", collectedJson.replace("\n", "").replace("\r", ""));
  }
}
