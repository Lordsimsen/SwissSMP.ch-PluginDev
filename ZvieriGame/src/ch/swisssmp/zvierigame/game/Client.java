package ch.swisssmp.zvierigame.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;
import com.google.gson.JsonObject;

public class Client {

    private final int maxPatienceTime = 2400; //2 minutes ingame

    protected NPCInstance npc;
    private int waitingTime = 0;

    public Client(NPCInstance npc){
        this.npc = npc;

        JsonObject json;
        if(npc.getJsonData()!=null) {
            json = npc.getJsonData();
        } else {
            json = new JsonObject();
        }
        JsonUtil.set("client", true, json);
        JsonUtil.set("tip", JsonUtil.getInt("baseTip", npc.getJsonData()), json);
        npc.setJsonData(json);
    }

    public void increaseWaitingTime(){
        waitingTime++;
        adjustTip();
    }

    protected void adjustTip(){
        int baseTip = JsonUtil.getInt("baseTip", npc.getJsonData());
        double patience = JsonUtil.getDouble("patience", npc.getJsonData());
        int patienceTime = (int) (maxPatienceTime*patience);
        double decay = (double) patienceTime / (double) waitingTime;
        int newTip = baseTip - (int) ((double) baseTip / (decay));
        if(decay > 1) newTip = 0;
        JsonUtil.set("tip", newTip, npc.getJsonData());
    }

    protected NPCInstance getNPCInstance(){
        return npc;
    }
}
