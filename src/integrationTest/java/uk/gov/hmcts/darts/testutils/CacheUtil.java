package uk.gov.hmcts.darts.testutils;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;

import java.time.Duration;

@UtilityClass
public class CacheUtil {
    public long getMillisForTimeToIdleMinusSeconds(CacheProperties properties, int seconds) {
        return Duration.ofSeconds(properties.getEntryTimeToIdleSeconds()).minusSeconds(seconds).toMillis();
    }

    public long getMillisForTimeToIdle(CacheProperties properties) {
        return Duration.ofSeconds(properties.getEntryTimeToIdleSeconds()).toMillis();
    }

    public long getMillisForSeconds(int seconds) {
        return Duration.ofSeconds(seconds).toMillis();
    }
}
