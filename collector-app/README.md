# Collector Application

Collect and store various sensor values of Ökofen heating systems. Following sources are supported:

- Ökofen JSON HTTP API (must be enabled in your Ökofen settings)

Following targets are supported:

- InfluxDB

## Quick Start

### Windows - Batch File

1. Clone the repository
2. Execute the gradle task 'buildSetupFolder'
3. A 'setup' folder is created in the gradle 'build' directory
4. Modify the configuration files in the 'config' folder
5. On Windows execute the 'start-collector-application.bat'

### Windows - Docker

1. Clone the repository
2. Execute the gradle task 'buildBootImage'
3. Tag the built image
   ```docker tag oekofen-collector-app:0.2.1 wernerfragner/oekofen-collector-app:0.2.3```
4. Login into Docker Hub (in the console window)
5. Push the image to Docker Hub
   ```docker push wernerfragner/oekofen-collector-app:0.2.3```

### Raspberry - Docker

Following steps describe building a docker image for Raspberry (arm32v7 platform). The docker image is build on the
Raspberry itself in order to have the required arm32v7 build environment for the docker image.

1. Clone the repository
2. Execute the gradle task 'buildDockerFolder'
3. A 'docker' folder is created in the gradle 'build' directory
4. Copy this folder to 'heating/collector-app' at your Raspberry (e.g., using WinSCP).
5. Open a SSH session to your Raspberry and navigate to this folder.
6. Build the docker image using following command (version tag must be changed!)
   ```docker build -t wernerfragner/oekofen-collector-app:0.2.3-armv7 .```
7. Push the image to Docker Hub
   ```docker push wernerfragner/oekofen-collector-app:0.2.3-armv7```

### VM Options

Following JavaVM options must be specified when starting the collector app. Otherwise an exception will occur and the
application will not start. The reason is the use of an internal reflection API by Retrofit, which is used by the
InfluxDB driver.

```
--illegal-access=warn --add-opens java.base/java.lang.invoke=ALL-UNNAMED
```

### Common commands

Restart collector application

```
docker-compose restart collector-app
docker container restart <container-id>
```

redirect stdout, stderr

```
docker container attach <container-id>
```