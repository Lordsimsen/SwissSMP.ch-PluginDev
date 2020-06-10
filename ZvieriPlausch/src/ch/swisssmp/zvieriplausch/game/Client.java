package ch.swisssmp.zvieriplausch.game;

import ch.swisssmp.npc.NPCInstance;
import ch.swisssmp.utils.JsonUtil;

public class Client {

    private final int maxPatienceTime = 2400; //2 minutes ingame

    protected NPCInstance npc;
    private int waitingTime = 0;

    public Client(NPCInstance npc){
        this.npc = npc;
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
        return tip;
    }

    protected NPCInstance getNPCInstance(){
        return npc;
    }
}
