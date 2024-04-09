package tukano.impl.java;

import tukano.api.java.Shorts;

public class ShortsClientFactory {


    public static Shorts getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("ShortsService", 1);
        if (serverURI[0].getFragment().contains("rest")) return new RestShortsClient(serverURI[0]);
        else return new GrpcShortsClient(serverURI[0]);
    }


}

