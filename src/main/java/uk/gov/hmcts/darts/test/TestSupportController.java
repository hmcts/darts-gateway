package uk.gov.hmcts.darts.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.darts.cache.AuthenticationCacheService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@SuppressWarnings({"PMD.UnnecessaryAnnotationValueElement", "PMD.TestClassWithoutTestCases"})
@ConditionalOnProperty(prefix = "darts-gateway.testing-support-endpoints", name = "enabled", havingValue = "true")
public class TestSupportController {

    private final RedisTemplate<String, String> template;

    @DeleteMapping(value = "/functional-tests/clean")
    public void cleanUpDataAfterFunctionalTests() {
        deleteKeyByPattern(AuthenticationCacheService.CACHE_PREFIX + ":*");
    }

    public void deleteKeyByPattern(String pattern) {
        List<String> keys;
        try (Cursor<String> cursor = template.scan(
            ScanOptions.scanOptions()
                .match(pattern)
                .build())) {
            keys = cursor.stream().toList();
        }
        log.info("Found {} redis keys matching pattern '{}'", keys.size(), pattern);
        keys.forEach(s -> {
            log.info("Deleting redis key: {}", s);
            template.delete(s);
        });
    }
}
