package org.oekofen.collector.target;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class InfluxDbTarget implements CollectorTarget, AutoCloseable
{

  private static final Logger LOG = LogManager.getLogger();
  private final String databaseURL;
  private final String user;
  private final String password;
  private final String database;
  @Value("${collect.target.influxdb.measurement:heating}")
  private String measurement;

  private InfluxDB influxDB;

  public InfluxDbTarget(@Value("${collect.target.influxdb.url}") String databaseURL,
                        @Value("${collect.target.influxdb.user}") String user,
                        @Value("${collect.target.influxdb.password}") String password,
                        @Value("${collect.target.influxdb.database}") String database)
  {
    this.databaseURL = databaseURL;
    this.user = user;
    this.password = password;
    this.database = database;

    getOrCreateConnection();
  }

  @NotNull
  private InfluxDB getOrCreateConnection()
  {
    if (influxDB != null)
    {
      return influxDB;
    }

    influxDB = InfluxDBFactory.connect(databaseURL, user, password);
    influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
    if (pingServer(influxDB, databaseURL, user))
    {
      if (!influxDB.databaseExists(database))
      {
        LOG.info("Creating database '{}' ...", database);
        influxDB.createDatabase(database);
        influxDB.createRetentionPolicy("defaultPolicy", database, "365d", 1, true);
        LOG.info("Successfully created database '{}'", database);
      }
      influxDB.setRetentionPolicy("defaultPolicy");
      influxDB.setDatabase(database);
    }
    else
    {
      // no valid connection, try again later
      influxDB = null;
    }
    return influxDB;
  }

  private boolean pingServer(InfluxDB influxDB, String databaseURL, String user)
  {
    try
    {
      // Ping and check for version string
      Pong response = influxDB.ping();
      if (response.getVersion().equalsIgnoreCase("unknown"))
      {
        LOG.error("Error pinging InfluxDB server. url={}, user={}", databaseURL, user);
        return false;
      }
      else
      {
        LOG.info("InfluxDB version: {}", response.getVersion());
        return true;
      }
    }
    catch (InfluxDBIOException e)
    {
      LOG.error("Exception while pinging database: url=" + databaseURL + ", user=" + user, e);
      return false;
    }
  }

  @Override
  public void accept(String collectedJson)
  {
    Point point = convertJsonToPoint(collectedJson);
    if (point != null)
    {
      LOG.info("Writing 'Point' to InfluxDB ...");
      getOrCreateConnection().write(point);
      LOG.info("Successfully written 'Point' to InfluxDB.");
    }
  }

  private Point convertJsonToPoint(String collectedJson)
  {
    ObjectMapper mapper = new ObjectMapper();
    try
    {
      Map<String, Object> jsonMap = mapper.readValue(collectedJson, Map.class);
      Point.Builder pointBuilder = Point.measurement(measurement)
              .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
      addFieldsFromMap(jsonMap, pointBuilder, "");
      return pointBuilder.build();
    }
    catch (JsonProcessingException e)
    {
      LOG.error("Cannot convert input JSON to Map: " + e.getMessage(), e);
      return null;
    }
  }

  private static void addFieldsFromMap(Map<String, Object> jsonMap, Point.Builder pointBuilder, String parentFieldName)
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
        addFieldsFromMap((Map<String, Object>) value, pointBuilder, fieldName);
      }
      if (value instanceof Number nr)
      {
        pointBuilder.addField(fieldName, nr);
      }
      if (value instanceof String str)
      {
        pointBuilder.addField(fieldName, str);
      }
      if (value instanceof Boolean b)
      {
        pointBuilder.addField(fieldName, b);
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
