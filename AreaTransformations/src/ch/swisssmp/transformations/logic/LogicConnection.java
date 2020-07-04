package ch.swisssmp.transformations.logic;

import com.google.gson.JsonObject;

import java.util.Optional;

public class LogicConnection extends LogicNode {
    protected static Optional<LogicConnection> loadConnection(JsonObject json){
        return Optional.empty();
    }

    @Override
    protected void saveData(JsonObject json) {

    }
}
