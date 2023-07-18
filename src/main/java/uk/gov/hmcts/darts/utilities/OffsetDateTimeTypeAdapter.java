package uk.gov.hmcts.darts.utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("PMD.LawOfDemeter")
public class OffsetDateTimeTypeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    @Override
    public JsonElement serialize(OffsetDateTime localDateTime, Type srcType,
                                 JsonSerializationContext context) {

        return new JsonPrimitive(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(localDateTime));
    }

    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) {

        return OffsetDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

