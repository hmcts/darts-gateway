package uk.gov.hmcts.darts.utilities.assetions;

import com.synapps.moj.dfs.response.CourtLogEntry;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
@SuppressWarnings("PMD.HideUtilityClassConstructor")
public class CustomAssertions {

    public static CourtLogEntriesAssert verifyThat(List<CourtLogEntry> actual) {
        return new CourtLogEntriesAssert(actual);
    }
}