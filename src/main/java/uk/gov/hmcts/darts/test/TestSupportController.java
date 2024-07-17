package uk.gov.hmcts.darts.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.cache.token.service.TokenRegisterable;

import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@SuppressWarnings({"PMD.UnnecessaryAnnotationValueElement", "PMD.TestClassWithoutTestCases"})
@ConditionalOnProperty(prefix = "darts-gateway.testing-support-endpoints", name = "enabled", havingValue = "true")
public class TestSupportController {

    private final RedisTemplate<String, Object> template;

    @SuppressWarnings({"unchecked", "PMD.CloseResource"})
    @DeleteMapping(value = "/functional-tests/clean")
    public void cleanUpDataAfterFunctionalTests() {
        deleteKeyByPattern(TokenRegisterable.CACHE_PREFIX + "*");
    }

    public boolean deleteKeyByPattern(String pattern) {
        Set<byte[]> patternResultConf = template.getConnectionFactory().getConnection().keys(pattern.getBytes());
        if(Objects.nonNull(patternResultConf) && !patternResultConf.isEmpty()) {
            template.getConnectionFactory().getConnection().del(patternResultConf.toArray(new byte[0][]));
        }
        return true;
    }
}
