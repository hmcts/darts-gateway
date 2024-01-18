package uk.gov.hmcts.darts.utils.matcher;

import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.MatchResult;

public class MultipartDartsProxyContentPattern extends ContentPattern<Object> {
    private static final String EMPTY = "";

    public MultipartDartsProxyContentPattern() {
        super(EMPTY);
    }

    @Override
    public MatchResult match(Object value) {
        String body =  new String((byte[])value);
        boolean dispositionValid = body.contains("form-data; name=\"metadata\"") && body.contains("form-data; name=\"file\"");
        boolean headerValid = body.contains("Content-Type: application/octet-stream") || body.contains("Content-Type: audio/mpeg")
            &&  body.contains("Content-Type: application/json");

        if (dispositionValid && headerValid) {
            return MatchResult.exactMatch();
        }
        return MatchResult.noMatch();
    }

    @Override
    public String getName() {
        return EMPTY;
    }

    @Override
    public String getExpected() {
        return EMPTY;
    }
}
