package condorcet.Utils;



import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    );
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
       // return new JsonPrimitive(src.format(formatter));
       return new JsonPrimitive(src.format(formatters.get(0)));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(json.getAsString(), formatter);
            } catch (Exception ignored) {
                // Пробуем следующий формат
            }
        }
        throw new JsonParseException("Unparseable date: " + json.getAsString());
    }
}
