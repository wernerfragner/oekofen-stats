package org.oekofen.collector;

import org.oekofen.collector.source.CollectorSource;
import org.oekofen.collector.target.CollectorTarget;
import org.oekofen.collector.transform.ConfigBasedTransformer;
import org.oekofen.collector.transform.FlattenDataTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Collector
{
  @Autowired
  private List<CollectorSource> sources;
  @Autowired
  private List<CollectorTarget> targets;
  @Autowired
  private FlattenDataTransformer flattenData;
  @Autowired
  private ConfigBasedTransformer mapData;

  @Scheduled(fixedRateString = "${collect.source.interval-seconds:60}", timeUnit = TimeUnit.SECONDS)
  public void collect() throws InterruptedException
  {
    for (CollectorSource source : sources)
    {
      collect(source);
      Thread.sleep(5000);
    }
  }

  private void collect(CollectorSource source)
  {
    List<CollectorRecord> records = source.collect();
    for (CollectorRecord rec : records)
    {
      rec = mapData.transform(rec);
      rec = flattenData.transform(rec);

      for (CollectorTarget target : targets)
      {
        target.accept(rec);
      }
    }
  }
}
