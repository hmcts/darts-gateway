package uk.gov.hmcts.darts.common.client.component;


import org.springframework.http.HttpEntity;

import java.util.function.Consumer;

/**
 * An interface that is called before a call is made to a downstream endpoint
 * whilst using the Spring rest clients e.g. {@link org.springframework.web.client.RestTemplate}
 */
@FunctionalInterface
public interface HttpEntityInterceptor extends Consumer<HttpEntity<?>> {
}
