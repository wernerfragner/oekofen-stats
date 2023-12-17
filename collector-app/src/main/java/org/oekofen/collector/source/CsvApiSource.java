package org.oekofen.collector.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oekofen.collector.CollectorRecord;
import org.oekofen.collector.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@ConditionalOnProperty(prefix = "collect.source", name = "csv-api.enabled")
public class CsvApiSource implements CollectorSource
{
  private static final Logger LOG = LogManager.getLogger();

  @Value("${collect.source.host}")
  private String host;
  @Value("${collect.source.port:4321}")
  private int port;
  @Value("${collect.source.password}")
  private String password;

  @Value("${collect.source.csv-api.interval:10}")
  private int interval = 10;

  private final CsvToRecordsConverter converter = new CsvToRecordsConverter();
  private final RestTemplate restTemplate = new RestTemplate();

  private final Map<String, String> csvHashes = new HashMap<>();
  private long requestCount = 0;

  public void setInterval(int interval)
  {
    this.interval = interval;
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

      LOG.info("Executing HTTP GET request: {}", buildUrl("*****", endpoint));
      String csvContent = restTemplate.getForObject(buildUrl(password, endpoint), String.class);
      allRecords.addAll(getNewRecords(endpoint, csvContent));

      // sleep in order to not hit the JSON API request time limit
      sleep(3000);
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

  private String buildUrl(String pwd, String endpoint)
  {
    return "http://" + host + ":" + port + "/" + pwd + "/" + endpoint;
  }
}
