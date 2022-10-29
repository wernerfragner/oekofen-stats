package org.oekofen.collector;

import org.oekofen.collector.source.JsonApiSource;
import org.oekofen.collector.target.CollectorTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Collector
{
  @Autowired
  private JsonApiSource source;
  @Autowired
  private List<CollectorTarget> targets;

  @Scheduled(fixedRateString = "${collect.interval-seconds:60}", timeUnit = TimeUnit.SECONDS)
  public void collect()
  {
    String collectedJson = source.collect();
    targets.forEach(target -> target.accept(collectedJson));
  }
}
