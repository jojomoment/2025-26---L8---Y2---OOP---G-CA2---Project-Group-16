package musichub.util;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import musichub.domain.MusicTrack;
import musichub.shared.ServerResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class MusicTrackJsonUtil
 {
    

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingStrategy(new FieldNamingStrategy() {
                @Override
                public String translateName(Field field) {
                    String name = field.getName();
                    if (name.startsWith("f") && name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
                        return Character.toLowerCase(name.charAt(1)) + name.substring(2);
                    }
                    return name;
                }
            })
            .create();
   

    public static <T> String toJson(T entity) 
    {
        return gson.toJson(entity);
    }
  
    public static <T> T fromJson(String json, Class<T> clazz) 
    {
        if (clazz == ServerResponse.class) {
            @SuppressWarnings("unchecked")
            T response = (T) parseServerResponse(json);
            return response;
        }

        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) 
    {
        return gson.fromJson(json, type);
    }

  
    public static <T> String listToJson(List<T> list)
    {
        return gson.toJson(list);
    }

    
    public static <T> List<T> listFromJson(String json, Class<T> clazz) 
    {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, type);
    }

    private static ServerResponse<?> parseServerResponse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        boolean success = root.has("success") && !root.get("success").isJsonNull() && root.get("success").getAsBoolean();
        String message = root.has("message") && !root.get("message").isJsonNull()
                ? root.get("message").getAsString()
                : null;

        JsonElement dataElement = root.has("data") ? root.get("data") : null;
        Object data = null;

        if (dataElement != null && !dataElement.isJsonNull()) {
            if (dataElement.isJsonObject()) {
                JsonObject dataObject = dataElement.getAsJsonObject();
                if (isMusicTrackObject(dataObject)) {
                    data = gson.fromJson(dataElement, MusicTrack.class);
                } else {
                    data = gson.fromJson(dataElement, Object.class);
                }
            } else if (dataElement.isJsonArray()) {
                if (isMusicTrackArray(dataElement.getAsJsonArray())) {
                    Type listType = TypeToken.getParameterized(List.class, MusicTrack.class).getType();
                    data = gson.fromJson(dataElement, listType);
                } else {
                    data = gson.fromJson(dataElement, Object.class);
                }
            } else {
                data = gson.fromJson(dataElement, Object.class);
            }
        }

        if (success) {
            return ServerResponse.ok(message, data);
        } else {
            return ServerResponse.error(message);
        }
    }

    private static boolean isMusicTrackObject(JsonObject object) {
        return (object.has("songId") && object.has("songTitle"))
                || (object.has("fSongId") && object.has("fSongTitle"));
    }

    private static boolean isMusicTrackArray(com.google.gson.JsonArray array) {
        return array.size() > 0
                && array.get(0).isJsonObject()
                && isMusicTrackObject(array.get(0).getAsJsonObject());
    }

    //Testing Code
    public static void main(String[] args) 
    {
    MusicTrack track = new MusicTrack(1, "Test Song", 120, 180.0);
    String json = toJson(track);
    System.out.println("JSON: " + json);

    MusicTrack back = fromJson(json, MusicTrack.class);
    System.out.println("Back: " + back);
}
}
