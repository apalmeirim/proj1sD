package tukano.impl;

import tukano.api.java.Blobs;
import tukano.impl.grpc.clients.GrpcBlobsClient;
import tukano.impl.java.Discovery;
import tukano.impl.rest.clients.RestBlobsClient;

public class BlobsClientFactory {

    public static Blobs getClients() {
        var serverURI = Discovery.getInstance().knownUrisOf("blobs", 1)[0];
        if (serverURI.toString().contains("rest")) return new RestBlobsClient(serverURI);
        else return new GrpcBlobsClient(serverURI);

    }


}

