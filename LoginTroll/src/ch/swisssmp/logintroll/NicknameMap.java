package ch.swisssmp.logintroll;

import ch.swisssmp.webcore.DataSource;
import ch.swisssmp.webcore.HTTPRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class NicknameMap {

    private static final HashMap<String,String> map = new HashMap<String,String>();

    protected static String getReplacement(String key){
        return map.get(key);
    }

    protected static void load(){
        map.clear();
        HTTPRequest request = DataSource.getResponse(LoginTrollPlugin.getInstance(), "names.php");
        request.onFinish(()->{
            JsonObject json = request.getJsonResponse();
            if(json==null || !json.has("names")) return;
            JsonObject names = json.getAsJsonObject("names");
            for(Map.Entry<String, JsonElement> entry : names.entrySet()){
                map.put(entry.getKey(),entry.getValue().getAsString());
            }
        });
    }

    protected static void clear(){
        map.clear();
    }
}
