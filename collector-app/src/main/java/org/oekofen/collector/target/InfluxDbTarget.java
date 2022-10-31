package org.oekofen.collector.target;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class InfluxDbTarget implements CollectorTarget, AutoCloseable
{

  private static final Logger LOG = LogManager.getLogger();
  private final String databaseURL;
  private final String token;
  private final String organization;
  private final String bucket;
  @Value("${collect.target.influxdb.measurement:heating}")
  private String measurement;

  private InfluxDBClient influxDB;

  public InfluxDbTarget(@Value("${collect.target.influxdb.url}") String databaseURL,
                        @Value("${collect.target.influxdb.token}") String token,
                        @Value("${collect.target.influxdb.organization}") String organization,
                        @Value("${collect.target.influxdb.bucket:oekofen}") String bucket)
  {
    this.databaseURL = databaseURL;
    this.token = token;
    this.organization = organization;
    this.bucket = bucket;

    getOrCreateConnection();
  }

  @NotNull
  private InfluxDBClient getOrCreateConnection()
  {
    if (influxDB != null)
    {
      return influxDB;
    }

    LOG.info("Connecting to InfluxDB: url={}, organisation={}, bucket={} ...",
            databaseURL, organization, bucket);
    influxDB = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), organization, bucket);
    LOG.info("Successfully connected to InfluxDB: url={}, organisation={}, bucket={}",
            databaseURL, organization, bucket);
    return influxDB;
  }


  @Override
  public void accept(Map<String, Object> data)
  {
    Point point = convertJsonToPoint(data);
    LOG.info("Writing 'Point' to InfluxDB ...");
    try
    {
      WriteApiBlocking writeApi = getOrCreateConnection().getWriteApiBlocking();
      writeApi.writePoint(point);
      LOG.info("Successfully written 'Point' to InfluxDB.");
    }
    catch (InfluxException e)
    {
      this.influxDB = null;
      LOG.atError().withThrowable(e).log("Error while writing data to InfluxDB: url={}, organisation={}, bucket={}, error={}", databaseURL, organization, bucket, e.getMessage());
    }
  }

  private Point convertJsonToPoint(Map<String, Object> jsonMap)
  {
    Point point = Point.measurement(measurement).time(Instant.now(), WritePrecision.S);
    addFieldsFromMap(jsonMap, point, "");
    return point;
  }

  private static void addFieldsFromMap(Map<String, Object> jsonMap, Point point, String parentFieldName)
  {
    for (Map.Entry<String, Object> entry : jsonMap.entrySet())
    {
      Object value = entry.getValue();
      if (value == null)
      {
        continue;
      }

      String fieldName = (parentFieldName.isEmpty() ? entry.getKey() : parentFieldName + "." + entry.getKey());

      if (value instanceof Map)
      {
        addFieldsFromMap((Map<String, Object>) value, point, fieldName);
      }
      if (value instanceof Number nr)
      {
        point.addField(fieldName, nr);
      }
      if (value instanceof String str)
      {
        point.addField(fieldName, str);
      }
      if (value instanceof Boolean b)
      {
        point.addField(fieldName, b);
      }
    }
  }

  @Override
  public void close()
  {
    if (influxDB != null)
    {
      influxDB.close();
    }
  }
}
