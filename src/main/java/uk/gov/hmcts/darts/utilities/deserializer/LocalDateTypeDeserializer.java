package uk.gov.hmcts.darts.utilities.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        return LocalDate.parse(parser.getText(), FORMATTER);
    }
}

