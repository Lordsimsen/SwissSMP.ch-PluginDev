package ch.swisssmp.transformations.logic;

import ch.swisssmp.transformations.AreaTransformations;
import ch.swisssmp.utils.JsonUtil;
import ch.swisssmp.utils.VectorKey;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Optional;

public class LogicGate extends LogicNode {

    private GateType type;

    private boolean isTrue(Location exclude, boolean excludedIsTrue){
        /*
        switch(type){
            case AND:
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        if(!excludedIsTrue) return false;
                        else continue;
                    };
                    Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                    if(current!=entry.getValue()){
                        AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
                        return false;
                    }
                }
                return true;
            case NAND:
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        if(!excludedIsTrue) return true;
                        else continue;
                    };
                    Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                    if(current!=entry.getValue()){
                        return true;
                    }
                    else{
                        AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
                    }
                }
                return false;
            case OR:
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        if(excludedIsTrue) return true;
                        else continue;
                    };
                    Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                    if(current==entry.getValue()){
                        return true;
                    }
                    else{
                        AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
                    }
                }
                return false;
            case NOR:
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        if(excludedIsTrue) return false;
                        else continue;
                    };
                    Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                    if(current==entry.getValue()){
                        AreaTransformations.info("[AreaTransformations] "+entry.getKey().toString()+" ist "+current+" vs "+entry.getValue());
                        return false;
                    }
                }
                return true;
            case XOR:{
                boolean firstCheck = true;
                boolean firstValue = true;
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    boolean stepResult;
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        stepResult = excludedIsTrue;
                    }
                    else{
                        Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                        stepResult = (current==entry.getValue());
                    }
                    if(firstCheck){
                        firstValue = stepResult;
                        firstCheck = false;
                    }
                    if((firstValue && !stepResult) || (!firstValue && stepResult)){
                        return true;
                    }
                }
                return false;
            }
            case XNOR:{
                boolean firstCheck = true;
                boolean firstValue = true;
                for(Map.Entry<VectorKey, Integer> entry : valueMap.entrySet()){
                    boolean stepResult;
                    Vector vector = entry.getKey().getVector();
                    if(vector.getBlockX()==exclude.getBlockX()&&vector.getBlockY()==exclude.getBlockY() && vector.getBlockZ()==exclude.getBlockZ()) {
                        stepResult = excludedIsTrue;
                    }
                    else{
                        Integer current = getCurrent(exclude.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
                        stepResult = (current==entry.getValue());
                    }
                    if(firstCheck){
                        firstValue = stepResult;
                        firstCheck = false;
                    }
                    if((firstValue && !stepResult) || (!firstValue && stepResult)){
                        return false;
                    }
                }
                return true;
            }
            default:
                return false;
        }*/
        return false;
    }

    protected static Optional<LogicGate> loadGate(JsonObject json){
        LogicGate result = new LogicGate();
        if (json.has("gate_type")) result.type = GateType.parse(JsonUtil.getString("gate_type", json));
        return Optional.empty();
    }

    @Override
    protected void saveData(JsonObject json) {

    }

    public enum GateType{
        AND,
        OR,
        NAND,
        NOR,
        XOR,
        XNOR;

        public static GateType parse(String s) {
            try {
                return GateType.valueOf(s);
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
