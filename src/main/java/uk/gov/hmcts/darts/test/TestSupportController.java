package uk.gov.hmcts.darts.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.cache.token.service.CacheProvider;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;

@Slf4j
@RequiredArgsConstructor
@RestController
@SuppressWarnings({"PMD.UnnecessaryAnnotationValueElement", "PMD.TestClassWithoutTestCases"})
@ConditionalOnProperty(prefix = "darts-gateway.testing-support-endpoints", name = "enabled", havingValue = "true")
public class TestSupportController {

    private final CacheProvider provider;

    @SuppressWarnings({"unchecked", "PMD.CloseResource"})
    @DeleteMapping(value = "/functional-tests/clean")
    public void cleanUpDataAfterFunctionalTests() {
        deleteKeyByPattern(TokenRegisterable.CACHE_PREFIX + "*");
    }

    public boolean deleteKeyByPattern(String pattern) {
        provider.cleanAll();
        return true;
    }
}