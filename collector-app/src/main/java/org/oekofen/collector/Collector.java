package org.oekofen.collector;

import org.oekofen.collector.source.JsonApiSource;
import org.oekofen.collector.target.CollectorTarget;
import org.oekofen.collector.transform.FlattenDataTransformer;
import org.oekofen.collector.transform.ConfigBasedTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class Collector
{
  @Autowired
  private JsonApiSource source;
  @Autowired
  private List<CollectorTarget> targets;
  @Autowired
  private FlattenDataTransformer flattenData;
  @Autowired
  private ConfigBasedTransformer mapData;

  @Scheduled(fixedRateString = "${collect.interval-seconds:60}", timeUnit = TimeUnit.SECONDS)
  public void collect()
  {
    Map<String, Object> data = source.collect();

    data = mapData.transform(data);
    data = flattenData.transform(data);

    for (CollectorTarget target : targets)
    {
      target.accept(data);
    }
  }
}
