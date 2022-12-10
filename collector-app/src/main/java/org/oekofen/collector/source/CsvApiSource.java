package org.oekofen.collector.source;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.oekofen.collector.CollectorRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

  private final CsvToRecordsConverter converter = new CsvToRecordsConverter();
  private final RestTemplate restTemplate = new RestTemplate();

  public List<CollectorRecord> collect()
  {
    LOG.info("Executing HTTP GET request: {}", buildUrl("*****"));
    String csvContent = restTemplate.getForObject(buildUrl(password), String.class);
    List<CollectorRecord> records = converter.convertToRecords(csvContent);
    LOG.info("{} new CSV records available.", records.size());
    return records;
  }

  private String buildUrl(String pwd)
  {
    return "http://" + host + ":" + port + "/" + pwd + "/log";
  }
}
