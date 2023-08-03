package uk.gov.hmcts.darts.utilities.assetions;

import com.synapps.moj.dfs.response.CourtLog;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("PMD.HideUtilityClassConstructor")
public class CustomAssertions {

    public static CourtLogAssert verifyThat(CourtLog actual) {
        return new CourtLogAssert(actual);
    }
}