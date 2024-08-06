package uk.gov.hmcts.darts.testutils.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

public class TokenStub {

    public void stubToken() {
        stubFor(post(urlPathEqualTo("/token"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("""
                                                          {"access_token":"eyJhbGciOiJSUzI1NiIsImtpZCI6Ilg1ZVhrNHh5b2pORnVtMWtsMll0djhkbE5QNC1jNTdkTzZRR1RWQndhTmsiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiIwNTNkNDRmOS1jZDI3LTQ2M2UtYTk5NS1iY2Y4MzUyMzNjZjgiLCJpc3MiOiJodHRwczovL2htY3RzZGFydHNiMmNzYm94LmIyY2xvZ2luLmNvbS84OWI4ZGUyZS1lYjFiLTQyZmItYTU5Yy04MTNlYTJiODJhNTYvdjIuMC8iLCJleHAiOjE3MjI5NDI4NzYsIm5iZiI6MTcyMjkzOTI3NiwiaWRwIjoiTG9jYWxBY2NvdW50Iiwib2lkIjoiNDNjYjNlMjEtYjAzYS00NjI5LTk5MDgtYjBkZGFmNzQ0OGI5Iiwic3ViIjoiNDNjYjNlMjEtYjAzYS00NjI5LTk5MDgtYjBkZGFmNzQ0OGI5IiwiZ2l2ZW5fbmFtZSI6IkRhcnRzIiwiZmFtaWx5X25hbWUiOiJUZXN0ZXIiLCJ0ZnAiOiJCMkNfMV9yb3BjX2RhcnRzX3NpZ25pbiIsInNjcCI6IkZ1bmN0aW9uYWwuVGVzdCIsImF6cCI6IjA1M2Q0NGY5LWNkMjctNDYzZS1hOTk1LWJjZjgzNTIzM2NmOCIsInZlciI6IjEuMCIsImlhdCI6MTcyMjkzOTI3Nn0.WqdVH-WtAfsYghFT_T27SbPar9B4ha4RazLrgSFsfGLaA3-NunfqV2UyCesaJjk4qmvocjltr-oxle9VdvUiY6J8QsXLQqnNOzVY6PY9VEjd8FqM6J5Cc7db1Sm8hMsjVZxbXHIAWgHCK3wVWEK7KgjmFSy6CU9OkVf6Fu7zDLApFnefzQ-hYGpL6dCcvH87Y9jWSwYDcNqqwMsEwLZaFzhBUltVLHJ607Mt-nd_8x3N0f0z8RL1VCpstPrTbzQiOetTtI6EaGmz3u2W6NT19FVgc5C8OVrrnT-x4dlVFC5rB7CGo_kEF2ixaZnSMrW1Ij31JCLyyZ9OJzIE3IyjfA","token_type":"Bearer","expires_in":"3600"}
                                                      """.trim())));
    }

    public void stubJwksKeys() {
        stubFor(get(urlPathEqualTo("/keys"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("""
                                                              {"keys":[{"kid":"X5eXk4xyojNFum1kl2Ytv8dlNP4-c57dO6QGTVBwaNk","nbf":1493763266,"use":"sig","kty":"RSA","e":"AQAB","n":"tVKUtcx_n9rt5afY_2WFNvU6PlFMggCatsZ3l4RjKxH0jgdLq6CScb0P3ZGXYbPzXvmmLiWZizpb-h0qup5jznOvOr-Dhw9908584BSgC83YacjWNqEK3urxhyE2jWjwRm2N95WGgb5mzE5XmZIvkvyXnn7X8dvgFPF5QwIngGsDG8LyHuJWlaDhr_EPLMW4wHvH0zZCuRMARIJmmqiMy3VD4ftq4nS5s8vJL0pVSrkuNojtokp84AtkADCDU_BUhrc2sIgfnvZ03koCQRoZmWiHu86SuJZYkDFstVTVSR0hiXudFlfQ2rOhPlpObmku68lXw-7V-P7jwrQRFfQVXw"}]}
                                                      """.trim())));

    }

    public void verifyNumberOfTimesKeysObtained(int numberOfTimes) {
        verify(exactly(numberOfTimes), getRequestedFor(urlPathEqualTo("/keys")));
    }
}
