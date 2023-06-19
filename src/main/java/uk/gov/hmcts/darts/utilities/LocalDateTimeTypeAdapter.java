package uk.gov.hmcts.darts.utilities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d:MMM:uuuu HH:mm:ss");

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType,
                                 JsonSerializationContext context) {

        return new JsonPrimitive(FORMATTER.format(localDateTime));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) {

        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}

