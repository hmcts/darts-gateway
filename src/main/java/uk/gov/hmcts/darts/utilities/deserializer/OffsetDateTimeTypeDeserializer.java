package uk.gov.hmcts.darts.utilities.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("PMD.LawOfDemeter")
public class OffsetDateTimeTypeDeserializer extends JsonDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        return OffsetDateTime.parse(parser.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

