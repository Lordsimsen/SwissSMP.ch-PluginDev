package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

public class Client {

    private final int maxPatienceTime = 2400; //2 minutes ingame

    protected NPCInstance npc;
    private int waitingTime = 0;

    public Client(NPCInstance npc){
        this.npc = npc;

        int baseTip = JsonUtil.getInt("baseTip", npc.getJsonData());
        Bukkit.getLogger().info("BaseTip client init: " + baseTip);

//        JsonObject json;
//        if(npc.getJsonData()!=null) {
//            json = npc.getJsonData();
//        } else {
//            json = new JsonObject();
//        }
//        JsonUtil.set("client", true, json);
//        JsonUtil.set("tip", JsonUtil.getInt("baseTip", npc.getJsonData()), json);
//        npc.setJsonData(json);
    }

    public void increaseWaitingTime(){
        waitingTime++;
    }

    protected int getTip(){
        int baseTip = JsonUtil.getInt("baseTip", npc.getJsonData());
        double patience = JsonUtil.getDouble("patience", npc.getJsonData());
        int patienceTime = (int) (maxPatienceTime*patience);
        double decay = (double) waitingTime / (double) patienceTime;
        int tip = baseTip - (int) ((double) baseTip * (decay));
        if(decay > 1) tip = 0;
        Bukkit.getLogger().info("Basetip: " + baseTip + ", Tip: " + tip);
        return tip;
    }

    protected NPCInstance getNPCInstance(){
        return npc;
    }
}
