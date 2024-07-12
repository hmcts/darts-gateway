package uk.gov.hmcts.darts.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * A client that drives the functional test state e.g. redis state as pre-requisites for functional tests.
 */
@RequiredArgsConstructor
@Slf4j
public class FunctionalTestClient {
    private final String baseLocation;

    public void clear() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        log.debug("Clearing down on url " + baseLocation + "/functional-tests/clean");
        HttpRequest httpRequest = HttpRequest
            .newBuilder()
            .DELETE().uri(URI.create(baseLocation + "/functional-tests/clean"))
            .build();

        HttpResponse<String> httpResponses = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.debug("Clearing down response is " + httpResponses.statusCode());
        Assertions.assertEquals(200, httpResponses.statusCode(), "Expected a 200 when communicating with " + baseLocation + "/functional-tests/clean. " +
                                "Response Status: " + httpResponses.statusCode() + "Response Body: " + httpResponses.body());
    }
}
