package uk.gov.hmcts.darts.common.client1.component;


import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * An interface that is called before a call is made to a downstream endpoint
 * whilst using the Spring rest clients e.g. {@link org.springframework.web.client.RestTemplate}
 */
@FunctionalInterface
public interface HttpHeadersInterceptor extends Consumer<HttpHeaders> {
}
