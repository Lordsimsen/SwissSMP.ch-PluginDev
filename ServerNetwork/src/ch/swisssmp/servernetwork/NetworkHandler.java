package ch.swisssmp.servernetwork;

import ch.swisssmp.servernetwork.client.NettyClient;
import ch.swisssmp.servernetwork.server.NettyServer;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {

    private final NettyServer server;
    private final List<NettyClient> clients = new ArrayList<NettyClient>();

    protected NetworkHandler(int listenerPort){
        this.server = new NettyServer(listenerPort);
    }

    public void initialize(){

    }

    public void disconnect(){
        server.stop();
        for(NettyClient client : clients){
            client.stop();
        }
    }
}
