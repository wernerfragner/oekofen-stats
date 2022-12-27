package org.oekofen.collector.target;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oekofen.collector.CollectorRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "collect.target", name = "console.enabled")
public class ConsoleTarget implements CollectorTarget
{
  private static final Logger LOG = LogManager.getLogger();

  @Override
  public void accept(CollectorRecord rec)
  {
    LOG.info("Collected JSON: {}", rec);
  }
}
