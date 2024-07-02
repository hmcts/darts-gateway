package uk.gov.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.cache.token.config.ExternalUserToInternalUserMapping;
import uk.gov.hmcts.darts.cache.token.config.SecurityProperties;

@RestController
@RequestMapping(value = "/functional-tests")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "darts-gateway.testing-support-endpoints", name = "enabled", havingValue = "true")
@SuppressWarnings({"PMD.UnnecessaryAnnotationValueElement", "PMD.TestClassWithoutTestCases"})
public class TestSupportController { ;

    private final RedisTemplate template;

    private final SecurityProperties securityProperties;

    @SuppressWarnings({"unchecked", "PMD.CloseResource"})
    @DeleteMapping(value = "/clean")
    public void cleanUpDataAfterFunctionalTests() {
        for (ExternalUserToInternalUserMapping mapping : securityProperties.getUserExternalInternalMappings()) {
            template.delete(mapping.getUserName());
        }
    }
}
