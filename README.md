# darts-gateway

# SoapUI

* [SoapUI](https://www.soapui.org/downloads/soapui/) can be used for "Try it out" functionality
  using the ServiceContext Header with a valid system user (CPP / XHIBIT / MID_TIER). Firstly you will need to set the
  following properties within SoapUI

    * username - The user to authenticate with
    * password - The password to authenticate with
    * token - The token (either jwt or documentum) to authenticate with

* **IMPORTANT! Do not add the properties as project properties as they will be saved back to the committed xml files. Instead set them as global properties.
  See https://www.soapui.org/docs/soap-and-wsdl/**

## DARTSService SoapUI

* To View the application SOAP Web Services:
    * http://localhost:8070/service/darts/DARTSService?wsdl
* Import SOAP Project [README-DARTSService](README-DARTSService-soapui-project.xml) with
  initial [DARTSService WSDL](src/main/resources/ws/dartsService.wsdl).
* Sample requests for all operations have been created (using both user name and password authentication as well as token authentication). Initial requests e.g.
  addCase will use the ServiceContext Soap Header with some custom project
  properties: `userName="${#Project#userName}" password="${#Project#password}"`
* When testing the add audio endpoint make sure to change the properties to support MTOM and Multipart. Additionally, attach an mp2 file

## ContextRegistryService SoapUI

* To View the application SOAP Web Services:
    * http://localhost:8070/service/darts/runtime/ContextRegistryService?wsdl
* Import SOAP Project [README-ContextRegistryService](README-ContextRegistryService-soapui-project.xml) with
  initial [ContextRegistryService WSDL](context/src/main/resources/ws/ContextRegistryService.wsdl).
* Sample requests for all operations have been created (using both user name and password authentication as well as token authentication). Initial requests e.g.
  register will use the ServiceContext Soap Header with some custom project
  properties: `userName="${#Project#userName}" password="${#Project#password}"`
* Requests for lookup / unregister operations use the JWT token property provided in the Soap Body, so you will need to remember to update it using the register
  response: `<token>${#Project#token}</token>`

# Postman

* [Postman](https://www.postman.com/)  can be used for "Try it out" functionality
  using the ServiceContext Header with a valid system user (CPP / XHIBIT / MID_TIER). Firstly you will need to set the
  following properties within Postman NOTE: This excludes the add audio test. SoapUi must be used in the case of addaudio

    * userToUse - The user to authenticate with
    * passwordToUse - The password to authenticate with
    * tokenToUse - The token (jwt by default) to authenticate with
    * gatewayurl - The gateway url to use

## DARTSService Postman

* To View the application SOAP Web Services:
    * http://localhost:8070/service/darts/DARTSService?wsdl
* Import Project [README-DARTSService](README-DARTSService-postman-project.json)
* Sample requests for all operations have been created (using both user name and password authentication as well as token authentication)`

## ContextRegistryService Postman

* To View the application SOAP Web Services:
    * http://localhost:8070/service/darts/runtime/ContextRegistryService?wsdl
* Import Postman Project [README-ContextRegistryService](README-ContextRegistryService-postman-project.json)
* Sample requests for all operations have been created (using both user name and password authentication as well as token authentication).

## Building and deploying the application

External dependencies:-

* darts-api
* redis

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

### Prerequisites

For this project to build successfully and run against the local darts api code you need the darts open api artifact in the local
maven repository. To do this manually then follow these steps:-

1) Checkout https://github.com/hmcts/darts-api
2) Run publishToMavenLocal to install the openapi artifact into the local maven repository

```bash
  ./gradlew build
```

### Environment variables

To run the service locally, you must set the following environment variables on your machine.
The required value of each variable is stored in Azure Key Vault as a Secret.

| Environment Variable Name                      | Corresponding Azure Key Vault Secret Name                                                                      |
|------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| AAD_B2C_TENANT_ID                              | AzureAdB2CTenantId                                                                                             |
| REDIS_CONNECTION_STRING                        | redis-connection-string (local Redis uri by default)                                                           |
| MAX_FILE_UPLOAD_SIZE_MEGABYTES                 | MaxFileUploadSizeInMegabytes (350mb by default)                                                                |
| AAD_B2C_ROPC_CLIENT_ID                         | AzureAdB2CFuncTestROPCClientId                                                                                 |
| AAD_B2C_CLIENT_ID                              | AzureAdB2CClientId                                                                                             |
| DAR_NOTIFY_EVENT_SECUREMENT_PASSWORD           | darts-gateway-DarNotifyEventSecurementPassword                                                                 |
| DAR_NOTIFY_EVENT_SECUREMENT_USERNAME           | darts-gateway-DarNotifyEventSecurementUsername                                                                 |
| DARTS_API_URL                                  | N/A (local darts uri by default)                                                                               |
| REDIS_SSL_ENABLED                              | N/A (true by default)                                                                                          |
| VIQ_EXTERNAL_USER_NAME                         | The VIQ username                                                                                               |
| VIQ_EXTERNAL_PASSWORD                          | The VIQ external facing password i.e. used by the client                                                       |
| VIQ_INTERNAL_PASSWORD                          | The VIQ internal facing password i.e. used by the gateway to talk to the Idp for token acquisition             |
| XHIBIT_EXTERNAL_USER_NAME                      | The XHIBIT username                                                                                            |
| XHIBIT_EXTERNAL_PASSWORD                       | The XHIBIT external facing password i.e. used by the client                                                    |
| XHIBIT_INTERNAL_PASSWORD                       | The XHIBIT internal facing password i.e. used by the gateway to talk to the Idp for token acquisition          |
| CP_EXTERNAL_USER_NAME                          | The common platform username                                                                                   |
| CP_EXTERNAL_PASSWORD                           | The common platform facing password i.e. used by the client                                                    |
| CP_INTERNAL_PASSWORD                           | The common platform facing password i.e. used by the gateway to talk to the Idp for token acquisition          |
| EXTERNAL_SERVICE_BASIC_AUTHORISATION_WHITELIST | comma separated list of external services ids which are allowed to call the DARTS API with basic authorisation |

To obtain the secret value, you may retrieve the keys from the Azure Vault by running the `az keyvault secret show`
command in the terminal. E.g. to obtain the value for `GOVUK_NOTIFY_API_KEY`, you should run:

```
az keyvault secret show --name GovukNotifyTestApiKey --vault-name darts-stg
```

and inspect the `"value"` field of the response.

Alternatively, you can log into the [Azure home page](https://portal.azure.com/#home), and navigate to
`Key Vault -> darts-stg -> Secrets`. Note in your Portal Settings you must have the `CJS Common Platform` directory
active for the secrets to be visible.

Once you have obtained the values, set the environment variables on your system. E.g. On Mac, you may run this command
in the terminal, replacing `<<env var name>>` and `<<secret value>>` as necessary:

```
launchctl setenv <<env var name>> <<secret value>>
```

> Note: there is also a convenient script for exporting all these secret values from the key-vault, ensure you have the Azure CLI, `az`, installed and have
> run `az login`.
> ```bash
> source bin/secrets-stg.sh
> ```

You will then need to restart intellij/terminal windows for it to take effect.

to make the changes permanent, make a `.zshrc` file in your users folder and populate it with this and their values:

```
export GOVUK_NOTIFY_API_KEY=
export AAD_B2C_TENANT_ID=
export REDIS_CONNECTION_STRING=
export MAX_FILE_UPLOAD_SIZE_MEGABYTES=
export AAD_B2C_ROPC_CLIENT_ID=
export DAR_NOTIFY_EVENT_SECUREMENT_PASSWORD=
export DAR_NOTIFY_EVENT_SECUREMENT_USERNAME=
export DARTS_API_URL=
export REDIS_SSL_ENABLED=
export AAD_B2C_CLIENT_ID=

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

### Troubleshooting

If the integration tests fail relating to a redis connection failure then please manually close down the running redis instance
through task manager

If we encounter a SOAP general error response (as shown below) please check that the redis server is accessible:-

<code>
&lt;SOAP-ENV:Envelope;&gt;
    &lt;SOAP-ENV:Header/&gt;
    &lt;SOAP-ENV:Body&gt;
        &lt;SOAP-ENV:Fault&gt;
            &lt;faultcode&gt;SOAP-ENV:Client&lt;/faultcode&gt;
            &lt;faultstring xml:lang=&quot;en&quot;&gt;An unexpected service exception occurred please check logs&lt;/faultstring&gt;
            &lt;detail&gt;
                &lt;ns3:ServiceInvocationException
                    xmlns:ns3=&quot;http://rt.fs.documentum.emc.com/&quot;&gt;
                    &lt;messageId&gt;E_UNKNOWN_CODE&lt;/messageId&gt;
                    &lt;message&gt;An unexpected service exception occurred please check logs&lt;/message&gt;
                    &lt;exceptionType/&gt;
                &lt;/ns3:ServiceInvocationException&gt;
            &lt;/detail&gt;
        &lt;/SOAP-ENV:Fault&gt;
    &lt;/SOAP-ENV:Body&gt;
&lt;/SOAP-ENV:Envelope&gt;
</code>

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

