package uk.gov.hmcts.darts.common.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * A client that drives the functional test state e.g. redis state as pre-requisites for functional tests.
 */
@RequiredArgsConstructor
public class FunctionalTestClient {
    private final String baseLocation;

    public void clear() throws IOException, InterruptedException {
       // DO nothing
    }
}
