package tukano.impl;

import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.impl.grpc.clients.GrpcShortsClient;
import tukano.impl.java.Discovery;
import tukano.impl.rest.clients.RestShortsClient;

public class ShortsClientFactory {


    public static Shorts getClients() {

        try {
            var serverURI = Discovery.getInstance().knownUrisOf("shorts", 1)[0];
            if (serverURI.toString().contains("rest")) return new RestShortsClient(serverURI);
            else return new GrpcShortsClient(serverURI);
        }catch (InterruptedException e){}
        return null;
    }


}



