# Collector Application
Collect and store various sensor values of Ökofen heating systems. Following sources are supported:
- Ökofen JSON HTTP API (must be enabled in your Ökofen settings)

Following targets are supported:
- InfluxDB

## Quick Start
1. Clone the repository
2. Execute the gradle task 'buildSetupFolder'
3. A 'setup' folder is created in the gradle 'build' directory
4. Modify the configuration files in the 'config' folder
5. On Windows execute the 'start-collector-application.bat'

### VM Options
Following JavaVM options must be specified when starting the collector app. Otherwise an exception will occur and the application will not start. The reason is the use of an internal reflection API by Retrofit, which is used by the InfluxDB driver.

```
--illegal-access=warn --add-opens java.base/java.lang.invoke=ALL-UNNAMED
```