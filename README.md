# darts-gateway

# SoapUI

* [SoapUI](https://www.soapui.org/downloads/soapui/) can be used for "Try it out" functionality
  using the ServiceContext Header with a valid system user (CPP / XHIBIT / MID_TIER).
* **IMPORTANT! The property values will need to be updated to suit your test user, remembering not to commit them if you make any changes!**
* Go to the top level project folder in SoapUI and choose Custom Properties to change them.
* See https://www.soapui.org/docs/soap-and-wsdl/

## DARTSService SoapUI

* To View the application SOAP Web Services:
  * http://localhost:8070/service/darts/DARTSService?wsdl
* Import SOAP Project [README-DARTSService](README-DARTSService-soapui-project.xml) with
  initial [DARTSService WSDL](src/main/resources/ws/dartsService.wsdl).
* Sample requests for all operations have been created. Initial requests e.g. addCase will use the ServiceContext Soap Header with some custom project
  properties: `userName="${#Project#userName}" password="${#Project#password}"`
* When testing the add audio endpoint make sure to change the properties to support MTOM and Multipart. Additionally, attach an mp2 file

## ContextRegistryService SoapUI

* To View the application SOAP Web Services:
  * http://localhost:8070/service/darts/runtime/ContextRegistryService?wsdl
* Import SOAP Project [README-ContextRegistryService](README-ContextRegistryService-soapui-project.xml) with
  initial [ContextRegistryService WSDL](context/src/main/resources/ws/ContextRegistryService.wsdl).
* Sample requests for all operations have been created. Initial requests e.g. register will use the ServiceContext Soap Header with some custom project
  properties: `userName="${#Project#userName}" password="${#Project#password}"`
* Requests for lookup / unregister operations use the JWT token property provided in the Soap Body, so you will need to remember to update it using the register
  response: `<token>${#Project#token}</token>`

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

### Prerequisites

For this project to build successfully you need the darts open api artifact in the local
maven repository. To do this then follow these steps:-

1) Checkout https://github.com/hmcts/darts-api
2) Run publishToMavenLocal to install the openapi artifact into the local maven repository

```bash
  ./gradlew build
```

### Running the application

Create the image of the application by executing the following command:

```bash
  ./gradlew assemble
```

Create docker image:

```bash
  docker-compose build
```

Run the distribution (created in `build/install/darts-gateway` directory)
by executing the following command:

```bash
  docker-compose up
```

This will start the API container exposing the application's port
(set to `8070` in this template app).

In order to test if the application is up, you can call its health endpoint:

```bash
  curl http://localhost:8070/health
```

You should get a response similar to this:

```
  {"status":"UP","diskSpace":{"status":"UP","total":249644974080,"free":137188298752,"threshold":10485760}}
```

### Alternative script to run application

To skip all the setting up and building, just execute the following command:

```bash
./bin/run-in-docker.sh
```

For more information:

```bash
./bin/run-in-docker.sh -h
```

Script includes bare minimum environment variables necessary to start api instance. Whenever any variable is changed or any other script regarding docker
image/container build, the suggested way to ensure all is cleaned up properly is by this command:

```bash
docker-compose rm
```

It clears stopped containers correctly. Might consider removing clutter of images too, especially the ones fiddled with:

```bash
docker images

docker image rm <image-id>
```

There is no need to remove postgres and java or similar core images.

### Configuring the WSDL APIs

#### Updating the DARTS Legacy SOAP API

If there are updates to the legacy darts API please add all supporting files into the directory [here](src/main/resources/ws/dartsService)

#### Updating the Documentum Context Registry SOAP API

If there are updates to the documentum context registry please add all supporting files into the directory [here](context/src/main/resources/ws)

### Building the new configuration

This project can be built by the gradle command :-

gradle clean build

Post build, you will find that the wsdl files directly under [here](src/main/resources/ws) will have updated and are ready for git commit. If you
are happy with the wsdl changes then commit them

#### Building just the new darts context registry wsdl

gradle clean processContextRegistryWSDL

#### Building just the new legacy darts wsdl

gradle clean processDartsServiceWSDL

### Other

Hystrix offers much more than Circuit Breaker pattern implementation or command monitoring.
Here are some other functionalities it provides:

* [Separate, per-dependency thread pools](https://github.com/Netflix/Hystrix/wiki/How-it-Works#isolation)
* [Semaphores](https://github.com/Netflix/Hystrix/wiki/How-it-Works#semaphores), which you can use to limit
  the number of concurrent calls to any given dependency
* [Request caching](https://github.com/Netflix/Hystrix/wiki/How-it-Works#request-caching), allowing
  different code paths to execute Hystrix Commands without worrying about duplicating work

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
