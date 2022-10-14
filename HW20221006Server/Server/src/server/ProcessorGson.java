package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class ProcessorGson {
    private static Gson gson = new Gson();

    public static String serializeListToString(List<Place> places){
        return gson.toJson(places);
    }

    public static List<Place> deserializeStringToList(String places){
        Type listType = new TypeToken<Collection<Place>>(){}.getType();
        return gson.fromJson(places, listType);
    }

}
