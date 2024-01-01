package org.oekofen.collector.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oekofen.collector.CollectorRecord;
import org.oekofen.collector.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConditionalOnProperty(prefix = "collect.source", name = "csv-api.enabled")
public class CsvApiSource implements CollectorSource
{
  private static final Logger LOG = LogManager.getLogger();

  @Value("${collect.source.csv-api.interval:10}")
  private int interval = 10;

  private final CsvApi csvApi;
  private final CsvToRecordsConverter converter = new CsvToRecordsConverter();
  private final Map<String, String> csvHashes = new HashMap<>();
  private long requestCount = 0;
  private long waitTimeMillis = 3000;

  @Autowired
  public CsvApiSource(CsvApi csvApi)
  {
    this.csvApi = csvApi;
  }

  public void setInterval(int interval)
  {
    this.interval = interval;
  }

  public void setWaitTimeMillis(long timeMillis)
  {
    this.waitTimeMillis = timeMillis;
  }

  private boolean csvAlreadyImported(String endpoint, String csvContent)
  {
    String newHash = HashUtil.createHash(csvContent);
    String csvHash = csvHashes.get(endpoint);
    if (newHash.equals(csvHash))
    {
      return true;
    }
    csvHashes.put(endpoint, newHash);
    return false;
  }

  public List<CollectorRecord> collect()
  {
    if (!shouldRequest())
    {
      return Collections.emptyList();
    }

    List<CollectorRecord> allRecords = new ArrayList<>();
    for (int i = 0; i < 4; i++)
    {
      String endpoint = "log" + i;

      String csvContent = csvApi.getCsvContent(endpoint);
      logCsvContent(endpoint, csvContent);
      allRecords.addAll(getNewRecords(endpoint, csvContent));

      // sleep in order to not hit the JSON API request time limit
      sleep(waitTimeMillis);
    }
    return allRecords;
  }

  boolean shouldRequest()
  {
    if (interval < 2)
    {
      return true;
    }

    // only request CSV every, for example, 10th collection interval
    requestCount++;
    return (requestCount % interval == 1);
  }

  private static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
  }

  List<CollectorRecord> getNewRecords(String endpoint, String csvContent)
  {
    List<CollectorRecord> records;
    if (csvContent == null || csvContent.isBlank())
    {
      records = Collections.emptyList();
      LOG.info("No CSV records are available for endpoint {}.", endpoint);
    }
    else if (csvAlreadyImported(endpoint, csvContent))
    {
      records = Collections.emptyList();
      LOG.info("No new CSV records are available for endpoint {}.", endpoint);
    }
    else
    {
      records = converter.convertToRecords(csvContent);
      LOG.info("{} new CSV records are available for endpoint {}.", records.size(), endpoint);
    }
    return records;
  }

  private void logCsvContent(String endpoint, String csvContent)
  {
    if (!LOG.isDebugEnabled())
    {
      return;
    }

    if (csvContent == null || csvContent.isBlank())
    {
      LOG.debug("CSV-Content for endpoint {}: empty", endpoint);
    }
    else
    {
      int maxLength = Math.min(1000, csvContent.length());
      LOG.debug("CSV-Content for endpoint {}: {}", endpoint, csvContent.substring(0, maxLength));
    }
  }


}
