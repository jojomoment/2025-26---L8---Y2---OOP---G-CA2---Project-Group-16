package musichub.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class MusicTrackJsonUtil {

    private static final Gson gson = new Gson();

   
    public static <T> String toJson(T entity) {
        return gson.toJson(entity);
    }

  
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

  
    public static <T> String listToJson(List<T> list) {
        return gson.toJson(list);
    }

    
    public static <T> List<T> listFromJson(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, type);
    }
}
