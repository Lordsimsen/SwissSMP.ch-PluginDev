package ch.swisssmp.transformations.logic;

import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.Optional;

public abstract class LogicNode {

    public JsonObject save(){
        JsonObject json = new JsonObject();
        saveData(json);
        return json;
    }

    protected abstract void saveData(JsonObject json);

    protected static Optional<? extends LogicNode> load(JsonObject json) {
        Type type = Type.parse(JsonUtil.getString("type", json));
        if (type == null) return Optional.empty();
        LogicNode node;
        switch (type) {
            case CONNECTION:
                node = LogicConnection.loadConnection(json).orElse(null);
                break;
            case GATE:
                node = LogicGate.loadGate(json).orElse(null);
                break;
            default: {
                Bukkit.getLogger().warning("[LogicSystem] LogicNode Type " + type + " not implemented!");
                return Optional.empty();
            }
        }
        if(node==null) return Optional.empty();

        return Optional.of(node);
    }

    public enum Type {
        CONNECTION,
        GATE;

        public static Type parse(String s) {
            try {
                return Type.valueOf(s);
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
