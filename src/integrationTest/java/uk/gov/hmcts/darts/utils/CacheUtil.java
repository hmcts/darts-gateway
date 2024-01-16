package uk.gov.hmcts.darts.utils;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.darts.cache.token.config.CacheProperties;

@UtilityClass
public class CacheUtil {
    public long getMillisForTimeToIdleMinusSeconds(CacheProperties properties, int seconds) {
        return properties.getEntryTimeToIdleSeconds()  * 1000 - seconds * 1000L;
    }

    public long getMillisForTimeToIdle(CacheProperties properties) {
        return properties.getEntryTimeToIdleSeconds() * 1000;
    }

    public long getMillisForSeconds(int seconds) {
        return seconds * 1000L;
    }
}
