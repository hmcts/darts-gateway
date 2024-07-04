package uk.gov.hmcts.darts.common.payload;

import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.darts.common.utils.TestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SubstitutablePayload {
    private final String fileName;

    private final Map<String, String> substitutionMap = new HashMap<>();

    public SubstitutablePayload setSubstituteValue(SubstituteKey key, String value) {
        substitutionMap.put(key.getKey(), value);
        return this;
    }

    public String substitute() throws IOException {
        return TestUtils.getContentsFromFile(fileName, substitutionMap);
    }
}
