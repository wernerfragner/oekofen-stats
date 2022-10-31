# Collector Application
Collect and store various sensor values of Ökofen pellet stoves. Following sources are supported:
- Ökofen JSON HTTP API (must be enabled in your Ökofen stove settings)

### VM Options
Following JavaVM options must be specified when starting the collector app. Otherwise an exception will occur and the application will not start. The reason is the use of an internal reflection API by Retrofit, which is used by the InfluxDB driver.

```
--illegal-access=warn --add-opens java.base/java.lang.invoke=ALL-UNNAMED
```