package tukano.impl.java;

import tukano.api.java.Shorts;
import tukano.impl.grpc.clients.GrpcShortsClient;

public class ShortsClientFactory {


    public static Shorts getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("ShortsService", 1)[0];
        if (serverURI.getFragment().contains("rest")) return new RestShortsClient(serverURI);
        else return new GrpcShortsClient(serverURI);
    }


}

